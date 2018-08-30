import java.util.ArrayList;

public class Message {
	public String type;
	public ArrayList<String> payload;
	
	public Message(String type, String payload) {
		this.type = type;
		this.payload = new ArrayList<String>();
		this.payload.add(payload);
	}
	
	public Message(String type, ArrayList<String> payload) {
		this.type = type;
		this.payload = payload;
	}

	public String toString() {
		return "Type: " + type + ", Payload: " + payload.toString();
	}
}
