import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private static final int GRID_SIZE = 20;
    private static final int TILE_SIZE = 30;
    private static final int WIDTH = GRID_SIZE * TILE_SIZE;
    private static final int HEIGHT = GRID_SIZE * TILE_SIZE;
    private static final int INIT_LENGTH = 3;

    private final Timer timer;
    private final ArrayList<Point> snake;
    private Point food;
    private int direction;
    private boolean gameOver;
    private int score;
    private int highScore;

    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (direction != KeyEvent.VK_DOWN) direction = KeyEvent.VK_UP;
                            break;
                        case KeyEvent.VK_DOWN:
                            if (direction != KeyEvent.VK_UP) direction = KeyEvent.VK_DOWN;
                            break;
                        case KeyEvent.VK_LEFT:
                            if (direction != KeyEvent.VK_RIGHT) direction = KeyEvent.VK_LEFT;
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (direction != KeyEvent.VK_LEFT) direction = KeyEvent.VK_RIGHT;
                            break;
                    }
                }
            }
        });

        snake = new ArrayList<>();
        direction = KeyEvent.VK_RIGHT;
        gameOver = false;
        score = 0;
        highScore = HighScoreManager.getHighScore();

        timer = new Timer(100, this);
        resetGame();
        timer.start();
    }

    private void resetGame() {
        snake.clear();
        for (int i = INIT_LENGTH - 1; i >= 0; i--) {
            snake.add(new Point(i, 0));
        }
        spawnFood();
        direction = KeyEvent.VK_RIGHT;
        gameOver = false;
        score = 0;
    }

    private void spawnFood() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(GRID_SIZE);
            y = rand.nextInt(GRID_SIZE);
        } while (snake.contains(new Point(x, y)));
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.drawString("Game Over! Score: " + score, WIDTH / 2 - 50, HEIGHT / 2);
            g.drawString("High Score: " + highScore, WIDTH / 2 - 50, HEIGHT / 2 + 20);
        } else {
            g.setColor(Color.RED);
            g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 10, 20);
            g.drawString("High Score: " + highScore, WIDTH - 150, 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        Point head = snake.get(0);
        Point newHead = (Point) head.clone();
        switch (direction) {
            case KeyEvent.VK_UP:
                newHead.translate(0, -1);
                break;
            case KeyEvent.VK_DOWN:
                newHead.translate(0, 1);
                break;
            case KeyEvent.VK_LEFT:
                newHead.translate(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                newHead.translate(1, 0);
                break;
        }

        if (newHead.equals(food)) {
            score++;
            snake.add(0, newHead);
            spawnFood();
        } else {
            snake.add(0, newHead);
            snake.remove(snake.size() - 1);
        }

        if (newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE || snake.subList(1, snake.size()).contains(newHead)) {
            gameOver = true;
            if (score > highScore) {
                highScore = score;
                HighScoreManager.setHighScore(highScore);
            }
        }

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
