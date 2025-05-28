package de.tum.cit.fop.maze;

import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Represents the maze for the game, loading from a file and providing tile data.
 */
public class Maze {

    private int[][] tiles;
    private int width;
    private int height;

    /**
     * Loads the maze from a properties file.
     *
     * @param filePath The file path to the maze properties file.
     */
    public void loadFromFile(String filePath) {
        Properties properties = new Properties();

        try (FileInputStream inStream = new FileInputStream(filePath)) {
            properties.load(inStream);
        } catch (IOException e) {
            throw new GdxRuntimeException("Failed to load maze file: " + filePath, e);
        }

        // Parse dimensions
        width = 40; // Default width
        height = 40; // Default height

        // Initialize all tiles to grass (-1)
        tiles = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                tiles[i][j] = -1; // Grass as default
            }
        }

        boolean hasEntry = false;
        boolean hasExit = false;

        // Populate the tiles array based on the properties file
        for (String key : properties.stringPropertyNames()) {
            String[] coordinates = key.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int tileType = Integer.parseInt(properties.getProperty(key));

            // Check for out-of-bounds coordinates
            if (x < 0 || x >= width || y < 0 || y >= height) {
                throw new IllegalArgumentException("Tile coordinates out of bounds: [" + x + ", " + y + "]");
            }

            // Check for valid tile type
            if (tileType < -1 || tileType > 5) {
                throw new IllegalArgumentException("Invalid tile value at [" + x + ", " + y + "]: " + tileType);
            }

            tiles[x][y] = tileType;

            // Debug log for each tile
            System.out.println("Tile [" + x + ", " + y + "] = " + tileType);

            if (tileType == 1) hasEntry = true;
            if (tileType == 2) hasExit = true;
        }

        // Ensure at least one entry and exit exist
        if (!hasEntry || !hasExit) {
            throw new IllegalStateException("Maze must contain at least one entry (1) and one exit (2).");
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileType(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return -1; // Grass as default for out-of-bounds tiles
        }
        return tiles[x][y];
    }
    public void setTileType(int x, int y, int tileType) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = tileType; // Assuming 'tiles' is your 2D array storing tile types
        }
    }


    
}
