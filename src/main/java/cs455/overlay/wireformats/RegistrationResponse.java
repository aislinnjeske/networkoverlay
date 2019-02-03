package cs455.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistrationResponse implements Event {
	
	 private int messageType;
	 private byte statusCode;
	 private String additionalInfo; 
	 
	 public RegistrationResponse(int messageType, byte statusCode, String additionalInfo) {
		 this.messageType = messageType;
		 this.statusCode = statusCode;
		 this.additionalInfo = additionalInfo;
	 }

	public byte[] getBytes() throws IOException {
		
		byte[] marshalledBytes = null;
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(); 
		DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteOutputStream)); 
		
		//Writing the message type
		dataOutputStream.writeInt(messageType);
		
		//Writing the status code
		dataOutputStream.writeByte(statusCode);
		
		//Writing the addtionalInfo
		byte[] additionalInfoBytes = additionalInfo.getBytes();
		int additionalInfoLength = additionalInfoBytes.length; 
		dataOutputStream.writeInt(additionalInfoLength);
		dataOutputStream.write(additionalInfoBytes);
		
		dataOutputStream.flush();
		marshalledBytes = byteOutputStream.toByteArray();
		
		byteOutputStream.close();
		dataOutputStream.close();
		
		return marshalledBytes; 
	}

	public int getType() {
		return messageType;
	}
	
	public byte getStatusCode() {
		return statusCode;
	}
	
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void confirmRegistration() {
		System.out.println("I got the registration response");
	}
}
