import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

public class DictionaryWorker extends Thread {
	
	private Socket clientSocket = null;
	private int clientNumber = 0;
	
	public DictionaryWorker(Socket clientSocket, int clientNumber) {
		this.clientSocket = clientSocket;
		this.clientNumber = clientNumber;
	}

	@Override
	public void run() {		
		try {
			Gson gson = new Gson();
			System.out.println("Connected to " + this.clientSocket.getInetAddress().getHostName());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
			
			Message helloMsg = new Message("hello", "You are client number " + clientNumber);
			out.println(gson.toJson(helloMsg));
			
			while(true) {
				String input = in.readLine();
				Message inputFromClient = gson.fromJson(input, Message.class);
				Message outputToClient = null;
				System.out.println(input);

				if(inputFromClient.type.equals("quit")) {
					out.println(gson.toJson(inputFromClient));
					break;
				}
				else if (inputFromClient.type.equals("query")) {
					outputToClient = this.processQueryWord(inputFromClient);
				}
				else if (inputFromClient.type.equals("add")) {
					outputToClient = this.processAddWord(inputFromClient);
				}
				else if (inputFromClient.type.equals("delete")) {
					outputToClient = this.processDeleteWord(inputFromClient);
				}
				
				if (outputToClient != null) {
					out.println(gson.toJson(outputToClient));
				}
			}
			
			this.clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Message processQueryWord(Message queryMsg) {
		String word = queryMsg.payload.get(0);
		String definition = DictionaryServer.dictionary.get(word);
		return new Message("reply", definition);
	}
	
	private Message processAddWord(Message addMsg) {
		String word = addMsg.payload.get(0);
		String definition = addMsg.payload.get(1);
		
		if (!DictionaryServer.dictionary.containsKey(word)) { 
			DictionaryServer.dictionary.put(word, definition);
			return new Message("reply", "success");
		}
		else {
			return new Message("reply", "failed");
		}
	}
	
	private Message processDeleteWord(Message deleteMsg) {
		String word = deleteMsg.payload.get(0);
		
		if (DictionaryServer.dictionary.containsKey(word)) { 
			DictionaryServer.dictionary.remove(word);
			return new Message("reply", "success");
		}
		else {
			return new Message("reply", "failed");
		}
	}

}
