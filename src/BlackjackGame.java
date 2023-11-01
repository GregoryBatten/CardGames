import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/* This class encapsulates the JPanel UI and game logic for an instance of a BlackjackGame that
 * accurately implements a loopable ruleset of the casino card game Blackjack. The userPlayer is
 * inherited from the UI class and casted to a BlackjackPlayer.
 */

public class BlackjackGame extends JPanel{
	// Customizable class constants
	private static final String BLACKJACK_WALLPAPER_PATH = "images/blackjack_wallpaper.jpg";	
	private static final String CARD_IMAGE_PATH = "images/PNG-cards-1.3/";
	private static final int MAX_BET = 1000;
	private static final int MIN_BET = 50;
	
	// Local variables for game related objects
	private static BlackjackPlayer userPlayer;
	private static BlackjackPlayer house;
	private static PlayingCardDeck deckCopy;
	private static PlayingCardDeck deck;
	private static Image wallpaper;
	private static boolean quit;
	
	// Local variables for betting mechanics
	private static int newBet = 100;
	private static int newInsuranceBet;
	private static String betString; // Added for editable text pane
	private static boolean offerInsurance;

	// Local variables for user turn actions
	private JButton hitButton = new JButton("Hit me!");
	private JButton standButton = new JButton("Stand");
	private JButton doubleDownButton = new JButton("Double Down");
	private JButton splitButton = new JButton("Split");
	
	// Local variables for insurance actions
	private JButton insuranceButton = new JButton("Insurance");;
	private JButton noInsuranceButton = new JButton("No Insurance");
	private JEditorPane insuranceTextPane = new JEditorPane();

	// Local variables for bet setting actions
	private JButton betButton = new JButton("Place Bet");
	private JButton addBetButton = new JButton();
	private JButton subtractBetButton = new JButton();
	private JButton freeChipsButton = new JButton("<html><center>FREE CHIPS<br>(watch ad)</center></html>");
	private JEditorPane betTextPane = new JEditorPane();
	private JEditorPane resultsText = new JEditorPane();

	// Sets the conditions for a new game of Blackjack. Called after bets are finalized
	private void resetGame() {
		userPlayer.reset();
		house.reset();
		deck.clear();
		deck.add(deckCopy);
		deck.hideAll();
		deck.shuffle();
		quit = false;

		// This method handles animating the initial card deal
		Timer timer = new Timer(300, new ActionListener() {
			private int turnCounter = 0;
			@Override
			public void actionPerformed(ActionEvent e) {
				if(turnCounter == 4) {
					
					// Offer insurance if the dealer has a visible ace
					if(house.hand().peek().rank() == 14)
						offerInsurance = true;

					// End the round if the user has blackjack, and reveal if the house push
					else if(userPlayer.hasBlackjack()) {
						if(house.hasBlackjack()) 
							house.hand().showAll();
						payBets();
						quit = true;
					}

					// Begin the user's turn if no blackjacks are present
					else userPlayer.setTurn(true);
					((Timer) e.getSource()).stop();
				}
			
				// Adding the cards to each hand, showing when appropriate
				else{
					if(turnCounter % 2 == 0) {
						userPlayer.hand().draw(deck);
						userPlayer.hand().peekLast().show(); // reveal all user cards
					}
					else if(turnCounter == 1) {
						house.hand().draw(deck);
						house.hand().peek().show(); // reveal 1 house card
					}
					else house.hand().draw(deck);
					turnCounter++;
				}
			}
		});

		// Activates the initial card deal and the beginning of a new round
		timer.setRepeats(true);
	    timer.start();
	}
	
