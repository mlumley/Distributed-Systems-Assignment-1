
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.gson.Gson;

/**
 * Panel that contains the GUI for searching for a word in the dictionary
 */
@SuppressWarnings("serial")
public class QueryWordPanel extends JPanel {

	private Gson gson = new Gson();

	/**
	 * Creates an QueryWordPanel that can write to the output stream and popup
	 * messages
	 * 
	 * @param out
	 *            The output stream to the server
	 * @param popup
	 *            The popup manager
	 */
	public QueryWordPanel(PrintWriter out, PopupManager popup) {
		JTextField wordInput = new JTextField(20);
		JButton searchButton = new JButton("Search for word");

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String word = wordInput.getText();

				if (word.isEmpty()) {
					popup.popupErrorMessage("Please enter a word");
				} else {
					Message msgToServer = new Message(Message.MessageTypes.QUERY, word);
					out.println(gson.toJson(msgToServer));
				}
				wordInput.setText(null);
			}
		});

		wordInput.setEditable(true);

		add(wordInput);
		add(searchButton);
	}
}
