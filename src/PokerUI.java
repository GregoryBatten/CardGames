import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public class PokerUI extends JPanel {
    private static final String POKER_WALLPAPER_PATH = "images/blackjack_wallpaper.jpg";
    private static final String ICONS_PATH = "images/Icons/PokerIcons/";

    private static PokerPlayer userPlayer;
    private static Image wallpaper;
    private static JButton startButton = new JButton("Start");
    private static JButton foldButton = new JButton("Fold");
    private static JButton checkButton = new JButton("Check");
    private static JButton callButton = new JButton("Call");
    private static JButton raiseButton = new JButton("Raise");

    private static LinkedList<JEditorPane> namePlates = new LinkedList<JEditorPane>();
    private static Image dealerIcon;

    public PokerUI() {
        System.out.println("Creating poker UI...");
        userPlayer = (PokerPlayer) UI.userPlayer;
        try { 
            wallpaper = ImageIO.read(new File(POKER_WALLPAPER_PATH)); 
            dealerIcon = ImageIO.read(new File(ICONS_PATH + "chip.png"));
        }
		catch(IOException e1) { throw new RuntimeException("Failed to load wallpaper image."); }

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                GameController.startGame();
                startButton.setEnabled(true);
            }
        });

        foldButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Fold clicked");
                foldButton.setEnabled(false);
                if(userPlayer.isTurn()) {

                    // Player event
                    userPlayer.setFold(true);
                    synchronized (Player.getLock()) {
                        Player.getLock().notifyAll();
                    }
                }
                foldButton.setEnabled(true);
            }
        });

        checkButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
                System.out.println("Check clicked");
                checkButton.setEnabled(false);
                if(userPlayer.isTurn()) {

                    // Player event
                    if(userPlayer.check()) {
                        synchronized (Player.getLock()) {
                            Player.getLock().notifyAll();
                        }
                    }
                }
                checkButton.setEnabled(true);
            }
        });

        callButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Call clicked");
                callButton.setEnabled(false);
                if(userPlayer.isTurn()) {

                    // Player event
                    if(userPlayer.call()){
                        synchronized (Player.getLock()) {
                            Player.getLock().notifyAll();
                        }
                    }
                }
                callButton.setEnabled(true);
            }
        });

        raiseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Raise clicked");
                raiseButton.setEnabled(false);
                if(userPlayer.isTurn()) {

                    // Player event
                    if(userPlayer.raise(50)){
                        synchronized (Player.getLock()) {
                            Player.getLock().notifyAll();
                        }
                    }
                }
                raiseButton.setEnabled(true);
            }
        });

        add(startButton);
        add(foldButton);
        add(checkButton);
        add(callButton);
        add(raiseButton);
        namePlates.clear();
    }

    // Repeating function to call UI render methods
	public void paintComponent(Graphics g) {
		// Draw poker wallpaper
		g.drawImage(wallpaper, 0, 0, getWidth(), getHeight(), null);

        PokerGame.getComCards().setBounds(UI.centerX, UI.centerY, UI.cardWidth, UI.cardHeight, 0.8, UI.spacerWidth);
        for(PlayingCard card : PokerGame.getComCards()) {
            g.drawImage(card.getImage(), card.getPosX(), card.getPosY(), card.getWidth(), card.getHeight(),  null);
        }

        LinkedList<PokerPlayer> bottomRow = new LinkedList<PokerPlayer>(PokerGame.getPlayers().subList(0, PokerGame.getPlayers().size() / 2));
        LinkedList<PokerPlayer> topRow = new LinkedList<PokerPlayer>(PokerGame.getPlayers().subList(PokerGame.getPlayers().size() / 2, PokerGame.getPlayers().size()));
        if(PokerGame.getPlayers().size() != namePlates.size()) updateNames();

        for(int i = 0; i < PokerGame.getPlayers().size(); i++) {
            PokerPlayer player = PokerGame.getPlayers().get(i);
            if(i < bottomRow.size()) {
                player.hand().setBounds((int) (getWidth() * (i + 1) / (bottomRow.size() + 1) - (i - ((bottomRow.size() - 1) / 2.0)) * UI.spacerWidth), getHeight() - UI.spacerWidth, UI.cardWidth, UI.cardHeight, 0.7, UI.spacerWidth);
                namePlates.get(i).setBounds(player.hand().getPosX() - UI.buttonWidth, player.hand().getPosY() - UI.cardWidth, UI.buttonWidth * 2, UI.buttonHeight);
                UI.formatEditorPane(namePlates.get(i), "<center>" + player.name() + "<br>Bet=" + player.getBet() + "</center>", UI.fontSize * 3 / 4, false, true);
            }

            else {
                player.hand().setBounds((int) (getWidth() * (PokerGame.getPlayers().size() - i) / (topRow.size() + 1) + (i - bottomRow.size() - ((topRow.size() - 1) / 2.0)) * UI.spacerWidth), UI.spacerWidth, UI.cardWidth, UI.cardHeight, 0.7, UI.spacerWidth);
                namePlates.get(i).setBounds(player.hand().getPosX() - UI.buttonWidth, player.hand().getPosY() + UI.cardWidth - UI.buttonHeight / 2, UI.buttonWidth * 2, UI.buttonHeight);
                UI.formatEditorPane(namePlates.get(i), "<center>" + player.name() + "<br>Bet=" + player.getBet() + "</center>", UI.fontSize * 3 / 4, false, true);
            }

            for(int j = 0; j < player.hand().size(); j++) {
                g.drawImage(player.hand().peek(j).getImage(), player.hand().peek(j).getPosX(), player.hand().peek(j).getPosY(), player.hand().peek(j).getWidth(), player.hand().peek(j).getHeight(), null);
            }

            if(player == PokerGame.getCurrPlayer()) {
                if(bottomRow.contains(player)) {
                    g.drawImage(dealerIcon, player.hand().getPosX() - UI.iconWidth / 3, player.hand().getPosY() - UI.cardHeight, UI.iconWidth * 2 / 3, UI.iconWidth * 2 / 3, null);
                }
                else {
                    g.drawImage(dealerIcon, player.hand().getPosX() - UI.iconWidth / 3, player.hand().getPosY() + UI.cardHeight - UI.iconWidth * 2 / 3, UI.iconWidth * 2 / 3, UI.iconWidth * 2 / 3, null);
                }
            }
        }
    }

    private void updateNames() {
        namePlates.clear();
        for (int i = 0; i < PokerGame.getPlayers().size(); i++) {
            JEditorPane newPlate = new JEditorPane();
            namePlates.add(newPlate);
            add(newPlate);
        }
    }
}
