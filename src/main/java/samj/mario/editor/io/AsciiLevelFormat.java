package samj.mario.editor.io;

import samj.mario.editor.data.TileMatrix;
import samj.mario.editor.data.Tile;
import samj.mario.editor.data.Level;
import samj.mario.editor.data.TileData;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AsciiLevelFormat implements LevelFormat {
    @Override
    public byte[] encode(Level level) {
        // Write the foreground layer as text
        TileMatrix fgLayer = level.getForegroundLayer();
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
        List<List<Tile>> foregroundTiles = new ArrayList<>();

        String chars = new String(bytes, StandardCharsets.US_ASCII);

        List<Tile> currentRow = new ArrayList<>();
        for (int i = 0; i < chars.length(); i++){
            char c = chars.charAt(i);
            if (c == '\n') {
                foregroundTiles.add(currentRow);
                currentRow = new ArrayList<>();
            } else {
                Tile tile = TileData.FOREGROUND_TILES_BY_CHAR.get(c);
                assert(tile != null);
                currentRow.add(tile);
            }
        }

        assert(!foregroundTiles.isEmpty());

        int width = foregroundTiles.get(0).size();
        int height = foregroundTiles.size();

        // Validate that all rows are of equal length
        for (List<Tile> row : foregroundTiles) {
            assert(row.size() == width);
        }

        List<Tile> tiles = new ArrayList<>();
        for (List<Tile> row : foregroundTiles) {
            tiles.addAll(row);
        }

        Level level = new Level();
        level.setDimensions(width, height);
        level.setForegroundLayer(new TileMatrix(width, height, tiles));

        return level;
    }
}
