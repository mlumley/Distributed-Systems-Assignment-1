/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class DictionaryClient {

	private static final String QUERY_HELP_TEXT = "Enter a word to search for it";
	private static final String ADD_HELP_TEXT = "Enter a word and it's definitions. Place other definitions on a newline";
	private static final String DELETE_HELP_TEXT = "Enter a word to delete it";
	
	private static String hostName = "localhost";
	
	private Gson gson = new Gson();
	private Socket socket = null;

	private BufferedReader in;
	private PrintWriter out;

	private JFrame frame = new JFrame("Dictionary Client");
	private JTextArea textOutput = new JTextArea(8, 60);
	private JLabel helpText = new JLabel(QUERY_HELP_TEXT);

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


		JPanel queryPanel = this.queryPanel();
		JPanel addPanel = this.addPanel();
		JPanel deletePanel = this.deletePanel();

		JPanel outputPanel = this.outputPanel();

		JPanel actionPanels = new JPanel(new CardLayout());
		actionPanels.add(queryPanel, "queryPanel");
		actionPanels.add(addPanel, "addPanel");
		actionPanels.add(deletePanel, "deletePanel");

		JPanel menuPanel = this.menuPanel(actionPanels);

		JPanel allPanels = new JPanel();

		JPanel helpPanel = new JPanel();
		helpPanel.add(this.helpText);

		allPanels.add(menuPanel);
		allPanels.add(helpPanel);
		allPanels.add(actionPanels);
		allPanels.add(outputPanel);
		allPanels.setLayout(new BoxLayout(allPanels, BoxLayout.PAGE_AXIS));

		frame.getContentPane().add(allPanels);

		CardLayout cl = (CardLayout) actionPanels.getLayout();
		cl.show(actionPanels, "queryPanel");
		frame.setSize(800, 400);
	}

	private JPanel menuPanel(JPanel actionPanels) {
		JPanel menuPanel = new JPanel();
		JButton query = new JButton("Query");
		JButton add = new JButton("Add");
		JButton delete = new JButton("Delete");

		query.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) actionPanels.getLayout();
				cl.show(actionPanels, "queryPanel");
				helpText.setText(QUERY_HELP_TEXT);
				textOutput.setText(null);
			}
		});

		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) actionPanels.getLayout();
				cl.show(actionPanels, "addPanel");
				helpText.setText(ADD_HELP_TEXT);
				textOutput.setText(null);
			}
		});

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) actionPanels.getLayout();
				cl.show(actionPanels, "deletePanel");
				helpText.setText(DELETE_HELP_TEXT);
				textOutput.setText(null);
			}
		});

		menuPanel.add(query);
		menuPanel.add(add);
		menuPanel.add(delete);

		return menuPanel;
	}
	
	private void popupMessage(String msg) {
		JOptionPane.showMessageDialog(frame, "Error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private JPanel queryPanel() {
		JPanel queryPanel = new JPanel();
		JTextField wordInput = new JTextField(20);
		JButton searchButton = new JButton("Search for word");

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String word = wordInput.getText();
				
				if (word.isEmpty()) {
					popupMessage("Please enter a word");
				}
				else {
					Message msgToServer = new Message(Message.MessageTypes.QUERY, word);
					out.println(gson.toJson(msgToServer));
				}
				wordInput.setText(null);
			}
		});

		wordInput.setEditable(true);

		queryPanel.add(wordInput);
		queryPanel.add(searchButton);

		return queryPanel;
	}

	private JPanel addPanel() {
		JPanel addPanel = new JPanel();
		JPanel textPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JTextField wordInput = new JTextField(20);
		JTextArea definitionInput = new JTextArea(8, 50);
		JButton addButton = new JButton("Add Word");

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String word = wordInput.getText();
				String definitionText = definitionInput.getText();
				String[] definitions = definitionText.split("\n");
				ArrayList<String> payload = new ArrayList<String>();
				
				if (word.isEmpty() || definitionText.isEmpty()) {
					popupMessage("Remember to add both the word and definitions");
				}
				else {
					payload.add(word);
	
					for (String definition : definitions) {
						payload.add(definition);
					}
	
					Message msgToServer = new Message(Message.MessageTypes.ADD, payload);
					out.println(gson.toJson(msgToServer));
				}
				wordInput.setText(null);
				definitionInput.setText(null);
			}
		});

		wordInput.setEditable(true);
		definitionInput.setEditable(true);

		textPanel.add(wordInput);
		textPanel.add(definitionInput);
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.add(addButton);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));

		addPanel.add(textPanel);
		addPanel.add(buttonPanel);

		return addPanel;
	}

	private JPanel deletePanel() {
		JPanel deletePanel = new JPanel();
		JTextField wordInput = new JTextField(20);
		JButton deleteButton = new JButton("Delete word");

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String word = wordInput.getText();
				Message msgToServer = new Message(Message.MessageTypes.DELETE, word);

				out.println(gson.toJson(msgToServer));
				wordInput.setText(null);
			}
		});

		wordInput.setEditable(true);

		deletePanel.add(wordInput);
		deletePanel.add(deleteButton);

		return deletePanel;
	}

	private JPanel outputPanel() {
		JPanel outputPanel = new JPanel();
		JScrollPane pane = new JScrollPane(textOutput);

		textOutput.setEditable(false);
		textOutput.setLineWrap(true);

		outputPanel.add(pane);

		return outputPanel;
	}

	private void run() {
		try {
			Message msgFromServer = null;
			while (true) {
				msgFromServer = gson.fromJson(in.readLine(), Message.class);

				if (msgFromServer != null) {
					if (msgFromServer.type == Message.MessageTypes.QUIT) {
						System.out.println("Quiting");
						break;
					}
					else if (msgFromServer.type == Message.MessageTypes.ERROR) {
						this.popupMessage(msgFromServer.payload.get(0));
					}
					else if (msgFromServer.type == Message.MessageTypes.HELLO) {
						textOutput.setText(msgFromServer.payload.get(0));
					}
					else {
						textOutput.setText(msgFromServer.getPayload());
					}
				}
			}
			socket.close();
		} catch (JsonSyntaxException e) {
			System.out.println("Error: Message from server malformed");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Error: Could not reach server");
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		DictionaryClient client = new DictionaryClient(DictionaryClient.hostName, Integer.parseInt(args[0]));
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
	}
}
