package cs455.overlay.wireformats;

public class Protocol {
	
	public int getNumOfMessageType(String msgType) {
		switch(msgType) {
		case "REGISTER_REQUEST":
			return 1;
		case "REGISTRATION_RESPONSE":
			return 2;
		case "DEREGISTER_REQUEST":
			return 3;
		case "MESSAGING_NODES_LIST":
			return 4;
		case "LINK_WEIGHTS":
			return 5;
		case "TASK_INITIATE":
			return 6;
		case "TASK_COMPLETE":
			return 7;
		case "PULL_TRAFFIC_SUMMARY":
			return 8;
		case "TRAFFIC_SUMMARY":
			return 9; 
		case "DEREGISTRATION_RESPONSE":
			return 10;
		default:
			return 0;
		}
	}
	
	public String getNameofMessageType(int msgType) {
		switch(msgType) {
		case 1:
			return "REGISTER_REQUEST";
		case 2:
			return "REGISTRATION_RESPONSE";
		case 3:
			return "DEREGISTER_REQUEST";
		case 4:
			return "MESSAGING_NODES_LIST";
		case 5:
			return "LINK_WEIGHTS";
		case 6:
			return "TASK_INITIATE";
		case 7:
			return "TASK_COMPLETE";
		case 8:
			return "PULL_TRAFFIC_SUMMARY";
		case 9:
			return "TRAFFIC_SUMMARY";
		case 10:
			return "DEREGISTRATION_RESPONSE";
		default:
			return "INVALID"; 
		}
	}

}
