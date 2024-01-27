/* This class encapsulates the properties and methods for a PlayingCard object.
 * PlayingCard represents the classic playing card used in playing card decks for
 * many generic card games. 
 */

public class PlayingCard extends Card {
	public static final int DIAMONDS = 0;
	public static final int CLUBS = 1;
	public static final int HEARTS = 2;
	public static final int SPADES = 3;
	public static final int RED = 0;
	public static final int BLACK = 1;

	private int rank;
	private int suit;
	private int color;
	private boolean face;
	
	// Create a playing card with set attributes
	PlayingCard(String name, int rank, int suit){
		this.name = name;
		this.rank = rank;
		this.suit = suit;
		color = suit % 2;
		face = rank > 10 && rank < 14;
		fileName = name;
		visible = false;
		
		switch(suit) {
		case DIAMONDS:	fileName += "_of_diamonds.png";
		break;
		case CLUBS:		fileName += "_of_clubs.png";
		break;
		case HEARTS:	fileName += "_of_hearts.png";
		break;
		case SPADES:	fileName += "_of_spades.png";
		break;
		}
	}
	
	// Create a playing card that is a copy of another playing card
	PlayingCard(PlayingCard card){
		super(card);
		rank = card.rank;
		suit = card.suit;
		color = card.color;
		face = card.face;
	}
	
	// Access Methods
	int rank() {
		return rank;
	}
	int suit() {
		return suit;
	}
	
	boolean face() {
		return face;
	}
	
	// Print Methods
	@Override void print(){
		String ncolor;
		String nsuit;
		
		if(color == RED) ncolor = "Red"; 
		else ncolor = "Black";
		
		if(suit == DIAMONDS) nsuit = "Diamonds"; 
		else if(suit == CLUBS) nsuit = "Clubs"; 
		else if(suit == HEARTS) nsuit = "Hearts"; 
		else if(suit == SPADES) nsuit = "Spades"; 
		else nsuit = "Other";
		
		System.out.println("name: " + name + "\nrank: " + rank + "\nsuit: " + nsuit + "\ncolor: " + ncolor + "\nface: " + face);
	}
}
