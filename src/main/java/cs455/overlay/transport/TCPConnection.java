package cs455.overlay.transport;

import cs455.overlay.node.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConnection {
	
	private Node node;
	private Socket socketToTheNode;
	private Thread messageReceiverThread;
	private TCPSender messageSender;
	
	public TCPConnection(Node node, Socket socketToTheNode) throws IOException{
		this.node = node;
		this.socketToTheNode = socketToTheNode;
		messageSender = new TCPSender(socketToTheNode); 
	}
	
	public void beginConnection() throws IOException {
		startReceiverThread();
	}
	
	private void startReceiverThread() throws IOException {
		messageReceiverThread = new Thread(new TCPReceiverThread(node, socketToTheNode));
		messageReceiverThread.start();
	}

	public boolean isSameIPAddress(String IPAddress) throws UnknownHostException {
//		System.out.println(socketToTheNode.getInetAddress().getHostName());
		return socketToTheNode.getInetAddress().getHostName().equals(IPAddress);
	}
	
	public boolean isSamePortNumber(int portNumber) {
//		System.out.println(socketToTheNode.getInetAddress());
		return socketToTheNode.getLocalPort() == portNumber;
	}
	
	public int getNodePortNumber() {
		return socketToTheNode.getPort();
	}
	
	public String getNodeHostName() {
		return socketToTheNode.getInetAddress().getHostName();
	}
	
	public void sendMessageToNode(byte[] messageToSend) throws IOException {
			messageSender.sendData(messageToSend);
	}
	

}
