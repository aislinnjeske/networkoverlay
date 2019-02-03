package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MessagingNode implements Node {
	
	private static String myIPAddress; 
	private static int myPortNumber;
	private static TCPConnection connectionToTheRegistry;
	private static Protocol protocol;
	
	public MessagingNode() {
		protocol = new Protocol();
	}
	
	public static void main(String[] args) {
		
		String registryIPAddress = args[0];
		int registryPortNumber = Integer.parseInt(args[1]);
		
		MessagingNode messagingNode = new MessagingNode();
		
		messagingNode.createConnectionToTheRegistry(registryIPAddress, registryPortNumber);
		messagingNode.sendRegisterMessage();
		messagingNode.waitForInstructions();
		
	}
	
	private void createConnectionToTheRegistry(String registryIPAddress, int registryPortNumber) {
		try {
			Socket socketToTheRegistry = new Socket(registryIPAddress, registryPortNumber);
			myIPAddress = socketToTheRegistry.getLocalAddress().getHostName();
			myPortNumber = socketToTheRegistry.getLocalPort();
			
			connectionToTheRegistry = new TCPConnection(this, socketToTheRegistry); 
			
			connectionToTheRegistry.beginConnection();
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	private void sendRegisterMessage() {
		//Creating new message
		Register registerMessage = new Register(protocol.getNumOfMessageType("REGISTER_REQUEST"), myIPAddress, myPortNumber); 
		
		//Sending the message
		try {
			
			byte[] bytesToSend = registerMessage.getBytes();
			
			connectionToTheRegistry.sendMessageToNode(bytesToSend);

		}catch(IOException e) {
			System.out.println(e);
		}
	}
	
	private void waitForInstructions() {
		
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			
			if(scanner.hasNext()) {
				String instruction = scanner.nextLine();

				switch(instruction) {
				case "print-shortest-path":
					System.out.println("Printing shortest path");
					//carrot–-8––broccoli––4––-zucchini––-2––brussels––1––onion
					break;
				case "exit-overlay":
					sendDeregisterMessage();
					break;
				default:
					System.out.println("Invalid command. Valid commands include: \"print-shortest-path\" and \"exit-overlay\"");
				}
			}
		}
	}
	
	public void onEvent(Event event, Socket socket) {

		switch(event.getType()) {
		case 2:
			//Registration Response
			readRegistrationResponse((RegistrationResponse) event); 
			break;
		case 4:
			//This is a messaging nodes list 
			System.out.println("Received a messaging nodes list");
			createConnectionsToNodes();
			break;
		case 10:
			//Deregistration Response
			readDeregistrationResponse((DeregistrationResponse) event);
		}
	}
	
	@Override
	public void addNewConnection(TCPConnection connection) {
		// TODO Auto-generated method stub
		
	}
	
	private void readDeregistrationResponse(DeregistrationResponse response) {
		System.out.println(response.getAdditionalInfo());
		System.exit(0);		
	}
	
	private void readRegistrationResponse(RegistrationResponse response) {
		System.out.println(response.getAdditionalInfo());
		
		//If registration fails, exit
		if(response.getStatusCode() == 0) {						
			System.exit(0);
		}
	}

	private void sendDeregisterMessage() {
		//Creating new message
		Deregister deregisterMessage = new Deregister(protocol.getNumOfMessageType("DEREGISTER_REQUEST"), myIPAddress, myPortNumber);
		
		//Sending the message
		try {
			byte[] bytesToSend = deregisterMessage.getBytes();
			
			connectionToTheRegistry.sendMessageToNode(bytesToSend);
			
		}catch(IOException e) {
			System.out.println(e);
		}
	}

	private void createConnectionsToNodes() {	
	}


}
