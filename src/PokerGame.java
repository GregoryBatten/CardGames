import java.util.LinkedList;

public class PokerGame extends PlayingCardGame {
    // Customizable class constants
    protected static final int BIG_BLIND = 50;
    private static final int SMALL_BLIND = BIG_BLIND / 2;
    private static final int TOTAL_PLAYERS = 6;
    private static final String[] BOT_NAMES = {"Mike", "Walter", "Jesse", "Gus", "Hank", "Skyler", "Jane", "Lydia", "Andrea", "Marie"};

    // Create pointers for every player
    private static LinkedList<PokerPlayer> players;
    private static LinkedList<PokerPlayer> playersCopy;
    private static PokerPlayer userPlayer;
    private static PokerPlayer currPlayer;
    private static PokerPlayer buttonPlayer; // The "dealer" player behind the blind players

    // Poker game variables
    private static PlayingCardDeck comCards;
    private static int pot;

    PokerGame() {
        // Initiate all players and decks
        super();
        UI.userPlayer = userPlayer = new PokerPlayer(UI.userPlayer);
        players = new LinkedList<PokerPlayer>();
        comCards = new PlayingCardDeck();
        players.add(userPlayer);
        for(int i = 1; i < TOTAL_PLAYERS; i++) {
            players.add(new PokerPlayer("Bot " + BOT_NAMES[i - 1], false, 5000));
        }
        playersCopy = new LinkedList<PokerPlayer>(players);
        buttonPlayer = players.get(random.nextInt(players.size()));

    }

    @Override
    public void startGame() {
        if(GameController.isRunning()) resetGame();
        while(GameController.isRunning() && !quit) startRound();
        if(GameController.isRunning()) payBets();
    }

    public static void resetGame() {
        PlayingCardGame.resetGame();
        comCards.clear();

        // Prep the remaining players for another round
        pot = 0;
        quit = false;
        for(int i = playersCopy.size() - 1; i >= 0; i--) {
            playersCopy.get(i).reset();
            if(playersCopy.get(i).getChips() <= 0) {
                if(playersCopy.get(i) == buttonPlayer) {
                    buttonPlayer = nextPlayer(buttonPlayer, -1); // Move the button if the player is broke
                }
                playersCopy.remove(i); // Remove the player if broke
            }
        }
        players.clear();
        players.addAll(playersCopy);
        
        if(players.size() > 1) {
            buttonPlayer = nextPlayer(buttonPlayer, -1);
            currPlayer = nextPlayer(buttonPlayer, -3);
            payBlinds();
            
            // Refresh the deck and deal
            for(int i = 0; i < players.size() * 2; i++) {
                players.get(i % players.size()).hand().draw(deck); // Deal 2 cards to each player
            }

            for(int i = 0; i < 5; i++) {
                comCards.draw(deck); // Deal 5 community cards
            }
            userPlayer.hand().showAll();
        }
        else quit = true;
    }

    protected static void startRound() {
        while(!isAllCheck() && players.size() > 1 && GameController.isRunning()) {
            currPlayer.setTurn(true);
            if(!currPlayer.isAllIn()) currPlayer.handleTurn(players, comCards);
            else currPlayer.setCheck(true);
            currPlayer.setTurn(false);
            currPlayer = nextPlayer(currPlayer, -1);
            if(nextPlayer(currPlayer, 1).isFold()) players.remove(nextPlayer(currPlayer, 1));
        }
        if(players.size() > 1) {
            if(!comCards.peek(0).visible()) {
                comCards.show(0, 3);
            }
            else if(!comCards.peek(3).visible()) {
                comCards.show(3, 4);
            }
            else if(!comCards.peek(4).visible()) {
                comCards.show(4, 5);
            }
            else {
                for(Player player : players)
                    player.hand().showAll();
                quit = true;
            }
        }
        else quit = true;

        for(PokerPlayer player : players)
        player.setCheck(false);
    }

    private static void payBlinds() {
        if(nextPlayer(buttonPlayer, -1).getChips() >= SMALL_BLIND) {
            nextPlayer(buttonPlayer, -1).payBet(SMALL_BLIND);
        } 
        else {
            nextPlayer(buttonPlayer, -1).allIn();
        }

        if(nextPlayer(buttonPlayer, -2).getChips() >= BIG_BLIND) {
            nextPlayer(buttonPlayer, -2).payBet(BIG_BLIND);
        } 
        else {
            nextPlayer(buttonPlayer, -2).allIn();
        }

        pot = nextPlayer(buttonPlayer, -1).getBet() + nextPlayer(buttonPlayer, -2).getBet();
    }

    private static void payBets() {

    }

    // Locates a player in relative position to another player
    protected static PokerPlayer nextPlayer(PokerPlayer player, int mod) {
        return players.get((players.indexOf(player) + mod + players.size()) % players.size());
    }

    private static boolean isAllCheck() {
        for(PokerPlayer player : players)
            if(!player.isCheck()) return false;
        return true;
    }

    public static LinkedList<PokerPlayer> getPlayers() {
        return playersCopy;
    }

    public static PokerPlayer getButtonPlayer() {
        return buttonPlayer;
    }

    public static PokerPlayer getCurrPlayer() {
        return currPlayer;
    }

    public static PlayingCardDeck getComCards() {
        return comCards;
    }

}
