package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessagingNodesList implements Event{
	
	private int messageType;
	private int numberOfPeerNodes; 
	private String[] peerNodes;
	
	public MessagingNodesList(int messageType, int numberOfPeerNodes, String[] peerNodes) {
		this.messageType = messageType;
		this.numberOfPeerNodes = numberOfPeerNodes;
		
		//Peer messaging nodes are in the format: hostname:portnumber
		this.peerNodes = peerNodes;
	}

	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteOutputStream));
		
		//Writing the message type
		dataOutputStream.writeInt(messageType);
		
		//Writing the number of peer nodes
		dataOutputStream.writeInt(numberOfPeerNodes);
		
		//Writing the peer nodes
		
		String peerNodeNamesToSend = "";
		
		//If there are no peer nodes, skip this step
		if(numberOfPeerNodes > 0) {
			peerNodeNamesToSend = combinePeerNodes();
		}
		
		byte[] peerNodesBytes = peerNodeNamesToSend.getBytes();
		int peerNodesLength = peerNodesBytes.length;
		dataOutputStream.writeInt(peerNodesLength);
		dataOutputStream.write(peerNodesBytes);
		
		dataOutputStream.flush();
		marshalledBytes = byteOutputStream.toByteArray();
		
		byteOutputStream.close();
		dataOutputStream.close();
		
		return marshalledBytes;
	}
	
	private String combinePeerNodes() {
		String peerNodeNamesToSend = peerNodes[0];
		
		//Using a single white space as a delimitor
		for(int i = 1; i < peerNodes.length; i++) {
			peerNodeNamesToSend += " " + peerNodes[i];
		}
		
		return peerNodeNamesToSend;
	}

	public int getType() {
		return messageType;
	}
	
	public int getNumberOfPeers() {
		return peerNodes.length;
	}
	
	public String[] getPeerNodes() {
		return peerNodes;
	}

}
