package cs455.overlay.wireformats;

import java.io.IOException;

public class MessagingNodesList implements Event{
	
	private int messageType;
	private int numPeerNodes; 
	private String[] peerNodes; 

	public byte[] getBytes() throws IOException {
		
		return null;
	}

	public int getType() {
		return messageType;
	}

}
