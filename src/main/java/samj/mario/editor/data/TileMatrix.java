package samj.mario.editor.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class TileMatrix implements Iterable<Tile> {
    private final int width;
    private final int height;
    private final List<Tile> tiles;

    public TileMatrix(int width, int height) {
        this.width = width;
        this.height = height;

        // Initialize the tiles array with empty tiles
        int size = width * height;
        tiles = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            tiles.add(new Tile(Tile.EMPTY_TILE));
        }
    }

    public TileMatrix(int width, int height, List<Tile> tiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    public TileMatrix(int width, int height, TileMatrix sourceLayer) {
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
                    tiles.add(new Tile(Tile.EMPTY_TILE));
                }
            }
        }
    }

    public void setTile(int x, int y, Tile tile) {
        int index = (width * y) + x;
        tiles.set(index, tile);
    }

    public Tile getTile(int x, int y) {
        int index = (width * y) + x;
        return tiles.get(index);
    }

    @Override
    public Iterator<Tile> iterator() {
        return tiles.iterator();
    }

    @Override
    public void forEach(Consumer<? super Tile> action) {
        tiles.forEach(action);
    }

    @Override
    public Spliterator<Tile> spliterator() {
        return tiles.spliterator();
    }
}
