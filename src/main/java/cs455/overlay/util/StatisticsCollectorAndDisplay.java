package cs455.overlay.util;

import java.util.ArrayList;

public class StatisticsCollectorAndDisplay {
	
	private ArrayList<String> nodeName;
	private ArrayList<Integer> numberOfMessagesSent;
	private ArrayList<Integer> numberOfMessagesReceived;
	private ArrayList<Integer> numberOfMessagesRelayed;
	private ArrayList<Long> sumOfMessagesSent;
	private ArrayList<Long> sumOfMessagesReceived;
	
	public StatisticsCollectorAndDisplay(int numberOfNodes) {
		nodeName = new ArrayList<>();
		numberOfMessagesSent = new ArrayList<>();
		numberOfMessagesReceived = new ArrayList<>();
		numberOfMessagesRelayed = new ArrayList<>();
		sumOfMessagesSent = new ArrayList<>();
		sumOfMessagesReceived = new ArrayList<>();
	}
	
	public void addNodeStatisticsToSummary(String name, int messagesSent, long sumMessagesSent, int messagesReceived, long sumMessagesReceived, int messagesRelayed) {
		nodeName.add(name);
		numberOfMessagesSent.add(messagesSent);
		numberOfMessagesReceived.add(messagesReceived);
		numberOfMessagesRelayed.add(messagesRelayed);
		sumOfMessagesSent.add(sumMessagesSent);
		sumOfMessagesReceived.add(sumMessagesReceived);
	}
	
	public void printDisplay() {
		System.out.printf("%40s %10s %10s %20s %20s %10s\n", "Node Name", "Num. Sent", "Num. Received", "Sum Sent", "Sum Received", "Num. Relayed");
		
		for(int i = 0; i < nodeName.size(); i++) {
			System.out.printf("%40s %10d %10d %20d %20d %10d\n", nodeName.get(i), numberOfMessagesSent.get(i), numberOfMessagesReceived.get(i), sumOfMessagesSent.get(i), sumOfMessagesReceived.get(i), numberOfMessagesRelayed.get(i));
		}
		
		int messagesSentTotal = getSum(numberOfMessagesSent);
		int messagesReceivedTotal = getSum(numberOfMessagesReceived);
		long sumMessagesSentTotal = getSum(sumOfMessagesSent, 0);
		long sumMessagesReceivedTotal = getSum(sumOfMessagesReceived, 0);
		
		System.out.printf("%40s %10d %10d %20d %20d\n", "Sum", messagesSentTotal, messagesReceivedTotal, sumMessagesSentTotal, sumMessagesReceivedTotal);		
	}

	private int getSum(ArrayList<Integer> numbers) {
		int sum = 0;
		for(int value : numbers) {
			sum += value;
		}
		return sum;
	}
	
	private long getSum(ArrayList<Long> numbers, int dummy) {
		long sum = 0;
		for(long value : numbers) {
			sum += value;
		}
		return sum;
	}

}
