package core;

import edu.princeton.cs.introcs.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class Main {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private static TERenderer ter = new TERenderer();
    private static TETile[][] world;
    private static boolean isPaused = false;
    private static boolean needsRedraw = true;
    private static boolean inMainMenu = true;
    private static boolean gameOver = false;
    private static long messageDisplayTime = 0;
    private static final long MESSAGE_DURATION = 3000; // Display message for 3 seconds

    public static void main(String[] args) {
        ter.initialize(WIDTH, HEIGHT);
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
        StdDraw.setTitle("Explorable World Pixel Game");
        showMainMenu();

        long lastTime = System.currentTimeMillis();
        long monsterMoveInterval = 500; // Move monsters every 500 milliseconds

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                System.out.println("Key pressed: " + key);
                if (inMainMenu) {
                    handleMainMenuInput(key);
                } else {
                    handleInput(key);
                }
                needsRedraw = true;
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime >= monsterMoveInterval && !gameOver && !isPaused && world != null) {
                AutograderBuddy.moveMonsters(world);
                lastTime = currentTime;
                needsRedraw = true;
            }

            if (needsRedraw) {
                renderGame();
                needsRedraw = false;
            }

            StdDraw.pause(20);
        }
    }

    private static void handleMainMenuInput(char key) {
        System.out.println("Handling main menu input: " + key);
        if (key == 'n' || key == 'N') {
            world = AutograderBuddy.generateWorld(WIDTH, HEIGHT);
            inMainMenu = false;
            isPaused = false;
            gameOver = false;
            System.out.println("New game started.");
        } else if (key == 'l' || key == 'L') {
            world = AutograderBuddy.loadWorld();
            if (world == null) {
                world = AutograderBuddy.generateWorld(WIDTH, HEIGHT);
            }
            inMainMenu = false;
            isPaused = false;
            gameOver = false;
            System.out.println("Game loaded.");
        } else if (key == 'q' || key == 'Q') {
            System.out.println("Quitting game.");
            System.exit(0);
        }
    }

    private static void handleInput(char key) {
        if (key == KeyEvent.VK_ESCAPE) {
            isPaused = !isPaused;
            System.out.println("Pause toggled: " + isPaused);
            if (isPaused) {
                displayPauseMenu();
            }
        } else if (isPaused) {
            handlePauseMenuInput(key);
        } else if (!gameOver) {
            AutograderBuddy.moveAvatar(world, key);
            if (AutograderBuddy.getLives() <= 0) {
                gameOver = true;
                displayGameOver();
                return;
            }
        }
    }

    private static void showMainMenu() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 24));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 4, "Explorable World Pixel Game");
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 18));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Quit (Q)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "Try to survive as many worlds as possible!");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Avoid monsters while collecting flowers to increase lives.");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 8, "Use W/A/S/D to move.");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 10, "Press ESC to pause the game.");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 12, "Step on the vortex '✹' to be transported to a new world.");
        StdDraw.show();
        System.out.println("Main menu displayed.");
    }

    private static void displayPauseMenu() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 24));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 4, "Game Paused");
        StdDraw.setFont(new Font("SansSerif", Font.PLAIN, 18));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2, "Press N for New Game");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Press L to Load Game");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Press S to Save Game");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "Press Q to Quit");
        StdDraw.show();
        System.out.println("Pause menu displayed.");
    }

    private static void displayGameOver() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setFont(new Font("SansSerif", Font.BOLD, 24));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Game Over");
        StdDraw.show();
        StdDraw.pause(2000); // Show the Game Over screen for 2 seconds
        showMainMenu();
        inMainMenu = true;
        gameOver = false;
    }

    private static void handlePauseMenuInput(char key) {
        System.out.println("Handling pause menu input: " + key);
        if (key == 'n' || key == 'N') {
            world = AutograderBuddy.generateWorld(WIDTH, HEIGHT);
            isPaused = false;
            gameOver = false;
            System.out.println("New game started.");
        } else if (key == 'l' || key == 'L') {
            world = AutograderBuddy.loadWorld();
            if (world == null) {
                world = AutograderBuddy.generateWorld(WIDTH, HEIGHT);
            }
            isPaused = false;
            gameOver = false;
            System.out.println("Game loaded.");
        } else if (key == 's' || key == 'S') {
            AutograderBuddy.saveWorld();
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Game Saved!");
            StdDraw.show();
            StdDraw.pause(1000); // Show the saved message for 1 second
            System.out.println("Game saved.");
        } else if (key == 'q' || key == 'Q') {
            System.out.println("Quitting game.");
            System.exit(0);
        } else if (key == KeyEvent.VK_ESCAPE) {
            isPaused = !isPaused;
            System.out.println("Exiting pause menu.");
        }
    }

    private static void renderGame() {
        StdDraw.clear(StdDraw.BLACK);
        if (inMainMenu) {
            showMainMenu();
        } else if (isPaused) {
            displayPauseMenu();
        } else {
            ter.renderFrame(world);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("SansSerif", Font.BOLD, 16));
            StdDraw.textLeft(1, HEIGHT - 1, "Collected Items: " + AutograderBuddy.getCollectedItems());
            StdDraw.textLeft(1, HEIGHT - 2, "Lives: " + AutograderBuddy.getLives());

            // Draw an arrow above the avatar
            StdDraw.setPenColor(new Color(138, 43, 226)); // Light indigo color
            StdDraw.text(AutograderBuddy.getAvatarX() + 0.5, AutograderBuddy.getAvatarY() + 1.5, "↑");

            // Show the transition message if there is one and it's within the duration
            if (AutograderBuddy.getMessage() != null && !AutograderBuddy.getMessage().isEmpty()) {
                if (System.currentTimeMillis() - messageDisplayTime < MESSAGE_DURATION) {
                    StdDraw.setPenColor(Color.YELLOW);
                    StdDraw.text(WIDTH / 2, HEIGHT - 3, AutograderBuddy.getMessage());
                } else {
                    AutograderBuddy.clearMessage();
                }
            }
        }
        StdDraw.show();
        System.out.println("Game rendered.");
    }
}
