import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * 
 */

/**
 * @author Michael
 *
 */
public class DictionaryServer {
	
	private static int clientNumber = 1;
	public static ConcurrentHashMap<String, String> dictionary;

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws JsonIOException 
	 * @throws JsonSyntaxException 
	 */
	public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		
		// Read Dictionary file
		Gson gson = new Gson();
		Type dictType = new TypeToken<ConcurrentHashMap<String, String>>(){}.getType();
		dictionary = gson.fromJson(new FileReader("Dictionary.json"), dictType);
		
		
		int port = Integer.parseInt(args[0]);
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server started on port " + port);
			System.out.println("Listening...");
		} catch (IOException e) {
			System.out.println("Could not start server on port " + port);
			e.printStackTrace();
		}

		// Wait for a connection and spawn a new thread to handle a new connection
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected");
				DictionaryWorker worker = new DictionaryWorker(clientSocket, clientNumber++);
				worker.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
