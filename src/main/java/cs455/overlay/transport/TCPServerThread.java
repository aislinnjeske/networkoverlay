package cs455.overlay.transport;

import cs455.overlay.node.Node;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerThread implements Runnable{
	
	private ServerSocket serverSocket;
	private Node node;
	
	public TCPServerThread(Node node, ServerSocket serverSocket) {
		this.node = node;
		this.serverSocket = serverSocket;
	}
	

	public void run() {
		
		while(true) {
			try {
				
				//Blocking call
				Socket incomingConnectionSocket = serverSocket.accept();
				
				//Creates a receiverThread that will receive messages on the socket
				Thread receiverThread = new Thread(new TCPReceiverThread(node, incomingConnectionSocket));
				receiverThread.start();

			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

}
