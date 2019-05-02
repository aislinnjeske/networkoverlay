To Run Registry: 

java cs455.overlay.node.Registry portNumber


To Run Messaging Node:

java cs455.overlay.node.MessagingNode registry_hostname registry_portnum


Node Package: 

Node
    -Interface that contains the onEvent() method, which is the basic functionality a TCPReceiverThread needs to react to a message that was received

MessagingNode
    -Contains the functionality of a messaging node including setting up a connection to the registry, sending messages to the registry, setting up connections to other nodes, sending messages to other nodes, receiving and responding to command line inputs, and upon the arrival of a message, determines whether to process the payload or relay to another node.

Registry
    -Contains the functionality of the registry including registering nodes, deregistering nodes, setting up an overlay, sending out link weights, starting rounds, and receiving and displaying traffic summaries.

OverlayNode
    -Contains the information of nodes in the overlay. This includes a list of all the nodes that the node with the corresponding hostname and port number is connected with, and a method to send messages to the node with the corresponding hostname and port number.

    
    
Dijkstra Package:

RoutingCache
    -Contains information about the shortest path from a source node to all nodes and accessor methods to get that information.

ShortestPath
    -Uses Dijkstra's algorithm to calculate the shortest path from the source node to every other node in the overlay.

WeightedNode
    -Used in ShortestPath as an encapsulation of a node that contains the current shortest path, a list of the adjacent nodes, and the distance from the source node.

    
    
Transport Package:

TCPReceiverThread
    -A thread that listens on a socket for incoming messages. When a message is received, it's sent to the EventFactory and then the corresponding node's onEvent() method is called.

TCPServerThread
    -A thread that listens on a server socket for incoming connections. When a connection is received, a TCPReceiverThread is constructed and started to listen on that socket.

TCPSender
    -A TCPSender object is contained inside every OverlayNode object so messages can be sent to the corresponding node.

    
        
Util Package:

OverlayCreator
    -Given an ArrayList of OverlayNodes, a new overlay with the given number of connections is created. Each OverlayNode now contains the nodes they need to connect to (create a socket and send a register message). A weighted overlay is also created, with random weights for each connection in the overlay.

StatisticsCollectorAndDisplay
    -Collects the traffic summary for each node and displays the summaries and the totals for each in a table on the console.

Wireformats Package: 

Event
    -Interface that contains the getBytes() and getType() method. This is the basis for all messages sent, and the EventFactory only creates objects of type Event.

EventFactory
    -A Singleton class that creates Event objects, given an array of bytes from a TCPReceiverThread. The class determines what type of message was sent, and unmarshalls the message accordingly.

Protocol
    -Contains information about the message type numbers and names. Given a message name, one can determine the message type number, and vice versa.

BasicMessage
    -The basic outline of a message that contains only the message type, node hostname, and node port number. This message is used to by Register messages, Deregister messages, and TaskComplete messages. Contains only the getBytes() method used for marshalling.

Deregister
    -A message sent from a MessagingNode to the Registry to deregister a node from the overlay.

LinkWeights
    -A message sent from the Registry to all MessagingNodes in the overlay that contains information about all connections and associated weights in the overlay.

MessagingNodesList
    -A unique message sent from the Registry to every MessagingNode that contains information about which nodes they need to establish a connection with.

PullTrafficSummary
    -A message sent from the Registry to all the MessagingNodes that tells the nodes to respond with a TrafficSummary message. Will be sent after a TaskComplete message has been received by the Registry from every MessagingNode.

Register
    -A message sent from a MessagingNode to the Registry as a request to register the node in the overlay. MessagingNode will receive a response whether the registration was successful or not.

Response
    -A message sent from the Registry to a MessagingNode that contains information on whether the registration was successful or not. In both cases, the MessagingNode will print additional information, and in the case of unsuccessful registration, the MessagingNode will print the reason why and exit.

RoundsMessage
    -A message sent from one MessagingNode to another. During a round, five messages are sent to a random destination, with a different RoundsMessage.

TaskComplete
    -A message sent from each MessagingNode to the Registry to confirm they have finished their rounds.

TaskInitiate
    -A message sent from the Registry to every MessagingNode to initiate the rounds.

TrafficSummary
    -A message sent from every MessagingNode to the Registry after they have received the PullTrafficSummary message. This contains the number of messages sent, received, and relayed, and the sum of the messages sent and received.

