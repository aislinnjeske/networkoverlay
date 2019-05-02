package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LinkWeights implements Event{
	
	private int messageType;
	private int numberOfLinks;
	private String[] overlayConnections;
	
	public LinkWeights(int messageType, int numberOfLinks, String[] overlayConnections) {
		this.messageType = messageType;
		this.numberOfLinks = numberOfLinks;
		
		//Each connection is in the format: hostnameA:portnumber hostnameB:portnumber weight
		this.overlayConnections = overlayConnections;
	}

	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteOutputStream));
		
		//Writing the message type
		dataOutputStream.writeInt(messageType);
		
		//Writing the number of links
		dataOutputStream.writeInt(numberOfLinks);
		
		//Writing the overlay connections
		String connectionsToSend = createConnectionString();
		
		byte[] overlayConnectionsBytes = connectionsToSend.getBytes();
		int overlayConnectionsLength = overlayConnectionsBytes.length;
		dataOutputStream.writeInt(overlayConnectionsLength);
		dataOutputStream.write(overlayConnectionsBytes);
		
		dataOutputStream.flush();
		marshalledBytes = byteOutputStream.toByteArray();
		
		byteOutputStream.close();
		dataOutputStream.close();
		
		return marshalledBytes;
	}
	
	private String createConnectionString() {
		String connectionsToSend = "";
		
		//Not using a delimitor because data will always be in the same order:
		//hostnameA_info hostnameB_info weight hostnameC_info hostnameD_info weight ......
		for(int i = 0; i < overlayConnections.length; i++) {
			connectionsToSend += overlayConnections[i] + " ";
		}
		
		return connectionsToSend;
	}

	public int getType() {
		return messageType;
	}
	
	public String[] getLinks() {
		return overlayConnections;
	}

}
