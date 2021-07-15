package cashel_comms_manager;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.awt.Dimension;
import javax.swing.Box;

public class Main_Window {
	public static boolean thread_running;
	public static int num_dau_to_display = 4;
	
	
	// 		gbl_panel_DAU1.columnWidths = new int[] {30, 37, 30, 30, 34, 20};
//	gbl_panel_DAU1.rowHeights = new int[] {30, 30, 26, 30, 30, 30, 30};
	private static int[] columnWidths =  new int[] {30, 37, 30, 30, 34, 20};
	private static int[] rowHeights =  new int[] {30, 30, 26, 30, 30, 30, 30};
	public static String dau1_temp;
	public static String dau2_temp;
	public static String dau3_temp;
	public static String dau4_temp;
	private final String auto_login_tooltip_text = "<html>When enabled appends username and password to the URL<br />"
			+ "e.g. http://user:password@<ip>/mfgindex.html<br />"
			+ "NOTE: Does not work with Internet Explorer"
			+ "</html>";
	private JFrame frame;
	
	private static JPanel panel_DAU3;
	private static JPanel panel_DAU4;
	
	private Timer timer;
	private Timer get_temperature_timer;
	private final String set_tod_dialog_message = "Set TOD?"; 
	private final String username = "xxxx";
	private final String password = "xxxx";
	private final String package_password = "xxxx";
	private final String fl_username = "xxxx";
	private final String fl_password = "xxxx";
	private List<String> listbox_values = new ArrayList<String>();
	private Map<String,String> links_dict = new LinkedHashMap<String, String>()
		{{
			put("Mfgindex", "mfgindex");
			put("Tabindex", "tabindex");
			put("Dashboard", "dashboard.html");
			put("Delete Logs", "xxxx/delrecs.cgi");
			put("Firmware Upgrade", "upgrade_english.html");
			put("Firmware Package Upgrade", "packageupgrade");
			put("GPS Status", "xxxx/gps_status.cgi");
			put("FL8 Home", "fl_home_page.html");
		}};
	public static String dau1_ip;
	private static String dau2_ip;
	private static String dau3_ip;
	private static String dau4_ip;
	private boolean dau1_auto_login;
	private boolean dau2_auto_login;
	private boolean dau3_auto_login;
	private boolean dau4_auto_login;
	private JTextField textFieldEpochTime;
	private Map<String, String> statusbar_messages = new LinkedHashMap<String, String>()
			{{
				put("idle", "Idle");
				put("connecting", "Connecting to... ");
				put("tod", "Sending TOD command to... ");
			}};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				thread_running = false;
				try {
					Main_Window window = new Main_Window();
					// Set the look and feel to match the system e.g. Windows current theme
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					SwingUtilities.updateComponentTreeUI(window.frame);
					
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main_Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main_Window.class.getResource("/images/Qualitrol Icon.ico")));
			
		frame.getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
//		frame.setBounds(100, 100, 600, 600);
		frame.setBounds(0, 0, 600, 640);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Populate the listbox_values from the dictionary (map)
		for (String key : links_dict.keySet())
		{
			listbox_values.add(key);
		}
		
		JLabel label_1 = new JLabel("");
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2, 0, 0));
		
		JLabel label = new JLabel("");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {300, 300};
		gridBagLayout.rowHeights = new int[] {30, 30, 30, 200, 200, 40};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 1.0};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JPanel panel_TopSpacer = new JPanel();
		GridBagConstraints gbc_panel_TopSpacer = new GridBagConstraints();
		gbc_panel_TopSpacer.gridwidth = 2;
		gbc_panel_TopSpacer.insets = new Insets(0, 0, 5, 5);
		gbc_panel_TopSpacer.fill = GridBagConstraints.BOTH;
		gbc_panel_TopSpacer.gridx = 0;
		gbc_panel_TopSpacer.gridy = 1;
		frame.getContentPane().add(panel_TopSpacer, gbc_panel_TopSpacer);
		
		JPanel panel_Top = new JPanel();
		FlowLayout fl_panel_Top = (FlowLayout) panel_Top.getLayout();
		fl_panel_Top.setVgap(0);
		fl_panel_Top.setHgap(0);
		GridBagConstraints gbc_panel_Top = new GridBagConstraints();
		gbc_panel_Top.fill = GridBagConstraints.BOTH;
		gbc_panel_Top.gridwidth = 2;
		gbc_panel_Top.insets = new Insets(0, 0, 5, 0);
		gbc_panel_Top.gridx = 0;
		gbc_panel_Top.gridy = 2;
		frame.getContentPane().add(panel_Top, gbc_panel_Top);
		
		JLabel lblEpochTime = new JLabel("Epoch Time ");
		panel_Top.add(lblEpochTime);
		
		textFieldEpochTime = new JTextField();
		panel_Top.add(textFieldEpochTime);
		textFieldEpochTime.setColumns(10);
		
		JButton btncopy = new JButton("Copy");
		panel_Top.add(btncopy);
		btncopy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection data = new StringSelection(textFieldEpochTime.getText());
				clipboard.setContents(data, null);
			}
		});

		JPanel panel_StatusBar = new JPanel();
		panel_StatusBar.setSize(new Dimension(300, 20));
		panel_StatusBar.setPreferredSize(new Dimension(300, 20));
		panel_StatusBar.setToolTipText("Statusbar");
		panel_StatusBar.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		FlowLayout flowLayout = (FlowLayout) panel_StatusBar.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_StatusBar = new GridBagConstraints();
		gbc_panel_StatusBar.anchor = GridBagConstraints.NORTH;
		gbc_panel_StatusBar.ipady = 5;
		gbc_panel_StatusBar.gridwidth = 2;
		gbc_panel_StatusBar.fill = GridBagConstraints.BOTH;
		gbc_panel_StatusBar.gridx = 0;
		gbc_panel_StatusBar.gridy = 5;
		frame.getContentPane().add(panel_StatusBar, gbc_panel_StatusBar);
		
		JLabel lblStatusBar = new JLabel("Idle");
		panel_StatusBar.add(lblStatusBar);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		FlowLayout menuBarLayout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		menuBar.setLayout(menuBarLayout);
		frame.setJMenuBar(menuBar);
		
		JMenu mntmfile = new JMenu("File");
		mntmfile.setHorizontalAlignment(SwingConstants.LEFT);
		mntmfile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mntmfile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setIconTextGap(0);
		mntmExit.addActionListener(new ExitApplication());
		mntmExit.setMnemonic(KeyEvent.VK_X);
		mntmfile.add(mntmExit);
		
		JMenu mnCommands = new JMenu("Commands");
		mnCommands.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Commands_Window commands_window = new Commands_Window();
				commands_window.setVisible(true);
			}
		});
		menuBar.add(mnCommands);
		
		JMenu mnDisplay = new JMenu("Display");
		mnDisplay.setMnemonic(KeyEvent.VK_D);
		menuBar.add(mnDisplay);
		
		JMenuItem mntm2DAU = new JMenuItem("2 DAU");
		mntm2DAU.addActionListener(new SetDAUVisibility());
		mntm2DAU.setMnemonic(KeyEvent.VK_2);
		mnDisplay.add(mntm2DAU);
		
		JMenuItem mntm4DAU = new JMenuItem("4 DAU");
		mntm4DAU.addActionListener(new SetDAUVisibility());
		mntm4DAU.setMnemonic(KeyEvent.VK_4);
		mnDisplay.add(mntm4DAU);
		
		JMenu mnSetIp = new JMenu("Set IP");
		mnSetIp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});
		menuBar.add(mnSetIp);
		
		JMenu mntmAbout = new JMenu("About");
		mntmAbout.setMnemonic(KeyEvent.VK_A);
		mntmAbout.setHorizontalAlignment(SwingConstants.LEFT);
		menuBar.add(mntmAbout);
		
		JMenu mntmHelp = new JMenu("Help");
		mntmHelp.setMnemonic(KeyEvent.VK_H);
		mntmHelp.setHorizontalAlignment(SwingConstants.LEFT);
		menuBar.add(mntmHelp);
		
		//================================================================================
		// DAU1
		//================================================================================
		JPanel panel_DAU1 = new JPanel();
		panel_DAU1.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagLayout gbl_panel_DAU1 = new GridBagLayout();
