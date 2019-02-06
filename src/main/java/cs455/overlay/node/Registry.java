package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Registry implements Node {

	private static ArrayList<TCPConnection> messagingNodeConnections;
	private static ArrayList<TCPConnection> registeredNodes;
	private static ServerSocket serverSocket;
	private static Protocol protocol;
	
	public static void main(String[] args) {
		messagingNodeConnections = new ArrayList<>();
		registeredNodes = new ArrayList<>();
		protocol = new Protocol();
		
		int myPortNumber = Integer.parseInt(args[0]);
		
		Registry registry = new Registry();
		
		registry.createServerSocket(myPortNumber);
		registry.waitForNodeConnections();
		registry.waitForInstructions();

	}
	
	private void waitForInstructions() {
		
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			
			if(scanner.hasNext()) {
				String commandLineInput = scanner.nextLine();
				
				Scanner lineScan = new Scanner(commandLineInput);
				
				String instruction = lineScan.next();
				
				switch(instruction) {
				case "setup-overlay":
					int numberOfConnections = lineScan.nextInt(); 
					setupOverlay(numberOfConnections);
					break;
				case "send-overlay-link-weights":
					//send link weights message to all nodes in overlay
					break;
				case "list-weights":
					//call method to list information about all the links and weights
					break;
				case "list-messaging-nodes":
					listMessagingNodes();
					break;
				case "start":
					//read in the number of rounds
					//send task-initiate to all nodes
					scanner.close();
					System.exit(0);
					break;
				default:
					System.out.println("Invalid command");
				}
				
				lineScan.close();
			}
		}
	}
	
	private void listMessagingNodes() {
		for(TCPConnection connection : registeredNodes) {
			System.out.println(connection.getNodeHostName() + ":" + connection.getNodePortNumber());
		}
	}
	
	private void setupOverlay(int numberOfConnections) {
		if(numberOfConnections > registeredNodes.size()) {
			System.out.printf("Number of connections must be less than %d.\n", registeredNodes.size());
		} else {
			createOverlay(numberOfConnections);
		}
	}
	
	private void createOverlay(int numberOfConnections) {
		OverlayCreator overlayCreator = new OverlayCreator(numberOfConnections, registeredNodes.size());
		
		ArrayList<OverlayNode> overlay = overlayCreator.createNewOverlay();
		
		//Call OverlayCreator in util package
		//Send messaging node list messages to every node
	}
	
	private void createServerSocket(int portNumber) {
		
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}
		
	}
	
	private  void waitForNodeConnections() {
		
		Thread serverThread = new Thread(new TCPServerThread(this, serverSocket));
		serverThread.start();

	}

	public void onEvent(Event event, Socket socket) {
		
		switch(event.getType()) {
		case 1:
			registerNode((Register) event, socket);
			break;
		case 3:
			deregisterNode((Deregister) event, socket);
			break;
		}
	}
	
	public void addNewConnection(TCPConnection newConnection) {
		messagingNodeConnections.add(newConnection);
	}
	
	private void registerNode(Register nodeToBeRegistered, Socket socket) {
		//Finding the correct TCP connection to send the register response to
		String IPAddressSent = nodeToBeRegistered.getNodeIPAddress(); 
		int portNumberSent = nodeToBeRegistered.getNodePortNumber();
		
		String IPAddressOfSender = socket.getInetAddress().getHostName();
		int portNumberOfSender = socket.getPort();
		
		try {
			if(isValidRequest(IPAddressSent, portNumberSent, IPAddressOfSender, portNumberOfSender)) {
				if(nodeEntryInOverlay(IPAddressSent, portNumberSent) == -1) {
					
					addNodeToOverlay(IPAddressSent, portNumberSent, socket);
					
					sendRegistrationResponse("SUCCESS", "Registration request successful. The number of messaging nodes currently constituting the overlay is " + registeredNodes.size(), socket);
					
				} else {
					sendRegistrationResponse("FAILURE", "Registration request FAILED. " + IPAddressSent + ":" + portNumberSent + " is already registered in the network.", socket);
				}

			} else {
				sendRegistrationResponse("FAILURE", "Registration request FAILED. Cannot register " + IPAddressSent + ":" + portNumberSent + " with a request sent from " + IPAddressOfSender + ":" + portNumberOfSender, socket);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
	
	private boolean isValidRequest(String IPAddressSent, int portSent, String IPRequestSender, int portRequestSender) {
		return IPAddressSent.equals(IPRequestSender) && portSent == portRequestSender;
	}
	
	private int nodeEntryInOverlay(String IPAddress, int portNumber) {
		for(int i = 0; i < registeredNodes.size(); i++) {
			TCPConnection connection = registeredNodes.get(i);
			
			if(connection.getNodeHostName().equals(IPAddress) && connection.getNodePortNumber() == portNumber) {
				return i; 
			}
		}
		
		return -1;
	}
	
	private void addNodeToOverlay(String IPAddress, int portNumber, Socket socket) {
		
		try {
			
			TCPConnection nodeConnection = new TCPConnection(this, socket);
			addNode(nodeConnection);
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	private synchronized void addNode(TCPConnection node) {
		registeredNodes.add(node);
	}
	
	private void sendRegistrationResponse(String registrationStatus, String additionalInfo, Socket socket) {
		byte statusCode;
		
		if(registrationStatus.equals("SUCCESS")) {
			statusCode = (byte) 1;
		} else {
			statusCode = (byte) 0;
		}
		
		RegistrationResponse message = new RegistrationResponse(protocol.getNumOfMessageType("REGISTRATION_RESPONSE"), statusCode, additionalInfo);
		sendMessage(message, socket);
	}
	
	private void sendMessage(Event message, Socket socket) {
		
		try {
			
			TCPConnection connection = new TCPConnection(this, socket);
			connection.sendMessageToNode(message.getBytes());
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	private void deregisterNode(Deregister nodeToBeDeregistered, Socket socket) {
		//Finding the correct TCP connection to send the register response to
		String IPAddressSent = nodeToBeDeregistered.getNodeIPAddress(); 
		int portNumberSent = nodeToBeDeregistered.getNodePortNumber();
		
		String IPAddressOfSender = socket.getInetAddress().getHostName();
		int portNumberOfSender = socket.getPort();
		
		try {
			if(isValidRequest(IPAddressSent, portNumberSent, IPAddressOfSender, portNumberOfSender)) {
				int nodeIndex = nodeEntryInOverlay(IPAddressSent, portNumberSent);
				
				//If the node is registered, remove it and send successful deregistration response
				if(nodeIndex != -1) {

					removeNodeFromOverlay(nodeIndex);
					
					sendDeregistrationResponse("SUCCESS", "Deregistration request successful. The number of messaging nodes currently constituting the overlay is " + registeredNodes.size(), socket);
					
				} else {
					
					//Invalid request because IP Address/Port number to be deregistered is not in the overlay
					sendDeregistrationResponse("FAILURE", "Deregistration request FAILED. " + IPAddressSent + ":" + portNumberSent + " is not registred in the network.", socket);
				}
				
			} else {
				//Invalid request because IP Address of sender != IP Address of node to be deregistered 
				sendDeregistrationResponse("FAILURE", "Deregistration request FAILED. Cannot deregister " + IPAddressSent + ":" + portNumberSent + " with a request sent from " + IPAddressOfSender + ":" + portNumberOfSender, socket);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private synchronized void removeNodeFromOverlay(int nodeIndex) {
		registeredNodes.remove(nodeIndex);
	}
	
	private void sendDeregistrationResponse(String registrationStatus, String additionalInfo, Socket socket) {
		byte statusCode;
		
		if(registrationStatus.equals("SUCCESS")) {
			statusCode = (byte) 1;
		} else {
			statusCode = (byte) 0;
		}
		
		DeregistrationResponse message = new DeregistrationResponse(protocol.getNumOfMessageType("DEREGISTRATION_RESPONSE"), statusCode, additionalInfo);
		sendMessage(message, socket); 
	}
	
}