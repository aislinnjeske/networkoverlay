package cs455.overlay.node;

import java.net.Socket;

import cs455.overlay.transport.*;
import cs455.overlay.wireformats.*;

public interface Node {
	
	public void onEvent(Event event, Socket socket);
	
	public void addNewConnection(TCPConnection connection);

}
