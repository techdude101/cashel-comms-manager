package cashel_comms_manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Commands_Window extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Commands_Window frame = new Commands_Window();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Commands_Window() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		getContentPane().setLayout(new BorderLayout(0, 0));
		contentPane = new JPanel();
		getContentPane().add(contentPane, BorderLayout.CENTER);
		
		JEditorPane dtrpnCommandText = new JEditorPane();
		JScrollPane scrollPane = new JScrollPane(dtrpnCommandText);
		dtrpnCommandText.setPreferredSize(new Dimension(500, 500));
		dtrpnCommandText.setContentType("text/html");
		dtrpnCommandText.setEditable(false);
		dtrpnCommandText.setText("Command Text");

		try
		{
			String text = FileIO.readTextFileOrderedList(new File(".").getCanonicalPath() + "\\text\\CF_Recovery.txt");
			dtrpnCommandText.setText(text);
		}
		catch (IOException e)
		{
			dtrpnCommandText.setText("Error reading file<br />" + e.getMessage());
		}
		
		contentPane.add(scrollPane);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.NORTH);
		
		JComboBox comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				int selected_index = comboBox.getSelectedIndex();
				String filename = "";
				String file_contents = "";
				String path = "";
				
				switch (selected_index)
				{
				case 0:
					filename = "\\text\\CF_Recovery.txt";
					break;
				case 1:
					filename = "\\text\\Delete_Logs.txt";
					break;
				case 2:
					filename = "\\text\\Change_IP.txt";
					break;
				case 3:
					filename = "\\text\\Show_IP.txt";
					break;
				case 4:
					filename = "\\text\\Load_Bootloader.txt";
					break;
				case 5:
					filename = "\\text\\Check_Interrupts.txt";
					break;
				case 6:
					filename = "\\text\\Check_NTP.txt";
					break;
				}
				try {
					path = new File(".").getCanonicalPath();
					if (filename != "")
					{
						file_contents = FileIO.readTextFileOrderedList(path + filename);
					}
					else { file_contents = "Invalid filename"; }
				} catch (IOException e) {
					file_contents = e.getMessage();
				} // End catch IOException

				dtrpnCommandText.setText(file_contents);
			}
		});

		panel_1.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {
				"CF Recovery", "Delete Logs and Records", "Change IP Address", 
				"Show IP Address", "Load Bootloader", "Check Interrupts", 
				"Slave - Check NTP Comms"
				}));
		comboBox.setSelectedIndex(0);
		
		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.SOUTH);
	}
}