//		gbl_panel_DAU1.columnWidths = new int[] {30, 37, 30, 30, 34, 20};
//		gbl_panel_DAU1.rowHeights = new int[] {30, 30, 26, 30, 30, 30, 30};
		gbl_panel_DAU1.columnWidths = columnWidths;
		gbl_panel_DAU1.rowHeights = rowHeights;
		gbl_panel_DAU1.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0};
//		gbl_panel_DAU1.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_DAU1.setLayout(gbl_panel_DAU1);
		
		JSpinner spinnerDAU1Byte4 = new JSpinner();
		spinnerDAU1Byte4.setModel(new SpinnerNumberModel(192, 1, 255, 1));
		GridBagConstraints gbc_spinnerDAU1Byte4 = new GridBagConstraints();
		gbc_spinnerDAU1Byte4.anchor = GridBagConstraints.WEST;
		gbc_spinnerDAU1Byte4.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU1Byte4.gridx = 1;
		gbc_spinnerDAU1Byte4.gridy = 1;
		panel_DAU1.add(spinnerDAU1Byte4, gbc_spinnerDAU1Byte4);
		
		JSpinner spinnerDAU1Byte3 = new JSpinner();
		spinnerDAU1Byte3.setModel(new SpinnerNumberModel(168, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU1Byte3 = new GridBagConstraints();
		gbc_spinnerDAU1Byte3.anchor = GridBagConstraints.WEST;
		gbc_spinnerDAU1Byte3.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU1Byte3.gridx = 2;
		gbc_spinnerDAU1Byte3.gridy = 1;
		panel_DAU1.add(spinnerDAU1Byte3, gbc_spinnerDAU1Byte3);
		
		JSpinner spinnerDAU1Byte2 = new JSpinner();
		spinnerDAU1Byte2.setModel(new SpinnerNumberModel(1, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU1Byte2 = new GridBagConstraints();
		gbc_spinnerDAU1Byte2.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU1Byte2.gridx = 3;
		gbc_spinnerDAU1Byte2.gridy = 1;
		panel_DAU1.add(spinnerDAU1Byte2, gbc_spinnerDAU1Byte2);
		
		JSpinner spinnerDAU1Byte1 = new JSpinner();
		spinnerDAU1Byte1.setModel(new SpinnerNumberModel(11, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU1Byte1 = new GridBagConstraints();
		gbc_spinnerDAU1Byte1.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU1Byte1.gridx = 4;
		gbc_spinnerDAU1Byte1.gridy = 1;
		panel_DAU1.add(spinnerDAU1Byte1, gbc_spinnerDAU1Byte1);
		
		
		//================================================================================
		// Spinner Events - DAU1
		//================================================================================
		spinnerDAU1Byte1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				dau1_ip = BuildIP(spinnerDAU1Byte4, spinnerDAU1Byte3, spinnerDAU1Byte2, spinnerDAU1Byte1);
			}
		});
		spinnerDAU1Byte2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				dau1_ip = BuildIP(spinnerDAU1Byte4, spinnerDAU1Byte3, spinnerDAU1Byte2, spinnerDAU1Byte1);
			}
		});		
		spinnerDAU1Byte3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				dau1_ip = BuildIP(spinnerDAU1Byte4, spinnerDAU1Byte3, spinnerDAU1Byte2, spinnerDAU1Byte1);
			}
		});
		spinnerDAU1Byte4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				dau1_ip = BuildIP(spinnerDAU1Byte4, spinnerDAU1Byte3, spinnerDAU1Byte2, spinnerDAU1Byte1);
			}
		});
		
		JList listDAU1 = new JList(listbox_values.toArray());
		
		//================================================================================
		// Double click handler for listbox - DAU1
		//================================================================================
		listDAU1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() == 2 && !arg0.isConsumed())
				{
					String link = listDAU1.getSelectedValue().toString();
					try
					{
						dau1_ip = BuildIP(spinnerDAU1Byte4, spinnerDAU1Byte3, spinnerDAU1Byte2, spinnerDAU1Byte1);
						String command = "";
						
						command = BuildCommand(link, dau1_ip, dau1_auto_login);
						SetStatusBarText(statusbar_messages.get("connecting") + dau1_ip, lblStatusBar);
						Runtime.getRuntime().exec("cmd /c start " + command);
//						JOptionPane.showMessageDialog(frame, command, "URL", JOptionPane.INFORMATION_MESSAGE);
						SetStatusBarText(statusbar_messages.get("idle"), lblStatusBar);
					}
					catch (Exception e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		});
		
		GridBagConstraints gbc_listDAU1 = new GridBagConstraints();
		gbc_listDAU1.fill = GridBagConstraints.BOTH;
		gbc_listDAU1.anchor = GridBagConstraints.EAST;
		gbc_listDAU1.gridheight = 4;
		gbc_listDAU1.gridwidth = 3;
		gbc_listDAU1.insets = new Insets(0, 0, 0, 5);
		gbc_listDAU1.gridx = 1;
		gbc_listDAU1.gridy = 2;
		panel_DAU1.add(listDAU1, gbc_listDAU1);
		
		JCheckBox chckbxAutoLoginDAU1 = new JCheckBox("Auto Login");
		chckbxAutoLoginDAU1.setToolTipText(auto_login_tooltip_text);
		//================================================================================
		// Checkbox handler when checkbox value is changed - DAU1
		//================================================================================
		chckbxAutoLoginDAU1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dau1_auto_login = chckbxAutoLoginDAU1.isSelected();
			}
		});
		
		GridBagConstraints gbc_chckbxAutoLoginDAU1 = new GridBagConstraints();
		gbc_chckbxAutoLoginDAU1.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxAutoLoginDAU1.gridwidth = 2;
		gbc_chckbxAutoLoginDAU1.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxAutoLoginDAU1.gridx = 4;
		gbc_chckbxAutoLoginDAU1.gridy = 2;
		panel_DAU1.add(chckbxAutoLoginDAU1, gbc_chckbxAutoLoginDAU1);
		GridBagConstraints gbc_panel_DAU1 = new GridBagConstraints();
		gbc_panel_DAU1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_DAU1.gridx = 0;
		gbc_panel_DAU1.gridy = 3;
		frame.getContentPane().add(panel_DAU1, gbc_panel_DAU1);
		
		JToolBar toolBarDAU1 = new JToolBar();
		toolBarDAU1.setFloatable(false);
		toolBarDAU1.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_toolBarDAU1 = new GridBagConstraints();
		gbc_toolBarDAU1.gridheight = 3;
		gbc_toolBarDAU1.anchor = GridBagConstraints.NORTH;
		gbc_toolBarDAU1.insets = new Insets(0, 0, 0, 5);
		gbc_toolBarDAU1.gridx = 4;
		gbc_toolBarDAU1.gridy = 3;
		panel_DAU1.add(toolBarDAU1, gbc_toolBarDAU1);
		
		JButton btnPuttyDAU1 = new JButton("");
		
		//================================================================================
		// SSH Button handler - DAU1
		//================================================================================
		btnPuttyDAU1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dau1_ip = BuildIP(spinnerDAU1Byte4, spinnerDAU1Byte3, spinnerDAU1Byte2, spinnerDAU1Byte1);
				SetStatusBarText(statusbar_messages.get("connecting") + dau1_ip + " via SSH", lblStatusBar);
				Cashel.openPutty(dau1_ip);
				SetStatusBarText(statusbar_messages.get("idle"), lblStatusBar);
			}
		});
		btnPuttyDAU1.setToolTipText("Open Putty");
		btnPuttyDAU1.setIcon(new ImageIcon(Main_Window.class.getResource("/images/ssh-icon-24x24.png")));
		toolBarDAU1.add(btnPuttyDAU1);
		
		JButton btnSetTodDAU1 = new JButton("");
		
		//================================================================================
		// Set TOD Button handler - DAU1
		//================================================================================
		btnSetTodDAU1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dau1_ip = BuildIP(spinnerDAU1Byte4, spinnerDAU1Byte3, spinnerDAU1Byte2, spinnerDAU1Byte1);
				if (thread_running)
				{
					JOptionPane.showMessageDialog(frame, "Set TOD command in progress", "Set TOD", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					int dialog_response = JOptionPane.showConfirmDialog(frame, set_tod_dialog_message);
					if (dialog_response == JOptionPane.OK_OPTION)
					{
						SetStatusBarText(statusbar_messages.get("tod") + dau1_ip, lblStatusBar);
						Thread t = new Thread(new SetTODThread(dau1_ip));
						t.start();
						JOptionPane.showMessageDialog(frame, "Set TOD command sent", "Set TOD", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				SetStatusBarText(statusbar_messages.get("idle"), lblStatusBar);
			}
		});
		btnSetTodDAU1.setToolTipText("Set TOD");
		btnSetTodDAU1.setIcon(new ImageIcon(Main_Window.class.getResource("/images/clock-icon-24x24.png")));
		toolBarDAU1.add(btnSetTodDAU1);
		
		JButton btnXmlDAU1 = new JButton("");
		btnXmlDAU1.setToolTipText("Activate XML Alarm File");
		btnXmlDAU1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JOptionPane.showMessageDialog(frame,  "Activate XML command sent to " + dau1_ip, "Activate XML", JOptionPane.INFORMATION_MESSAGE);
				Cashel.activateXML(dau1_ip);
			}
		});
		btnXmlDAU1.setIcon(new ImageIcon(Main_Window.class.getResource("/images/xml-icon-24x24.png")));
		toolBarDAU1.add(btnXmlDAU1);
		
		JButton btnFlDAU1 = new JButton("FL");
		btnFlDAU1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JOptionPane.showMessageDialog(frame, "Setup FL command sent to " + dau1_ip, "Setup FL", JOptionPane.INFORMATION_MESSAGE);
				Cashel.setupFL(dau1_ip);
			}
		});
		toolBarDAU1.add(btnFlDAU1);
		
		//================================================================================
		// DAU2
		//================================================================================
		JPanel panel_DAU2 = new JPanel();
		panel_DAU2.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagLayout gbl_panel_DAU2 = new GridBagLayout();
