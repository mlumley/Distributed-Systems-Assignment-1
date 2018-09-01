
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.util.ArrayList;

/**
 * Represents a message that is sent over the network. The message contains a
 * type and a payload which is an ArryList of Strings
 */
public class Message {
	public enum MessageTypes {HELLO, QUERY, ADD, DELETE, REPLY, ERROR, QUIT};
	public MessageTypes type;
	public ArrayList<String> payload;

	/**
	 * Creates a message with a single String as a payload
	 * 
	 * @param type
	 *            The type of message
	 * @param payload
	 *            The payload of the message
	 */
	public Message(MessageTypes type, String payload) {
		this.type = type;
		this.payload = new ArrayList<String>();
		this.payload.add(payload);
	}

	/**
	 * Creates a message with an ArrayList of Strings
	 * 
	 * @param type
	 *            The type of message
	 * @param payload
	 *            The payload of the message
	 */
	public Message(MessageTypes type, ArrayList<String> payload) {
		this.type = type;
		this.payload = payload;
	}

	/**
	 * Number the items of the payload and return the payload a single string with a
	 * double newline between each item
	 * 
	 * @return The string of payload
	 */
	public String getPayload() {
		String output = "";
		int i = 1;

		for (String item : this.payload) {
			output += i + ". " + item + "\n\n";
			i++;
		}

		return output;
	}

	@Override
	public String toString() {
		return "Type: " + type + ", Payload: " + payload.toString();
	}
}
