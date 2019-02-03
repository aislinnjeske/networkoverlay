package cs455.overlay.wireformats;

import java.io.IOException;

public class TaskComplete implements Event {
	
	private int messageType;
	private String nodeIPAddress;
	private int nodePortNumber;
	
	public TaskComplete(int messageType, String nodeIPAddress, int nodePortNumber) {
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

}
