package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrafficSummary implements Event {
	
	private int messageType;
	private String nodeHostname;
	private int nodePortNumber;
	private int numberOfMessagesSent;
	private long sumOfMessagesSent;
	private int numberOfMessagesReceived;
	private long sumOfMessagesReceived;
	private int numberOfMessagesRelayed; 
	
	public TrafficSummary(int messageType, String nodeHostname, int nodePortNumber, int messagesSent, long sumOfMessagesSent,
			              int messagesReceived, long sumOfMessagesReceived, int messagesRelayed) {
		
		this.messageType = messageType;
		this.nodeHostname = nodeHostname;
		this.nodePortNumber = nodePortNumber;
		this.numberOfMessagesSent = messagesSent;
		this.sumOfMessagesSent = sumOfMessagesSent; 
		this.numberOfMessagesReceived = messagesReceived;
		this.sumOfMessagesReceived = sumOfMessagesReceived;
		this.numberOfMessagesRelayed = messagesRelayed; 
	}

	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteOutputStream)); 
		
		//Writing the message type
		dataOutputStream.writeInt(messageType);
		
		//Writing the node IP Address
		byte[] nodeHostnameBytes = nodeHostname.getBytes();
		int nodeHostnameLength = nodeHostnameBytes.length;
		dataOutputStream.writeInt(nodeHostnameLength);
		dataOutputStream.write(nodeHostnameBytes);
		
		//Writing the node port number
		dataOutputStream.writeInt(nodePortNumber);
		
		//Writing the number of messages sent
		dataOutputStream.writeInt(numberOfMessagesSent);
		
		//Writing the sum of the messages sent
		dataOutputStream.writeLong(sumOfMessagesSent);
		
		//Writing the number of messages received
		dataOutputStream.writeInt(numberOfMessagesReceived);
		
		//Writing the sum of the messages received
		dataOutputStream.writeLong(sumOfMessagesReceived);
		
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
	
	public String getNameToSend() {
		return nodeHostname + ":" + nodePortNumber;
	}
	
	public int getMessagesSent() {
		return numberOfMessagesSent;
	}
	
	public long getSumOfMessagesSent() {
		return sumOfMessagesSent;
	}
	
	public int getMessagesReceived() {
		return numberOfMessagesReceived;
	}
	
	public long getSumOfMessagesReceived() {
		return sumOfMessagesReceived;
	}
	
	public int getNumberOfMessagesRelayed() {
		return numberOfMessagesRelayed;
	}

}
