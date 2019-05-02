package cs455.overlay.wireformats;

import java.io.IOException;

public class Register implements Event {
	
	private int messageType;
	private String nodeHostname;
	private int nodePortNumber;
	
	public Register(int messageType, String nodeHostname, int nodePortNumber) {
		this.messageType = messageType;
		this.nodeHostname = nodeHostname;
		this.nodePortNumber = nodePortNumber; 
	}
	
	public byte[] getBytes() throws IOException {
		BasicMessage msgToRegistry = new BasicMessage();
		
		return msgToRegistry.getBytes(messageType, nodeHostname, nodePortNumber);
	}

	public int getType() {
		return messageType;
	}
	
	public String getHostname() {
		return nodeHostname;
	}
	
	public int getPortNumber() {
		return nodePortNumber;
	}

	public void registerNode() {
		System.out.println("I'm registering the node");
	}
	
	public void sendRegistrationResponse() {
		System.out.println("I'm sending the registration response");
	}
	
}
