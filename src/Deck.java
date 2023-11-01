import java.util.LinkedList;
import java.util.Random;

/* This class encapsulates the generic properties and methods for a deck (LinkedList)
 * of generic Card types. The methods allow reading and writing deck data, as well as 
 * controlling visual elements such as coordinates and scaling.
 */

public class Deck<T extends Card> extends LinkedList<T>{
	protected int cardSpacing;
	protected int width;
	protected int posX;
	protected int posY;
	protected double scaleFactor;

	// Counts the duplicates of a given card in the deck
	int countDuplicates(T card) {
		int count = 0;
		for(T currCard : this)
			if(currCard == card) count++;
		return count;
	}
	
	// Returns the card at the given index in the deck
	T peek(int index) {
		if(index >= 0 && index < size())
			return get(index);
		else return null;
	}
	
	// Returns the first instance of the given card in the deck
	T peek(T card) {
		if(contains(card))
			return get(indexOf(card));
		else return null;
	}
	
	// Returns the first instance of the card with the given name
	T peek(String name) {
		for(T card : this)
			if(card.name() == name) return card;
		return null;
	}

	// Reveals all cards
	void showAll() {
		for(Card card : this)
			card.show();
	}
	
	// Conceals all cards
	void hideAll() {
		for(Card card : this)
			card.hide();
	}

	// Adds a new deck to the top of the existing deck
	void add(Deck<T> deck) {
		if(!deck.isEmpty())
			addAll(0, deck);
	}
	
	// Adds a new deck at the index in the existing deck
	void add(int index, Deck<T> deck) {
		if(!deck.isEmpty() && index >= 0 && index <= size())
			addAll(index, deck);
	}
	
	// Adds a new deck to the bottom of the existing deck
	void addLast(Deck<T> deck) {
		if(!deck.isEmpty())
			addAll(deck);
	}
	
	// Removes a number of cards starting at index
	void remove(int index, int numCards) {
		for(int i = 0; i < numCards; i++) {
			if(!isEmpty())
				remove(index);
		}
	}
	
	// Removes all occurrences of the given card
	void removeAll(T card) {
		while(contains(card))
			remove(card);
	}
	
	// Draw a card from the top of the deck
	void draw(Deck<T> deck) {
		add(deck.drawFrom());
		Audio.drawSound();
	}
	
	// Draw a card from the given index in the deck
	void draw(Deck<T> deck, int index) {
		add(deck.drawFrom(index));
		Audio.drawSound();
	}
	
	// Draw the first instance of a given card in the deck
	// Great for debugging edge cases in a game class
	void draw(Deck<T> deck, T card) {
		add(deck.drawFrom(card));
		Audio.drawSound();
	}
	
	
	// Returns and removes a card from the top of the deck
	T drawFrom() {
		if(!isEmpty())
			return removeFirst();
		else return null;
	}
	
	// Returns and removes the first occurrence of a card
	T drawFrom(T card) {
		if(contains(card))
			return remove(indexOf(card));
		else return null;
		}
		
	// Returns and removes a card from the index in the deck
	T drawFrom(int index) {
		if(index >= 0 && index < size())
			return remove(index);
		else return null;
	}
	
	// Returns and removes a card from the bottom of the deck
	T drawBottom() {
		if(!isEmpty())
			return removeLast();
		else return null;
	}
	
	// Randomizes the deck order
	void shuffle() {
		Random rand = new Random();
		for(int i = 0; i < size(); i++) {
			int index = rand.nextInt(size());
			
			T temp = get(i);
			set(i, get(index));
			set(index, temp);
		}
	}
	
	// Cut the deck at the index and swap the top and bottom portions
	void cut(int index) {
		addLast(split(index));
	}
	
	// Removes and returns the list of cards from index 0 to the specific index
	Deck<T> split(int index) {
		LinkedList<T> sublist = new LinkedList<T>(subList(0, index));
		subList(0, index).clear();
		
		Deck<T> temp = new Deck<T>();
		temp.addAll(sublist);
		return temp;
	}
	
	// Applies scaling and positioning to all cards in the deck, useful for rendering player hands in a game class
	void setBounds(int posX, int posY, int cardWidth, int cardHeight, double scaleFactor, int cardSpacing) {
		this.width = (int) ((size() * cardWidth + (size() - 1) * cardSpacing) * (scaleFactor));
		this.posX = posX;
		this.posY = posY;
		this.scaleFactor = scaleFactor;
		this.cardSpacing = cardSpacing;
		
		posX -= this.width / 2;
		for(Card card : this) {
			card.setBounds(posX, posY, cardWidth, cardHeight, scaleFactor);
			posX += card.getWidth() + cardSpacing;
		}
	}
	
	// Applies positioning only to all cards in the deck
	void setLocation(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
		posX -= width / 2;
		for(Card card : this) {
			card.setLocation(posX, posY);
			posX += card.getWidth() + cardSpacing;
		}
	}
	
	// Returns the X coordinate to the center of the deck
	int getPosX() {
		return posX;
	}
	
	// Returns the Y coordinate to the top of the deck
	int getPosY() {
		return posY;
	}

	// Returns the scale factor of the deck
	double getScaleFactor() {
		return scaleFactor;
	}
	
	// Print Methods
	void print() {
		for(T card : this) 
			System.out.print(card.name() + " ");
		System.out.print("\n");
	}

	
}

