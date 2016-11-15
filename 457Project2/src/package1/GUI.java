package package1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * GUI - an interface that allows the user to perform various actions and interact
 * with the program.
 * 
 * @author David Fletcher, Taylor Coleman
 * @version 1.0 | Last Updated: 10/25/16
 */
public class GUI extends JFrame implements ActionListener{
	
	/** An instance of a Controller class */
	private Controller controller;

	/** Main panel on the JFrame */
	private JPanel mainPanel;
	
	/** Top panel for the Connection components */
	private JPanel topPanel;
	
	/** The upper panel for the Connection components */
	private JPanel toptopPanel;
	
	/** The lower panel for the Connection components */
	private JPanel topBottomPanel;
	
	/** The center panel for the Search components */
	private JPanel centerPanel;
	
	/** The upper panel for the Search components */
	private JPanel centerTopPanel;
	
	/** The lower panel for the Search components */
	private JPanel centerBottomPanel;
	
	/** The bottom panel for the FTP components */
	private JPanel bottomPanel;
	
	/** The upper panel for the FTP components */
	private JPanel bottomTopPanel;
	
	/** The lower panel for the FTP components */
	private JPanel bottomBottomPanel;
	
	/** Label for the Server Hostname */
	private JLabel serverHostnameLabel;
	
	/** The text field for the Server Hostname */
	private JTextField serverHostnameField;
	
	/** The Label for the Port number */
	private JLabel portLabel;
	
	/** The text field for the Port number */
	private JTextField portField;
	
	/** The label for the username */
	private JLabel usernameLabel;
	
	/** The text field for the username */
	private JTextField usernameField;
	
	/** The label for the hostname */
	private JLabel hostnameLabel;
	
	/** The text field for the hostname */
	private JTextField hostnameField;
	
	/** The label for the speed */
	private JLabel speedLabel;
	
	/** A combo box for the speed */
	private JComboBox speedComboBox;
	
	/** List of strings for the speed combo box */
	private String[] speedList = {"Modem", "Ethernet", "T1", "T3"};
	
	/** A button to connect */
	private JButton connectButton;
	
	/** A label for the keyword */
	private JLabel keywordLabel;
	
	/** The text field for the keyword */
	private JTextField keywordField;
	
	/** A button that allows the user to search */
	private JButton searchButton;
	
	/** A label for the command */
	private JLabel enterCommandLabel;
	
	/** A text field for the command */
	private JTextField enterCommandField;
	
	/** A button that submits the command */
	private JButton goButton;
	
	/** A text area for the results of the command */
	private JTextArea ftpArea;

	/** A JTable for the search results */
	private JTable searchResultsTable;

	/** A JScrollPane */
	private JScrollPane jScrollPane1;

	/** The listening label for the port number */
	private JLabel listeningLabel;

	/** A JTextField for the listening port */
	private JTextField listeningPortField;

	/** Button that quits */
	private JButton quitButton;
	
	/**
	 * Constructor that initializes the GUI 
	 */
	public GUI(){
		
		controller = new Controller();
		
		mainPanel = new JPanel();
		mainPanel.setOpaque(true);
		mainPanel.setBackground(Color.DARK_GRAY);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BorderLayout(5, 5));
		
		

		//Top Panel Components
		topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(true);
		topPanel.setBackground(Color.WHITE);
		toptopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toptopPanel.setSize(150, 200);
		toptopPanel.setOpaque(true);
		toptopPanel.setBackground(Color.WHITE);
		topBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topBottomPanel.setOpaque(true);
		topBottomPanel.setBackground(Color.WHITE);
		topPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
		serverHostnameLabel = new JLabel("Server Hostname:");
		serverHostnameField = new JTextField(10);
		portLabel = new JLabel("Port:");
		portField = new JTextField(5);
		listeningLabel = new JLabel("Listening Port: ");
		listeningPortField = new JTextField(5);
		connectButton = new JButton("Connect");
		connectButton.addActionListener(this);
		usernameLabel = new JLabel("Username:");
		usernameField = new JTextField(10);
		hostnameLabel = new JLabel("Hostname:");
		hostnameField = new JTextField(10);
		speedLabel = new JLabel("Speed:");
		speedComboBox = new JComboBox(speedList);
		toptopPanel.add(serverHostnameLabel);
		toptopPanel.add(serverHostnameField);
		toptopPanel.add(portLabel);
		toptopPanel.add(portField);
		toptopPanel.add(listeningLabel);
		toptopPanel.add(listeningPortField);
		toptopPanel.add(connectButton);
		topBottomPanel.add(usernameLabel);
		topBottomPanel.add(usernameField);
		topBottomPanel.add(hostnameLabel);
		topBottomPanel.add(hostnameField);
		topBottomPanel.add(speedLabel);
		topBottomPanel.add(speedComboBox);
		topPanel.add(BorderLayout.PAGE_START, toptopPanel);
		topPanel.add(BorderLayout.PAGE_END, topBottomPanel);
		
