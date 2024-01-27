import java.util.LinkedList;
import java.util.Random;

public abstract class PlayingCardGame {
	private static final String CARD_IMAGE_PATH = "images/PNG-cards-1.3/";

    protected static LinkedList<? extends Player> players; // Active players in the round
    protected static LinkedList<? extends Player> playersCopy; // All players at the table
    protected static PlayingCardDeck deck; // Active deck in the round
    protected static PlayingCardDeck deckCopy; // Reference deck with every card, unshuffled
    protected static Player userPlayer; // Reference for overhead user player
    protected static Player currPlayer; // Reference for current turn player
    protected static boolean quit;
    protected static Random random = new Random();

    public PlayingCardGame() {
        //System.out.println("Creating game...");
        deck = new PlayingCardDeck();
        deckCopy = new PlayingCardDeck(CARD_IMAGE_PATH);
        quit = true;
    }

    public static void resetGame() {
        deck.clear();
        deck.add(deckCopy);
        deck.hideAll();
        deck.shuffle();
    }

    public void startGame() {
        
    }
}
