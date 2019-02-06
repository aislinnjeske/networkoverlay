package cs455.overlay.util;

import cs455.overlay.node.OverlayNode;
import java.util.ArrayList;

public class OverlayCreator {
	
	private int numberOfConnections;
	private int numberOfNodes;
	private ArrayList<OverlayNode> overlay;
	
	public OverlayCreator(int numberOfConnections, int numberOfNodes) {
		this.numberOfConnections = numberOfConnections;
		this.numberOfNodes = numberOfNodes;
		overlay = new ArrayList<>(); 
	}
	
	public void addToOverlay(OverlayNode node) {
		overlay.add(node);
	}
	
	public ArrayList<OverlayNode> createNewOverlay(){
		
		//If numberOfConnections / 2 is even
		if(numberOfConnections % 2 == 0) {
			//put all verticies in a circle and join each to it's m nearest neighbors on either side
			int numberOfNeighbors = numberOfConnections / 2;
			joinNeighboringVerticies(numberOfNeighbors);
			
		} else if(numberOfConnections % 2 == 1 && (numberOfNodes % 2 == 0)) {
			//put all verticies on a circle, join each to it's m nearest neighbors on each side
			//and to the vertex directly opposite
			
			int numberOfNeighbors = (numberOfConnections - 1) / 2;
			joinNeighboringVerticies(numberOfNeighbors);
			joinOppositeVerticies();
		}
		
		return overlay;
	}
	
	private void joinNeighboringVerticies(int m) {
		
		for(int i = 0; i < numberOfNodes - 1; i++) {
			for(int neighbor = m; neighbor > 0; neighbor--) {
				
				OverlayNode currentNode = overlay.get(i);
				OverlayNode firstNode = overlay.get((i + neighbor) % numberOfNodes);
				OverlayNode secondNode = overlay.get((numberOfNodes + (i - neighbor)) % numberOfNodes);

				if(!firstNode.contains(currentNode)) {
					currentNode.addConnection(firstNode);
				}
				
				if(!secondNode.contains(currentNode)) {
					currentNode.addConnection(secondNode);
				}
			}
		}
	}
	
	private void joinOppositeVerticies() {
		
		for(int i = 0; i <= numberOfNodes / 2; i++) {
			OverlayNode currentNode = overlay.get(i);
			OverlayNode diagonalNode = overlay.get((i + (numberOfNodes / 2)) % numberOfNodes);
			
			if(!diagonalNode.contains(currentNode)) {
				currentNode.addConnection(diagonalNode);
			}	
		}	
	}
}
