package cs455.overlay.wireformats;

import java.io.IOException;

public class LinkWeights implements Event{
	
	private int messageType;

	public byte[] getBytes() throws IOException {
		return null;
	}

	public int getType() {
		return messageType;
	}

}
