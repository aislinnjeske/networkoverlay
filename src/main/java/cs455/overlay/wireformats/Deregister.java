package cs455.overlay.wireformats;

import java.io.IOException;

public class Deregister implements Event {
	
	private int messageType;
	private String nodeHostname;
	private int nodePortNumber;
	
	public Deregister(int messageType, String nodeHostname, int nodePortNumber) {
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
	
	public String getNodeHostname() {
		return nodeHostname;
	}
	
	public int getNodePortNumber() {
		return nodePortNumber;
	}

}
