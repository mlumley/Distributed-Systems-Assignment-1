
/**
 * COMP90015 : Distributed Systems - Assignment 1
 * Multi-threaded Dictionary Server
 * 
 * @author Michael Lumley <mlumley@student.unimelb.edu.au> : 695059
 */

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Panel that contains the GUI for the menu to switch between actionPanels
 */
@SuppressWarnings("serial")
public class MenuPanel extends JPanel {

	private static final String QUERY_HELP_TEXT = "Enter a word to search for it";
	private static final String ADD_HELP_TEXT = "Enter a word and it's definitions. Place other definitions on a newline";
	private static final String DELETE_HELP_TEXT = "Enter a word to delete it";

	/**
	 * Creates a MenuPanel that contains buttons to switch between ActionPanels
	 * 
	 * @param actionPanels
	 *            The actionPanels
	 * @param textOutput
	 *            The output stream to the server
	 */
	public MenuPanel(ActionPanels actionPanels, JTextArea textOutput) {
		JPanel menu = new JPanel();
		JButton query = new JButton("Query");
		JButton add = new JButton("Add");
		JButton delete = new JButton("Delete");
		JLabel helpText = new JLabel(QUERY_HELP_TEXT);

		query.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) actionPanels.getLayout();
				cl.show(actionPanels, "queryPanel");
				helpText.setText(QUERY_HELP_TEXT);
				textOutput.setText(null);
			}
		});

		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) actionPanels.getLayout();
				cl.show(actionPanels, "addPanel");
				helpText.setText(ADD_HELP_TEXT);
				textOutput.setText(null);
			}
		});

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CardLayout cl = (CardLayout) actionPanels.getLayout();
				cl.show(actionPanels, "deletePanel");
				helpText.setText(DELETE_HELP_TEXT);
				textOutput.setText(null);
			}
		});

		menu.add(query);
		menu.add(add);
		menu.add(delete);

		add(menu);
		add(helpText);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}

}
