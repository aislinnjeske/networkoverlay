package cs455.overlay.transport;

import java.io.*;
import java.net.Socket;

public class TCPSender {
	
	private Socket socket;
	private DataOutputStream outputStream;
	
	public TCPSender(Socket socket) throws IOException {
		this.socket = socket;
		outputStream = new DataOutputStream(socket.getOutputStream()); 
	}
	
	public void sendData(byte[] msgToSend) throws IOException {
		int msgToSendLength = msgToSend.length; 

		outputStream.writeInt(msgToSendLength);
		outputStream.write(msgToSend, 0, msgToSendLength);
		outputStream.flush();
	}
	
}
