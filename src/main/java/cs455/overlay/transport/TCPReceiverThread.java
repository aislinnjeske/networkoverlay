package cs455.overlay.transport;

import cs455.overlay.node.Node;
import cs455.overlay.wireformats.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread implements Runnable {
	
	private Node node;
	private Socket socket;
	private DataInputStream dataInputStream;
	private EventFactory eventFactory = EventFactory.getInstance();
	
	public TCPReceiverThread(Node node, Socket socket) throws IOException {
		this.node = node; 
		this.socket = socket;
		this.dataInputStream = new DataInputStream(socket.getInputStream());
	}

	public void run() {
		int incomingMsgLength; 

		//While our socket is still open
		while(socket != null) {
			
			try {	
				//Only read if there is something to read
				if(dataInputStream.available() != 0) {
					incomingMsgLength = dataInputStream.readInt();
					
					byte[] incomingMsg = new byte[incomingMsgLength];
					dataInputStream.readFully(incomingMsg, 0, incomingMsgLength);
					
					Event eventReceived = eventFactory.createEvent(incomingMsg); 
					node.onEvent(eventReceived, socket);
				}
			} catch (SocketException se) {
				System.out.println("Socket exception in TCP receiver thread");
				break;
				
			} catch (IOException e) {
				System.out.println("IOException in TCP receiver thread");
				break;
			}
		}
		
	}

}
