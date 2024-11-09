import javax.swing.JFrame;

public class Snake {
    final static int BOARD_WIDTH = 600;
    final static int BOARD_HEIGHT = 600;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake"); // new Frame
        frame.setVisible(true); // make Frame visible

        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT); // frame size
        frame.setResizable(false); // cannot be made bigger or smaller
        frame.setLocationRelativeTo(null); // screen at center

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit button

        SnakeGame snakeGame = new SnakeGame(BOARD_WIDTH, BOARD_HEIGHT); // game window
        frame.add(snakeGame);
        frame.pack(); // title bar not included in game window
        snakeGame.requestFocus(); // listens to key presses
    }
}