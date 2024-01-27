import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/* UI class encapsulates the UI framework for all JPanel games used in this package, including the
 * main JFrame container, permanent UI fixtures (UIPanel), component sizes and formats, and loading
 * screen functionality. UI handles substituting different game instances in currentPanel, and 
 * executing transitions between changing panels. The main method is located here as well.
 */

public class UI {
	// Customizable class constants (only 16:9 aspect ratio has been tested)
	private static final String ICONS_PATH = "images/Icons/";
	private static final String MENU_WALLPAPER_PATH = "images/menu_wallpaper.jpg";
	private static final String LOADING_WALLPAPER_PATH = "images/loading_wallpaper.jpg";
    private static final Dimension STARTING_DIMENSION = new Dimension(1280, 720);
	private static final double ASPECT_RATIO_WIDTH = 16.0;
	private static final double ASPECT_RATIO_HEIGHT = 9.0;

	// Local variables for the main JFrame container and JPanel transitions
	private static final JFrame frame = new JFrame("Blackjack Demo");
    private static final JLayeredPane containerPane = new JLayeredPane();
    public static JPanel currentPanel;
    private static JPanel UIPanel;
	private static boolean fullscreen = false;
	private static boolean loading = false;

    // Local variables for wallpaper images
	public static Image mainWallpaper; // Loaded in memory for in MainMenu.java
	private static Image loadingWallpaper; // Loaded in memory for loading screens

    // Public variables for UI dimensions
    public static int centerX;
    public static int centerY;
	public static int buttonWidth;
	public static int buttonHeight;
	public static int cardWidth;
	public static int cardHeight;
	public static int spacerWidth;
	public static int iconWidth;
	public static int fontSize;

    // Overhead user extended by game specific subclasses. Future plans are to implement a login screen and an SQL
    // database to handle multiple accounts and user metadata management
    public static Player userPlayer = new Player("Greg", true, 5000);

    // Starts the UI to begin the application 
	public static void main(String[] args) {
		SwingUtilities.invokeLater(UI::new);
	}

    public UI() {
		// Create the main JFrame container
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize((int) (STARTING_DIMENSION.getWidth() + frame.getInsets().left + frame.getInsets().right), (int) (STARTING_DIMENSION.getHeight() + frame.getInsets().top + frame.getInsets().bottom));
	    
		// Locks the window into a set aspect ratio for rendering consistancy
	    frame.addComponentListener(new ComponentAdapter() {
	    	public void componentResized(ComponentEvent e) {
                // Re-size the container pane and JPanels in uniform with the main JFrame
	            int newWidth = frame.getWidth() - frame.getInsets().left - frame.getInsets().right;
	            int newHeight = (int) (newWidth * ASPECT_RATIO_HEIGHT / ASPECT_RATIO_WIDTH);
	            frame.setSize(newWidth + frame.getInsets().left + frame.getInsets().right, newHeight + frame.getInsets().top + frame.getInsets().bottom);
                containerPane.setSize(newWidth, newHeight);
                UIPanel.setSize(containerPane.getSize());
                currentPanel.setSize(containerPane.getSize());

				// Define public variables for UI dimensions
                centerX = newWidth / 2;
                centerY = newHeight / 2;
				buttonWidth = newWidth / 10;
				buttonHeight = buttonWidth / 2;
				cardWidth = newWidth / 10;
				cardHeight = cardWidth * 3 / 2;
				spacerWidth = newWidth / 100;
				iconWidth = newWidth / 20;
				fontSize = newWidth / 180;
	        }
	    });

		// Load the main menu wallpaper and the loading screen wallpaper once and keep in memory
		try {
            mainWallpaper = ImageIO.read(new File(MENU_WALLPAPER_PATH));
            loadingWallpaper = ImageIO.read(new File(LOADING_WALLPAPER_PATH));
        }
		catch(IOException e) { throw new RuntimeException("Failed to load wallpaper image."); }
        
        // Create the starting JPanels and add to the container
        UIPanel = new UIPanel();
        currentPanel = new MainMenu();
        containerPane.add(currentPanel, JLayeredPane.DEFAULT_LAYER);
        containerPane.add(UIPanel, JLayeredPane.PALETTE_LAYER);

        // Add the container to the JFrame and make the frame visible, ending the startup process
        frame.add(containerPane);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
	}

    // Loading methods are called in other classes before and after threading a new instance of a JPanel game and assigning it 
    // to UI.currentPanel (see the BlackjackButton ActionListener implementation in MainMenu.java for an example)

    // Bring up the loading screen and stop all current processes
    public static void startLoading() {
        System.out.println("Loading...");
        loading = true;
        frame.revalidate();
        frame.repaint();
        Audio.stopMusic();
        Audio.stopAmbience();
        containerPane.remove(currentPanel);
        currentPanel = null;
    }

    // Add the current panel back to the container and remove the loading screen
    public static void stopLoading() {
        currentPanel.setSize(containerPane.getSize());
        containerPane.add(currentPanel, JLayeredPane.DEFAULT_LAYER);
        loading = false;
        frame.revalidate();
        frame.repaint();
    }

