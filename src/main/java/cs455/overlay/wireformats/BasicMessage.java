package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BasicMessage {
	
	public byte[] getBytes(int messageType, String nodeHostname, int nodePortNumber) throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteOutputStream)); 
		
		//Writing the message type
		dataOutputStream.writeInt(messageType);
		
		//Writing the node IP address
		byte[] nodeIPAddressBytes = nodeHostname.getBytes();
		int nodeIPAddressLength = nodeIPAddressBytes.length;
		dataOutputStream.writeInt(nodeIPAddressLength);
		dataOutputStream.write(nodeIPAddressBytes);
		
		//Writing the node port number
		dataOutputStream.writeInt(nodePortNumber); 
		
		dataOutputStream.flush();
		marshalledBytes = byteOutputStream.toByteArray();
		
		byteOutputStream.close();
		dataOutputStream.close();
		
		return marshalledBytes;
	}

}
