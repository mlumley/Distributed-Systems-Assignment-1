
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.gson.Gson;

/**
 * Panel that contains the GUI for adding a new word to the dictionary
 */
@SuppressWarnings("serial")
public class AddWordPanel extends JPanel {

	private Gson gson = new Gson();

	/**
	 * Creates an AddWordPanel that can write to the output stream and popup
	 * messages
	 * 
	 * @param out
	 *            The output stream to the server
	 * @param popup
	 *            The popup manager
	 */
	public AddWordPanel(PrintWriter out, PopupManager popup) {
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
					popup.popupErrorMessage("Remember to add both the word and definitions");
				} else {
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

		add(textPanel);
		add(buttonPanel);

	}
}
