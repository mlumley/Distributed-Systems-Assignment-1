
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class PopupManager {

	JFrame frame = null;

	/**
	 * Creates a PopupManager linked to a JFrame
	 * 
	 * @param frame
	 */
	public PopupManager(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Create a popup error message
	 * 
	 * @param msg
	 *            The message
	 */
	public void popupErrorMessage(String msg) {
		JOptionPane.showMessageDialog(frame, "Error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
