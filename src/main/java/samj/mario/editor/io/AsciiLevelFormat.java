package samj.mario.editor.io;

import samj.mario.editor.data.Level;
import samj.mario.editor.data.Tile;
import samj.mario.editor.data.TileData;
import samj.mario.editor.data.TileMatrix;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AsciiLevelFormat implements LevelFormat {
    @Override
    public byte[] encode(Level level) {
        // Write the tiles as text
        TileMatrix fgLayer = level.getTileMatrix();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                Tile tile = fgLayer.getTile(x, y);
                sb.append(tile.getTileChar());
            }
            sb.append('\n');
        }
        String output = sb.toString();
        return output.getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public Level decode(byte[] bytes) {
        List<List<Tile>> tiles = new ArrayList<>();

        String chars = new String(bytes, StandardCharsets.US_ASCII);

        List<Tile> currentRow = new ArrayList<>();
        for (int i = 0; i < chars.length(); i++){
            char c = chars.charAt(i);
            if (c == '\n') {
                tiles.add(currentRow);
                currentRow = new ArrayList<>();
            } else {
                Tile tile = TileData.TILES_BY_CHAR.get(c);
                assert(tile != null);
                currentRow.add(tile);
            }
        }

        assert(!tiles.isEmpty());

        int width = tiles.get(0).size();
        int height = tiles.size();

        // Validate that all rows are of equal length
        for (List<Tile> row : tiles) {
            assert(row.size() == width);
        }

        List<Tile> flatTiles = new ArrayList<>();
        for (List<Tile> row : tiles) {
            flatTiles.addAll(row);
        }

        Level level = new Level();
        level.setDimensions(width, height);
        level.setTileMatrix(new TileMatrix(width, height, flatTiles));

        return level;
    }
}
