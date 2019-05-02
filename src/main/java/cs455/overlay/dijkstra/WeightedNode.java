package cs455.overlay.dijkstra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class WeightedNode {
	
	private String hostname;
	private int portNumber;
	private LinkedList<WeightedNode> shortestPath;
	private int distanceFromSource;
	private Map<WeightedNode, Integer> adjacentNodes;
	
	public WeightedNode(String nodeInformation) {
		int delimitorIndex = nodeInformation.indexOf(':');
		this.hostname = nodeInformation.substring(0, delimitorIndex);
		this.portNumber = Integer.parseInt(nodeInformation.substring(delimitorIndex + 1)); 

		shortestPath = new LinkedList<>();
		adjacentNodes = new HashMap<>();
		distanceFromSource = Integer.MAX_VALUE;
	}
	
	public void addAdjacentNode(WeightedNode node, int weight) {
		adjacentNodes.put(node, weight);
	}
	
	public Map<WeightedNode, Integer> getAdjacencyList(){
		return adjacentNodes;
	}
	
	public int getDistanceFromSource() {
		return distanceFromSource;
	}
	
	public void setDistanceFromSource(int distance) {
		distanceFromSource = distance;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public int getPortNumber() {
		return portNumber;
	}

	public void setShortestPath(LinkedList<WeightedNode> shortestPath) {
		this.shortestPath = shortestPath;
	}
	
	public LinkedList<WeightedNode> getShortestPath() {
		return shortestPath;
	}
	
	public boolean equals(String hostname, int portNumber) {
		boolean hasSameHostname = this.hostname.equals(hostname);
		boolean hasSamePortNumber = this.portNumber == portNumber;
		
		return hasSameHostname && hasSamePortNumber;
	}
	
	public boolean equals(String nodeName) {
		return nodeName.equals(getNameToSend());
	}
	
	public String getNameToSend() {
		return hostname + ":" + portNumber;
	}
}
