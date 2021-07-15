package cashel_comms_manager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Set_IP_Dialog extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldDeviceIP;
	public JTextField textFieldRearIP;
	private JTextField textFieldFrontIP;
	private String device_ip = "";
	private String front_ip = "";
	private String rear_ip = "";
	private boolean change_front_ip = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Set_IP_Dialog dialog = new Set_IP_Dialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Set_IP_Dialog() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {50, 150, 100, 50};
		gbl_contentPanel.rowHeights = new int[] {20, 0, 0, 20, 20, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblDeviceIp = new JLabel("Device IP Address:");
			GridBagConstraints gbc_lblDeviceIp = new GridBagConstraints();
			gbc_lblDeviceIp.fill = GridBagConstraints.BOTH;
			gbc_lblDeviceIp.insets = new Insets(0, 0, 5, 5);
			gbc_lblDeviceIp.gridx = 1;
			gbc_lblDeviceIp.gridy = 0;
			contentPanel.add(lblDeviceIp, gbc_lblDeviceIp);
		}
		{
			textFieldDeviceIP = new JTextField();
			GridBagConstraints gbc_textFieldDeviceIP = new GridBagConstraints();
			gbc_textFieldDeviceIP.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldDeviceIP.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldDeviceIP.gridx = 2;
			gbc_textFieldDeviceIP.gridy = 0;
			contentPanel.add(textFieldDeviceIP, gbc_textFieldDeviceIP);
			textFieldDeviceIP.setText("192.168.0.11");
			textFieldDeviceIP.setColumns(10);
		}
		{
			JLabel lblFrontIp = new JLabel("New Front IP Address (eth0):");
			GridBagConstraints gbc_lblFrontIp = new GridBagConstraints();
			gbc_lblFrontIp.fill = GridBagConstraints.BOTH;
			gbc_lblFrontIp.insets = new Insets(0, 0, 5, 5);
			gbc_lblFrontIp.gridx = 1;
			gbc_lblFrontIp.gridy = 1;
			contentPanel.add(lblFrontIp, gbc_lblFrontIp);
		}
		{
			textFieldFrontIP = new JTextField();
			textFieldFrontIP.setText("192.168.0.");
			GridBagConstraints gbc_textFieldFrontIP = new GridBagConstraints();
			gbc_textFieldFrontIP.fill = GridBagConstraints.BOTH;
			gbc_textFieldFrontIP.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldFrontIP.gridx = 2;
			gbc_textFieldFrontIP.gridy = 1;
			contentPanel.add(textFieldFrontIP, gbc_textFieldFrontIP);
			textFieldFrontIP.setColumns(10);
		}
		{
			JLabel lblNewRearIp = new JLabel("New Rear IP Address (eth1):");
			GridBagConstraints gbc_lblNewRearIp = new GridBagConstraints();
			gbc_lblNewRearIp.fill = GridBagConstraints.BOTH;
			gbc_lblNewRearIp.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewRearIp.gridx = 1;
			gbc_lblNewRearIp.gridy = 2;
			contentPanel.add(lblNewRearIp, gbc_lblNewRearIp);
		}
		{
			textFieldRearIP = new JTextField();
			GridBagConstraints gbc_textFieldRearIP = new GridBagConstraints();
			gbc_textFieldRearIP.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldRearIP.fill = GridBagConstraints.BOTH;
			gbc_textFieldRearIP.gridx = 2;
			gbc_textFieldRearIP.gridy = 2;
			contentPanel.add(textFieldRearIP, gbc_textFieldRearIP);
			textFieldRearIP.setText("192.168.1.");
			textFieldRearIP.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						device_ip = textFieldDeviceIP.getText();
						front_ip = textFieldRearIP.getText();
						rear_ip = textFieldFrontIP.getText();
						System.out.println("Device IP: " + device_ip);
						System.out.println("Vaild IP: " + Networking.isValidIPV4Address(device_ip));
						boolean ips_valid = false;
						ips_valid = Networking.isValidIPV4Address(device_ip);
						ips_valid &= Networking.isValidIPV4Address(front_ip);
						ips_valid &= Networking.isValidIPV4Address(rear_ip);
						System.out.println(ips_valid);
						if (ips_valid)
						{
							if (change_front_ip)
							{
								Cashel.setIP(device_ip, front_ip, rear_ip);
							}
							else
							{
								Cashel.setIP(device_ip, rear_ip);
							}
							System.out.println("Setting new IP address");
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			JCheckBox chckbxChangeFrontIp = new JCheckBox("Change Front IP");

			chckbxChangeFrontIp.setSelected(true);
			chckbxChangeFrontIp.addActionListener(this);

			GridBagConstraints gbc_chckbxChangeFrontIp = new GridBagConstraints();
			gbc_chckbxChangeFrontIp.insets = new Insets(0, 0, 5, 0);
			gbc_chckbxChangeFrontIp.gridx = 3;
			gbc_chckbxChangeFrontIp.gridy = 1;
			contentPanel.add(chckbxChangeFrontIp, gbc_chckbxChangeFrontIp);
		}
	}
	public void actionPerformed(ActionEvent ae)
	{
		JCheckBox checkBox = (JCheckBox)ae.getSource();
		if (textFieldFrontIP != null)
		{
			textFieldFrontIP.setEnabled(checkBox.isSelected());
			change_front_ip = checkBox.isSelected();
		}
	}
}
