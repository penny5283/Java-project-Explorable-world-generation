package tileengine;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

import edu.princeton.cs.introcs.StdDraw;
import utils.RandomUtils;

public class TETile implements Serializable {
    private static final long serialVersionUID = 1L;
    private final char character;
    private final Color textColor;
    private final Color backgroundColor;
    private final String description;
    private final String filepath;
    private final int id;

    public TETile(char character, Color textColor, Color backgroundColor, String description,
                  String filepath, int id) {
        this.character = character;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.filepath = filepath;
        this.id = id;
    }

    public TETile(char character, Color textColor, Color backgroundColor, String description, int id) {
        this(character, textColor, backgroundColor, description, null, id);
    }

    public TETile(TETile t, Color textColor) {
        this(t.character, textColor, t.backgroundColor, t.description, t.filepath, t.id);
    }

    public TETile(TETile t, char c) {
        this(c, t.textColor, t.backgroundColor, t.description, t.filepath, t.id);
    }

    public void draw(double x, double y) {
        if (filepath != null) {
            try {
                StdDraw.picture(x + 0.5, y + 0.5, filepath);
                return;
            } catch (IllegalArgumentException e) {
            }
        }

        StdDraw.setPenColor(backgroundColor);
        StdDraw.filledSquare(x + 0.5, y + 0.5, 0.5);
        StdDraw.setPenColor(textColor);
        StdDraw.text(x + 0.5, y + 0.5, Character.toString(character()));
    }

    public char character() {
        return character;
    }

    public String description() {
        return description;
    }

    public int id() {
        return id;
    }

    public static TETile colorVariant(TETile t, int dr, int dg, int db, Random r) {
        Color oldColor = t.textColor;
        int newRed = newColorValue(oldColor.getRed(), dr, r);
        int newGreen = newColorValue(oldColor.getGreen(), dg, r);
        int newBlue = newColorValue(oldColor.getBlue(), db, r);

        Color c = new Color(newRed, newGreen, newBlue);

        return new TETile(t, c);
    }

    private static int newColorValue(int v, int dv, Random r) {
        int rawNewValue = v + RandomUtils.uniform(r, -dv, dv + 1);
        int newValue = Math.min(255, Math.max(0, rawNewValue));
        return newValue;
    }

    public static String toString(TETile[][] world) {
        int width = world.length;
        int height = world[0].length;
        StringBuilder sb = new StringBuilder();

        for (int y = height - 1; y >= 0; y -= 1) {
            for (int x = 0; x < width; x += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y + " is null.");
                }
                sb.append(world[x][y].character());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static TETile[][] copyOf(TETile[][] tiles) {
        if (tiles == null) {
            return null;
        }

        TETile[][] copy = new TETile[tiles.length][];

        int i = 0;
        for (TETile[] column : tiles) {
            copy[i] = Arrays.copyOf(column, column.length);
            i += 1;
        }

        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return (o instanceof TETile otherTile && otherTile.id == this.id);
    }
}
