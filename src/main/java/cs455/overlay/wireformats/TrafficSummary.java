package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrafficSummary implements Event {
	
	private int messageType;
	private String nodeIPAddress;
	private int nodePortNumber;
	private int numberOfMessagesSent;
	private int sumOfMessagesSent;
	private int numberOfMessagesReceived;
	private int sumOfMessagesReceived;
	private int numberOfMessagesRelayed; 
	
	public TrafficSummary(int messageType, String nodeIPAddress, int nodePortNumber, int messagesSent, int sumOfMessagesSent,
			              int messagesRecieved, int sumOfMessagesRecieved, int messagesRelayed) {
		
		this.messageType = messageType;
		this.nodeIPAddress = nodeIPAddress;
		this.nodePortNumber = nodePortNumber;
		this.numberOfMessagesSent = messagesSent;
		this.sumOfMessagesSent = sumOfMessagesSent; 
		this.numberOfMessagesReceived = messagesRecieved;
		this.sumOfMessagesReceived = sumOfMessagesRecieved;
		this.numberOfMessagesRelayed = messagesRelayed; 
	}

	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteOutputStream)); 
		
		//Writing the message type
		dataOutputStream.writeInt(messageType);
		
		//Writing the node IP Address
		byte[] nodeIPAddressBytes = nodeIPAddress.getBytes();
		int nodeIPAddressLength = nodeIPAddressBytes.length;
		dataOutputStream.writeInt(nodeIPAddressLength);
		dataOutputStream.write(nodeIPAddressBytes);
		
		//Writing the node port number
		dataOutputStream.writeInt(nodePortNumber);
		
		//Writing the number of messages sent
		dataOutputStream.writeInt(numberOfMessagesSent);
		
		//Writing the sum of the messages sent
		dataOutputStream.writeInt(sumOfMessagesSent);
		
		//Writing the number of messages received
		dataOutputStream.writeInt(numberOfMessagesReceived);
		
		//Writing the sum of the messages received
		dataOutputStream.writeInt(sumOfMessagesReceived);
		
		//Writing the number of messages relayed
		dataOutputStream.writeInt(numberOfMessagesRelayed); 
		
		
		dataOutputStream.flush();
		marshalledBytes = byteOutputStream.toByteArray();
		
		byteOutputStream.close();
		dataOutputStream.close();
		
		return marshalledBytes;
	}

	public int getType() {
		return messageType; 
	}

}
