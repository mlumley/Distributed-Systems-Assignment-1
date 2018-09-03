
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles connection requests from clients and creates new threads to handle
 * communication with each client
 */
public class DictionaryServer {

	private static ServerSocket serverSocket = null;
	private static int port = 8080;
	private static int clientNumber = 1;
	private static Dictionary dictionary = null;

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("Error: Incorrect number of arguments. Please enter the port number and the dictionary filename");
			System.exit(0);
		}

		try {
			port = Integer.parseInt(args[0]);
			serverSocket = new ServerSocket(port);
			System.out.println("Server started on port " + port);
			System.out.println("Listening...");
		} catch (IOException e) {
			System.out.println("Could not start server on port " + port);
			System.exit(0);
		} catch (NumberFormatException e) {
			System.out.println("Port value is not an integer ");
			System.exit(0);
		}

		dictionary = new Dictionary(args[1]);

		// Wait for a connection and spawn a new thread to handle a new connection
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected");
				DictionaryWorker worker = new DictionaryWorker(clientSocket, clientNumber++, dictionary);
				worker.start();
			} catch (IOException e) {
				System.out.println("Error: Could not create thread to handle client");
				System.exit(0);
			}
		}
	}

}