		//Center Panel Components
		centerTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		centerBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		quitButton = new JButton("Disconnect");
		quitButton.setEnabled(false);
		quitButton.addActionListener(this);
		centerTopPanel.setOpaque(true);
		centerBottomPanel.setOpaque(true);
		centerTopPanel.setBackground(Color.WHITE);
		centerBottomPanel.setBackground(Color.WHITE);
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setOpaque(true);
		centerPanel.setBackground(Color.WHITE);		
		centerPanel.setBorder(BorderFactory.createTitledBorder("Search"));
		keywordLabel = new JLabel("Keyword:");
		keywordField = new JTextField(15);
		searchButton = new JButton("Search");
		searchButton.setEnabled(false);
		searchButton.addActionListener(this);
		centerTopPanel.add(keywordLabel);
		centerTopPanel.add(keywordField);
		centerTopPanel.add(searchButton);
		centerTopPanel.add(quitButton);
		
		jScrollPane1 = new javax.swing.JScrollPane();
		jScrollPane1.setSize(200,200);
		centerBottomPanel.setPreferredSize(new Dimension(200,200));
		centerBottomPanel.add(jScrollPane1);
		searchResultsTable = new JTable();
		searchResultsTable.setModel(new javax.swing.table.DefaultTableModel(
				new Object [][] {

				},
				new String [] {
						"Speed", "Hostname", "Filename"
				}
		));
		centerBottomPanel.add(searchResultsTable);
		searchResultsTable.setCellSelectionEnabled(false);
		searchResultsTable.setColumnSelectionAllowed(false);
		searchResultsTable.setRowSelectionAllowed(true);
		searchResultsTable.setPreferredSize(new Dimension(200,200));
		searchResultsTable.setGridColor(Color.gray);
		jScrollPane1.setViewportView(searchResultsTable);

