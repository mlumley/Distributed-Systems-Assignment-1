
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
 * Panel that contains the GUI for deleting a word from the dictionary
 */
@SuppressWarnings("serial")
public class DeleteWordPanel extends JPanel {

	Gson gson = new Gson();

	/**
	 * Creates an DeleteWordPanel that can write to the output stream and popup
	 * messages
	 * 
	 * @param out
	 *            The output stream to the server
	 * @param popup
	 *            The popup manager
	 */
	public DeleteWordPanel(PrintWriter out, PopupManager popup) {
		JTextField wordInput = new JTextField(20);
		JButton deleteButton = new JButton("Delete word");

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String word = wordInput.getText().toLowerCase();
				Message msgToServer = new Message(Message.MessageTypes.DELETE, word);

				out.println(gson.toJson(msgToServer));
				wordInput.setText(null);
			}
		});

		wordInput.setEditable(true);

		add(wordInput);
		add(deleteButton);
	}

}
