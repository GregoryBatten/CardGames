import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* This class encapsulates the central game (JPanel) navigation menu and welcome screen.
 * Future plans are to implement a login screen and an SQL database to handle multiple accounts 
 * and user metadata management
 */

public class MainMenu extends JPanel {
	// Local variables for menu objects
	private static GameController gameController;
	private static JEditorPane usernameText = new JEditorPane();
	private static JButton blackjackButton = new JButton("Blackjack");
	private static JButton pokerButton = new JButton("Texas Hold 'Em");
	private static JButton solitareButton = new JButton("");
	private static JButton mtgButton = new JButton("");
	
	// Menu Panel Constructor
	public MainMenu() {
		System.out.println("Creating main menu...");
		// Defines the method to load the blackjack game panel
		blackjackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UI.startLoading();

				// Preserves the loading screen while loading BlackjackGame assets
        		SwingUtilities.invokeLater(new Runnable() {
            		public void run() {
                		UI.currentPanel = new BlackjackGame();
                		UI.stopLoading();
            		}
        		});
			}
		});

		pokerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e ) {
				UI.startLoading();

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						gameController = new GameController(new PokerGame(), new PokerUI());
						UI.currentPanel = gameController.getView();
						UI.stopLoading();
					}
				});
			}
		});
		
		// Define set textboxes as not editable
		usernameText.setEditable(false);
		
		// Add all components to the menu panel
        add(usernameText);
		add(blackjackButton);
		add(pokerButton);
		add(solitareButton);
		add(mtgButton);

		// Start the music and ambience
		Audio.menuMusic(false);
		Audio.menuAmbience();
	}
	
	// Repeating function to call UI render methods
	public void paintComponent(Graphics g) {
		// Draw main menu wallpaper
		g.drawImage(UI.mainWallpaper, 0, 0, getWidth(), getHeight(), null);
		
		// User specific UI setup
		usernameText.setBounds(getWidth() / 2 - UI.buttonWidth * 2, UI.spacerWidth, UI.buttonWidth * 4, UI.buttonHeight * 2);
		UI.formatEditorPane(usernameText, "<center>We missed you, " + UI.userPlayer.name() + "</center>", UI.fontSize, false, true);
		
		// Game selection UI setup
		blackjackButton.setBounds(UI.spacerWidth, getHeight() - UI.buttonHeight * 2 * 3, UI.buttonWidth * 2, UI.buttonHeight * 2);
		UI.formatButton(blackjackButton, null, 0, 0, 0, 150, UI.fontSize * 3, true);

		pokerButton.setBounds(UI.spacerWidth, getHeight() - UI.buttonHeight * 2 * 2 + UI.spacerWidth, UI.buttonWidth * 2, UI.buttonHeight * 2);
		UI.formatButton(pokerButton, null, 0, 0, 0, 150, UI.fontSize * 3, true);

		solitareButton.setBounds(getWidth() - UI.buttonWidth * 2 - UI.spacerWidth, getHeight() - UI.buttonHeight * 2 * 3, UI.buttonWidth * 2, UI.buttonHeight * 2);
		UI.formatButton(solitareButton, null, 0, 0, 0, 150, UI.fontSize * 3, true);

		mtgButton.setBounds(getWidth() - UI.buttonWidth * 2 - UI.spacerWidth, getHeight() - UI.buttonHeight * 2 * 2 + UI.spacerWidth, UI.buttonWidth * 2, UI.buttonHeight * 2);
		UI.formatButton(mtgButton, null, 0, 0, 0, 150, UI.fontSize * 3, true);
	}
}
