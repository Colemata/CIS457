package package1;

import static org.junit.Assert.*;

import org.junit.Test;

public class ControllerTest {

	@Test
	public void testSetGetPortNumber() {
		
		Controller c = new Controller();
		c.setPortNumber(1234);
		assertEquals(c.getPortNumber(), 1234);
	}
	
	@Test
	public void testSetGetServerHostname() {
		
		Controller c = new Controller();
		c.setServerHostname("My Server");
		assertEquals(c.getServerHostname(), "My Server");
	}
	
	@Test
	public void testSetGetHostname() {
		
		Controller c = new Controller();
		c.setHostname("My hostname");
		assertEquals(c.getHostname(), "My hostname");
	}
	
	@Test
	public void testSetGetSpeed() {
		
		Controller c = new Controller();
		c.setSpeed("Ethernet");
		assertEquals(c.getSpeed(), "Ethernet");
	}
	
	@Test
	public void testSetGetCommand(){
		
		Controller c = new Controller();
		c.setCommand("This is my command");
		assertEquals(c.getCommand(), "This is my command");
	}
	
	@Test
	public void testSetGetKeyword(){
		
		Controller c = new Controller();
		c.setKeyword("keyword");
		assertEquals(c.getKeyword(), "keyword");
	}

}
