package cs455.overlay.node;

import cs455.overlay.dijkstra.*;
import cs455.overlay.transport.*;
import cs455.overlay.wireformats.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class MessagingNode implements Node {
	
	public static String myHostname; 
	public static int myPortNumber;
	private static OverlayNode registry;
	private static ServerSocket serverSocket;
	private static RoutingCache cache;
	private static Protocol protocol;
	private static Random random;
	private static ShortestPath shortestPath;
	private static ArrayList<OverlayNode> connectedNodes;
	private static ArrayList<WeightedNode> overlay;
	
	private static int numberOfMessagesSent;
	private static long sumOfMessagesSent;
	private static int numberOfMessagesReceived;
	private static long sumOfMessagesReceived;
	private static int numberOfMessagesRelayed; 
	
	public MessagingNode() {
		protocol = new Protocol();
		random = new Random();
		connectedNodes = new ArrayList<>();
		overlay = new ArrayList<>();
		
		numberOfMessagesSent = 0;
		sumOfMessagesSent = 0;
		numberOfMessagesReceived = 0;
		sumOfMessagesReceived = 0;
		numberOfMessagesRelayed = 0;
	}
	
	public static void main(String[] args) {
		
		String registryHostname = args[0];
		int registryPortNumber = Integer.parseInt(args[1]);
		
		MessagingNode messagingNode = new MessagingNode();
		
		messagingNode.createConnectionToTheRegistry(registryHostname, registryPortNumber);
		messagingNode.createServerSocket(9000);
		messagingNode.waitForNodeConnections();
		
		messagingNode.sendRegisterMessage();
		messagingNode.waitForInstructions();
		
	}
	
	private void createConnectionToTheRegistry(String registryHostname, int registryPortNumber) {
		try {
			Socket socketToTheRegistry = new Socket(registryHostname, registryPortNumber);
			registry = new OverlayNode(socketToTheRegistry);
			
			myHostname = socketToTheRegistry.getLocalAddress().getHostName();
			
			Thread registryReceiverThread = new Thread(new TCPReceiverThread(this, socketToTheRegistry));
			registryReceiverThread.start();
			
		} catch (IOException e) {
			System.out.println(e);
			System.exit(0);
		}
	}
	
	private void createServerSocket(int portNumber) {
		try {
			serverSocket = new ServerSocket(portNumber);
			myPortNumber = serverSocket.getLocalPort();
			
		} catch (Exception e) {
			createServerSocket(++portNumber);
		}
	}
	
	private void waitForNodeConnections() {
		Thread serverThread = new Thread(new TCPServerThread(this, serverSocket));
		serverThread.start();
	}

	private void sendRegisterMessage() {
		Register registerMessage = new Register(protocol.getNumOfMessageType("REGISTER_REQUEST"), myHostname, myPortNumber); 
		sendMessage(registerMessage, registry);
	}
	
	private void sendMessage(Event message, OverlayNode receiver) {
		try {
			byte[] bytesToSend = message.getBytes();
			
			receiver.sendMessageToThisNode(bytesToSend);
		} catch (IOException e) {
			System.out.println(e);
			System.exit(0);
		}
	}
	
	private void waitForInstructions() {
		
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			if(scanner.hasNext()) {
				String instruction = scanner.nextLine();

				switch(instruction) {
				case "print-shortest-path":
					printShortestPath();
					break;
				case "exit-overlay":
					scanner.close();
					sendDeregisterMessage();
					System.exit(0);
					break;
				default:
					System.out.println("Invalid command. Valid commands include: \"print-shortest-path\" and \"exit-overlay\"");
				}
			}
		}
	}
	
	private void printShortestPath() {
		Map<String, String> routesFromCache = cache.getRoutesFromCache();

		for(String destination : routesFromCache.keySet()) {
			System.out.println(routesFromCache.get(destination));
		}
	}
	
	private void sendDeregisterMessage() {
		Deregister deregisterMessage = new Deregister(protocol.getNumOfMessageType("DEREGISTER_REQUEST"), myHostname, myPortNumber);
		
		sendMessage(deregisterMessage, registry);
	}
	
	public void onEvent(Event event, Socket socket) {
		switch(event.getType()) {
		case 1:
			receivedNodeConnection((Register) event, socket);
			break;
		case 2:
			readResponse((Response) event); 
			break;
		case 4:
			makeAllNodeConnections((MessagingNodesList) event);
			break;
		case 5:
			readLinkWeights((LinkWeights) event);
			calculateShortestPaths(myHostname + ":" + myPortNumber);
			cacheRoutes();
			break;
		case 6:
			startRounds((TaskInitiate) event);
			sendTaskCompleteMessage();
			break;
		case 8:
			sendTrafficSummary();
			resetMessageCounters();
			break;
		case 10:
			readRoundsMessage((RoundsMessage) event);
			break;
		}
	}

	private synchronized void receivedNodeConnection(Register message, Socket socket) {
		try {
			OverlayNode node = new OverlayNode(socket, message.getHostname(), message.getPortNumber());
			connectedNodes.add(node);
		} catch(IOException e) {
			System.out.println(e);
			System.exit(0);
		}
	}
	
	private void readResponse(Response response) {
		System.out.println(response.getAdditionalInfo());
		
		//If registration/deregistration fails, exit
		if(response.getStatusCode() == 0) {						
			System.exit(0);
		}
	}

	private void makeAllNodeConnections(MessagingNodesList message) {
		String[] nodesToConnectTo = message.getPeerNodes();
		
		for(int i = 0; i < nodesToConnectTo.length; i++) {
			OverlayNode node = createOverlayNode(nodesToConnectTo[i]);
			connectedNodes.add(node);
			sendMessageToConnectedNode(node);
		}
		System.out.printf("All connections are established. Number of connections: %d\n", nodesToConnectTo.length);
	}
	
	private OverlayNode createOverlayNode(String hostNameAndPortNumber) {
		int indexOfDelimitor = hostNameAndPortNumber.indexOf(':');
		String hostName = hostNameAndPortNumber.substring(0, indexOfDelimitor);
		int portNumber = Integer.parseInt(hostNameAndPortNumber.substring(indexOfDelimitor + 1));
		
		try {
			Socket socketToTheNode = new Socket(hostName, portNumber); 
			Thread receiverThread = new Thread(new TCPReceiverThread(this, socketToTheNode));
			receiverThread.start();
			
			return new OverlayNode(socketToTheNode, hostName, portNumber);
			
		} catch (IOException e) {
			System.out.println(e);
			System.exit(0);
			return null;
		}
		
	}
	
	private void sendMessageToConnectedNode(OverlayNode node) {
		Register message = new Register(protocol.getNumOfMessageType("REGISTER_REQUEST"), myHostname, myPortNumber);
		sendMessage(message, node);
	}
	
	private void readLinkWeights(LinkWeights message) {
		String[] allLinks = message.getLinks();
		
		for(int i = 0; i < allLinks.length; i++) {
			
			Scanner stringScan = new Scanner(allLinks[i]);
			
			//Reading assuming the message is in the correct format
			String firstNodeString = stringScan.next();
			String secondNodeString = stringScan.next();
			int weight = stringScan.nextInt();
			
			stringScan.close();
			
			int firstNodeIndex = findNodeIndexInOverlay(firstNodeString);
			int secondNodeIndex = findNodeIndexInOverlay(secondNodeString);
			
			if(firstNodeIndex != -1 && secondNodeIndex != -1) {
				
				overlay.get(firstNodeIndex).addAdjacentNode(overlay.get(secondNodeIndex), weight);
				overlay.get(secondNodeIndex).addAdjacentNode(overlay.get(firstNodeIndex), weight);
				
			} else if(firstNodeIndex == -1 && secondNodeIndex != -1) {
				
				WeightedNode firstNode = new WeightedNode(firstNodeString);
				firstNode.addAdjacentNode(overlay.get(secondNodeIndex), weight);
				overlay.add(firstNode);
				firstNodeIndex = findNodeIndexInOverlay(firstNodeString);
				
				overlay.get(secondNodeIndex).addAdjacentNode(overlay.get(firstNodeIndex), weight);
			
			} else if(firstNodeIndex != -1 && secondNodeIndex == -1) {
				
				WeightedNode secondNode = new WeightedNode(secondNodeString);
				secondNode.addAdjacentNode(overlay.get(firstNodeIndex), weight);
				overlay.add(secondNode);
				secondNodeIndex = findNodeIndexInOverlay(secondNodeString);
				
				overlay.get(firstNodeIndex).addAdjacentNode(overlay.get(secondNodeIndex), weight);
				 
			} else {
				
				WeightedNode firstNode = new WeightedNode(firstNodeString);
				WeightedNode secondNode = new WeightedNode(secondNodeString);
				
				overlay.add(firstNode);
				overlay.add(secondNode);
				
				firstNodeIndex = findNodeIndexInOverlay(firstNodeString);
				secondNodeIndex = findNodeIndexInOverlay(secondNodeString);
				
				overlay.get(firstNodeIndex).addAdjacentNode(overlay.get(secondNodeIndex), weight);
				overlay.get(secondNodeIndex).addAdjacentNode(overlay.get(firstNodeIndex), weight);
			}
		}
		System.out.println("Link weights are received and processed. Ready to send messages");
	}
	
	private int findNodeIndexInOverlay(String otherNodeName) {
		for(int i = 0; i < overlay.size(); i++) {
			WeightedNode node = overlay.get(i);
			
			if(otherNodeName.equals(node.getNameToSend())) {
				return i;
			}
		}
		return -1;
	}

	private void calculateShortestPaths(String source) {
		shortestPath = new ShortestPath(overlay);
		shortestPath.calculateShortestPaths(source);
	}

	private void cacheRoutes() {
		Set<WeightedNode> paths = shortestPath.getShortestPaths();
		
		cache = new RoutingCache(paths);
		
		cache.cacheRoutes(myHostname, myPortNumber);
	}

	private void startRounds(TaskInitiate event) {
		int numberOfRounds = event.getNumberOfRounds();
		
		for(int i = 0; i < numberOfRounds; i++) {
			String destination = getRandomDestination();
			String pathToDestination = cache.getPathToSend(destination);
			String firstStop = getNextStop(pathToDestination);

			//Each node sends 5 messages per round
			for(int j = 0; j < 5; j++) {
				RoundsMessage message = createRoundsMessage(pathToDestination);
				
				sendRoundsMessage(message, firstStop);
				sentMessage();
				sentMessageCheckSum(message.getRandomNumber());
			}
		}
	}
	
	private String getRandomDestination() {
		Random randomIndex = new Random(); 
		
		int indexOfDestination = randomIndex.nextInt(overlay.size()); 
		String nameOfDestination = overlay.get(indexOfDestination).getNameToSend();
		
		if(nameOfDestination.equals(myHostname + ":" + myPortNumber)) {
			return getRandomDestination();
		} else {
			return nameOfDestination;
		}
	}
	
	private synchronized String getNextStop(String path) {
		Scanner scanner = new Scanner(path);
		
		while(scanner.hasNext()) {
			String hop = scanner.next();
			
			//If this is the destination, return it as the next stop
			if(!scanner.hasNext()) {
				scanner.close();
				return hop;
			}
			
			//When I find myself in the hop path, return the next hop
			if(hop.equals(myHostname + ":" + myPortNumber)) {
				String next = scanner.next();
				scanner.close();
				return next;
			}
		}
		scanner.close();
		return null;
	}
	
	private RoundsMessage createRoundsMessage(String pathToDestination) {
		int randomNumber = getRandomNumber();
		return new RoundsMessage(protocol.getNumOfMessageType("ROUNDS_MESSAGE"), randomNumber, pathToDestination);
	}
	
	private int getRandomNumber() {
		return random.nextInt();
	}
	
	public void sendRoundsMessage(RoundsMessage message, String stop) {
		for(OverlayNode node : connectedNodes) {
			if(stop.equals(node.getNameToSend())) {
				sendMessage(message, node);
			}
		}
	}
	
	private void readRoundsMessage(RoundsMessage message) {
		String pathToDestination = message.getPath();
		String nextStop = getNextStop(pathToDestination);
		
		if(nextStop.equals(myHostname + ":" + myPortNumber)) {
			addPayload(message);
		} else {
			relayRoundsMessage(message, nextStop);
		}
		
	}
	
	private void addPayload(RoundsMessage message) {
		receivedMessage();
		receivedMessageCheckSum(message.getRandomNumber());
	}
	
	private void relayRoundsMessage(RoundsMessage message, String nextStop) {
		relayedMessage();
		sendRoundsMessage(message, nextStop);
	}
	
	private void sendTaskCompleteMessage() {
		TaskComplete message = new TaskComplete(protocol.getNumOfMessageType("TASK_COMPLETE"), myHostname, myPortNumber);
		sendMessage(message, registry);
	}
	
	public synchronized void receivedMessage() {
		numberOfMessagesReceived++;
	}
	
	public synchronized void receivedMessageCheckSum(int valueReceived) {
		sumOfMessagesReceived += valueReceived;
	}
	
	private void sentMessage() {
		numberOfMessagesSent++;
	}
	
	private void sentMessageCheckSum(int valueSent) {
		sumOfMessagesSent += valueSent;
	}
	
	public synchronized void relayedMessage() {
		numberOfMessagesRelayed++;
	}
	
	private void resetMessageCounters() {
		numberOfMessagesReceived = numberOfMessagesSent = numberOfMessagesRelayed = 0;
		sumOfMessagesReceived = sumOfMessagesSent = 0;
	}
	
	private void sendTrafficSummary() {
		TrafficSummary summary = new TrafficSummary(protocol.getNumOfMessageType("TRAFFIC_SUMMARY"), myHostname, myPortNumber, numberOfMessagesSent, sumOfMessagesSent,
				                                    numberOfMessagesReceived, sumOfMessagesReceived, numberOfMessagesRelayed);
		sendMessage(summary, registry);
	}
}
