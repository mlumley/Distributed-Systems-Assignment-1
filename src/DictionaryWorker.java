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
					break;
				}
				else if (inputFromClient.type.equals("query")) {
					outputToClient = this.processQuery(inputFromClient);
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
	
	private Message processQuery(Message queryMsg) {
		String query = queryMsg.payload.get(0);
		String definition = DictionaryServer.dictionary.get(query);
		return new Message("reply", definition);
	}

}
