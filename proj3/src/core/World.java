package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.io.*;
import java.util.Random;

public class World implements Serializable {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;
    private TETile[][] world;
    private Random random;

    public World(Random random) {
        this.random = random;
        world = new TETile[WIDTH][HEIGHT];
        initializeWorld();
        generateRoomsAndHallways();
    }

    private void initializeWorld() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void generateRoomsAndHallways() {
        int numRooms = RandomUtils.uniform(random, 5, 15); // Number of rooms
        for (int i = 0; i < numRooms; i++) {
            generateRoom();
        }
        generateHallways();
    }

    private void generateRoom() {
        int roomWidth = RandomUtils.uniform(random, 3, 10);
        int roomHeight = RandomUtils.uniform(random, 3, 10);
        int x = RandomUtils.uniform(random, WIDTH - roomWidth);
        int y = RandomUtils.uniform(random, HEIGHT - roomHeight);

        for (int i = x; i < x + roomWidth; i++) {
            for (int j = y; j < y + roomHeight; j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
        addWalls(x, y, roomWidth, roomHeight);
    }

    private void addWalls(int x, int y, int width, int height) {
        for (int i = x - 1; i <= x + width; i++) {
            world[i][y - 1] = Tileset.WALL;
            world[i][y + height] = Tileset.WALL;
        }
        for (int j = y - 1; j <= y + height; j++) {
            world[x - 1][j] = Tileset.WALL;
            world[x + width][j] = Tileset.WALL;
        }
    }

    private void generateHallways() {
        // Implement hallway generation logic using `random`
        // Example:
        int numHallways = RandomUtils.uniform(random, 5, 10);
        for (int i = 0; i < numHallways; i++) {
            int length = RandomUtils.uniform(random, 5, 15);
            int x = RandomUtils.uniform(random, WIDTH - length);
            int y = RandomUtils.uniform(random, HEIGHT);
            for (int j = 0; j < length; j++) {
                world[x + j][y] = Tileset.FLOOR;
                if (j == 0 || j == length - 1) {
                    world[x + j][y - 1] = Tileset.WALL;
                    world[x + j][y + 1] = Tileset.WALL;
                }
            }
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    // Save the current game state to a file
    public void save(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load the game state from a file
    public static World load(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (World) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