//		gbl_panel_DAU2.columnWidths = new int[] {30, 37, 30, 30, 34, 20};
//		gbl_panel_DAU2.rowHeights = new int[] {30, 30, 26, 0, 30, 30, 30};
		gbl_panel_DAU2.columnWidths = columnWidths;
		gbl_panel_DAU2.rowHeights = rowHeights;
		gbl_panel_DAU2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_panel_DAU2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		panel_DAU2.setLayout(gbl_panel_DAU2);
		
		JSpinner spinnerDAU2Byte4 = new JSpinner();
		spinnerDAU2Byte4.setModel(new SpinnerNumberModel(192, 1, 255, 1));
		GridBagConstraints gbc_spinnerDAU2Byte4 = new GridBagConstraints();
		gbc_spinnerDAU2Byte4.anchor = GridBagConstraints.WEST;
		gbc_spinnerDAU2Byte4.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU2Byte4.gridx = 1;
		gbc_spinnerDAU2Byte4.gridy = 1;
		panel_DAU2.add(spinnerDAU2Byte4, gbc_spinnerDAU2Byte4);
		
		JSpinner spinnerDAU2Byte3 = new JSpinner();
		spinnerDAU2Byte3.setModel(new SpinnerNumberModel(168, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU2Byte3 = new GridBagConstraints();
		gbc_spinnerDAU2Byte3.anchor = GridBagConstraints.WEST;
		gbc_spinnerDAU2Byte3.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU2Byte3.gridx = 2;
		gbc_spinnerDAU2Byte3.gridy = 1;
		panel_DAU2.add(spinnerDAU2Byte3, gbc_spinnerDAU2Byte3);
		
		JSpinner spinnerDAU2Byte2 = new JSpinner();
		spinnerDAU2Byte2.setModel(new SpinnerNumberModel(1, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU2Byte2 = new GridBagConstraints();
		gbc_spinnerDAU2Byte2.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU2Byte2.gridx = 3;
		gbc_spinnerDAU2Byte2.gridy = 1;
		panel_DAU2.add(spinnerDAU2Byte2, gbc_spinnerDAU2Byte2);
		
		JSpinner spinnerDAU2Byte1 = new JSpinner();
		spinnerDAU2Byte1.setModel(new SpinnerNumberModel(12, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU2Byte1 = new GridBagConstraints();
		gbc_spinnerDAU2Byte1.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU2Byte1.gridx = 4;
		gbc_spinnerDAU2Byte1.gridy = 1;
		panel_DAU2.add(spinnerDAU2Byte1, gbc_spinnerDAU2Byte1);
		
		//================================================================================
		// Spinner Events - DAU2
		//================================================================================
		spinnerDAU2Byte1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				dau2_ip = BuildIP(spinnerDAU2Byte4, spinnerDAU2Byte3, spinnerDAU2Byte2, spinnerDAU2Byte1);
			}
		});
		spinnerDAU2Byte2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				dau2_ip = BuildIP(spinnerDAU2Byte4, spinnerDAU2Byte3, spinnerDAU2Byte2, spinnerDAU2Byte1);
			}
		});		
		spinnerDAU2Byte3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				dau2_ip = BuildIP(spinnerDAU2Byte4, spinnerDAU2Byte3, spinnerDAU2Byte2, spinnerDAU2Byte1);
			}
		});
		spinnerDAU2Byte4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				dau2_ip = BuildIP(spinnerDAU2Byte4, spinnerDAU2Byte3, spinnerDAU2Byte2, spinnerDAU2Byte1);
			}
		});
		
		JList listDAU2 = new JList(listbox_values.toArray());
		
		//================================================================================
		// Double click handler for listbox - DAU2
		//================================================================================
		listDAU2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					String link = listDAU2.getSelectedValue().toString();
					try
					{
						dau2_ip = BuildIP(spinnerDAU2Byte4, spinnerDAU2Byte3, spinnerDAU2Byte2, spinnerDAU2Byte1);
						String command = "";
						
						command = BuildCommand(link, dau2_ip, dau2_auto_login);
						SetStatusBarText(statusbar_messages.get("connecting") + dau2_ip, lblStatusBar);
						Runtime.getRuntime().exec("cmd /c start " + command);
						SetStatusBarText(statusbar_messages.get("idle"), lblStatusBar);
					}
					catch (Exception ex)
					{
						System.out.println(ex.getMessage());
					}
				}
			}
		});
		GridBagConstraints gbc_listDAU2 = new GridBagConstraints();
		gbc_listDAU2.fill = GridBagConstraints.BOTH;
		gbc_listDAU2.anchor = GridBagConstraints.EAST;
		gbc_listDAU2.gridheight = 4;
		gbc_listDAU2.gridwidth = 3;
		gbc_listDAU2.insets = new Insets(0, 0, 5, 5);
		gbc_listDAU2.gridx = 1;
		gbc_listDAU2.gridy = 2;
		panel_DAU2.add(listDAU2, gbc_listDAU2);
		
		JCheckBox chckbxAutoLoginDAU2 = new JCheckBox("Auto Login");
		chckbxAutoLoginDAU2.setToolTipText(auto_login_tooltip_text);
		//================================================================================
		// Checkbox handler when checkbox value is changed - DAU2
		//================================================================================
		chckbxAutoLoginDAU2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dau2_auto_login = chckbxAutoLoginDAU2.isSelected();
			}
		});
		GridBagConstraints gbc_chckbxAutoLoginDAU2 = new GridBagConstraints();
		gbc_chckbxAutoLoginDAU2.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxAutoLoginDAU2.gridwidth = 2;
		gbc_chckbxAutoLoginDAU2.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxAutoLoginDAU2.gridx = 4;
		gbc_chckbxAutoLoginDAU2.gridy = 2;
		panel_DAU2.add(chckbxAutoLoginDAU2, gbc_chckbxAutoLoginDAU2);
		GridBagConstraints gbc_panel_DAU2 = new GridBagConstraints();
		gbc_panel_DAU2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_DAU2.gridx = 1;
		gbc_panel_DAU2.gridy = 3;
		frame.getContentPane().add(panel_DAU2, gbc_panel_DAU2);
		
		JToolBar toolBarDAU2 = new JToolBar();
		toolBarDAU2.setFloatable(false);
		toolBarDAU2.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.gridheight = 3;
		gbc_toolBar.anchor = GridBagConstraints.NORTH;
		gbc_toolBar.insets = new Insets(0, 0, 5, 5);
		gbc_toolBar.gridx = 4;
		gbc_toolBar.gridy = 3;
		panel_DAU2.add(toolBarDAU2, gbc_toolBar);
		
		JButton btnPuttyDAU2 = new JButton("");
		//================================================================================
		// SSH Button handler - DAU2
		//================================================================================
		btnPuttyDAU2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				SetStatusBarText(statusbar_messages.get("connecting") + dau2_ip + " via SSH", lblStatusBar);
				dau2_ip = BuildIP(spinnerDAU2Byte4, spinnerDAU2Byte3, spinnerDAU2Byte2, spinnerDAU2Byte1);
				Cashel.openPutty(dau2_ip);
				SetStatusBarText(statusbar_messages.get("idle"), lblStatusBar);
			}
		});
		btnPuttyDAU2.setToolTipText("Open Putty");
		btnPuttyDAU2.setIcon(new ImageIcon(Main_Window.class.getResource("/images/ssh-icon-24x24.png")));
		toolBarDAU2.add(btnPuttyDAU2);
		
		JButton btnSetTodDAU2 = new JButton("");
		
		//================================================================================
		// Set TOD Button handler - DAU2
		//================================================================================
		btnSetTodDAU2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dau2_ip = BuildIP(spinnerDAU2Byte4, spinnerDAU2Byte3, spinnerDAU2Byte2, spinnerDAU2Byte1);
				SetStatusBarText(statusbar_messages.get("tod") + dau2_ip, lblStatusBar);
				if (thread_running)
				{
					JOptionPane.showMessageDialog(frame, "Set TOD command in progress", "Set TOD", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					int dialog_response = JOptionPane.showConfirmDialog(frame, set_tod_dialog_message);
					if (dialog_response == JOptionPane.OK_OPTION)
					{
						SetStatusBarText(statusbar_messages.get("tod") + dau2_ip, lblStatusBar);
						Thread t = new Thread(new SetTODThread(dau2_ip));
						t.start();
						JOptionPane.showMessageDialog(frame, "Set TOD command sent", "Set TOD", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		btnPuttyDAU2.setToolTipText("Open Putty");
		btnPuttyDAU2.setIcon(new ImageIcon(Main_Window.class.getResource("/images/ssh-icon-24x24.png")));
		toolBarDAU2.add(btnPuttyDAU2);
		
		btnSetTodDAU2.setToolTipText("Set TOD");
		btnSetTodDAU2.setIcon(new ImageIcon(Main_Window.class.getResource("/images/clock-icon-24x24.png")));
		toolBarDAU2.add(btnSetTodDAU2);
		
		JButton btnXmlDAU2 = new JButton("");
		btnXmlDAU2.setToolTipText("Activate XML Alarm File");
		btnXmlDAU2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dau2_ip = BuildIP(spinnerDAU2Byte4, spinnerDAU2Byte3, spinnerDAU2Byte2, spinnerDAU2Byte1);
				JOptionPane.showMessageDialog(frame,  "Activate XML command sent to " + dau2_ip, "Activate XML", JOptionPane.INFORMATION_MESSAGE);
				Cashel.activateXML(dau2_ip);
			}
		});
		btnXmlDAU2.setIcon(new ImageIcon(Main_Window.class.getResource("/images/xml-icon-24x24.png")));
		toolBarDAU2.add(btnXmlDAU2);
		
		JButton btnFlDAU2 = new JButton("FL");
		btnFlDAU2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JOptionPane.showMessageDialog(frame, "Setup FL command sent to " + dau2_ip, "Setup FL", JOptionPane.INFORMATION_MESSAGE);
				Cashel.setupFL(dau2_ip);
			}
		});
		toolBarDAU2.add(btnFlDAU2);
		
		//================================================================================
		// DAU3
		//================================================================================
