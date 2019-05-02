package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskInitiate implements Event {
	
	private int messageType;
	private int numberOfRounds;
	
	public TaskInitiate(int messageType, int numberOfRounds) {
		this.messageType = messageType;
		this.numberOfRounds = numberOfRounds; 
	}

	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteOutputStream)); 
		
		//Writing the message type
		dataOutputStream.writeInt(messageType);
		
		//Writing the number of rounds
		dataOutputStream.writeInt(numberOfRounds);
		
		dataOutputStream.flush();
		marshalledBytes = byteOutputStream.toByteArray();
		
		byteOutputStream.close();
		dataOutputStream.close();
		
		return marshalledBytes;	
	}

	public int getType() {
		return messageType;
	}
	
	public int getNumberOfRounds() {
		return numberOfRounds;
	}

}
