/* This class encapsulates the properties and methods for a player in a game of Blackjack.
 * The methods and metadata prepare the player to be used in BlackjackGame, and solve the 
 * logistics of having up to 4 different hands/bets due to hand splitting.
 */

public class BlackjackPlayer extends Player {
	private PlayingCardDeck hand;
	private PlayingCardDeck[] splitHands; // Array of all possible hands for the player
	private int currHandIndex; // Pointer for the current hand at play
	private int[] splitBets; // Array of all the bets a player has for each active hand
	private boolean split;
	
	
	// Create the data for a new blackjack player and reset to default conditions
	BlackjackPlayer(String name, boolean human, int chips) {
		super(name, human, chips);
		this.hand = new PlayingCardDeck();
		this.splitHands = new PlayingCardDeck[]{this.hand, new PlayingCardDeck(), new PlayingCardDeck(), new PlayingCardDeck()};
		this.splitBets = new int[4];
		reset();
	}
	
	// Create the data for a blackjack player from the userPlayer
	BlackjackPlayer(Player player) {
		super(player);
		this.hand = new PlayingCardDeck();
		this.splitHands = new PlayingCardDeck[]{this.hand, new PlayingCardDeck(), new PlayingCardDeck(), new PlayingCardDeck()};
		this.splitBets = new int[4];
		reset();
	}
	
	// Reset the conditions for a new round of Blackjack after bets are finalized
	void reset() {
		paid = false;
		turn = false;
		split = false;
		for(int i = 0; i < 4; i++) {
			splitHands[i].clear();
		}
		
		currHandIndex = 0;
	}
	
	// Returns the first/original hand the player has
	PlayingCardDeck hand() {
		return hand;
	}
	
	// Returns true if the hand value is greater than 21
	boolean isBust(PlayingCardDeck hand) {
		if(handValue(hand) > 21)
			return true;
		else return false;
	}
	
	// Returns true if the starting hand value is 21
	boolean hasBlackjack() {
		if(hand.size() == 2 && handValue(hand) == 21 && !split)
			return true;
		else return false;
	}
	
	// Returns true if an ace substitutes its value for 11
	boolean isSoftHand(PlayingCardDeck hand) {
		int count = 0;
		
		for(PlayingCard card : hand) {
			if(card.rank() == 14) count += 1;
			else if(card.face()) count += 10;
			else count += card.rank();
		}
		
		if(count < handValue(hand)) return true;
		return false;
	}
	
	// Handles creating a new hand for the user and assigning its location in the array of possible hands
	int split(int hand) {
		int splitIndex;
		split = true;
		
		if(splitHands[1].isEmpty()) splitIndex = 1;
		else splitIndex = hand + 2;
		splitHands[splitIndex].draw(splitHands[hand]);
		
		return splitIndex;
	}
	
	boolean isSplit() {
		return split;
	}
	
	// Returns true if the player can split in a game of Blackjack. Rules are implemented so that cards of the same
	// value can split, including face cards, but face cards cannot split with a 10
	boolean canSplit(int hand) {
		if(hand < 2 && splitHands[hand + 2].isEmpty() && splitHands[hand].size() == 2) {
			if((splitHands[hand].peekFirst().face() && splitHands[hand].peekLast().face()))
				return true;
			else if(splitHands[hand].peekFirst().rank() == splitHands[hand].peekLast().rank())
				return true;
		}
		return false;
	}
	
	PlayingCardDeck[] splitHands() {
		return splitHands;
	}
	
	PlayingCardDeck getSplitHand(int splitIndex) {
		return splitHands[splitIndex];
	}
	
	PlayingCardDeck currHand() {
		return splitHands[currHandIndex];
	}
	
	void setCurrHandIndex(int index) {
		currHandIndex = index;
	}
	int currHandIndex() {
		return currHandIndex;
	}
	
	// Assigns the current hand pointer to the next hand, or end the player's turn if none are available. The player's
	// next active hand follows a top-down, left-right pattern when rendered.
	void nextHand() {
		if(currHandIndex == 2) currHandIndex = 0;
		else if(currHandIndex == 3 || currHandIndex == 0) currHandIndex = 1;
		else if(currHandIndex == 1) turn = false;
		if(currHand().isEmpty()) turn = false;
	}
	
	// Returns the value of all the cards in the players hand, and counts an ace as 11 instead of 1 if able
	int handValue(PlayingCardDeck hand) {
		int count = 0;
		boolean aces = false;
		
		for(PlayingCard card : hand) {
			if(card.rank() == 14) {
				count += 1;
				aces = true;
			}
			else if(card.face()) count += 10;
			else count += card.rank();
		}
		
		if(aces && count + 10 <= 21) count += 10;
		return count;
	}
	
	int[] splitBets() {
		return splitBets;
	}
	
	int getBet(int splitIndex) {
		return splitBets[splitIndex];
	}
	
	void setBet(int splitIndex, int bet) {
		splitBets[splitIndex] = bet;
	}
	
	void setPaid(boolean paid) {
		super.setPaid(paid);
		for(int i = 0; i < 4; i++)
			splitBets[i] = 0;
	}

	// Print Methods
	void printHand(){
		hand.print();
	}
	
	void printHandValue(PlayingCardDeck hand){
		System.out.println(name + ": " + handValue(hand));
	}
}
