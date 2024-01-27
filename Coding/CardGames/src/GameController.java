import javax.swing.*;

public class GameController {
    private static PlayingCardGame gameModel;
    private static JPanel gameView;
    private static Thread gameThread;
    private static boolean running;

    public GameController(PlayingCardGame gameModel, JPanel gameView) {
        System.out.println("Creating controller...");
        GameController.gameModel = gameModel;
        GameController.gameView = gameView;
    }

    public JPanel getView() {
        return gameView;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void startGame() {
        if(!running) {
            running = true;
            gameThread = new Thread(() -> {
                System.out.println("Creating game thread...");
                gameModel.startGame();
            });
            gameThread.start();
        }
    }

    public static void destroy() {
        System.out.println("Destroying controller...");
        if (running) {
            running = false;
            if (gameThread != null) {
                gameThread.interrupt();
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        gameModel = null;
        gameView = null;
        gameThread = null;
        System.out.println("Controller destroyed");
    }
}
