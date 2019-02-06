package cs455.overlay.node;

import java.util.HashSet;
import java.util.Set;

public class OverlayNode {
	
	private Set<OverlayNode> connections;
	
	public OverlayNode() {
		connections = new HashSet<>();
	}
	
	public void addConnection(OverlayNode node) {
		connections.add(node);
	}
	
	public boolean contains(OverlayNode node) {
		return connections.contains(node);
	}
	
	public Set<OverlayNode> getConnections(){
		return connections;
	}
}
