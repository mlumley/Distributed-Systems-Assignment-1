
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.awt.CardLayout;
import java.io.PrintWriter;

import javax.swing.JPanel;

/**
 * Panel that contains panels for actions the user can make
 */
@SuppressWarnings("serial")
public class ActionPanels extends JPanel {

	/**
	 * Creates an ActionPanel that can write to the output stream and popup messages
	 * 
	 * @param out
	 *            The output stream to the server
	 * @param popup
	 *            The popup manager
	 */
	public ActionPanels(PrintWriter out, PopupManager popup) {
		QueryWordPanel queryPanel = new QueryWordPanel(out, popup);
		AddWordPanel addPanel = new AddWordPanel(out, popup);
		DeleteWordPanel deletePanel = new DeleteWordPanel(out, popup);

		CardLayout cardLayout = new CardLayout();

		setLayout(cardLayout);

		add(queryPanel, "queryPanel");
		add(addPanel, "addPanel");
		add(deletePanel, "deletePanel");

		cardLayout.show(this, "queryPanel");
	}

}
