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
    final int MOVING_Y_VALUE = TILE_SIZE * 19;

    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    Random rand = new Random();

    Tile snakeHead1;
    ArrayList<Tile> snakeBody1;

    Tile snakeHead2;
    ArrayList<Tile> snakeBody2;

    Tile food;

    Timer gameLoop;

    int velocityX1 = 0;
    int velocityY1 = 0;

    int velocityX2 = 0;
    int velocityY2 = 0;

    boolean gameOver = false;

    boolean title = true;
    Font pixelSport;

    int movingY = MOVING_Y_VALUE;

    int score;

    SnakeGame(int boardWidth, int boardHeight) {
        this.BOARD_HEIGHT = boardWidth;
        this.BOARD_WIDTH = boardHeight;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead1 = new Tile(5, 5);
        snakeBody1 = new ArrayList<Tile>();

        snakeHead2 = new Tile(15, 15);
        snakeBody2 = new ArrayList<Tile>();

        food = new Tile(5, 5);

        placeFood();

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

        gameLoop = new Timer(200, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (title) {
            title(g);
        } else {
            draw(g);
        }
    }

    public void title(Graphics g) {
        g.setColor(new Color(70, 120, 80));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setFont(pixelSport);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 70F));
        String text = "Double Trouble";
        int length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
        int x = BOARD_WIDTH / 2 - length / 2;
        int y = TILE_SIZE * 7;

        // Shadow
        g.setColor(Color.BLACK);
        g.drawString(text, x + 5, y + 5);

        // Text
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);

        // MENU
        g.setFont(g.getFont().deriveFont(Font.BOLD, 40F));
        text = "Press Enter to Start";
        length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
        if (movingY > MOVING_Y_VALUE) {
            movingY = MOVING_Y_VALUE;
        } else {
            movingY = MOVING_Y_VALUE + 15;
        }
        x = BOARD_WIDTH / 2 - length / 2;

        g.setColor(Color.WHITE);
        g.drawString(text, x, movingY);

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
        // g.setColor(Color.black);
        for (int i = 0; i < BOARD_WIDTH / TILE_SIZE; i++) {
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, BOARD_HEIGHT); // columns
            g.drawLine(0, i * TILE_SIZE, BOARD_WIDTH, i * TILE_SIZE); // rows
        }

        // Food
        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Snake1
        g.setColor(new Color(129, 186, 123));
        g.fillRect(snakeHead1.x * TILE_SIZE, snakeHead1.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // SnakeBody1
        for (int i = 0; i < snakeBody1.size(); i++) {
            Tile snakePart = snakeBody1.get(i);
            g.fillRect(snakePart.x * TILE_SIZE, snakePart.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Snake
        g.setColor(new Color(219, 138, 192));
        g.fillRect(snakeHead2.x * TILE_SIZE, snakeHead2.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // SnakeBody
        for (int i = 0; i < snakeBody2.size(); i++) {
            Tile snakePart = snakeBody2.get(i);
            g.fillRect(snakePart.x * TILE_SIZE, snakePart.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        g.setColor(Color.WHITE);
        g.setFont(pixelSport);
        score = snakeBody1.size() + snakeBody2.size();
        g.setFont(g.getFont().deriveFont(Font.BOLD, 30F));
        g.drawString("Score: " + Integer.toString(score), TILE_SIZE, TILE_SIZE * 2);
        if (gameOver) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, 70F));
            String text = "Game Over";
            int length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
            int x = BOARD_WIDTH / 2 - length / 2;
            int y = TILE_SIZE * 15;
            g.drawString(text, x, y);
        }
    }

    public void placeFood() {
        food.x = rand.nextInt(BOARD_WIDTH / TILE_SIZE);
        food.y = rand.nextInt(BOARD_HEIGHT / TILE_SIZE);
    }

    public void move1() {

        if (collision(snakeHead1, food)) {
            snakeBody1.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody1.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody1.get(i);
            if (i == 0) {
                snakePart.x = snakeHead1.x;
                snakePart.y = snakeHead1.y;
            } else {
                Tile prevSnakePart = snakeBody1.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        snakeHead1.x += velocityX1;
        snakeHead1.y += velocityY1;

        for (int i = 0; i < snakeBody1.size(); i++) {
            Tile snakePart = snakeBody1.get(i);
            if (collision(snakeHead1, snakePart)) {
                gameOver = true;
            } else if (collision(snakeHead1, snakeHead2)) {
                gameOver = true;
            }
        }

        for (int i = 0; i < snakeBody2.size(); i++) {
            Tile snakePart = snakeBody2.get(i);
            if (collision(snakeHead1, snakePart)) {
                gameOver = true;
            }
        }

        if (snakeHead1.x < 0 || snakeHead1.x * TILE_SIZE >= BOARD_WIDTH ||
                snakeHead1.y < 0 || snakeHead1.y * TILE_SIZE >= BOARD_HEIGHT) {
            gameOver = true;
        }
    }

    public void move2() {

        if (collision(snakeHead2, food)) {
            snakeBody2.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody2.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody2.get(i);
            if (i == 0) {
                snakePart.x = snakeHead2.x;
                snakePart.y = snakeHead2.y;
            } else {
                Tile prevSnakePart = snakeBody2.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        snakeHead2.x += velocityX2;
        snakeHead2.y += velocityY2;

        for (int i = 0; i < snakeBody2.size(); i++) {
            Tile snakePart = snakeBody2.get(i);
            if (collision(snakeHead2, snakePart)) {
                gameOver = true;
            } else if (collision(snakeHead2, snakeHead1)) {
                gameOver = true;
            }
        }

        for (int i = 0; i < snakeBody1.size(); i++) {
            Tile snakePart = snakeBody1.get(i);
            if (collision(snakeHead2, snakePart)) {
                gameOver = true;
            }
        }

        if (snakeHead2.x < 0 || snakeHead2.x * TILE_SIZE >= BOARD_WIDTH ||
                snakeHead2.y < 0 || snakeHead2.y * TILE_SIZE >= BOARD_HEIGHT) {
            gameOver = true;
        }
    }

    public boolean collision(Tile Tile1, Tile Tile2) {
        if (Tile1.x == Tile2.x && Tile1.y == Tile2.y) {
            return true;
        } else {
            return false;
        }
    }

    public void restart() {
        gameOver = false;
        title = true;
        gameLoop.start();
        score = 0;

        velocityX1 = 0;
        velocityY1 = 0;
        velocityX2 = 0;
        velocityY2 = 0;

        snakeBody1.clear();
        snakeBody2.clear();

        snakeHead1.x = 5;
        snakeHead1.y = 5;
        snakeHead2.x = 15;
        snakeHead2.y = 15;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move1();
        move2();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY1 != 1) {
            velocityX1 = 0;
            velocityY1 = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY1 != -1) {
            velocityX1 = 0;
            velocityY1 = +1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX1 != 1) {
            velocityX1 = -1;
            velocityY1 = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX1 != -1) {
            velocityX1 = 1;
            velocityY1 = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_W && velocityY2 != 1) {
            velocityX2 = 0;
            velocityY2 = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_S && velocityY2 != -1) {
            velocityX2 = 0;
            velocityY2 = +1;
        } else if (e.getKeyCode() == KeyEvent.VK_A && velocityX2 != 1) {
            velocityX2 = -1;
            velocityY2 = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_D && velocityX2 != -1) {
            velocityX2 = 1;
            velocityY2 = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
