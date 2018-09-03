
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The client used to interact with the dictionary
 */
public class DictionaryClient {

	private Gson gson = new Gson();

	private Socket socket = null;

	// Streams
	private BufferedReader in;
	private PrintWriter out;

	// GUI
	private JFrame frame = new JFrame("Dictionary Client");
	private JTextArea textOutput = new JTextArea(8, 60);
	private PopupManager popup = new PopupManager(frame);

	/**
	 * Create a new client and connect to a server
	 * 
	 * @param hostName
	 *            The hostName of the server
	 * @param port
	 *            The port number of the server
	 */
	public DictionaryClient(String hostName, int port) {
		try {
			socket = new Socket(hostName, port);
		} catch (IOException e) {
			System.out.println("Error: Could not connect to server. Please check HostName and Port Number");
			System.exit(0);
		}

		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("Error: Could not recieve/send data to server");
			System.exit(0);
		}

		this.createGUI(this.out, this.textOutput, this.popup);
	}

	/**
	 * Creates the GUI for the client and connects it to the output stream to server
	 * 
	 * @param out
	 *            The output stream
	 * @param textOutput
	 *            The text area for messages from the server
	 * @param popup
	 *            The popup manager
	 */
	private void createGUI(PrintWriter out, JTextArea textOutput, PopupManager popup) {
		OutputPanel outputPanel = new OutputPanel(textOutput);
		ActionPanels actionPanel = new ActionPanels(out, popup);
		MenuPanel menuPanel = new MenuPanel(actionPanel, textOutput);
		JPanel allPanels = new JPanel();

		allPanels.add(menuPanel);
		allPanels.add(actionPanel);
		allPanels.add(outputPanel);
		allPanels.setLayout(new BoxLayout(allPanels, BoxLayout.PAGE_AXIS));

		frame.getContentPane().add(allPanels);
		frame.setSize(800, 400);

		// When user closes the window don't close but sent quit message to server.
		// The server will then send a quit message back to the client so that both know
		// to close the connection.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				out.println(gson.toJson(new Message(Message.MessageTypes.QUIT, "")));
			}
		});

		frame.setVisible(true);
	}

	/**
	 * Main logic for the client Wait for messages from the server and display them
	 * to the textOuput area
	 */
	private void run() {
		try {
			Message msgFromServer = null;
			while (true) {
				msgFromServer = gson.fromJson(in.readLine(), Message.class);

				if (msgFromServer != null) {
					if (msgFromServer.type == Message.MessageTypes.QUIT) {
						System.out.println("Quiting");
						break;
					} else if (msgFromServer.type == Message.MessageTypes.ERROR) {
						this.popup.popupErrorMessage(msgFromServer.payload.get(0));
					} else if (msgFromServer.type == Message.MessageTypes.HELLO) {
						this.textOutput.setText(msgFromServer.payload.get(0));
					} else {
						this.textOutput.setText(msgFromServer.getPayload());
					}
				}
			}
			this.socket.close();
		} catch (JsonSyntaxException e) {
			System.out.println("Error: Message from server malformed");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Error: Could not reach server");
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Error: Incorrect number of arguments. Please enter the hostname and the port number");
			System.exit(0);
		}
		
		// Default server location
		String hostName = "loachost";
		int port = 8080;
		
		// Load from terminal
		try {
			hostName = args[0];
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("Port value is not an integer ");
			System.exit(0);
		}
		
		DictionaryClient client = new DictionaryClient(hostName, port);
		client.run();
		System.exit(0);
	}
}
