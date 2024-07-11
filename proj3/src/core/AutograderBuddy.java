package core;

import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class AutograderBuddy {
    private static final Random RANDOM = new Random();
    private static final File SAVE_FILE = new File("world.sav");
    private static int collectedItems = 0;
    private static int lives = 1;
    private static int avatarX;
    private static int avatarY;
    private static TETile[][] world;
    private static ArrayList<Point> monsters = new ArrayList<>();
    private static boolean transitioning = false;
    private static String message = "";

    public static TETile[][] generateWorld(int width, int height) {
        TETile[][] newWorld = new TETile[width][height];

        // Initialize world with nothing
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                newWorld[x][y] = Tileset.NOTHING;
            }
        }

        // Generate rooms and corridors
        ArrayList<Rectangle> rooms = generateRooms(newWorld, width, height);
        generateCorridors(newWorld, rooms);

        // Add walls around the rooms and corridors
        addWalls(newWorld);

        // Place avatar in the center of a random room
        Rectangle avatarRoom = rooms.get(RandomUtils.uniform(RANDOM, rooms.size()));
        avatarX = avatarRoom.x + avatarRoom.width / 2;
        avatarY = avatarRoom.y + avatarRoom.height / 2;
        newWorld[avatarX][avatarY] = Tileset.AVATAR;

        // Add decorations outside the walkable area
        addDecorations(newWorld, width, height);

        // Place items within the walkable area
        placeItems(newWorld);

        placeMonsters(newWorld, rooms);

        addVortex(newWorld);

        return newWorld;
    }

    private static ArrayList<Rectangle> generateRooms(TETile[][] world, int width, int height) {
        ArrayList<Rectangle> rooms = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            int roomWidth = RandomUtils.uniform(RANDOM, 4, 10);
            int roomHeight = RandomUtils.uniform(RANDOM, 4, 10);
            int x = RandomUtils.uniform(RANDOM, width - roomWidth - 1);
            int y = RandomUtils.uniform(RANDOM, height - roomHeight - 1);

            Rectangle room = new Rectangle(x, y, roomWidth, roomHeight);
            if (!overlaps(room, rooms)) {
                rooms.add(room);
                for (int rx = room.x; rx < room.x + room.width; rx++) {
                    for (int ry = room.y; ry < room.y + room.height; ry++) {
                        world[rx][ry] = Tileset.FLOOR;
                    }
                }
            }
        }
        return rooms;
    }

    private static boolean overlaps(Rectangle room, ArrayList<Rectangle> rooms) {
        for (Rectangle otherRoom : rooms) {
            if (room.intersects(otherRoom)) {
                return true;
            }
        }
        return false;
    }

    private static void generateCorridors(TETile[][] world, ArrayList<Rectangle> rooms) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Rectangle room1 = rooms.get(i);
            Rectangle room2 = rooms.get(i + 1);

            int x1 = room1.x + room1.width / 2;
            int y1 = room1.y + room1.height / 2;
            int x2 = room2.x + room2.width / 2;
            int y2 = room2.y + room2.height / 2;

            if (RandomUtils.bernoulli(RANDOM)) {
                addHorizontalHallway(world, x1, x2, y1);
                addVerticalHallway(world, y1, y2, x2);
            } else {
                addVerticalHallway(world, y1, y2, x1);
                addHorizontalHallway(world, x1, x2, y2);
            }
        }
    }

    private static void addHorizontalHallway(TETile[][] world, int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            world[x][y] = Tileset.FLOOR;
        }
    }

    private static void addVerticalHallway(TETile[][] world, int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            world[x][y] = Tileset.FLOOR;
        }
    }

    private static void addWalls(TETile[][] world) {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] == Tileset.FLOOR) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int wx = x + dx;
                            int wy = y + dy;
                            if (wx >= 0 && wx < world.length && wy >= 0 && wy < world[0].length && world[wx][wy] == Tileset.NOTHING) {
                                world[wx][wy] = Tileset.WALL;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void addDecorations(TETile[][] world, int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (world[x][y] == Tileset.NOTHING) {
                    double rand = RandomUtils.uniform(RANDOM);
                    if (rand < 0.05) {
                        world[x][y] = Tileset.TREE;
                    } else if (rand < 0.10) {
                        createWaterBody(world, x, y);
                    } else if (rand < 0.15) {
                        createMountainRange(world, x, y);
                    }
                }
            }
        }
    }

    private static void createWaterBody(TETile[][] world, int x, int y) {
        int radius = RandomUtils.uniform(RANDOM, 2, 5);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int wx = x + dx;
                int wy = y + dy;
                if (wx >= 0 && wx < world.length && wy >= 0 && wy < world[0].length && dx * dx + dy * dy <= radius * radius) {
                    if (world[wx][wy] == Tileset.NOTHING) {
                        world[wx][wy] = Tileset.WATER;
                    }
                }
            }
        }
    }

    private static void createMountainRange(TETile[][] world, int x, int y) {
        int length = RandomUtils.uniform(RANDOM, 5, 10);
        boolean horizontal = RandomUtils.bernoulli(RANDOM);
        for (int i = 0; i < length; i++) {
            int wx = x + (horizontal ? i : 0);
            int wy = y + (horizontal ? 0 : i);
            if (wx >= 0 && wx < world.length && wy >= 0 && wy < world[0].length) {
                if (world[wx][wy] == Tileset.NOTHING) {
                    world[wx][wy] = Tileset.MOUNTAIN;
                }
            }
        }
    }

    private static void placeItems(TETile[][] world) {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] == Tileset.FLOOR && RANDOM.nextInt(100) < 10) {
                    world[x][y] = Tileset.FLOWER;
                }
            }
        }
    }

    private static void placeMonsters(TETile[][] world, ArrayList<Rectangle> rooms) {
        for (Rectangle room : rooms) {
            int numMonsters = RandomUtils.uniform(RANDOM, 1, 2);
            for (int i = 0; i < numMonsters; i++) {
                int monsterX = RandomUtils.uniform(RANDOM, room.x + 1, room.x + room.width - 1);
                int monsterY = RandomUtils.uniform(RANDOM, room.y + 1, room.y + room.height - 1);
                monsters.add(new Point(monsterX, monsterY));
                world[monsterX][monsterY] = Tileset.MONSTER;
            }
        }
    }

    public static void moveAvatar(TETile[][] world, char key) {
        int newX = avatarX;
        int newY = avatarY;

        if (key == 'w' || key == 'W') newY += 1;
        if (key == 's' || key == 'S') newY -= 1;
        if (key == 'a' || key == 'A') newX -= 1;
        if (key == 'd' || key == 'D') newX += 1;

        if (newX >= 0 && newX < world.length && newY >= 0 && newY < world[0].length &&
                world[newX][newY] != Tileset.WALL && world[newX][newY] != Tileset.TREE && world[newX][newY] != Tileset.WATER) {
            if (world[newX][newY] == Tileset.FLOWER) {
                collectedItems++;
                if (collectedItems % 10 == 0) {
                    lives++;
                }
            } else if (world[newX][newY] == Tileset.MONSTER) {
                lives--;
                if (lives <= 0) {
                    return;
                }
            } else if (world[newX][newY] == Tileset.UNLOCKED_DOOR) {
                transitioning = true;
                setMessage("You were transported to a new world!");
                transitionToNewWorld();
                return;
            }

            world[avatarX][avatarY] = Tileset.FLOOR;
            avatarX = newX;
            avatarY = newY;
            world[avatarX][avatarY] = Tileset.AVATAR;
        }
    }

    private static void transitionToNewWorld() {
        System.out.println("Transitioning to new world...");
        clearCurrentWorld();
        generateNewWorld();
        System.out.println("New world generated.");
        transitioning = false;
    }

    private static void clearCurrentWorld() {
        if (world != null) {
            for (int x = 0; x < world.length; x++) {
                for (int y = 0; y < world[0].length; y++) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
        }
        monsters.clear();
    }

    private static void generateNewWorld() {
        world = generateWorld(world.length, world[0].length);
        avatarX = -1;
        avatarY = -1;
        placeAvatarInNewWorld();
    }

    private static void placeAvatarInNewWorld() {
        boolean placed = false;
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] == Tileset.AVATAR) {
                    avatarX = x;
                    avatarY = y;
                    placed = true;
                    break;
                }
            }
            if (placed) {
                break;
            }
        }
    }

    public static void moveMonsters(TETile[][] world) {
        ArrayList<Point> newMonsterPositions = new ArrayList<>();

        for (Point monster : monsters) {
            int newX = monster.x;
            int newY = monster.y;

            switch (RandomUtils.uniform(RANDOM, 4)) {
                case 0 -> newX++;
                case 1 -> newX--;
                case 2 -> newY++;
                case 3 -> newY--;
            }

            if (newX >= 0 && newX < world.length && newY >= 0 && newY < world[0].length &&
                    (world[newX][newY] == Tileset.FLOOR || world[newX][newY] == Tileset.AVATAR)) {
                if (world[newX][newY] == Tileset.AVATAR) {
                    lives--;
                    if (lives <= 0) {
                        return;
                    }
                }

                world[monster.x][monster.y] = Tileset.FLOOR;
                world[newX][newY] = Tileset.MONSTER;
                newMonsterPositions.add(new Point(newX, newY));
            } else {
                newMonsterPositions.add(monster);
            }
        }

        monsters = newMonsterPositions;
    }

    public static int getCollectedItems() {
        return collectedItems;
    }

    public static int getLives() {
        return lives;
    }

    public static void resetGameStats() {
        collectedItems = 0;
        lives = 1;
    }

    public static void saveWorld() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(world);
            out.writeInt(collectedItems);
            out.writeInt(lives);
            out.writeInt(avatarX);
            out.writeInt(avatarY);
            System.out.println("Game saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TETile[][] loadWorld() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            world = (TETile[][]) in.readObject();
            collectedItems = in.readInt();
            lives = in.readInt();
            avatarX = in.readInt();
            avatarY = in.readInt();
            return world;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getAvatarX() {
        return avatarX;
    }

    public static int getAvatarY() {
        return avatarY;
    }

    public static boolean isTransitioning() {
        return transitioning;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String msg) {
        message = msg;
    }

    public static void clearMessage() {
        message = "";
    }

    private static void addVortex(TETile[][] world) {
        int x = RandomUtils.uniform(RANDOM, world.length);
        int y = RandomUtils.uniform(RANDOM, world[0].length);

        while (world[x][y] != Tileset.FLOOR) {
            x = RandomUtils.uniform(RANDOM, world.length);
            y = RandomUtils.uniform(RANDOM, world[0].length);
        }
        world[x][y] = Tileset.UNLOCKED_DOOR;
    }
}
