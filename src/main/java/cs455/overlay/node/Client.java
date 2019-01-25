package cs455.overlay.node;
import java.io.*;
import java.net.*;

public class Client {
	
	public static void main(String[] args) throws IOException{
		String serverAddress = args[0];
		Integer serverPort = Integer.parseInt(args[1]);
		
		
		//Declare sockets and streams
		Socket socketToTheServer = null;
		DataInputStream inputStream = null;
		DataOutputStream outputStream = null;
		
		try {
			//Create socket to the server
			socketToTheServer = new Socket(serverAddress, serverPort); 
			
			//Create streams
			inputStream = new DataInputStream(socketToTheServer.getInputStream());
			outputStream = new DataOutputStream(socketToTheServer.getOutputStream());
		} catch (IOException e) {
			System.out.println("Creation of socket or streams failed");
		}
		
		System.out.println("Connection to the server successful");
		
		try {
			
			//Recieving message
			
			Integer msgLength = 0;
			
			//Try to read from the inputstream; blocking call, so will wait if there's nothing to read
			msgLength = inputStream.readInt();
			
			//If we get here, that means that we were able to read the length
			System.out.println("length of recieved message is " + msgLength);
			
			//Try to read the entire incoming message
			byte[] incomingMsg = new byte[msgLength];
			inputStream.readFully(incomingMsg, 0, msgLength);
			
			//We've read the incoming message
			System.out.println("Recieved message: " + new String(incomingMsg));
			
			
			
			//Responding to message
			String message = "Friday";
			byte[] outgoingMsg = message.getBytes();
			Integer outgoingMsgLength = outgoingMsg.length; 
			
			//Send length of message first
			//TODO writeInt() vs write()?
			outputStream.writeInt(outgoingMsgLength);
			
			//Then send message
			outputStream.write(outgoingMsg, 0, outgoingMsgLength);
			
			
			
			//Close socket and streams
			socketToTheServer.close();
			inputStream.close();
			outputStream.close();
		}catch(IOException e) {
			System.out.println("Error occurred somewhere");
		}
		
		
	}

}
