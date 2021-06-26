package editor;

import java.util.ArrayList;
import java.util.List;

public class ForegroundLayer {
    private int width;
    private int height;
    private List<ForegroundTile> tiles;

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
