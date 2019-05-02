package cs455.overlay.transport;

import java.io.*;
import java.net.Socket;

public class TCPSender {
	
	private DataOutputStream outputStream;
	
	public TCPSender(Socket socket) throws IOException {
		outputStream = new DataOutputStream(socket.getOutputStream()); 
	}
	
	public synchronized void sendData(byte[] msgToSend) throws IOException {
		int msgToSendLength = msgToSend.length; 

		outputStream.writeInt(msgToSendLength);
		outputStream.write(msgToSend, 0, msgToSendLength);
		outputStream.flush();
	}
	
}