	// This method handles the actions taken by the AI (house) when the user turn is over
	private void houseTurn() {
		// Skip the house's turn if the user bust on their only hand
		if(userPlayer.isBust(userPlayer.hand()) && !userPlayer.isSplit()) house.setTurn(false);
		else house.setTurn(true);
		
		// This method handles animating the house's actions
		Timer timer = new Timer(600, new ActionListener() {
			int turnCounter = 0;
			@Override
			public void actionPerformed(ActionEvent e) {
				if(house.isTurn()) {
					// Wait on the first turn for smoother animations
					if(turnCounter == 0) turnCounter += 1;
					
					// Check if the house busted
					else if(house.isBust(house.hand())) house.setTurn(false); 
					
					// The house must draw when its hand value is less than 17 or a soft 17
					else if (house.handValue(house.hand()) > 16 && !(house.handValue(house.hand()) == 17 && house.isSoftHand(house.hand()))) {
						house.setTurn(false);
					}
					else house.hand().draw(deck);
					house.hand().showAll();
				}
				// Stop the timer after the house's turn, ending the current round
				if(!house.isTurn()) {
					quit = true;
					((Timer) e.getSource()).stop();
					payBets();
				}
			}
		});
		
		// Activates the house's turn
		timer.setRepeats(true);
	   	timer.start();
	}
	
	// Called between each round when the user changes their bet
	private int updateBet(int newBet, int betMin, int betMax) {
			// Lower bet to maximum
			if(newBet > betMax)
	     	   newBet = betMax;
			
			// Raise bet to minimum
			else if(newBet < betMin)
	     		newBet = betMin;
	     	
			// If user chips are insufficient, lower the bet to the remaining chips
			if(newBet > userPlayer.getChips()) newBet = userPlayer.getChips();
	     	return newBet;
	}
	
	// Called immediately after each round to pay the user for each possible hand and flag him as paid
	private void payBets() {
		if(!userPlayer.isPaid()) {
			for(int splitIndex = 0; splitIndex < 4; splitIndex++) {
				if(userPlayer.isBust(userPlayer.getSplitHand(splitIndex)) || (userPlayer.handValue(userPlayer.getSplitHand(splitIndex)) < house.handValue(house.hand()) && !house.isBust(house.hand())) || (!userPlayer.hasBlackjack() && house.hasBlackjack())) continue;
				else if(userPlayer.hasBlackjack() && !house.hasBlackjack()) userPlayer.addChips(userPlayer.getBet(0) * 5 / 2);
				else if(userPlayer.handValue(userPlayer.getSplitHand(splitIndex)) == house.handValue(house.hand())) userPlayer.addChips(userPlayer.getBet(splitIndex));
				else userPlayer.addChips(userPlayer.getBet(splitIndex) * 2);
			}

			// Setup the next bet and mark the user as paid
			newBet = updateBet(newBet, MIN_BET, MAX_BET);
			betString = Integer.toString(newBet);
			userPlayer.setPaid(true);
		}
	}
	
	// Generates the text displayed when a game ends and handles paying out bets
	// (Possible to modify method and JEditorPane resultsText to show the results of each split hand)
	private String getResultStr(PlayingCardDeck hand) {
		if (userPlayer.hand().isEmpty()) return "Welcome to Blackjack!<br>Place a bet to begin.";
		else{
			if(userPlayer.isSplit())
				return"<br>Bets paid accordingly.";
				
			else if(userPlayer.isBust(hand))
				return "<br>You bust.";
			
			else if(house.isBust(house.hand()))
				return "The dealer bust.<br>You win!";
				
			else if(userPlayer.handValue(hand) == house.handValue(house.hand()) && house.hasBlackjack() == userPlayer.hasBlackjack())
				return "<br>Push.";

			else if(userPlayer.hasBlackjack())
				return "You have blackjack.<br>You win!";
			
			else if(house.hasBlackjack())
				return "The dealer has blackjack.<br>You lose.";
		
			else if(userPlayer.handValue(hand) > house.handValue(house.hand()))
				return "You have the highest value.<br>You win!";
			
			else if(userPlayer.handValue(hand) < house.handValue(house.hand()))
				return "The dealer has the highest value.<br>You lose.";
			
			else return "ERROR";
		}
	}
	
