
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Panel that contains the GUI for the output of messages to the user
 */
@SuppressWarnings("serial")
public class OutputPanel extends JPanel {

	/**
	 * Creates a MenuPanel that contains buttons to switch between ActionPanels
	 * 
	 * @param textOutput
	 *            The output stream to the server
	 */
	public OutputPanel(JTextArea textOutput) {
		JScrollPane pane = new JScrollPane(textOutput);

		textOutput.setEditable(false);
		textOutput.setLineWrap(true);

		add(pane);
	}

}
