package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class EventFactory {
	
	private static EventFactory instance = new EventFactory();
	private Protocol protocol;
	
	private EventFactory() {
		protocol = new Protocol(); 
	}
	
	public static EventFactory getInstance() {
		return instance; 
	}
	
	public Event createEvent(byte[] receivedMsg) throws IOException{

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivedMsg);
		DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(byteArrayInputStream)); 
		
		int msgType = determineMessageType(dataInputStream);

		Event receivedMessage = unmarshallMessage(msgType, dataInputStream); 
		
		byteArrayInputStream.close();
		dataInputStream.close(); 
		
		return receivedMessage;
	}
	
	private int determineMessageType(DataInputStream dataInputStream) throws IOException {
		return dataInputStream.readInt();
	}
	
	private Event unmarshallMessage(int messageType, DataInputStream dataInputStream) throws IOException {
		switch(messageType) {
		case 1:
			return unmarshallRegister(dataInputStream); 
		case 2:
			return unmarshallRegistrationResponse(dataInputStream);
		case 3: 
			return unmarshallDeregister(dataInputStream); 
		case 4: 
			return unmarshallMessagingNodesList(dataInputStream);
		case 5:
			return unmarshallLinkWeights(dataInputStream);
		case 6:
			return unmarshallTaskInitiate(dataInputStream);
		case 7:
			return unmarshallTaskComplete(dataInputStream);
		case 8:
			return unmarshallPullTrafficSummary(dataInputStream);
		case 9:
			return unmarshallTrafficSummary(dataInputStream);
		case 10:
			return unmarshallDeregistrationResponse(dataInputStream);
		default:
			return null; 
		}
	}

	private Register unmarshallRegister(DataInputStream dataInputStream) throws IOException {

		//We already read the message type, so starting with the node IPAddress
		int nodeIPAddressLength = dataInputStream.readInt();
		byte[] nodeIPAddressBytes = new byte[nodeIPAddressLength]; 
		dataInputStream.readFully(nodeIPAddressBytes); 
		String nodeIPAddress = new String(nodeIPAddressBytes); 
		
		//Reading node port number
		int nodePortNumber = dataInputStream.readInt(); 
		
		return new Register(protocol.getNumOfMessageType("REGISTER_REQUEST"), nodeIPAddress, nodePortNumber); 		
	}
	
	private Deregister unmarshallDeregister(DataInputStream dataInputStream) throws IOException {
		
		//We already read the message type, so starting with the node IPAddress
		int nodeIPAddressLength = dataInputStream.readInt();
		byte[] nodeIPAddressBytes = new byte[nodeIPAddressLength]; 
		dataInputStream.readFully(nodeIPAddressBytes); 
		String nodeIPAddress = new String(nodeIPAddressBytes); 
		
		//Reading node port number
		int nodePortNumber = dataInputStream.readInt(); 
		
		return new Deregister(protocol.getNumOfMessageType("DEREGISTER_REQUEST"), nodeIPAddress, nodePortNumber); 
			
	}
	
	private RegistrationResponse unmarshallRegistrationResponse(DataInputStream dataInputStream) throws IOException {
		//Already got message type, so starting with status code
		byte statusCode = dataInputStream.readByte(); 
		
		//Reading the additional info
		int additionalInfoLength = dataInputStream.readInt();
		byte[] additionalInfoBytes = new byte[additionalInfoLength]; 
		dataInputStream.readFully(additionalInfoBytes);
		String additionalInfo = new String(additionalInfoBytes);
		
		return new RegistrationResponse(protocol.getNumOfMessageType("REGISTRATION_RESPONSE"), statusCode, additionalInfo);

	}
	
	private DeregistrationResponse unmarshallDeregistrationResponse(DataInputStream dataInputStream) throws IOException {

		//Already got message type, so starting with status code
		byte statusCode = dataInputStream.readByte(); 
		
		//Reading the additional info
		int additionalInfoLength = dataInputStream.readInt();
		byte[] additionalInfoBytes = new byte[additionalInfoLength]; 
		dataInputStream.readFully(additionalInfoBytes);
		String additionalInfo = new String(additionalInfoBytes);
		
		return new DeregistrationResponse(protocol.getNumOfMessageType("DEREGISTRATION_RESPONSE"), statusCode, additionalInfo);

	}
	
	//TODO: implement
	private MessagingNodesList unmarshallMessagingNodesList(DataInputStream dataInputStream) throws IOException {
		return null;
	}

	//TODO: implement
	private LinkWeights unmarshallLinkWeights(DataInputStream dataInputStream) throws IOException {
		return null;
	}
	
	private TaskInitiate unmarshallTaskInitiate(DataInputStream dataInputStream) throws IOException {
		//Already got message type, so start with number of rounds
		int numberOfRounds = dataInputStream.readInt();
		
		return new TaskInitiate(protocol.getNumOfMessageType("TASK_INITIATE"), numberOfRounds);
	}
	
	private TaskComplete unmarshallTaskComplete(DataInputStream dataInputStream) throws IOException {

		//We already read the message type, so starting with the node IPAddress
		int nodeIPAddressLength = dataInputStream.readInt();
		byte[] nodeIPAddressBytes = new byte[nodeIPAddressLength]; 
		dataInputStream.readFully(nodeIPAddressBytes); 
		String nodeIPAddress = new String(nodeIPAddressBytes); 
		
		//Reading node port number
		int nodePortNumber = dataInputStream.readInt(); 
		
		return new TaskComplete(protocol.getNumOfMessageType("TASK_COMPLETE"), nodeIPAddress, nodePortNumber); 

	}
	
	private PullTrafficSummary unmarshallPullTrafficSummary(DataInputStream dataInputStream) throws IOException {
		
		//this message only has a message type and so no more reading is required
		return new PullTrafficSummary(protocol.getNumOfMessageType("PULL_TRAFFIC_SUMMARY"));
	}
	
	private TrafficSummary unmarshallTrafficSummary(DataInputStream dataInputStream) throws IOException {

		//We already read the message type, so starting with the node IPAddress
		int nodeIPAddressLength = dataInputStream.readInt();
		byte[] nodeIPAddressBytes = new byte[nodeIPAddressLength]; 
		dataInputStream.readFully(nodeIPAddressBytes); 
		String nodeIPAddress = new String(nodeIPAddressBytes); 
		
		//Reading node port number
		int nodePortNumber = dataInputStream.readInt(); 
		
		//Reading the number of messages sent
		int numberOfMessagesSent = dataInputStream.readInt();
		
		//Reading the sum of messages sent
		int sumOfMessagesSent = dataInputStream.readInt();
		
		//Reading the number of messages received
		int numberOfMessagesReceived = dataInputStream.readInt(); 
		
		//Reading the sum of messages received
		int sumOfMessagesReceived = dataInputStream.readInt();
		
		//Reading the number of messages relayed
		int numberOfMessagesRelayed = dataInputStream.readInt();
		
		return new TrafficSummary(protocol.getNumOfMessageType("TRAFFIC_SUMMARY"), nodeIPAddress, nodePortNumber, numberOfMessagesSent, 
				sumOfMessagesSent, numberOfMessagesReceived, sumOfMessagesReceived, numberOfMessagesRelayed); 

	}
	
}