		centerPanel.add(BorderLayout.NORTH, centerTopPanel);
		centerPanel.add(BorderLayout.CENTER, centerBottomPanel);

		
		//Bottom Panel Components
		bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setOpaque(true);
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.setBorder(BorderFactory.createTitledBorder("FTP"));
		bottomTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottomTopPanel.setOpaque(true);
		bottomTopPanel.setBackground(Color.WHITE);
		bottomBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottomBottomPanel.setOpaque(true);
		bottomBottomPanel.setBackground(Color.WHITE);
		enterCommandLabel = new JLabel("Enter Command:");
		enterCommandField = new JTextField(30);
		goButton = new JButton("Go");
		goButton.addActionListener(this);
		ftpArea = new JTextArea(10, 50);
		ftpArea.setBackground(Color.LIGHT_GRAY);
		ftpArea.setEditable(false);
		bottomTopPanel.add(enterCommandLabel);
		bottomTopPanel.add(enterCommandField);
		bottomTopPanel.add(goButton);
		bottomBottomPanel.add(ftpArea);
		bottomPanel.add(BorderLayout.NORTH, bottomTopPanel);
		bottomPanel.add(BorderLayout.SOUTH, bottomBottomPanel);
		
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));		
		
		add(mainPanel);
		mainPanel.add(topPanel);
		mainPanel.add(centerPanel);
		mainPanel.add(bottomPanel);
		
		setTitle("GV-NAP File Sharing System");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);

		usernameField.setText("taylor");
		hostnameField.setText("localhost");
		serverHostnameField.setText("localhost");
		portField.setText("33333");
		listeningPortField.setText("6278");

	}
	
	/**
	 * Sets the port number in the Controller class
	 */
	void setPortNumber(){
		
		controller.setPortNumber(Integer.parseInt(portField.getText()));
	}

	/**
	 * Sets the listening port number
	 */
	void setListeningPortNumber(){

		controller.setListeningPortNumber(Integer.parseInt(listeningPortField.getText()));
	}
	
	/**
	 * Sets the server hostname number in the Controller class
	 */
	void setServerHostname(){
		
		controller.setServerHostname(serverHostnameField.getText());
	}
	
	/**
	 * Sets the command in the Controller class
	 */
	void setCommand(){
		
		controller.setCommand(enterCommandField.getText());
	}
	
	/**
	 * Sets the keyword in the Controller class
	 */
	void setKeyword(){
		
		controller.setKeyword(keywordField.getText());
	}
	
	/**
	 * Sets the hostname in the Controller class
	 */
	void setHostname(){
		
		controller.setHostname(hostnameField.getText());
	}
	
	/**
	 * Sets the speed in the Controller class
	 */
	void setSpeed(){
		
		controller.setSpeed((String) speedComboBox.getSelectedItem());
	}

	/**
	 * Sets the speed in the Controller class
	 */
	void setUsername(){

		controller.setUsername(usernameField.getText());
	}
	
	/**
	 * Action Performed method that handles the events for the GUI.
	 *
	 * @param ActionEvent event
	 */
	public void actionPerformed(ActionEvent event){
		
		JComponent e = (JComponent)event.getSource();
		String status;


		if(keywordField.getText() != null){
			

		}

		if(e == quitButton){

			status = controller.sendQuitCommandToServer();
			connectButton.setBackground(Color.red);
			quitButton.setEnabled(false);
			searchButton.setEnabled(false);
			ftpArea.append("\n" + status);
			ftpArea.revalidate();
			ftpArea.repaint();
		}
		
		if(e == connectButton){
			setServerHostname();
			setPortNumber();
			setUsername();
			setHostname();
			setSpeed();
			setListeningPortNumber();
			boolean connectionMade = controller.connectToServer();
			if(connectionMade){
				quitButton.setEnabled(true);
				searchButton.setEnabled(true);
				connectButton.setBackground(Color.green);
				status = "Connect to server: " + controller.getServerHostname() + ":" + controller.getPortNumber();
			}else{
				connectButton.setBackground(Color.red);
				status = "Unable to connect to server: " + controller.getServerHostname() + ":" + controller.getPortNumber();
			}
			ftpArea.append("\n" + status);
			ftpArea.revalidate();
			ftpArea.repaint();
		}
		
		if(e == searchButton && keywordField.getText() != null){

			//each time we do a search, clear the table.
			DefaultTableModel dm = (DefaultTableModel) searchResultsTable.getModel();
			int rowCount = dm.getRowCount();

			//gotta remove it backwards.
			for (int i = rowCount - 1; i >= 0; i--) {
				dm.removeRow(i);
			}

			setKeyword();
			ArrayList<FileData> retVal = controller.sendSearchCritera();
			int index = 0;
			File curDir = new File(".");
			File[] FileList = curDir.listFiles();
			boolean weDontHaveFileAlready = false;
			for(FileData file: retVal){
				for (File f : FileList) {
					if (f.getName().equalsIgnoreCase(file.getFilename())) {
						weDontHaveFileAlready = true;
					}
				}
				if(!weDontHaveFileAlready) {
					DefaultTableModel model = (DefaultTableModel) searchResultsTable.getModel();
					model.addRow(new Object[]{null, null, null});
					searchResultsTable.setValueAt(file.getSpeed(), index, 0);
					searchResultsTable.setValueAt(file.getHostname() + ":" + file.getPort(), index, 1);
					searchResultsTable.setValueAt(file.getFilename(), index, 2);
					index++;
				}
				weDontHaveFileAlready = false;
			}
			searchResultsTable.revalidate();
			searchResultsTable.repaint();
		}
		
		if(e == goButton){
			
			if(enterCommandField.getText() == null){
				
				System.out.println("Please enter a command");
			}
			else{

				setCommand();
				ftpArea.append("\n>> " + controller.getCommand());
				ftpArea.revalidate();
				ftpArea.repaint();
				status = controller.sendCommandToOtherClient();
				ftpArea.append("\n" + status);
				ftpArea.revalidate();
				ftpArea.repaint();
			}
		}
	}
	
	
	
	
}



