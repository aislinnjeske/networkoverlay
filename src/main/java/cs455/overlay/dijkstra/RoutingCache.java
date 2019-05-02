package cs455.overlay.dijkstra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class RoutingCache {
	
	private Set<WeightedNode> shortestPaths;
	private Map<String, String> cache;
	
	public RoutingCache(Set<WeightedNode> shortestPaths) {
		this.shortestPaths = shortestPaths;
		cache = new HashMap<>();
	}
	
	public void cacheRoutes(String sourceHostname, int sourcePortNumber) {
		
		for(WeightedNode node : shortestPaths) {
			
			//Don't print shortest path to yourself
			if(!node.equals(sourceHostname, sourcePortNumber)) {
				
				String pathString = "";
				
				LinkedList<WeightedNode> pathToNode = node.getShortestPath();
				int numberOfSteps = pathToNode.size();
				int stepCount = 0;
				
				while(stepCount < numberOfSteps) {
					WeightedNode currentStep = pathToNode.get(stepCount);
					WeightedNode nextStep;
					
					
					if(stepCount + 1 == numberOfSteps) {
						nextStep = node;
					} else {
						nextStep = pathToNode.get(stepCount + 1);
					}
					
					int weight = currentStep.getAdjacencyList().get(nextStep);
					
					pathString += currentStep.getNameToSend() + " -- " + weight + " -- ";

					stepCount++;
				}
				pathString += node.getNameToSend();
				cache.put(node.getHostname() + ":" + node.getPortNumber(), pathString);
			}
		}
		
	}
	
	public String getPathToSend(String destination) {
		Scanner scanner = new Scanner(cache.get(destination));
		
		String pathToSend = "";
		
		while(scanner.hasNext()) {
			if(!scanner.hasNextInt()) {
				String next = scanner.next();
				
				if(!next.equals("--")) {
					pathToSend += next + " ";
				}
			} else {
				scanner.nextInt();
			}
		}

		scanner.close();
		return pathToSend;
	}
	
	public Map<String, String> getRoutesFromCache(){
		return cache;
	}
	
	public String getPathToPrint(String destination) {
		return cache.get(destination);
	}
	

}
