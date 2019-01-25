package cs455.overlay.node;
import java.io.*;
import java.net.*;


public class Server {
	static Integer ourPort = 5001;
	
	public static void main(String[] args) throws IOException {
		Integer numConnections = 10;
		
		//What we will use to listen for requests & will make a new socket for communication
		ServerSocket ourServerSocket = null; 
		
		try {
			ourServerSocket = new ServerSocket(ourPort, numConnections); 
			
		} catch(IOException e) {
			System.out.println("Creation of server socket failed");
		}
		
		try {
			//Blocking call; waits for a request and then returns a socket for us to use
			Socket incomingConnectionSocket = ourServerSocket.accept(); 
			
			System.out.println("We've received a connection");
			
			//We have yet to block again, so we can handle this connection however we want to
			//Let's send a message and wait for the response
			
			
			//Creating these so we can send messages and read messages once we've sent them
			DataOutputStream outputStream = new DataOutputStream(incomingConnectionSocket.getOutputStream()); //for sending
			DataInputStream inputStream = new DataInputStream(incomingConnectionSocket.getInputStream()); //for reading
			
			
			//Our messages should be byte[]
			//Sending a message
			String message = "What day of the week is it.";
			byte[] messageToClient = message.getBytes();
			Integer messageToClientLength = messageToClient.length; 
			
			//Send the length of the message first
			outputStream.writeInt(messageToClientLength);
			
			//Then send message
			//TODO look up what these parameters are!
			outputStream.write(messageToClient, 0, messageToClientLength);
			
			
			//Waiting for a response
			Integer recievedMsgLength = 0;
			
			//Try to read from the stream; blocking call if there is nothing to read
			recievedMsgLength = inputStream.readInt();
			
			//If we get here, that means we recieved the length of the message
			System.out.println("Length of the message recieved is " + recievedMsgLength);
			
			//Try to read actual message now
			byte[] recievedMsg = new byte[recievedMsgLength];
			
			//Read full message; readFully will read exactly recievedMsgLength number of bytes so use this
			//TODO is this a blocking call?
			inputStream.readFully(recievedMsg, 0, recievedMsgLength); 
			
			System.out.println("Message recieved: " + new String(recievedMsg));
			
			
			
			//Close streams and then sockets
			inputStream.close();
			outputStream.close();
			incomingConnectionSocket.close();
			ourServerSocket.close();
		} catch (IOException e) {
			System.out.println("Something else failed");
		}
		
	}

}
