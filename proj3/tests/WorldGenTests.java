import core.AutograderBuddy;
import edu.princeton.cs.introcs.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

public class WorldGenTests {
    public static void main(String[] args) {
        System.out.println("Running basicTest...");
        basicTest();
        System.out.println("Running basicInteractivityTest...");
        basicInteractivityTest();
        System.out.println("Running basicSaveTest...");
        basicSaveTest();
    }

    public static void basicTest() {
        // Generate a new world and render it
        TETile[][] world = AutograderBuddy.generateWorld(80, 30);
        TERenderer ter = new TERenderer();
        ter.initialize(world.length, world[0].length);
        ter.renderFrame(world);
        StdDraw.pause(5000); // Pause for 5 seconds to inspect the output
    }

    public static void basicInteractivityTest() {
        // Generate a new world and simulate user inputs
        TETile[][] world = AutograderBuddy.generateWorld(80, 30);
        TERenderer ter = new TERenderer();
        ter.initialize(world.length, world[0].length);

        // Simulate user inputs
        AutograderBuddy.moveAvatar(world, 'w');
        AutograderBuddy.moveAvatar(world, 'a');
        AutograderBuddy.moveAvatar(world, 's');
        AutograderBuddy.moveAvatar(world, 'd');

        // Render the resulting world
        ter.renderFrame(world);
        StdDraw.pause(5000); // Pause for 5 seconds to inspect the output
    }

    public static void basicSaveTest() {
        // Generate a new world and save it
        TETile[][] world = AutograderBuddy.generateWorld(80, 30);
        AutograderBuddy.saveWorld();

        // Load the saved world and render it
        world = AutograderBuddy.loadWorld();
        TERenderer ter = new TERenderer();
        ter.initialize(world.length, world[0].length);
        ter.renderFrame(world);
        StdDraw.pause(5000); // Pause for 5 seconds to inspect the output
    }
}