//		JPanel panel_DAU3 = new JPanel();
		panel_DAU3 = new JPanel();
		panel_DAU3.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagLayout gbl_panel_DAU3 = new GridBagLayout();
		gbl_panel_DAU3.columnWidths = new int[] {30, 37, 30, 30, 34, 20};
		gbl_panel_DAU3.rowHeights = new int[] {30, 30, 26, 30, 30, 30, 30};
		gbl_panel_DAU3.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_panel_DAU3.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		panel_DAU3.setLayout(gbl_panel_DAU3);
		
		JSpinner spinnerDAU3Byte4 = new JSpinner();
		spinnerDAU3Byte4.setModel(new SpinnerNumberModel(192, 1, 255, 1));
		GridBagConstraints gbc_spinnerDAU3Byte4 = new GridBagConstraints();
		gbc_spinnerDAU3Byte4.anchor = GridBagConstraints.WEST;
		gbc_spinnerDAU3Byte4.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU3Byte4.gridx = 1;
		gbc_spinnerDAU3Byte4.gridy = 1;
		panel_DAU3.add(spinnerDAU3Byte4, gbc_spinnerDAU3Byte4);
		
		JSpinner spinnerDAU3Byte3 = new JSpinner();
		spinnerDAU3Byte3.setModel(new SpinnerNumberModel(168, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU3Byte3 = new GridBagConstraints();
		gbc_spinnerDAU3Byte3.anchor = GridBagConstraints.WEST;
		gbc_spinnerDAU3Byte3.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU3Byte3.gridx = 2;
		gbc_spinnerDAU3Byte3.gridy = 1;
		panel_DAU3.add(spinnerDAU3Byte3, gbc_spinnerDAU3Byte3);
		
		JSpinner spinnerDAU3Byte2 = new JSpinner();
		spinnerDAU3Byte2.setModel(new SpinnerNumberModel(1, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU3Byte2 = new GridBagConstraints();
		gbc_spinnerDAU3Byte2.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU3Byte2.gridx = 3;
		gbc_spinnerDAU3Byte2.gridy = 1;
		panel_DAU3.add(spinnerDAU3Byte2, gbc_spinnerDAU3Byte2);
		
		JSpinner spinnerDAU3Byte1 = new JSpinner();
		spinnerDAU3Byte1.setModel(new SpinnerNumberModel(13, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU3Byte1 = new GridBagConstraints();
		gbc_spinnerDAU3Byte1.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerDAU3Byte1.gridx = 4;
		gbc_spinnerDAU3Byte1.gridy = 1;
		panel_DAU3.add(spinnerDAU3Byte1, gbc_spinnerDAU3Byte1);
		
		JList listDAU3 = new JList(listbox_values.toArray());
		GridBagConstraints gbc_listDAU3 = new GridBagConstraints();
		gbc_listDAU3.anchor = GridBagConstraints.EAST;
		gbc_listDAU3.gridheight = 4;
		gbc_listDAU3.gridwidth = 3;
		gbc_listDAU3.insets = new Insets(0, 0, 5, 5);
		gbc_listDAU3.fill = GridBagConstraints.BOTH;
		gbc_listDAU3.gridx = 1;
		gbc_listDAU3.gridy = 2;
		panel_DAU3.add(listDAU3, gbc_listDAU3);
		
		//================================================================================
		// Double click handler for listbox - DAU3
		//================================================================================
		listDAU3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					String link = listDAU3.getSelectedValue().toString();
					try
					{
						dau3_ip = BuildIP(spinnerDAU3Byte4, spinnerDAU3Byte3, spinnerDAU3Byte2, spinnerDAU3Byte1);
						String command = "";
						
						command = BuildCommand(link, dau3_ip, dau3_auto_login);
						SetStatusBarText(statusbar_messages.get("connecting") + dau3_ip, lblStatusBar);
						Runtime.getRuntime().exec("cmd /c start " + command);
						SetStatusBarText(statusbar_messages.get("idle"), lblStatusBar);
					}
					catch (Exception ex)
					{
						System.out.println(ex.getMessage());
					}
				}
			}
		});
		
		JCheckBox chckbxAutoLoginDAU3 = new JCheckBox("Auto Login");
		chckbxAutoLoginDAU3.setToolTipText(auto_login_tooltip_text);
		//================================================================================
		// Checkbox handler when checkbox value is changed - DAU3
		//================================================================================
		chckbxAutoLoginDAU3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dau3_auto_login = chckbxAutoLoginDAU3.isSelected();
			}
		});
		GridBagConstraints gbc_chckbxAutoLoginDAU3 = new GridBagConstraints();
		gbc_chckbxAutoLoginDAU3.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxAutoLoginDAU3.gridwidth = 2;
		gbc_chckbxAutoLoginDAU3.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxAutoLoginDAU3.gridx = 4;
		gbc_chckbxAutoLoginDAU3.gridy = 2;
		panel_DAU3.add(chckbxAutoLoginDAU3, gbc_chckbxAutoLoginDAU3);
		
		JToolBar toolBarDAU3 = new JToolBar();
		toolBarDAU3.setFloatable(false);
		toolBarDAU3.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_toolBarDAU3 = new GridBagConstraints();
		gbc_toolBarDAU3.gridheight = 3;
		gbc_toolBarDAU3.anchor = GridBagConstraints.NORTH;
		gbc_toolBarDAU3.insets = new Insets(0, 0, 5, 5);
		gbc_toolBarDAU3.gridx = 4;
		gbc_toolBarDAU3.gridy = 3;
		panel_DAU3.add(toolBarDAU3, gbc_toolBarDAU3);
		
		JButton btnPuttyDAU3 = new JButton("");
		//================================================================================
		// SSH Button handler - DAU3
		//================================================================================
		btnPuttyDAU3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				SetStatusBarText(statusbar_messages.get("connecting") + dau3_ip + " via SSH", lblStatusBar);
				dau3_ip = BuildIP(spinnerDAU3Byte4, spinnerDAU3Byte3, spinnerDAU3Byte2, spinnerDAU3Byte1);
				Cashel.openPutty(dau3_ip);
				SetStatusBarText(statusbar_messages.get("idle"), lblStatusBar);
			}
		});
		btnPuttyDAU3.setToolTipText("Open Putty");
		btnPuttyDAU3.setIcon(new ImageIcon(Main_Window.class.getResource("/images/ssh-icon-24x24.png")));
		toolBarDAU3.add(btnPuttyDAU3);
		
		JButton btnSetTodDAU3 = new JButton("");
		
		//================================================================================
		// Set TOD Button handler - DAU3
		//================================================================================
		btnSetTodDAU3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dau3_ip = BuildIP(spinnerDAU3Byte4, spinnerDAU3Byte3, spinnerDAU3Byte2, spinnerDAU3Byte1);
				if (thread_running)
				{
					JOptionPane.showMessageDialog(frame, "Set TOD command in progress", "Set TOD", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					int dialog_response = JOptionPane.showConfirmDialog(frame, set_tod_dialog_message);
					if (dialog_response == JOptionPane.OK_OPTION)
					{
						SetStatusBarText(statusbar_messages.get("tod") + dau3_ip, lblStatusBar);
						Thread t = new Thread(new SetTODThread(dau3_ip));
						t.start();
						JOptionPane.showMessageDialog(frame, "Set TOD command sent", "Set TOD", JOptionPane.INFORMATION_MESSAGE);						
					}
				}
			}
		});
		btnPuttyDAU3.setToolTipText("Open Putty");
		btnPuttyDAU3.setIcon(new ImageIcon(Main_Window.class.getResource("/images/ssh-icon-24x24.png")));
		toolBarDAU3.add(btnPuttyDAU3);
		
		btnSetTodDAU3.setToolTipText("Set TOD");
		btnSetTodDAU3.setIcon(new ImageIcon(Main_Window.class.getResource("/images/clock-icon-24x24.png")));
		toolBarDAU3.add(btnSetTodDAU3);
		
		JButton btnXmlDAU3 = new JButton("");
		btnXmlDAU3.setToolTipText("Activate XML Alarm File");
		btnXmlDAU3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dau3_ip = BuildIP(spinnerDAU3Byte4, spinnerDAU3Byte3, spinnerDAU3Byte2, spinnerDAU3Byte1);
				JOptionPane.showMessageDialog(frame,  "Activate XML command sent to " + dau3_ip, "Activate XML", JOptionPane.INFORMATION_MESSAGE);
				Cashel.activateXML(dau3_ip);
			}
		});
		btnXmlDAU3.setIcon(new ImageIcon(Main_Window.class.getResource("/images/xml-icon-24x24.png")));
		toolBarDAU3.add(btnXmlDAU3);
		
		GridBagConstraints gbc_panel_DAU3 = new GridBagConstraints();
		gbc_panel_DAU3.insets = new Insets(0, 0, 5, 5);
		gbc_panel_DAU3.gridx = 0;
		gbc_panel_DAU3.gridy = 4;
		frame.getContentPane().add(panel_DAU3, gbc_panel_DAU3);
		
		//================================================================================
		// DAU4
		//================================================================================
		panel_DAU4 = new JPanel();
		panel_DAU4.setBorder(new LineBorder(new Color(0, 0, 0)));
		GridBagLayout gbl_panel_DAU4 = new GridBagLayout();
		gbl_panel_DAU4.columnWidths = new int[] {30, 37, 30, 30, 34, 20};
		gbl_panel_DAU4.rowHeights = new int[] {30, 30, 26, 30, 30, 30, 30};
		gbl_panel_DAU4.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_panel_DAU4.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		panel_DAU4.setLayout(gbl_panel_DAU4);
		
		JSpinner spinnerDAU4Byte4 = new JSpinner();
		spinnerDAU4Byte4.setModel(new SpinnerNumberModel(192, 1, 255, 1));
		GridBagConstraints gbc_spinnerDAU4Byte4 = new GridBagConstraints();
		gbc_spinnerDAU4Byte4.anchor = GridBagConstraints.WEST;
		gbc_spinnerDAU4Byte4.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU4Byte4.gridx = 1;
		gbc_spinnerDAU4Byte4.gridy = 1;
		panel_DAU4.add(spinnerDAU4Byte4, gbc_spinnerDAU4Byte4);
		
		JSpinner spinnerDAU4Byte3 = new JSpinner();
		spinnerDAU4Byte3.setModel(new SpinnerNumberModel(168, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU4Byte3 = new GridBagConstraints();
		gbc_spinnerDAU4Byte3.anchor = GridBagConstraints.WEST;
		gbc_spinnerDAU4Byte3.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU4Byte3.gridx = 2;
		gbc_spinnerDAU4Byte3.gridy = 1;
		panel_DAU4.add(spinnerDAU4Byte3, gbc_spinnerDAU4Byte3);
		
		JSpinner spinnerDAU4Byte2 = new JSpinner();
		spinnerDAU4Byte2.setModel(new SpinnerNumberModel(1, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU4Byte2 = new GridBagConstraints();
		gbc_spinnerDAU4Byte2.insets = new Insets(0, 0, 5, 5);
		gbc_spinnerDAU4Byte2.gridx = 3;
		gbc_spinnerDAU4Byte2.gridy = 1;
		panel_DAU4.add(spinnerDAU4Byte2, gbc_spinnerDAU4Byte2);
		
		JSpinner spinnerDAU4Byte1 = new JSpinner();
		spinnerDAU4Byte1.setModel(new SpinnerNumberModel(14, 0, 255, 1));
		GridBagConstraints gbc_spinnerDAU4Byte1 = new GridBagConstraints();
		gbc_spinnerDAU4Byte1.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerDAU4Byte1.gridx = 4;
		gbc_spinnerDAU4Byte1.gridy = 1;
		panel_DAU4.add(spinnerDAU4Byte1, gbc_spinnerDAU4Byte1);
		
		JList listDAU4 = new JList(listbox_values.toArray());
		GridBagConstraints gbc_listDAU4 = new GridBagConstraints();
		gbc_listDAU4.anchor = GridBagConstraints.EAST;
		gbc_listDAU4.gridheight = 4;
		gbc_listDAU4.gridwidth = 3;
		gbc_listDAU4.insets = new Insets(0, 0, 5, 5);
		gbc_listDAU4.fill = GridBagConstraints.BOTH;
		gbc_listDAU4.gridx = 1;
		gbc_listDAU4.gridy = 2;
		panel_DAU4.add(listDAU4, gbc_listDAU4);
		
		//================================================================================
		// Double click handler for listbox - DAU4
		//================================================================================
		listDAU4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed())
				{
					String link = listDAU4.getSelectedValue().toString();
					try
					{
						dau4_ip = BuildIP(spinnerDAU4Byte4, spinnerDAU4Byte3, spinnerDAU4Byte2, spinnerDAU4Byte1);
						String command = "";
						
						command = BuildCommand(link, dau4_ip, dau4_auto_login);
						SetStatusBarText(statusbar_messages.get("connecting") + dau4_ip, lblStatusBar);
						Runtime.getRuntime().exec("cmd /c start " + command);
						SetStatusBarText(statusbar_messages.get("idle"), lblStatusBar);
					}
					catch (Exception ex)
					{
						System.out.println(ex.getMessage());
					}
				}
			}
		});
		
		JCheckBox chckbxAutoLoginDAU4 = new JCheckBox("Auto Login");
		chckbxAutoLoginDAU4.setToolTipText(auto_login_tooltip_text);
		//================================================================================
		// Checkbox handler when checkbox value is changed - DAU4
		//================================================================================
		chckbxAutoLoginDAU4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dau4_auto_login = chckbxAutoLoginDAU4.isSelected();
			}
		});
		GridBagConstraints gbc_chckbxAutoLoginDAU4 = new GridBagConstraints();
		gbc_chckbxAutoLoginDAU4.anchor = GridBagConstraints.NORTHWEST;
		gbc_chckbxAutoLoginDAU4.gridwidth = 2;
		gbc_chckbxAutoLoginDAU4.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxAutoLoginDAU4.gridx = 4;
		gbc_chckbxAutoLoginDAU4.gridy = 2;
		panel_DAU4.add(chckbxAutoLoginDAU4, gbc_chckbxAutoLoginDAU4);
		
		JToolBar toolBarDAU4 = new JToolBar();
		toolBarDAU4.setFloatable(false);
		toolBarDAU4.setOrientation(SwingConstants.VERTICAL);
		GridBagConstraints gbc_toolBarDAU4 = new GridBagConstraints();
		gbc_toolBarDAU4.gridheight = 3;
		gbc_toolBarDAU4.anchor = GridBagConstraints.NORTH;
		gbc_toolBarDAU4.insets = new Insets(0, 0, 5, 5);
		gbc_toolBarDAU4.gridx = 4;
		gbc_toolBarDAU4.gridy = 3;
		panel_DAU4.add(toolBarDAU4, gbc_toolBarDAU4);
		
		JButton btnPuttyDAU4 = new JButton("");
		//================================================================================
		// SSH Button handler - DAU4
		//================================================================================
		btnPuttyDAU4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				SetStatusBarText(statusbar_messages.get("connecting") + dau4_ip + " via SSH", lblStatusBar);
				dau4_ip = BuildIP(spinnerDAU4Byte4, spinnerDAU4Byte3, spinnerDAU4Byte2, spinnerDAU4Byte1);
				Cashel.openPutty(dau4_ip);
				SetStatusBarText(statusbar_messages.get("idle"), lblStatusBar);
			}
		});
		btnPuttyDAU4.setToolTipText("Open Putty");
		btnPuttyDAU4.setIcon(new ImageIcon(Main_Window.class.getResource("/images/ssh-icon-24x24.png")));
		toolBarDAU4.add(btnPuttyDAU4);
		
		JButton btnSetTodDAU4 = new JButton("");
		
		//================================================================================
		// Set TOD Button handler - DAU4
		//================================================================================
		btnSetTodDAU4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dau4_ip = BuildIP(spinnerDAU4Byte4, spinnerDAU4Byte3, spinnerDAU4Byte2, spinnerDAU4Byte1);
				if (thread_running)
				{
					JOptionPane.showMessageDialog(frame, "Set TOD command in progress", "Set TOD", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					int dialog_response = JOptionPane.showConfirmDialog(frame, set_tod_dialog_message);
					if (dialog_response == JOptionPane.OK_OPTION)
					{
						SetStatusBarText(statusbar_messages.get("tod") + dau4_ip, lblStatusBar);
						Thread t = new Thread(new SetTODThread(dau4_ip));
						t.start();
						JOptionPane.showMessageDialog(frame, "Set TOD command sent", "Set TOD", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		btnPuttyDAU4.setToolTipText("Open Putty");
		btnPuttyDAU4.setIcon(new ImageIcon(Main_Window.class.getResource("/images/ssh-icon-24x24.png")));
		toolBarDAU4.add(btnPuttyDAU4);
		
		btnSetTodDAU4.setToolTipText("Set TOD");
		btnSetTodDAU4.setIcon(new ImageIcon(Main_Window.class.getResource("/images/clock-icon-24x24.png")));
		toolBarDAU4.add(btnSetTodDAU4);
		
		JButton btnXmlDAU4 = new JButton("");
		btnXmlDAU4.setToolTipText("Activate XML Alarm File");
		btnXmlDAU4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dau4_ip = BuildIP(spinnerDAU4Byte4, spinnerDAU4Byte3, spinnerDAU4Byte2, spinnerDAU4Byte1);
				JOptionPane.showMessageDialog(frame,  "Activate XML command sent to " + dau4_ip, "Activate XML", JOptionPane.INFORMATION_MESSAGE);
				Cashel.activateXML(dau4_ip);
			}
		});
		btnXmlDAU4.setIcon(new ImageIcon(Main_Window.class.getResource("/images/xml-icon-24x24.png")));
		toolBarDAU4.add(btnXmlDAU4);
		
		GridBagConstraints gbc_panel_DAU4 = new GridBagConstraints();
		gbc_panel_DAU4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_DAU4.gridx = 1;
		gbc_panel_DAU4.gridy = 4;
		frame.getContentPane().add(panel_DAU4, gbc_panel_DAU4);
		
		//Create a timer
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent evt)
			{
				textFieldEpochTime.setText(GetEpochTime());
				if (!thread_running)
				{
					SetStatusBarText("Idle", lblStatusBar);
				}
			}
		};
		
		timer = new Timer(1000, al);
		timer.start();
	}
	
	private void SetStatusBarText(String text, JLabel label)
	{
		label.setText(text);
		label.repaint();
	}
	
	static class ExitApplication implements ActionListener
	{
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
	}
	
	static class SetDAUVisibility implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JMenuItem source = (JMenuItem)(e.getSource());
