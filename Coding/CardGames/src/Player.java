/* This class encapsulates the properties and methods for a generic player,
 * intended to be extended by game-specific player classes. Universal player data
 * includes signaling when to play (turn), spendable currency (chips), and identifiers
 * such as name and the human or AI boolean
 */

public class Player {
	protected String name;
	protected boolean human;
	protected Deck<?> hand;
	protected int chips;
	protected int bet;
	protected boolean paid;
	protected boolean turn;
    protected static final Object lock = new Object();
	
	// Creates an empty player
	Player() {}

	// Creates a player with set attributes
	Player(String name, boolean human, int chips) {
		this.name = name;
		this.human = human;
		this.chips = chips;
		this.hand = new Deck<>();
		this.bet = 0;
		this.paid = true;
		this.turn = false;
	}

	// Creates a player that is a copy of another player
	Player(Player player) {
		this.name = player.name;
		this.human = player.human;
		this.chips = player.chips;
		this.hand = player.hand;
		this.bet = player.bet;
		this.paid = player.paid;
		this.turn = player.turn;
	}
	
	// Reset the player to default conditions
	void reset() {
		this.hand.clear();
		this.paid = false;
		this.turn = false;
	}
	
	// Access and modifier methods
	String name() {
		return this.name;
	}
	
	boolean isHuman() {
		return this.human;
	}
	
	Deck<?> hand(){
		return this.hand;
	}
	
	int getChips() {
		return this.chips;
	}

	void setChips(int amount) {
		this.chips = amount;
	}
	
	void addChips(int amount) {
		this.chips += amount;
	}
	
	int removeChips(int amount) {
		this.chips -= amount;
		return amount;
	}
	
	int getBet() {
		return this.bet;
	}

	void setBet(int amount) {
		bet = amount;
	}

	void addBet(int amount) {
		bet += amount;
		chips -= amount;
	}

	int payBet(int amount) {
		setBet(removeChips(amount));
		return getBet();
	}
	
	void setPaid(boolean paid) {
		if(paid) bet = 0;
		this.paid = paid;
	}
	
	boolean isPaid() {
		return this.paid;
	}
	
	void setTurn(boolean turn) {
		this.turn = turn;
	}
	
	boolean isTurn() {
		return this.turn;
	}
	
	static Object getLock() {
		return lock;
	}
}