	// Blackjack Game Constructor
	public BlackjackGame() {
		userPlayer = new BlackjackPlayer(UI.userPlayer); // Copy overhead userPlayer data into a new BlackjackPlayer
		UI.userPlayer = userPlayer; // Assign the overhead userPlayer to the new BlackjackPlayer
		house = new BlackjackPlayer("House", false, 0);
		deckCopy = new PlayingCardDeck(CARD_IMAGE_PATH);
		deck = new PlayingCardDeck();
		quit = true;
		offerInsurance = false;
		newBet = updateBet(newBet, MIN_BET, MAX_BET);
		betString = Integer.toString(newBet);
		try { wallpaper = ImageIO.read(new File(BLACKJACK_WALLPAPER_PATH)); }
		catch(IOException e1) { throw new RuntimeException("Failed to load wallpaper image."); }
		
		// Defines the method for hitting on a hand
        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		userPlayer.currHand().draw(deck);
        		userPlayer.currHand().peekLast().show();
        		
				// If the player bust, point to the next turn, either the house or a split hand
                if(userPlayer.isBust(userPlayer.currHand()))
        			userPlayer.nextHand();
        		if(!userPlayer.isTurn()) houseTurn();
            }
        });
        
		// Defines the method for standing on a hand
        standButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				Audio.knockSound();
				
				// point to the next turn, either the house or a split hand
        		userPlayer.nextHand();
        		if(!userPlayer.isTurn()) houseTurn();
            }
        });
        
		// Defines the method for doubling down on a bet
        doubleDownButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				// Handle currency exchange
            	if(userPlayer.getBet(userPlayer.currHandIndex()) <= userPlayer.getChips()) {
					userPlayer.removeChips(userPlayer.getBet(userPlayer.currHandIndex()));
            		userPlayer.setBet(userPlayer.currHandIndex(), userPlayer.getBet(userPlayer.currHandIndex()) * 2);

					// Handle game logic
            		userPlayer.currHand().draw(deck);
            		userPlayer.currHand().peekLast().show();
                	standButton.doClick(0);
            	}
            }
        });
		
		// Defines the method for spliting a hand
        splitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				// Handle currency exchange
            	if(userPlayer.getChips() >= userPlayer.getBet(userPlayer.currHandIndex())) {
					userPlayer.setTurn(false);
					int newHandIndex = userPlayer.split(userPlayer.currHandIndex());
            		userPlayer.setBet(newHandIndex, userPlayer.getBet(userPlayer.currHandIndex()));
					userPlayer.removeChips(userPlayer.getBet(userPlayer.currHandIndex()));

					// This method handles animating splitting hands
					Timer timer = new Timer(600, new ActionListener() {
					int turnCounter = 0;
					@Override
					public void actionPerformed(ActionEvent e) {
						switch(turnCounter){
							case 0: 
								userPlayer.currHand().draw(deck);
								userPlayer.currHand().peekLast().show();
								break;

							case 1:
								userPlayer.setCurrHandIndex(newHandIndex);
								userPlayer.currHand().draw(deck);
								userPlayer.currHand().peekLast().show();
								if(newHandIndex == 1) userPlayer.setCurrHandIndex(0);
								break;

							case 2:
								userPlayer.setTurn(true);
								((Timer) e.getSource()).stop();
								break;
						}
						turnCounter++;
					}
				});
		
				timer.setRepeats(true);
	    		timer.start();
            	}
            }
        });
        
		// Defines the method for placing an insurance bet
        insuranceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				// Simulate pressing enter so the bet can be validated
            	insuranceTextPane.dispatchEvent(new KeyEvent(betTextPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED));
            	
				// Insurance bet maxes at half the original bet and mins at half the original minimum bet
            	if(newInsuranceBet >= MIN_BET / 2 && newInsuranceBet <= userPlayer.getChips() && newInsuranceBet <= userPlayer.getBet(0) / 2) {
                	offerInsurance = false;
                	userPlayer.removeChips(newInsuranceBet);
                	

                	if(userPlayer.hasBlackjack()) 
						quit = true;
					
					// Handle payout and quit if house had blackjack
                	if(house.hasBlackjack()) {
                		house.hand().showAll();
                		userPlayer.addChips(newInsuranceBet * 2);
                		quit = true;
                	}
                	
					// Continue the round if there is no blackjacks
                	if(!quit) userPlayer.setTurn(true);
					else payBets();
            	}
            }
        });
        
		// Defines the method for declining insurance
        noInsuranceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	offerInsurance = false;
            	
            	if(userPlayer.hasBlackjack()) 
					quit = true;
            	
            	if(house.hasBlackjack()) {
            		house.hand().showAll();
            		quit = true;
            	}
            	
				// Continue the round if there is no blackjacks
            	if(!quit) userPlayer.setTurn(true);
            }
        });
        
		// Defines the method for the editable insurance bet value in text pane
        insuranceTextPane.addKeyListener(new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
				// Obtain the bet and processes validation
            	if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            		try {
        				newInsuranceBet = Integer.parseInt(insuranceTextPane.getText());
                    	newInsuranceBet = updateBet(newInsuranceBet, MIN_BET / 2, userPlayer.getBet(0) / 2);
                    	
                	} catch(NumberFormatException e1) {
                		System.out.println("Not a valid bet");
                	}
            	}
        	}
        });
        
        // Defines the method for placing a bet and starting a round
        betButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				// Simulate pressing enter so the bet can be validated
            	betTextPane.dispatchEvent(new KeyEvent(betTextPane, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, KeyEvent.CHAR_UNDEFINED));

				// Handle currency exchange
            	if(quit && newBet >= MIN_BET && newBet <= userPlayer.getChips() && newBet <= MAX_BET) {
            		userPlayer.setBet(0, newBet);
					userPlayer.removeChips(userPlayer.getBet(0));
            		newInsuranceBet = updateBet(newBet / 2, MIN_BET / 2, userPlayer.getBet(0) / 2);

					// Start a new round
            		resetGame();
            	}
            }
        });
        
		// Defines the method for adding increments to the bet text pane
        addBetButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(!offerInsurance) {
            		newBet = updateBet(newBet + 50, MIN_BET, MAX_BET); // Modifiable interval
        		}
        		else {
        			newInsuranceBet = updateBet(newInsuranceBet + 50, MIN_BET / 2, userPlayer.getBet(0) / 2); // Modifiable interval
        		}
				betString = Integer.toString(newBet);
        	}
        });
        
		// Defines the method for subtracting increments from the bet text pane
        subtractBetButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(!offerInsurance) {
        			newBet = updateBet(newBet - 50, MIN_BET, MAX_BET); // Modifiable interval
        		}
        		else {
        			newInsuranceBet = updateBet(newInsuranceBet - 50, MIN_BET / 2, userPlayer.getBet(0) / 2); // Modifiable interval
        		}
				betString = Integer.toString(newBet);
        	}
        });

        // Defines the method for free chips
        freeChipsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				// Future plans are to link popout YouTube videos as a placeholder for ads
            	userPlayer.addChips(5000);
            }
        });

		// Defines the method for the editable bet value in text pane
        betTextPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
				betString = betTextPane.getText().trim();
				
				// Obtain the bet and processes validation
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					try {
						newBet = Integer.parseInt(betTextPane.getText().trim());
						newBet = updateBet(newBet, MIN_BET, MAX_BET);
						
					} catch (NumberFormatException ex) {
						System.out.println("Not a valid bet");
					}
					betString = Integer.toString(newBet);
				} 
            }
        });
        
        
		// Set select displays as un-editable
		resultsText.setEditable(false);
 
		// Add all components to the game panel
        add(hitButton);
        add(standButton);
        add(doubleDownButton);
        add(splitButton);
        
        add(insuranceButton);
        add(noInsuranceButton);
        add(insuranceTextPane);
        
        add(betButton);
        add(addBetButton);
        add(subtractBetButton);
        add(freeChipsButton);
        add(betTextPane);

        add(resultsText);

		// Start the music and ambience to end the startup process
		Audio.casinoAmbience();
		Audio.casinoJazz(true);
	}

	// Repeating function to call UI render methods
	public void paintComponent(Graphics g) {

		// Draw blackjack wallpaper
		g.drawImage(wallpaper, 0, 0, getWidth(), getHeight(), null);
		
		// Set the location of each possible hand
		// posX = center of hand, posY = top of hand
		if(!userPlayer.isSplit()) { // User has only 1 hand
			userPlayer.splitHands()[0].setBounds(getWidth() / 2, getHeight() - UI.cardHeight - UI.spacerWidth, UI.cardWidth, UI.cardHeight, 1.0, UI.spacerWidth);
		}
		else { // User has up to 4 split hands
			if(userPlayer.splitHands()[0] != null) 
				userPlayer.splitHands()[0].setBounds(getWidth() / 3 - UI.cardWidth / 4, getHeight() - UI.cardHeight - UI.spacerWidth, UI.cardWidth, UI.cardHeight, 0.8, UI.spacerWidth / 2);
			if(userPlayer.splitHands()[1] != null) 
				userPlayer.splitHands()[1].setBounds(getWidth() * 2 / 3 + UI.cardWidth / 4, getHeight() - UI.cardHeight - UI.spacerWidth, UI.cardWidth, UI.cardHeight, 0.8, UI.spacerWidth / 2);
			if(userPlayer.splitHands()[2] != null) 
				userPlayer.splitHands()[2].setBounds(getWidth() / 3 - UI.cardWidth, getHeight() - UI.cardHeight * 2 - UI.spacerWidth * 2 , UI.cardWidth, UI.cardHeight, 0.8, UI.spacerWidth / 2);
			if(userPlayer.splitHands()[3] != null) 
				userPlayer.splitHands()[3].setBounds(getWidth() * 2 / 3 + UI.cardWidth, getHeight() - UI.cardHeight * 2 - UI.spacerWidth * 2, UI.cardWidth, UI.cardHeight, 0.8, UI.spacerWidth / 2);
		}
		
		house.hand().setBounds( getWidth() / 2, UI.spacerWidth, UI.cardWidth, UI.cardHeight, 1.0, UI.spacerWidth);
		
		// Draw card images for each possible hand
		for(PlayingCardDeck deck : userPlayer.splitHands()){
			for(PlayingCard card : deck) {
				g.drawImage(card.getImage(), card.getPosX(), card.getPosY(), card.getWidth(), card.getHeight(), null);
			}
		}
		for(PlayingCard card : house.hand()) {
			g.drawImage(card.getImage(), card.getPosX(), card.getPosY(), card.getWidth(), card.getHeight(), null);
		}
		
		// User turn actions UI setup
        hitButton.setBounds(userPlayer.currHand().getPosX() - UI.buttonWidth - UI.spacerWidth / 2, userPlayer.currHand().getPosY() - UI.buttonHeight - UI.spacerWidth * 2, UI.buttonWidth, UI.buttonHeight);
        UI.formatButton(hitButton, null, 10, 80, 10, 180, UI.fontSize * 2, userPlayer.isTurn());
        
        standButton.setBounds(userPlayer.currHand().getPosX() + UI.spacerWidth / 2, userPlayer.currHand().getPosY() - UI.buttonHeight - UI.spacerWidth * 2, UI.buttonWidth, UI.buttonHeight);
        UI.formatButton(standButton, null, 80, 10, 10, 180, UI.fontSize * 2, userPlayer.isTurn());
        
        doubleDownButton.setBounds(getWidth() - UI.buttonWidth, getHeight() - UI.buttonHeight, UI.buttonWidth, UI.buttonHeight);
        UI.formatButton(doubleDownButton, null, 0, 0, 0, 150, UI.fontSize * 2, userPlayer.isTurn() && userPlayer.currHand().size() == 2);
        
        splitButton.setBounds(userPlayer.currHand().getPosX() - UI.buttonWidth / 2, userPlayer.currHand().getPosY() - UI.buttonHeight * 2 - UI.spacerWidth * 3, UI.buttonWidth, UI.buttonHeight);
        UI.formatButton(splitButton, null, 0, 0, 0, 150, UI.fontSize * 2, userPlayer.isTurn() && userPlayer.canSplit(userPlayer.currHandIndex()));
        
        // Insurance actions UI setup
        insuranceButton.setBounds(getWidth() - UI.buttonWidth, getHeight() - UI.buttonHeight, UI.buttonWidth, UI.buttonHeight);
        UI.formatButton(insuranceButton, null, 0, 0, 0, 150, UI.fontSize * 2, offerInsurance);
        
        noInsuranceButton.setBounds(getWidth() / 2 - UI.buttonWidth / 2, getHeight() / 2 + UI.spacerWidth * 2, UI.buttonWidth, UI.buttonHeight);
        UI.formatButton(noInsuranceButton, null, 0, 0, 0, 150, UI.fontSize * 2, offerInsurance);
        
        insuranceTextPane.setBounds(betButton.getX(), betButton.getY() - betButton.getHeight(), betButton.getWidth(), betButton.getHeight());
        UI.formatEditorPane(insuranceTextPane, "<center>" + Integer.toString(newInsuranceBet) + "</center>", UI.fontSize, quit, offerInsurance);
        
        // Betting actions UI setup
        betButton.setBounds(getWidth() - UI.buttonWidth, getHeight() - UI.buttonHeight, UI.buttonWidth, UI.buttonHeight);
        UI.formatButton(betButton, null, 0, 0, 0, 150, UI.fontSize * 2, quit);
        
        addBetButton.setBounds(getWidth() - UI.buttonWidth - UI.buttonHeight / 2, getHeight() - UI.buttonHeight, UI.buttonHeight / 2, UI.buttonHeight / 2);
        UI.formatButton(addBetButton, null, 10, 80, 10, 150, UI.fontSize, quit || offerInsurance);
        
        subtractBetButton.setBounds(getWidth() - UI.buttonWidth - UI.buttonHeight / 2, getHeight() - UI.buttonHeight / 2, UI.buttonHeight / 2, UI.buttonHeight / 2);
        UI.formatButton(subtractBetButton, null, 80, 10, 10, 150, UI.fontSize, quit || offerInsurance);
        
        freeChipsButton.setBounds(0, getHeight() - UI.buttonHeight, UI.buttonWidth, UI.buttonHeight);
        UI.formatButton(freeChipsButton, null, 0, 0, 0, 150, UI.fontSize * 2, true);
        
        betTextPane.setBounds(betButton.getX(), betButton.getY() - betButton.getHeight(), betButton.getWidth(), betButton.getHeight());
        if(quit) UI.formatEditorPane(betTextPane, "<center>" + betString + "</center>", UI.fontSize, quit, !offerInsurance);
        else UI.formatEditorPane(betTextPane, "<center>" + Integer.toString(userPlayer.getBet(0) + userPlayer.getBet(1) + userPlayer.getBet(2) + userPlayer.getBet(3)) + "</center>", UI.fontSize, quit, !offerInsurance);
        
        // Permanent fixtures UI setup
		resultsText.setBounds(getWidth() / 2 - UI.buttonWidth * 2, getHeight() / 2 - UI.buttonHeight, UI.buttonWidth * 4, UI.buttonHeight * 2);
		UI.formatEditorPane(resultsText, "<center><br>" + getResultStr(userPlayer.hand()) + "</center>", (int) (UI.fontSize * 0.86), false, quit);
	}
}