//			System.out.println(num_dau_to_display);
//			System.out.println(source.getText());
			if (source.getText() == "2 DAU")
			{ num_dau_to_display = 2; }
			else
			{ num_dau_to_display = 4; }
			
			switch(num_dau_to_display) {
			case 2:
				panel_DAU3.setVisible(false);
				panel_DAU4.setVisible(false);
				break;
			case 4:
				panel_DAU3.setVisible(true);
				panel_DAU4.setVisible(true);
				break;
			}
		}
	}
	
	private String BuildIP(JSpinner byte4, JSpinner byte3, JSpinner byte2, JSpinner byte1)
	{
		String ip = byte4.getValue().toString() + "." 
				+ byte3.getValue().toString() + "."
				+ byte2.getValue().toString() + "."
				+ byte1.getValue().toString();
		return ip;
	}
	
	private String BuildCommand(String link, String ip, boolean auto_login)
	{	
		String credentials = "";
		String command = "";
		
		if ((link == "Mfgindex") || (link == "Tabindex") || (link == "Delete Logs"))
		{
			credentials = username + ":" + password + "@";
		}
		else if (link == "Firmware Package Upgrade")
		{
			credentials = username + ":" + package_password + "@";
		}
		else if (link == "FL8 Home")
		{
			credentials = fl_username + ":" + fl_password + "@";
		}
		
		link = links_dict.get(link);
		if (auto_login == true)
		{
			command = "http://" + credentials + ip + "/" + link;
		}
		else { command = "http://" + ip + "/" + link; }
//		System.out.println(command);
		return command;
	}
	
	private String GetEpochTime()
	{
		Instant i = Instant.now();
		
		return String.format("%d", i.toEpochMilli() / 1000);
	}
}

