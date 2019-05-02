package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ShortestPath {
	
	private Set<WeightedNode> graph;
	private Set<WeightedNode> visited;
	
	public ShortestPath(ArrayList<WeightedNode> overlay) {
		visited = new HashSet<>();
		graph = new HashSet<>();
		
		for(WeightedNode node : overlay) {
			graph.add(node);
		}
	}
	
	public void calculateShortestPaths(String sourceName) {
		//Find source node
		WeightedNode source = null;
		
		for(WeightedNode node : graph) {
			if(node.equals(sourceName)) {
				source = node;
			}
		}
		
		
		//Shortest distance to itself is 0
		source.setDistanceFromSource(0);
		
		//We haven't visited the source yet, so add it to unvisited
		Set<WeightedNode> unvisited = new HashSet<>();
		unvisited.add(source);
		
		//while we still have nodes to visit
		while(!unvisited.isEmpty()) {
			WeightedNode currentNode = findLowestDistanceNode(unvisited);
			unvisited.remove(currentNode);
			
			//Loop through the current node's adjacency list
			for(WeightedNode adjacentNode : currentNode.getAdjacencyList().keySet()) {
				
				//If adjacent node is unvisited,
				if(!visited.contains(adjacentNode)) {
					int weight = currentNode.getAdjacencyList().get(adjacentNode);
					calculateMinimumDistance(currentNode, weight, adjacentNode);
					unvisited.add(adjacentNode);
				}
			}
			visited.add(currentNode);
			
		}
	}
	
	//We are finding the node in unvisited that has the shortest distance to the source
	//In the first iteration, this will be the source
	private WeightedNode findLowestDistanceNode(Set<WeightedNode> unvisited) {
		WeightedNode lowestDistanceNode = null;
		int lowestDistance = Integer.MAX_VALUE;
		
		for(WeightedNode node : unvisited) {
			int nodeDistance = node.getDistanceFromSource();
			
			if(nodeDistance < lowestDistance) {
				lowestDistance = nodeDistance;
				lowestDistanceNode = node;
			}
		}
		return lowestDistanceNode;
	}
	
	
	private void calculateMinimumDistance(WeightedNode currentNode, int weight, WeightedNode adjacentNode) {
		int sourceDistance = currentNode.getDistanceFromSource();
		
		//The first time, get distance from source will be infinite, so everything will be smaller
		if(sourceDistance + weight < adjacentNode.getDistanceFromSource()) {
			//If this way is shorter, update distance, get the shortest path from current node and then add current node
			adjacentNode.setDistanceFromSource(sourceDistance + weight); 
			LinkedList<WeightedNode> shortestPath = new LinkedList<>(currentNode.getShortestPath());
			shortestPath.add(currentNode);
			adjacentNode.setShortestPath(shortestPath);
		}
	}
	
	public Set<WeightedNode> getShortestPaths(){
		return graph;
	}
	
	
}
