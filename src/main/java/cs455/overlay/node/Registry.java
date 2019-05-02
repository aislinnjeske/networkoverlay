package cs455.overlay.node;

import cs455.overlay.transport.*;
import cs455.overlay.util.*;
import cs455.overlay.wireformats.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class Registry implements Node {

	private static String[] weightedOverlay;
	private static ArrayList<OverlayNode> registeredNodes;
	private static ServerSocket serverSocket;
	private static Protocol protocol;
	private static StatisticsCollectorAndDisplay statsCollector;
	private static int nodesDoneWithRounds;
	private static int numberOfReceivedSummaries;
	
	public static void main(String[] args) {

		registeredNodes = new ArrayList<>();
		protocol = new Protocol();
		nodesDoneWithRounds = 0;
		numberOfReceivedSummaries = 0;
		
		int myPortNumber = Integer.parseInt(args[0]);
		
		Registry registry = new Registry();
		
		registry.createServerSocket(myPortNumber);
		registry.waitForNodeConnections();
		registry.waitForInstructions();

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
					
					if(isValidNumberOfConnections(numberOfConnections)) {
						
						ArrayList<OverlayNode> unweightedOverlay = createOverlay(numberOfConnections);
						sendMessagingNodesList(unweightedOverlay);
						
					} else {
						System.out.printf("Please enter a number of connections less than %d.\n", registeredNodes.size());
					}
					
					break;
				case "send-overlay-link-weights":
					sendLinkWeights();
					break;
				case "list-weights":
					listLinkWeights();
					break;
				case "list-messaging-nodes":
					listRegisteredNodes();
					break;
				case "start":
					if(lineScan.hasNextInt()) {
						int numberOfRounds = lineScan.nextInt();
						sendTaskInitiate(numberOfRounds);
					} else {
						System.out.println("Usage: start number_of_rounds \n For example: start 25000 \n");
					}
					break;
				default:
					System.out.println("Invalid command");
				}
				
				lineScan.close();
			}
		}
	}
	
	private void sendTaskInitiate(int numberOfRounds) {	
		TaskInitiate message = new TaskInitiate(protocol.getNumOfMessageType("TASK_INITIATE"), numberOfRounds);
		sendMessageToAllNodes(message);
	}
	
	private boolean isValidNumberOfConnections(int numberOfConnections) {
		return numberOfConnections < registeredNodes.size();
	}
	
	private ArrayList<OverlayNode> createOverlay(int numberOfConnections) {
		OverlayCreator overlayCreator = new OverlayCreator(registeredNodes, numberOfConnections);

		overlayCreator.createOverlay();
		
		weightedOverlay = overlayCreator.getWeightedOverlay();
		
		return overlayCreator.getConnectionsForEachNode();
	}
	
	private void sendMessagingNodesList(ArrayList<OverlayNode> unweightedOverlay) {
		for(OverlayNode node : unweightedOverlay) {
			
			MessagingNodesList message = createMessagingNodesList(node);
			sendMessage(message, node.getSocket());
		}
	}
	
	private MessagingNodesList createMessagingNodesList(OverlayNode node) {
		int messageType = protocol.getNumOfMessageType("MESSAGING_NODES_LIST");
		
		Set<OverlayNode> connections = node.getConnections();
		int numberOfPeerNodes = connections.size();
		
		String[] peerMessagingNodes = new String[connections.size()];
		int counter = 0;
		
		for(OverlayNode connectedNode : connections) {
			String nodeName = connectedNode.getNameToSend();
			peerMessagingNodes[counter++] = nodeName;
		}
		
		return new MessagingNodesList(messageType, numberOfPeerNodes, peerMessagingNodes);
	}
	
	private void sendMessage(Event message, Socket socket) {
		try {
			
			OverlayNode node = new OverlayNode(socket);
			node.sendMessageToThisNode(message.getBytes());
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	private void sendLinkWeights() {
		Event message = createLinkWeightsMessage();
		sendMessageToAllNodes(message);
	}
	
	private Event createLinkWeightsMessage() {
		int messageType = protocol.getNumOfMessageType("LINK_WEIGHTS");

		return new LinkWeights(messageType, weightedOverlay.length, weightedOverlay);
	}
	
	private void sendMessageToAllNodes(Event message) {
		for(OverlayNode node : registeredNodes) {
			sendMessage(message, node.getSocket());
		}
	}
	
	private void listLinkWeights() {
		for(int i = 0; i < weightedOverlay.length; i++) {
			System.out.println(weightedOverlay[i]);
		}
	}
	
	private void listRegisteredNodes() {
		for(OverlayNode node : registeredNodes) {
			System.out.println(node.getNameToSend());
		}
	}
	
	public void onEvent(Event event, Socket socket) {
		
		switch(event.getType()) {
		case 1:
			registerNode((Register) event, socket);
			break;
		case 3:
			deregisterNode((Deregister) event, socket);
			break;
		case 7:
			incrementNodesDoneWithRounds();
			break;
		case 9:
			incrementSummariesReceived((TrafficSummary) event);
			break;
		}
	}
	
	private synchronized void registerNode(Register nodeToBeRegistered, Socket socket) {
		try {
			if(isValidRegisterRequest(nodeToBeRegistered, socket)) {
				OverlayNode nodeToBeAdded = new OverlayNode(socket, nodeToBeRegistered.getHostname(), nodeToBeRegistered.getPortNumber());
				addNodeToOverlay(nodeToBeAdded);
				sendSuccessfulRegistrationResponse(socket);
			} else {
				String reasonForFailure = getReasonForRegistrationFailure(nodeToBeRegistered, socket);
				sendFailureResponse(reasonForFailure, socket);
			}
		} catch(IOException e) {
			System.out.println(e);
		}		
	}
	
	private boolean isValidRegisterRequest(Register nodeToBeRegistered, Socket socket) {
		String hostnameSent = nodeToBeRegistered.getHostname(); 
		String hostnameOfSender = socket.getInetAddress().getHostName();
		
		boolean hasMatchingHostname = hostnameSent.equals(hostnameOfSender);
		boolean isNotCurrentlyRegistered = nodeEntryInOverlay(hostnameSent, nodeToBeRegistered.getPortNumber()) == -1;
		
		return  hasMatchingHostname && isNotCurrentlyRegistered ;
	}
	
	private int nodeEntryInOverlay(String IPAddress, int portNumber) {
		for(int i = 0; i < registeredNodes.size(); i++) {
			OverlayNode connection = registeredNodes.get(i);
			
			if(connection.getHostname().equals(IPAddress) && connection.getPortNumber() == portNumber) {
				return i; 
			}
		}
		
		return -1;
	}
	
	private synchronized void addNodeToOverlay(OverlayNode nodeToBeAdded) {
		registeredNodes.add(nodeToBeAdded);
	}
	
	private void sendSuccessfulRegistrationResponse(Socket socket) {
		byte statusCode = 1;
		String additionalInfo = "Registration successful. The number of messaging nodes currently constituting the overlay is " + registeredNodes.size();
		
		Response response = new Response(protocol.getNumOfMessageType("RESPONSE"), statusCode, additionalInfo);
		sendMessage(response, socket);
	}
	
	private String getReasonForRegistrationFailure(Register nodeToBeRegistered, Socket socket) {
		String hostnameSent = nodeToBeRegistered.getHostname(); 
		String hostnameOfSender = socket.getInetAddress().getHostName();
		
		if(!hostnameSent.equals(hostnameOfSender)) {
			return "Registration request FAILED. Cannot register " + hostnameSent + " with a request sent from " + hostnameOfSender;
		} else {
			return "Registration request FAILED. Node is already registered.";
		}
	}
	
	private void sendFailureResponse(String reasonForFailure, Socket socket) {
		byte statusCode = 0;
		
		Response response = new Response(protocol.getNumOfMessageType("RESPONSE"), statusCode, reasonForFailure);
		sendMessage(response, socket);
	}
	
	private synchronized void deregisterNode(Deregister nodeToBeDeregistered, Socket socket) {
		int nodeIndexInOverlay = nodeEntryInOverlay(nodeToBeDeregistered.getNodeHostname(), nodeToBeDeregistered.getNodePortNumber());
		
		if(isValidDeregisterRequest(nodeToBeDeregistered, socket, nodeIndexInOverlay)) {
			removeNodeFromOverlay(nodeIndexInOverlay);
		} else {
			String reasonForFailure = getReasonForDeregistrationFailure(nodeToBeDeregistered, socket);
			sendFailureResponse(reasonForFailure, socket);
		}
	}
	
	private boolean isValidDeregisterRequest(Deregister nodeToBeDeregistered, Socket socket, int indexInOverlay) {
		String hostnameSent = nodeToBeDeregistered.getNodeHostname(); 
		String hostnameOfSender = socket.getInetAddress().getHostName();
		
		boolean hasMatchingHostname = hostnameSent.equals(hostnameOfSender);
		boolean isCurrentlyRegistered = indexInOverlay != -1;
		
		return hasMatchingHostname && isCurrentlyRegistered;
	}
	
	private synchronized void removeNodeFromOverlay(int nodeIndex) {
		registeredNodes.remove(nodeIndex);
	}
	
	private String getReasonForDeregistrationFailure(Deregister nodeToBeDeregistered, Socket socket) {
		String hostnameSent = nodeToBeDeregistered.getNodeHostname(); 
		String hostnameOfSender = socket.getInetAddress().getHostName();
		
		if(!hostnameSent.equals(hostnameOfSender)) {
			return "Deregistration request FAILED. Cannot register " + hostnameSent + " with a request sent from " + hostnameOfSender;
		} else {
			return "Deregistration request FAILED. Node is not currently registered.";
		}
	}

	private synchronized void incrementNodesDoneWithRounds() {
		nodesDoneWithRounds++;
		
		if(nodesDoneWithRounds == registeredNodes.size()) {
			nodesDoneWithRounds = 0;
			sendPullTrafficSummary();
			createStatsCollector();
		}
	}
	
	private void sendPullTrafficSummary() {
		try {
			Thread.sleep(15000);
			PullTrafficSummary message = new PullTrafficSummary(protocol.getNumOfMessageType("PULL_TRAFFIC_SUMMARY"));
			sendMessageToAllNodes(message);
			
		} catch(InterruptedException e) {
			System.out.println(e);
			System.exit(0);
		}
	}
	
	private void createStatsCollector() {
		statsCollector = new StatisticsCollectorAndDisplay(registeredNodes.size());
	}
	
	private synchronized void incrementSummariesReceived(TrafficSummary message) {
		numberOfReceivedSummaries++;
		readTrafficSummary(message);
		
		if(numberOfReceivedSummaries == registeredNodes.size()) {
			numberOfReceivedSummaries = 0;
			displayStats();
		}
	}
	
	private void readTrafficSummary(TrafficSummary message) {
		statsCollector.addNodeStatisticsToSummary(message.getNameToSend(), message.getMessagesSent(),  message.getSumOfMessagesSent(), message.getMessagesReceived(), message.getSumOfMessagesReceived(), message.getNumberOfMessagesRelayed());
	}
	
	private void displayStats() {
		statsCollector.printDisplay();
	}
}

