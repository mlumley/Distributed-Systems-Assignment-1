import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.gson.Gson;

public class DictionaryClient {
	
	Gson gson = new Gson();
	Socket socket = null;
	static String hostName = "localhost";
	BufferedReader inFromUser;
	BufferedReader in;
	PrintWriter out;
	
    JFrame frame = new JFrame("Dictionary Client");

    JTextArea textOutput = new JTextArea(4, 40);
	
	public DictionaryClient(String hostName, int port) throws IOException {
		try {
			socket = new Socket(hostName, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		inFromUser = new BufferedReader(new InputStreamReader(System.in));
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
		
		JPanel queryPanel = this.queryPanel();
		JPanel addPanel = this.addPanel();
		JPanel deletePanel = this.deletePanel();
	     
		JPanel outputPanel = this.outputPanel();
		

		
		JPanel actionPanels = new JPanel(new CardLayout());
		actionPanels.add(queryPanel,"queryPanel");
		actionPanels.add(addPanel, "addPanel");
		actionPanels.add(deletePanel, "deletePanel");
		
		JPanel menuPanel = this.menuPanel(actionPanels);
        
        frame.getContentPane().add(BorderLayout.NORTH, menuPanel);
        frame.getContentPane().add(BorderLayout.CENTER, actionPanels);
        frame.getContentPane().add(BorderLayout.SOUTH, outputPanel);
        CardLayout cl = (CardLayout) actionPanels.getLayout();
        cl.show(actionPanels, "queryPanel");
        frame.pack();
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
	    	}
	    });
		
		add.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		CardLayout cl = (CardLayout) actionPanels.getLayout();
	    		cl.show(actionPanels, "addPanel");
	    	}
	    });
		
		delete.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		CardLayout cl = (CardLayout) actionPanels.getLayout();
	    		cl.show(actionPanels, "deletePanel");
	    	}
	    });
		
		menuPanel.add(query);
		menuPanel.add(add);
		menuPanel.add(delete);
		
		return menuPanel;
	}
	
	private JPanel addPanel() {
		JPanel addPanel = new JPanel();
	    JTextField wordInput = new JTextField(20);
	    JTextField definitionInput = new JTextField(20);
	    JButton search = new JButton("Search");
	    search.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		String word = wordInput.getText();
	    		String definition = definitionInput.getText();
	    		
	    		ArrayList<String> payload = new ArrayList<String>();
	    		payload.add(word);
	    		payload.add(definition);
	    		Message msgToServer = new Message("query", payload);
	    		out.println(gson.toJson(msgToServer));
	    	}
	    });
	    
	    wordInput.setEditable(true);
	    definitionInput.setEditable(true);
	    
	    addPanel.add(wordInput);
	    addPanel.add(definitionInput);
	    addPanel.add(search);
	    
	    return addPanel;
	}
	
	private JPanel queryPanel() {
		JPanel queryPanel = new JPanel();
	    JTextField textInput = new JTextField(20);
	    JButton search = new JButton("Search");
	    search.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		String word = textInput.getText();
	    		Message msgToServer = new Message("query", word);
	    		out.println(gson.toJson(msgToServer));
	    	}
	    });
	    
	    textInput.setEditable(true);
	    
	    queryPanel.add(textInput);
	    queryPanel.add(search);
	    
	    return queryPanel;
	}
	
	private JPanel deletePanel() {
		JPanel deletePanel = new JPanel();
	    JTextField textInput = new JTextField(20);
	    JButton search = new JButton("Delete");
	    search.addActionListener(new ActionListener() {
	    	@Override
	    	public void actionPerformed(ActionEvent e) {
	    		String word = textInput.getText();
	    		Message msgToServer = new Message("delete", word);
	    		out.println(gson.toJson(msgToServer));
	    	}
	    });
	    
	    textInput.setEditable(true);
	    
	    deletePanel.add(textInput);
	    deletePanel.add(search);
	    
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
	
	private void run() throws IOException {
		Message msgFromServer;
		Message msgToServer;
		String input;
		String output;
		
		while(true) {
			input = in.readLine();
			msgFromServer = gson.fromJson(input, Message.class);
			
			if (msgFromServer != null) {
				if (msgFromServer.type.equals("quit")) {
					System.out.println("Quiting");
					break;
				}
				System.out.println(msgFromServer.toString());
				textOutput.setText(msgFromServer.getPayload());
			}
			
			//msgToServer = this.consoleInput();
			
			//output = gson.toJson(msgToServer);
			//out.println(output);
		}
        socket.close();
	}
	
	private Message consoleInput() throws IOException {
		String type = inFromUser.readLine();
		ArrayList<String> message = new ArrayList<String>();
		if (type.equals("query")) {
			message.add(inFromUser.readLine());
		}
		else if (type.equals("add")) {
			message.add(inFromUser.readLine());
			message.add(inFromUser.readLine());
		}
		else if (type.equals("delete")) {
			message.add(inFromUser.readLine());
		}
		
		return new Message(type, message);
	}

	public static void main(String[] args) throws IOException {
        DictionaryClient client = new DictionaryClient(hostName, Integer.parseInt(args[0]));
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
	}
}
