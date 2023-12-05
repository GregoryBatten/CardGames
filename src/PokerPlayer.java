import java.util.LinkedList;

public class PokerPlayer extends Player {
    private PlayingCardDeck hand;
    private boolean check;
    private boolean fold;
    private boolean allIn;

    PokerPlayer(Player player) {
        super(player);
        hand = new PlayingCardDeck();
    }

    PokerPlayer(String name, boolean human, int chips) {
        super(name, human, chips);
        hand = new PlayingCardDeck();
    }

    protected void reset() {
        super.reset();
        hand.clear();
        check = false;
        fold = false;
        allIn = false;
    }

    PlayingCardDeck hand() {
        return hand;
    }

    protected boolean check() {
        if(isTurn() && !check && PokerGame.nextPlayer(this, 1).getBet() <= getBet()) {
            check = true;
            return true;
        }
        else {
            //System.out.println("Cannot check");
            return false;
        }
    }

    protected void setCheck(boolean check) {
        this.check = check;
    }

    protected boolean isCheck() {
        return check;
    }

    protected boolean raise(int chips) {
        if(isTurn() && getChips() >= chips && getBet() + chips >= PokerGame.nextPlayer(this, 1).getBet() + PokerGame.BIG_BLIND) {
            addBet(chips);
            setCheck(true);
            PokerGame.nextPlayer(this, 1).setCheck(false);
            return true;
        }
        else {
            //System.out.println("Cannot raise"); 
            return false;
        }
    }

    protected boolean call() {
        int lastBet = PokerGame.nextPlayer(this, 1).getBet();
        if(isTurn() && lastBet <= getBet() + getChips() && lastBet > getBet()) {
            addBet(lastBet - getBet());
            setCheck(true);
            return true;
        }
        else {
            //System.out.println("Cannot call");
            return false;
        }
    }

    protected boolean allIn() {
        if(isTurn() && !allIn) {
            allIn = true;
            addBet(getChips());
            setCheck(true);
            return true;
        }
        else {
            //System.out.println("Cannot all-in");
            return false;
        }
    }

    protected boolean isAllIn() {
        return allIn;
    }

        protected boolean fold() {
        if(isTurn() && !fold) {
            this.fold = true;
            return true;
        }
        else {
            //System.out.println("Cannot fold");
            return false;
        }
    }

    protected void setFold(boolean fold) {
        this.fold = fold;
    }

    protected boolean isFold() {
        return fold;
    }

    protected void handleTurn(LinkedList<PokerPlayer> players, PlayingCardDeck comCards) {
        if (this == UI.userPlayer) {
            System.out.println("User turn by - " + name);
            synchronized (getLock()) {
                try { 
                    System.out.println("Game thread paused");
                    getLock().wait(); 
                    System.out.println("Game thread resumed");
                } 
                catch (InterruptedException e) { System.out.println("Turn interrupted"); return; }
            }
        }

        else if(!human) {
            // Machine learning logic goes here
            if(check());
            else if(call());
            else if(allIn());
            try { Thread.sleep(1000); } 
            catch (InterruptedException e) { System.out.println("Turn interrupted"); return; }
        }
    }

    protected int evalHand(PlayingCardDeck comDeck) {
        int score = 0;
        PlayingCardDeck temp = new PlayingCardDeck(hand);
        temp.add(comDeck);
        temp.sortDesc();
        temp.print();
        
        return score;
    }
}
