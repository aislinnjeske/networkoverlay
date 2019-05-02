package cs455.overlay.node;

import cs455.overlay.transport.TCPSender;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class OverlayNode {
	
	private String hostname;
	private int portNumber;
	private Socket socketToTheNode;
	private TCPSender messageSender;
	private Set<OverlayNode> connections;
	
	public OverlayNode(Socket socketToTheNode) throws IOException {
		this.socketToTheNode = socketToTheNode;
		messageSender = new TCPSender(socketToTheNode);
		connections = new HashSet<>();
		portNumber = socketToTheNode.getPort();
		hostname = socketToTheNode.getInetAddress().getHostName();
	}
	
	public OverlayNode(Socket socketToTheNode, String hostname, int portNumber) throws IOException{
		this.socketToTheNode = socketToTheNode;
		this.hostname = hostname;
		this.portNumber = portNumber;
		messageSender = new TCPSender(socketToTheNode);
		connections = new HashSet<>();
	}
	
	public boolean isSameHostname(String hostname) throws UnknownHostException {
		return socketToTheNode.getInetAddress().getHostName().equals(hostname);
	}
	
	public boolean isSamePortNumber(int portNumber) {
		return socketToTheNode.getLocalPort() == portNumber;
	}

	public int getPortNumber() {
		return portNumber;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void sendMessageToThisNode(byte[] messageToSend) throws IOException {
		messageSender.sendData(messageToSend);
	}
	
	public boolean contains(OverlayNode node) {
		return connections.contains(node);
	}
	
	public Set<OverlayNode> getConnections(){
		return connections;
	}
	
	public void addConnection(OverlayNode node) {
		connections.add(node);
	}
	
	public Socket getSocket() {
		return socketToTheNode;
	}
	
	public String getNameToSend() {
		return hostname + ":" + portNumber;
	}
	
	public boolean equals(OverlayNode other) {
		return other.getHostname().equals(this.getHostname()) && other.getPortNumber() == this.getPortNumber();
	}
}
