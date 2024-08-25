import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

/**
 * SnakeGame is a JPanel that handles the snake game logic and GUI.
 * It extends JPanel and implements ActionListener to handle game events.
 */
public class SnakeGame extends JPanel implements ActionListener {
    // Constants for game configuration
    private static final int GRID_SIZE = 20; // Number of tiles in each row/column
    private static final int TILE_SIZE = 30; // Size of each tile in pixels
    private static final int WIDTH = GRID_SIZE * TILE_SIZE; // Width of the game area
    private static final int HEIGHT = GRID_SIZE * TILE_SIZE; // Height of the game area
    private static final int INIT_LENGTH = 3; // Initial length of the snake
    private static final String HIGH_SCORE_FILE = "highscore.txt"; // File to store the high score

    // Game variables
    private final Timer timer; // Timer to control the game loop
    private final ArrayList<Point> snake; // List to keep track of the snake's body
    private Point food; // Current position of the food
    private int direction; // Current direction of the snake's movement
    private boolean gameOver; // Flag to indicate if the game is over
    private int score; // Current score of the game
    private int highScore; // High score read from the file

    /**
     * Constructor for SnakeGame. Initializes the game and sets up the GUI.
     */
    public SnakeGame() {
        // Set the preferred size and background color of the panel
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        // Add a key listener to handle player input
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    // Change the direction based on the key pressed
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W:
                        case KeyEvent.VK_UP:
                            if (direction != KeyEvent.VK_DOWN) direction = KeyEvent.VK_UP;
                            break;
                        case KeyEvent.VK_S:
                        case KeyEvent.VK_DOWN:
                            if (direction != KeyEvent.VK_UP) direction = KeyEvent.VK_DOWN;
                            break;
                        case KeyEvent.VK_A:
                        case KeyEvent.VK_LEFT:
                            if (direction != KeyEvent.VK_RIGHT) direction = KeyEvent.VK_LEFT;
                            break;
                        case KeyEvent.VK_D:
                        case KeyEvent.VK_RIGHT:
                            if (direction != KeyEvent.VK_LEFT) direction = KeyEvent.VK_RIGHT;
                            break;
                    }
                }
            }
        });

        // Initialize game variables
        snake = new ArrayList<>();
        direction = KeyEvent.VK_RIGHT; // Start moving to the right
        gameOver = false;
        score = 0;
        highScore = getHighScore(); // Load the high score from file

        // Create a timer that triggers the game loop every 100 milliseconds
        timer = new Timer(100, this);
        resetGame(); // Reset the game to the initial state
        timer.start(); // Start the game loop
    }

    /**
     * Resets the game to its initial state.
     */
    private void resetGame() {
        snake.clear(); // Clear the snake's body
        // Initialize the snake with the given length
        for (int i = INIT_LENGTH - 1; i >= 0; i--) {
            snake.add(new Point(i, 0));
        }
        spawnFood(); // Place the first food
        direction = KeyEvent.VK_RIGHT; // Reset direction to the right
        gameOver = false; // Game is not over
        score = 0; // Reset score
    }

    /**
     * Spawns a new piece of food at a random location.
     */
    private void spawnFood() {
        Random rand = new Random();
        int x, y;
        do {
            // Randomly select a location for the food
            x = rand.nextInt(GRID_SIZE);
            y = rand.nextInt(GRID_SIZE);
        } while (snake.contains(new Point(x, y))); // Ensure food is not placed on the snake
        food = new Point(x, y); // Set the food's position
    }

    /**
     * Paints the game elements (snake, food) on the panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameOver) {
            // Display "Game Over" message and scores if the game is over
            g.setColor(Color.WHITE);
            g.drawString("Game Over! Score: " + score, WIDTH / 2 - 50, HEIGHT / 2);
            g.drawString("High Score: " + highScore, WIDTH / 2 - 50, HEIGHT / 2 + 20);
        } else {
            // Draw the food and snake
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

    /**
     * Handles game updates at each timer tick.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return; // If the game is over, do nothing

        // Get the current head of the snake and create a new head based on direction
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

        // Check if the new head collides with the food
        if (newHead.equals(food)) {
            score++; // Increase the score
            snake.add(0, newHead); // Add the new head to the snake
            spawnFood(); // Place new food
        } else {
            snake.add(0, newHead); // Add the new head to the snake
            snake.remove(snake.size() - 1); // Remove the tail of the snake
        }

        // Check for collisions (walls or self)
        if (newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE || snake.subList(1, snake.size()).contains(newHead)) {
            gameOver = true; // End the game
            // Update high score if necessary
            if (score > highScore) {
                highScore = score;
                setHighScore(highScore);
            }
        }

        repaint(); // Repaint the panel to reflect changes
    }

    /**
     * Reads the high score from the high score file.
     * @return The high score.
     */
    private int getHighScore() {
        try {
            if (Files.exists(Paths.get(HIGH_SCORE_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(HIGH_SCORE_FILE)));
                return Integer.parseInt(content.trim());
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if no high score is found or an error occurs
    }

    /**
     * Writes the new high score to the high score file.
     * @param score The new high score.
     */
    private void setHighScore(int score) {
        try {
            Files.write(Paths.get(HIGH_SCORE_FILE), String.valueOf(score).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method to create and display the game window.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game"); // Create a new JFrame with title
        SnakeGame game = new SnakeGame(); // Create an instance of SnakeGame
        frame.add(game); // Add the game panel to the frame
        frame.pack(); // Adjust the frame size to fit the game panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the application when the frame is closed
        frame.setVisible(true); // Show the frame
    }
}
