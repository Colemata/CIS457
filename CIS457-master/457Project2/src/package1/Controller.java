package package1;

/**
 * Controller - sets the variables and allows the model classes to access
 * the variables the user inputted in the GUI.
 * 
 * @author David Fletcher, Taylor Coleman
 * @version 1.0 | Last Updated: 10/25/16
 */
public class Controller {
	
	/** The port number */
	private int port;
	
	/** The server host name */
	private String serverHostname;
	
	/** The keyword used to search */
	private String keyword;
	
	/** The command entered by the user */
	private String command;
	
	/** The hostname */
	private String hostname;
	
	/**The speed specified by the user */
	private String speed;

	/**
	 * Sets the port number from the GUI.
	 * @param port the port number
	 */
	void setPortNumber(int port){
		
		this.port = port;
	}
	
	/**
	 * Sets the server host name from the GUI.
	 * @param serverHostName the server's hostname
	 */
	void setServerHostname(String serverHostname){
		
		this.serverHostname = serverHostname;
	}
	
	/**
	 * Sets the keyword from the GUI.
	 * @param keyword the keyword entered to search
	 */
	void setKeyword(String keyword){
		
		this.keyword = keyword;
	}
	
	/**
	 * Sets the command from the GUI.
	 * @param command command specified by the user
	 */
	void setCommand(String command){
		
		this.command = command;
	}
	
	/**
	 * Sets the host name from the GUI.
	 * @param hostname sets the hostname
	 */
	void setHostname(String hostname){
		
		this.hostname = hostname;
	}
	
	/**
	 * Sets the speed specified from the GUI.
	 * @param speed sets the connection speed
	 */
	void setSpeed(String speed){
		
		this.speed = speed;
	}
	
	/**
	 * Returns the port number set by the GUI.
	 * @return port number
	 */
	int getPortNumber(){
		
		return port;
	}
	
	/**
	 * Returns the server hostname set by the GUI.
	 * @return server hostname
	 */
	String getServerHostname(){
		
		return serverHostname;
	}
	
	/**
	 * Returns the keyword set by the GUI.
	 * @return keyword
	 */
	String getKeyword(){
		
		return keyword;
	}
	
	/**
	 * Returns the command set by the GUI.
	 * @return command
	 */
	String getCommand(){
		
		return command;
	}
	
	/**
	 * Returns the hostname set by the GUI.
	 * @return hostname
	 */
	String getHostname(){
		
		return hostname;
	}
	
	/**
	 * Returns the speed set by the GUI.
	 * @return speed
	 */
	String getSpeed(){
		
		return speed;
	}
	
}
