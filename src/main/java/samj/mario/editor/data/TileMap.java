package samj.mario.editor.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class TileMap implements Iterable<TileDefinition> {
    private final int width;
    private final int height;
    private final List<TileDefinition> tiles;

    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;

        // Initialize the tiles array with empty tiles
        int size = width * height;
        tiles = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            tiles.add(TileDefinition.EMPTY_TILE);
        }
    }

    public TileMap(int width, int height, List<TileDefinition> tiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    public TileMap(int width, int height, TileMap sourceLayer) {
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
                    tiles.add(TileDefinition.EMPTY_TILE);
                }
            }
        }
    }

    public void setTile(int x, int y, TileDefinition tile) {
        int index = (width * y) + x;
        tiles.set(index, tile);
    }

    public TileDefinition getTile(int x, int y) {
        int index = (width * y) + x;
        return tiles.get(index);
    }

    public void resetTile(int x, int y) {
        int index = (width * y) + x;
        tiles.set(index, TileDefinition.EMPTY_TILE);
    }

    @Override
    public Iterator<TileDefinition> iterator() {
        return tiles.iterator();
    }

    @Override
    public void forEach(Consumer<? super TileDefinition> action) {
        tiles.forEach(action);
    }

    @Override
    public Spliterator<TileDefinition> spliterator() {
        return tiles.spliterator();
    }
}
