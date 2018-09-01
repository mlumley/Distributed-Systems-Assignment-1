
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

/**
 * Worker thread to handle communication with a client
 */
public class DictionaryWorker extends Thread {

	private Socket clientSocket = null;
	private int clientNumber = 0;
	private Dictionary dictionary = null;
	private Gson gson = new Gson();
	private BufferedReader in = null;
	private PrintWriter out = null;

	/**
	 * Creates a new worker
	 * 
	 * @param clientSocket
	 *            The socket for the client
	 * @param clientNumber
	 *            The number of the client assigned by the server
	 * @param dictionary
	 *            The dictionary to use
	 */
	public DictionaryWorker(Socket clientSocket, int clientNumber, Dictionary dictionary) {
		this.clientSocket = clientSocket;
		this.clientNumber = clientNumber;
		this.dictionary = dictionary;
	}

	/**
	 * Sets up the in and out channels using the socket and sends a welcome message
	 * to the client
	 */
	private void connectToClient() {
		System.out.println("Connected to client");

		try {
			in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			out = new PrintWriter(this.clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Error: Could not recieve/send data to client");
			System.exit(0);
		}

		Message helloMsg = new Message(Message.MessageTypes.HELLO, "Connected to server. You are client number " + clientNumber);
		out.println(gson.toJson(helloMsg));
	}

	@Override
	public void run() {
		try {

			this.connectToClient();

			while (true) {
				String input = in.readLine();
				Message msgFromClient = gson.fromJson(input, Message.class);
				Message msgToClient = null;
				System.out.println(input);

				if (msgFromClient.type == Message.MessageTypes.QUIT) {
					// Send quit command to client
					out.println(gson.toJson(msgFromClient));
					break;
				} else if (msgFromClient.type == Message.MessageTypes.QUERY) {
					msgToClient = this.processQueryWord(msgFromClient);
				} else if (msgFromClient.type == Message.MessageTypes.ADD) {
					msgToClient = this.processAddWord(msgFromClient);
				} else if (msgFromClient.type == Message.MessageTypes.DELETE) {
					msgToClient = this.processDeleteWord(msgFromClient);
				}

				if (msgToClient != null) {
					out.println(gson.toJson(msgToClient));
				}
			}

			this.clientSocket.close();
		} catch (IOException e) {
			System.out.println("Error: Could not read message from client");
		}
	}

	/**
	 * Looks up the word in the dictionary and returns the definition as a message
	 * 
	 * @param queryMsg
	 *            The incoming message from the client
	 * @return A message containing the definition or an error message if the word
	 *         was not found
	 */
	private Message processQueryWord(Message queryMsg) {
		String word = queryMsg.payload.get(0);

		if (this.dictionary.hasWord(word)) {
			ArrayList<String> definition = this.dictionary.getDefinitions(word);
			return new Message(Message.MessageTypes.REPLY, definition);
		} else {
			return new Message(Message.MessageTypes.ERROR, "Word not in dictionary");
		}
	}

	/**
	 * Adds a word and definitions to the dictionary and returns a message with
	 * whether it was successful or not
	 * 
	 * @param addMsg
	 *            The incoming message from the client
	 * @return A message containing if the operation was successful or not
	 */
	private Message processAddWord(Message addMsg) {
		String word = addMsg.payload.get(0);
		ArrayList<String> definition = addMsg.payload;
		definition.remove(word);

		if (!this.dictionary.hasWord(word)) {
			this.dictionary.addWord(word, definition);
			return new Message(Message.MessageTypes.REPLY, "success");
		} else {
			return new Message(Message.MessageTypes.ERROR, "Word aleady in dictionary");
		}
	}

	/**
	 * Delete's a word from the dictionary and returns a message with whether it was
	 * successful or not
	 * 
	 * @param deleteMsg
	 *            The incoming message from the client
	 * @return A message containing if the operation was successful or not
	 */
	private Message processDeleteWord(Message deleteMsg) {
		String word = deleteMsg.payload.get(0);

		if (this.dictionary.hasWord(word)) {
			this.dictionary.deleteWord(word);
			return new Message(Message.MessageTypes.REPLY, "success");
		} else {
			return new Message(Message.MessageTypes.ERROR, "Word not in dictionary");
		}
	}
}
