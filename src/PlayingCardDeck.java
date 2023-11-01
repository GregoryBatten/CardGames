import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/* This class encapsulates the properties and methods for a deck of PlayingCards.
 * PlayingCardDeck represents the classic, 4 suit, 52-card set for common casino
 * games. Methods procedurally build the deck and assign image data in memory.
 */

public class PlayingCardDeck extends Deck<PlayingCard>{

	// Creates an empty PlayingCardDeck
	public PlayingCardDeck() {}
	
	// Creates a copy of another PlayingCardDeck
	public PlayingCardDeck(PlayingCardDeck deck) {
		for(PlayingCard card : deck)
			add(new PlayingCard(card));
	}
	
	// Creates a standard 52 card deck and assigns images to all the cards
	public PlayingCardDeck(String imageParentPath) {
		fillStandard();
		setImages(imageParentPath);
	}
	
	// Procedurally assign images for the cards using the given path and card file names
	void setImages(String imageParentPath){
		for(PlayingCard card : this) {
				try {
					card.setImage(ImageIO.read(new File(imageParentPath + card.fileName())), true);
					card.setImage(ImageIO.read(new File(imageParentPath + "card_back.png")), false);
					
				} catch (IOException e) {
					throw new RuntimeException("Failed to load card image.");
				}
		}
	}

	// Replaces the deck with a standard 52 playing card deck, not shuffled
	void fillStandard() {
		clear();
		for(int i = 0; i < 4; i++) {
			add(new PlayingCard("ace", 14, i));
			add(new PlayingCard("king", 13, i));
			add(new PlayingCard("queen", 12, i));
			add(new PlayingCard("jack", 11, i));
			add(new PlayingCard("10", 10, i));
			add(new PlayingCard("9", 9, i));
			add(new PlayingCard("8", 8, i));
			add(new PlayingCard("7", 7, i));
			add(new PlayingCard("6", 6, i));
			add(new PlayingCard("5", 5, i));
			add(new PlayingCard("4", 4, i));
			add(new PlayingCard("3", 3, i));
			add(new PlayingCard("2", 2, i));
		}
	}
	
	// Replaces the deck with playing cards and allows a custom amount of suits. not shuffled
	void fill(int numSuits) {
		clear();
		for(int i = 0; i < numSuits; i++) {
			add(new PlayingCard("ace", 14, i));
			add(new PlayingCard("king", 13, i));
			add(new PlayingCard("queen", 12, i));
			add(new PlayingCard("jack", 11, i));
			add(new PlayingCard("10", 10, i));
			add(new PlayingCard("9", 9, i));
			add(new PlayingCard("8", 8, i));
			add(new PlayingCard("7", 7, i));
			add(new PlayingCard("6", 6, i));
			add(new PlayingCard("5", 5, i));
			add(new PlayingCard("4", 4, i));
			add(new PlayingCard("3", 3, i));
			add(new PlayingCard("2", 2, i));
		}
	}
	
	// Counts the number of cards with the same suit
	int countSuit(int suit) {
		int count = 0;
		for(PlayingCard i : this)
			if((i.suit() == suit)) count++;
		return count;
	}
}
