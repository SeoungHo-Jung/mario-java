package samj.mario.editor;

import java.util.ArrayList;
import java.util.List;

public class ForegroundLayer {
    private final int width;
    private final int height;
    private final List<ForegroundTile> tiles;

    public ForegroundLayer(int width, int height) {
        this.width = width;
        this.height = height;

        // Initialize the tiles array with empty tiles
        int size = width * height;
        tiles = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            tiles.add(ForegroundTile.EMPTY_TILE);
        }
    }

    public ForegroundLayer(int width, int height, List<ForegroundTile> tiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    public ForegroundLayer(int width, int height, ForegroundLayer sourceLayer) {
        this.width = width;
        this.height = height;

        // Initialize the tiles array with the tiles from the source layer
        int size = width * height;
        tiles = new ArrayList<>(size);
        int sourceWidth = Math.min(sourceLayer.width, this.width);
        int sourceHeight = Math.min(sourceLayer.height, this.height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x < sourceWidth && y < sourceHeight) {
                    tiles.add(sourceLayer.getTile(x, y));
                } else {
                    // Pad any empty space with EMPTY_TILE
                    tiles.add(ForegroundTile.EMPTY_TILE);
                }
            }
        }
    }

    public void setTile(int x, int y, ForegroundTile tile) {
        int index = (width * y) + x;
        tiles.set(index, tile);
    }

    public ForegroundTile getTile(int x, int y) {
        int index = (width * y) + x;
        return tiles.get(index);
    }

    public void resetTile(int x, int y) {
        int index = (width * y) + x;
        tiles.set(index, ForegroundTile.EMPTY_TILE);
    }
}
