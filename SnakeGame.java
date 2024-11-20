import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    final int BOARD_WIDTH;
    final int BOARD_HEIGHT;
    final int TILE_SIZE = 20;
    final int MOVING_Y_VALUE = TILE_SIZE * 19; // Title Screen "Enter" jumping text position

    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    Random rand = new Random();

    // Snake 1
    Tile snakeHead1;
    ArrayList<Tile> snakeBody1;

    // Snake 2
    Tile snakeHead2;
    ArrayList<Tile> snakeBody2;

    // Food
    Tile food;

    // Game Loop
    Timer gameLoop;

    // Snake 1 Velocity
    int velocityX1 = 0;
    int velocityY1 = 0;

    // Snake 2 Velocity
    int velocityX2 = 0;
    int velocityY2 = 0;

    // Game End
    boolean gameOver = false;

    // Title Screen
    boolean title = true;

    // Font
    Font pixelSport;

    // Title Screen "Enter" text declaring
    int movingY = MOVING_Y_VALUE;

    // Initializing score
    int score;

    SnakeGame(int boardWidth, int boardHeight) {
        this.BOARD_HEIGHT = boardWidth;
        this.BOARD_WIDTH = boardHeight;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black); // Background color

        // Takes keyboard input
        addKeyListener(this);
        setFocusable(true);

        // Snake 1
        snakeHead1 = new Tile(5, 5); // starting position
        snakeBody1 = new ArrayList<Tile>();

        // Snake 2
        snakeHead2 = new Tile(15, 15); // starting position
        snakeBody2 = new ArrayList<Tile>();

        food = new Tile(5, 5); // starting position for food but this will be immediately changed because we
                               // want randomized food placement

        placeFood(); // randomly places food

        // Creates font for the game
        InputStream is = getClass().getResourceAsStream("/font/PixelSport-nRVRV.ttf");
        try {
            pixelSport = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    // Draws everything on screen
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (title) {
            title(g);
        } else {
            draw(g);
        }
    }

    // Specifies how to draw the title
    public void title(Graphics g) {
        g.setColor(new Color(70, 120, 80)); // Background Color
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Sets font
        g.setFont(pixelSport);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 70F));

        // Title
        String text = "Double Trouble";
        int length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth(); // length of font
        int x = BOARD_WIDTH / 2 - length / 2; // x coordinate
        int y = TILE_SIZE * 7; // y coordinate

        // Shadow
        g.setColor(Color.BLACK);
        g.drawString(text, x + 5, y + 5);

        // Draws Title
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);

        // MENU
        g.setFont(g.getFont().deriveFont(Font.BOLD, 40F));
        text = "Press Enter to Start";
        length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();

        // Makes the "Enter" text jump
        if (movingY > MOVING_Y_VALUE) {
            movingY = MOVING_Y_VALUE;
        } else {
            movingY = MOVING_Y_VALUE + 15;
        }
        x = BOARD_WIDTH / 2 - length / 2;

        // Draws the "Enter" text
        g.setColor(Color.WHITE);
        g.drawString(text, x, movingY);

        // Makes the snakes stay in their starting position
        velocityX1 = 0;
        velocityY1 = 0;

        velocityX2 = 0;
        velocityY2 = 0;

    }

    public void draw(Graphics g) {
        // Background
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        // Grid
        // IF YOU WANT TO PUT LINES ON YOUR BACKGROUD, UNCOMMENT THE NEXT FEW LINES
        // g.setColor(Color.black);
        // for (int i = 0; i < BOARD_WIDTH / TILE_SIZE; i++) {
        // g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, BOARD_HEIGHT); // columns
        // g.drawLine(0, i * TILE_SIZE, BOARD_WIDTH, i * TILE_SIZE); // rows
        // }

        // Food
        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Snake 1
        g.setColor(new Color(129, 186, 123));
        g.fillRect(snakeHead1.x * TILE_SIZE, snakeHead1.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // SnakeBody 1
        for (int i = 0; i < snakeBody1.size(); i++) {
            Tile snakePart = snakeBody1.get(i);
            g.fillRect(snakePart.x * TILE_SIZE, snakePart.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Snake 2
        g.setColor(new Color(219, 138, 192));
        g.fillRect(snakeHead2.x * TILE_SIZE, snakeHead2.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // SnakeBody 2
        for (int i = 0; i < snakeBody2.size(); i++) {
            Tile snakePart = snakeBody2.get(i);
            g.fillRect(snakePart.x * TILE_SIZE, snakePart.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Score
        g.setColor(Color.WHITE); // color
        g.setFont(pixelSport); // font
        score = snakeBody1.size() + snakeBody2.size(); // total score

        g.setFont(g.getFont().deriveFont(Font.BOLD, 30F));
        g.drawString("Score: " + Integer.toString(score), TILE_SIZE, TILE_SIZE * 2);

        // Game Over text
        if (gameOver) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, 70F));
            String text = "Game Over";
            int length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth(); // length of font
            int x = BOARD_WIDTH / 2 - length / 2;
            int y = TILE_SIZE * 15;
            g.drawString(text, x, y);
        }
    }

    // Randomly places food
    public void placeFood() {
        food.x = rand.nextInt(BOARD_WIDTH / TILE_SIZE);
        food.y = rand.nextInt(BOARD_HEIGHT / TILE_SIZE);
    }

    // Moves Snake 1
    public void move1() {
        // If Snake 1 eats food, adds a new body part to Snake 1 and places new food
        if (collision(snakeHead1, food)) {
            snakeBody1.add(new Tile(food.x, food.y));
            placeFood();
        }

        // Moves Snake 1 Body
        for (int i = snakeBody1.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody1.get(i);
            if (i == 0) {
                // Special case for the part right after the Snake Head
                snakePart.x = snakeHead1.x;
                snakePart.y = snakeHead1.y;
            } else {
                Tile prevSnakePart = snakeBody1.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Moves Snake 1 Head
        snakeHead1.x += velocityX1;
        snakeHead1.y += velocityY1;

        // Checks collision with Snake 1 Body
        for (int i = 0; i < snakeBody1.size(); i++) {
            Tile snakePart = snakeBody1.get(i);
            if (collision(snakeHead1, snakePart)) {
                gameOver = true;
            } else if (collision(snakeHead1, snakeHead2)) {
                gameOver = true;
            }
        }

        // Checks collision with Snake 2 Body
        for (int i = 0; i < snakeBody2.size(); i++) {
            Tile snakePart = snakeBody2.get(i);
            if (collision(snakeHead1, snakePart)) {
                gameOver = true;
            }
        }

        // Checks collision with walls
        if (snakeHead1.x < 0 || snakeHead1.x * TILE_SIZE >= BOARD_WIDTH ||
                snakeHead1.y < 0 || snakeHead1.y * TILE_SIZE >= BOARD_HEIGHT) {
            gameOver = true;
        }
    }

    // Moves Snake 2
    public void move2() {
        // If Snake 2 eats food, adds a new body part to Snake 2 and places new food
        if (collision(snakeHead2, food)) {
            snakeBody2.add(new Tile(food.x, food.y));
            placeFood();
        }

        // Moves Snake 2 Body
        for (int i = snakeBody2.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody2.get(i);
            if (i == 0) {
                // Special case for the part right after the Snake Head
                snakePart.x = snakeHead2.x;
                snakePart.y = snakeHead2.y;
            } else {
                Tile prevSnakePart = snakeBody2.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Moves Snake 2 Head
        snakeHead2.x += velocityX2;
        snakeHead2.y += velocityY2;

        // Checks collision with Snake 2 Body
        for (int i = 0; i < snakeBody2.size(); i++) {
            Tile snakePart = snakeBody2.get(i);
            if (collision(snakeHead2, snakePart)) {
                gameOver = true;
            } else if (collision(snakeHead2, snakeHead1)) {
                gameOver = true;
            }
        }

        // Checks collision with Snake 1 Body
        for (int i = 0; i < snakeBody1.size(); i++) {
            Tile snakePart = snakeBody1.get(i);
            if (collision(snakeHead2, snakePart)) {
                gameOver = true;
            }
        }

        // Checks collision with walls
        if (snakeHead2.x < 0 || snakeHead2.x * TILE_SIZE >= BOARD_WIDTH ||
                snakeHead2.y < 0 || snakeHead2.y * TILE_SIZE >= BOARD_HEIGHT) {
            gameOver = true;
        }
    }

    // Detects Collision
    public boolean collision(Tile Tile1, Tile Tile2) {
        if (Tile1.x == Tile2.x && Tile1.y == Tile2.y) {
            return true;
        } else {
            return false;
        }
    }

    // Restarts the game
    public void restart() {
        // Sets everything back to default
        gameOver = false;
        title = true;
        gameLoop.start();
        score = 0;

        // Removes all the snake bodies
        snakeBody1.clear();
        snakeBody2.clear();

        // Moves the snakes back to their initial positions
        snakeHead1.x = 5;
        snakeHead1.y = 5;
        snakeHead2.x = 15;
        snakeHead2.y = 15;
    }

    // Makes the game screen move
    @Override
    public void actionPerformed(ActionEvent e) {
        move1();
        move2();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    // User Inputs
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY1 != 1) {
            // Snake 1 goes up if not going down
            velocityX1 = 0;
            velocityY1 = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY1 != -1) {
            // Snake 1 goes down if not going up
            velocityX1 = 0;
            velocityY1 = +1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX1 != 1) {
            // Snake 1 goes left if not going right
            velocityX1 = -1;
            velocityY1 = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX1 != -1) {
            // Snake 1 goes right if not going left
            velocityX1 = 1;
            velocityY1 = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_W && velocityY2 != 1) {
            // Snake 2 goes up if not going down
            velocityX2 = 0;
            velocityY2 = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_S && velocityY2 != -1) {
            // Snake 2 goes down if not going up
            velocityX2 = 0;
            velocityY2 = +1;
        } else if (e.getKeyCode() == KeyEvent.VK_A && velocityX2 != 1) {
            // Snake 2 goes left if not going right
            velocityX2 = -1;
            velocityY2 = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_D && velocityX2 != -1) {
            // Snake 2 goes right if not going left
            velocityX2 = 1;
            velocityY2 = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // Starts the game
            title = false;
            velocityX1 = 1;
            velocityX2 = -1;
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            restart();
        }
    }

    // Does nothing but "implemented"
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