/**

 * An interface that can be used by the NotificationThread class to notify an

 * object that a thread has completed. 

 * @author Greg Cope

 */

interface TaskListener {

	/**

	 * Notifies this object that the Runnable object has completed its work. 

	 * @param runner The runnable interface whose work has finished.

	 */

	public void threadComplete( Runnable runner );

}

class SetTODThread implements Runnable {
	String ip;
	
	/**

	 * Our list of listeners to be notified upon thread completion.

	 */

	private java.util.List<TaskListener> listeners = Collections.synchronizedList( new ArrayList<TaskListener>() );
	
	SetTODThread(String ip_address) { ip = ip_address; }
	
	/**

	 * Adds a listener to this object. 

	 * @param listener Adds a new listener to this object. 

	 */

	public void addListener( TaskListener listener ){

		listeners.add(listener);

	}

	/**

	 * Removes a particular listener from this object, or does nothing if the listener

	 * is not registered. 

	 * @param listener The listener to remove. 

	 */

	public void removeListener( TaskListener listener ){

		listeners.remove(listener);

	}

	/**

	 * Notifies all listeners that the thread has completed.

	 */

	private final void notifyListeners() {

		synchronized ( listeners ){

			for (TaskListener listener : listeners) {

			  listener.threadComplete(this);

			}

		}

	}
	
	public void run() {
		Main_Window.thread_running = true;
		Cashel.setTOD(ip);
		notifyListeners();
		Main_Window.thread_running = false;
	}
}