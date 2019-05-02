package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RoundsMessage implements Event {
	
	private int messageType;
	private int randomNumber;
	private String pathToDestination;
	
	public RoundsMessage(int messageType, int randomNumber, String pathToDestination) {
		this.messageType = messageType;
		this.randomNumber = randomNumber;
		this.pathToDestination = pathToDestination;
	}
	
	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteOutputStream)); 
		
		//Writing the message type
		dataOutputStream.writeInt(messageType);
		
		//Writing the random number
		dataOutputStream.writeInt(randomNumber);
		
		//Writing the path to the destination
		byte[] pathBytes = pathToDestination.getBytes();
		int pathLength = pathBytes.length;
		dataOutputStream.writeInt(pathLength);
		dataOutputStream.write(pathBytes);
		
		dataOutputStream.flush();
		marshalledBytes = byteOutputStream.toByteArray();
		
		byteOutputStream.close();
		dataOutputStream.close();
		
		return marshalledBytes;
	}

	public int getType() {
		return messageType;
	}
	
	public String getPath() {
		return pathToDestination;
	}
	
	public void setPath(String pathToDestination) {
		this.pathToDestination = pathToDestination;
	}
	
	public int getRandomNumber() {
		return randomNumber;
	}

}