    // Local class for building the UI Panel
    private class UIPanel extends JPanel {
        // Permanent UI fixtures declaration
        private JButton exitButton = new JButton();
        private ImageIcon exitIcon = new ImageIcon(ICONS_PATH + "exit_white.png");
        private JButton musicButton = new JButton();
        private ImageIcon musicIcon = new ImageIcon(ICONS_PATH + "music_white.png");
        private ImageIcon noMusicIcon = new ImageIcon(ICONS_PATH + "no_music_white.png");
        private JButton fullscreenButton = new JButton();
        private ImageIcon fullscreenIcon = new ImageIcon(ICONS_PATH + "fullscreen_white.png");
        private JEditorPane loadingText = new JEditorPane();
        private JEditorPane userChipsText = new JEditorPane();


        private UIPanel() {
            setOpaque(false);

            // Defines the method for returning to the main menu or exiting the application
            exitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(currentPanel instanceof MainMenu)
                        System.exit(0);
                    else {
                        // This is not a new runnable because the contents of MainMenu are already in memory,
                        // causing a brief and unnecessary flicker of the loading screen if implemented
                        startLoading();
                        GameController.destroy();
                        currentPanel = new MainMenu();
                        userPlayer = new Player(userPlayer.name(), userPlayer.isHuman(), userPlayer.getChips()); // Assign the overhead user to the parent class
                        stopLoading();
                    }
                }
            });

            // Defines the method for the music toggle
            musicButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Audio.toggleMusic();
                }
            });

            // Defines the method for the fullscreen toggle
            fullscreenButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // True fullscreen implementation for rendering efficiency (WARNING - only 16:9 aspect ratio has been tested)
                    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    fullscreen = !fullscreen;
                    frame.dispose();

                    if (fullscreen) {
                        frame.setUndecorated(true);
                        gd.setFullScreenWindow(frame);	
                    } 
                    else {
                        gd.setFullScreenWindow(null);
                        frame.setUndecorated(false);
                        frame.setSize(STARTING_DIMENSION);
                    }		
                    frame.setVisible(true);
                }
            });
            
            // Define set textboxes as not editable
            loadingText.setEditable(false);
		    userChipsText.setEditable(false);

            // Add all components to UIPanel
            add(loadingText);
            add(userChipsText);
            add(exitButton);
            add(musicButton);
            add(fullscreenButton);
        }

        // Repeating function to call UI render methods
        public void paintComponent(Graphics g) {
            // Draw loading wallpaper if in a loading screen
            if(loading) g.drawImage(loadingWallpaper, 0, 0, getWidth(), getHeight(), null);
            
            // Permanent fixtures UI setup
            exitButton.setBounds(getWidth() - iconWidth, 0, iconWidth, iconWidth);
            formatButton(exitButton, exitIcon, 0, 0, 0, 0, fontSize, !loading);

            musicButton.setBounds(getWidth() - iconWidth * 2 - spacerWidth, 0, iconWidth, iconWidth);
            if(Audio.isMusicMuted()) formatButton(musicButton, noMusicIcon, 0, 0, 0, 0, fontSize, !loading);
            else formatButton(musicButton, musicIcon, 0, 0, 0, 0, fontSize, !loading);

            fullscreenButton.setBounds(getWidth() - iconWidth * 3 - spacerWidth * 2, 0, iconWidth, iconWidth);
            formatButton(fullscreenButton, fullscreenIcon, 0, 0, 0, 0, fontSize, !loading);

            // User specific UI setup
            userChipsText.setBounds(spacerWidth, spacerWidth, buttonWidth, buttonHeight);
            formatEditorPane(userChipsText, "$" + Integer.toString(userPlayer.getChips()), fontSize, false, !loading);

            // Loading screen UI setup
            loadingText.setBounds(getWidth() / 2 - buttonWidth, getHeight() / 2 - buttonHeight, buttonWidth * 2, buttonHeight * 2);
            formatEditorPane(loadingText, "<center><br>Finding a seat...</center>", fontSize, false, loading);

            containerPane.repaint();
            containerPane.revalidate();
        }
    }

    // Custom template for all JButtons. Use null if there is no icon
    public static void formatButton(JButton button, ImageIcon buttonIcon, int r, int g, int b, int a, int fontSize, boolean visible){
        button.setForeground(Color.white);
        button.setBackground(new Color(r, g, b, a));
        button.setFont(new Font("Candara", Font.BOLD, fontSize));
        button.setFocusPainted(false);
        button.setVisible(visible);
        
        if(buttonIcon != null) {
            buttonIcon.getImage();
            button.setIcon(new ImageIcon(buttonIcon.getImage().getScaledInstance(button.getWidth(), button.getHeight(), Image.SCALE_DEFAULT)));
            button.setBorderPainted(false);
        }
    }

    // Custom template for all JEditorPanes
    public static void formatEditorPane(JEditorPane editorPane, String text, int fontSize, boolean editable, boolean visible) {
        editorPane.setContentType("text/html");
        editorPane.setEditable(editable);
        editorPane.setText("<html><b><font color='white' size=" + Integer.toString(fontSize) +">" + text + "</b></html>");
        editorPane.setOpaque(false);
        editorPane.setVisible(visible);
    }
}

