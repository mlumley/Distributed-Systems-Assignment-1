import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

public class DictionaryClient {

	public static void main(String[] args) throws IOException {
		String hostName = "localhost";
		int port = Integer.parseInt(args[0]);
		Socket socket = null;
		
		try {
			socket = new Socket(hostName, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
		while(true) {
			Gson gson = new Gson();
				
			String input = in.readLine();
			Message msgFromServer = gson.fromJson(input, Message.class);
			
			if (msgFromServer != null) {
				if (msgFromServer.type.equals("quit")) {
					System.out.println("Quiting");
					break;
				}
				System.out.println(msgFromServer.toString());
			}
			

			// Console
			String type = inFromUser.readLine();
			String message = inFromUser.readLine();
			Message msgToServer = new Message(type, message);
			
			String json = gson.toJson(msgToServer);
			
			out.println(json);
		}
		socket.close();
	}
}