package cs455.overlay.wireformats;

import java.io.IOException;

public class Register implements Event {
	
	private int messageType;
	private String nodeIPAddress;
	private int nodePortNumber;
	
	public Register(int messageType, String nodeIPAddress, int nodePortNumber) {
		this.messageType = messageType;
		this.nodeIPAddress = nodeIPAddress;
		this.nodePortNumber = nodePortNumber; 
	}
	
	public byte[] getBytes() throws IOException {
		BasicMessage msgToRegistry = new BasicMessage();
		
		return msgToRegistry.getBytes(messageType, nodeIPAddress, nodePortNumber);
	}

	public int getType() {
		return messageType;
	}
	
	public String getNodeIPAddress() {
		return nodeIPAddress;
	}
	
	public int getNodePortNumber() {
		return nodePortNumber;
	}

	public void registerNode() {
		System.out.println("I'm registering the node");
	}
	
	public void sendRegistrationResponse() {
		System.out.println("I'm sending the registration response");
	}
	
}
