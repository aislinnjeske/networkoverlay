package cs455.overlay.util;

import cs455.overlay.node.OverlayNode;
import java.util.ArrayList;
import java.util.Random;

public class OverlayCreator {
	
	private Random random;
	private int numberOfNodeConnections;
	private int totalNumberOfConnections;
	private int numberOfNodes;
	private ArrayList<OverlayNode> connectionsForEachNode;
	private String[] weightedOverlay;
	
	public OverlayCreator(ArrayList<OverlayNode> overlay, int numberOfConnections) {
		this.numberOfNodeConnections = numberOfConnections;
		this.numberOfNodes = overlay.size();
		this.connectionsForEachNode = overlay;
		random = new Random();
	}

	public void createOverlay(){
		createUnweightedOverlay();
		addWeightsToOverlay();
	}
	
	private void createUnweightedOverlay() {
		//If numberOfConnections / 2 is even
		if(numberOfNodeConnections % 2 == 0) {
			//put all verticies in a circle and join each to it's m nearest neighbors on either side
			int numberOfNeighbors = numberOfNodeConnections / 2;
			joinNeighboringVerticies(numberOfNeighbors);
			
		} else if(numberOfNodeConnections % 2 == 1 && (numberOfNodes % 2 == 0)) {
			//put all verticies on a circle, join each to it's m nearest neighbors on each side
			//and to the vertex directly opposite
			
			int numberOfNeighbors = (numberOfNodeConnections - 1) / 2;
			joinNeighboringVerticies(numberOfNeighbors);
			joinOppositeVerticies();
		}
	}
	
	private void joinNeighboringVerticies(int m) {
		
		for(int i = 0; i < numberOfNodes - 1; i++) {
			for(int neighbor = m; neighbor > 0; neighbor--) {
				
				OverlayNode currentNode = connectionsForEachNode.get(i);
				OverlayNode firstNode = connectionsForEachNode.get((i + neighbor) % numberOfNodes);
				OverlayNode secondNode = connectionsForEachNode.get((numberOfNodes + (i - neighbor)) % numberOfNodes);

				if(!firstNode.contains(currentNode) && !firstNode.equals(currentNode)) {
					currentNode.addConnection(firstNode);
					totalNumberOfConnections++;
				}
				
				if(!secondNode.contains(currentNode) && !secondNode.equals(currentNode)) {
					currentNode.addConnection(secondNode);
					totalNumberOfConnections++;
				}
			}
		}
	}
	
	private void joinOppositeVerticies() {
		
		for(int i = 0; i <= numberOfNodes / 2; i++) {
			OverlayNode currentNode = connectionsForEachNode.get(i);
			OverlayNode diagonalNode = connectionsForEachNode.get((i + (numberOfNodes / 2)) % numberOfNodes);
			
			if(!diagonalNode.contains(currentNode)) {
				currentNode.addConnection(diagonalNode);
				totalNumberOfConnections++;
			}	
		}	
	}
	
	private void addWeightsToOverlay(){
		this.weightedOverlay = new String[totalNumberOfConnections];
		int counter = 0;
		
		for(int i = 0; i < connectionsForEachNode.size(); i++) {
			OverlayNode firstNode = connectionsForEachNode.get(i);
			
			for(OverlayNode secondNode : firstNode.getConnections()) {
				
				String link = firstNode.getNameToSend() + " " + secondNode.getNameToSend() + " " + getRandomWeight(); 

				weightedOverlay[counter++] = link;
			}
		}
	}

	private int getRandomWeight() {
		return random.nextInt(10) + 1;
	}
	
	public int getNumberOfConnectionsInOverlay() {
		return totalNumberOfConnections;
	}
	
	public ArrayList<OverlayNode> getConnectionsForEachNode(){
		return connectionsForEachNode;
	}
	
	public String[] getWeightedOverlay(){
		return weightedOverlay;
	}
}
