import java.awt.Image;

/* This class encapsulates the generic properties and methods for a card object. 
 * The methods allow reading and writing card data, as well as controlling visual 
 * elements such as coordinates and scaling.
 */

public class Card {
	protected String name;
	protected String fileName;
	protected Boolean visible;
	protected Image frontImage;
	protected static Image backImage;
	
	int posX;
	int posY;
	int cardWidth;
	int cardHeight;
	double scaleFactor;
	
	// Creates an empty card
	Card(){
		name = "";
		fileName = "";
		visible = false;
		scaleFactor = 1.0;
	}
	
	// Creates a card with a name
	Card(String name){
		this.name = name;
		fileName = "";
		visible = false;
	}
	
	// Creates a card that is a copy of another card
	Card(Card card){
		name = card.name;
		fileName = card.fileName;
		visible = card.visible;
		frontImage = card.frontImage;
		
		posX = card.posX;
		posY = card.posY;
		cardWidth = card.cardWidth;
		cardHeight = card.cardHeight;
		scaleFactor = card.scaleFactor;
	}
	
	String name(){
		return name;
	}
	
	String fileName() {
		return fileName;
	}
	
	Boolean visible() {
		return visible;
	}
	
	// Mark the card as "face up"
	void show() {
		visible = true;
	}
	
	// Mark the card as "face down"
	void hide() {
		visible = false;
	}
	
	// Returns the image that corresponds with the visible side of the card
	Image getImage() {
		if(visible) return frontImage;
		else return backImage;
	}
	
	// Assign the image for either the front of back of a card
	void setImage(Image image, boolean front){
		if(front)
			frontImage = image;
		else 
			backImage = image;
	}
	
	// Applies scaling and positioning to the card
	void setBounds(int posX, int posY, int width, int height, double scaleFactor) {
		this.posX = posX;
		this.posY = posY;
		this.cardWidth = (int) (width * scaleFactor);
		this.cardHeight = (int) (height * scaleFactor);
		this.scaleFactor = scaleFactor;
	}
	
	// Applies positioning only to the card
	void setLocation(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	int getPosX(){
		return posX;
	}
	
	void setPosX(int posX) {
		this.posX = posX;
	}
	
	int getPosY(){
		return posY;
	}
	
	void setPosY(int posY) {
		this.posY = posY;
	}
	
	int getWidth() {
		return cardWidth;
	}
	
	void setWidth(int width) {
		this.cardWidth = (int) (width * scaleFactor);
	}
	
	int getHeight() {
		return cardHeight;
	}
	
	void setHeight(int height) {
		this.cardHeight = (int) (height * scaleFactor);
	}
	
	double getScaleFactor(){
		return scaleFactor;
	}
	
	void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
		cardWidth = (int) (cardWidth * scaleFactor);
		cardHeight = (int) (cardHeight * scaleFactor);
	}
	
	// Print Methods
	void print() {
		System.out.println("failed");
	}
	
	void printName() {
		System.out.println(name);
	}
}
