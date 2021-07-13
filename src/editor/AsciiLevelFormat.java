package editor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AsciiLevelFormat implements LevelFormat {
    @Override
    public byte[] encode(Level level) {
        // Write the foreground layer as text
        ForegroundLayer fgLayer = level.getForegroundLayer();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                ForegroundTile tile = fgLayer.getTile(x, y);
                sb.append(tile.getTileChar());
            }
            sb.append('\n');
        }
        String output = sb.toString();
        return output.getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public Level decode(byte[] bytes) {
        List<List<ForegroundTile>> foregroundTiles = new ArrayList<>();

        String chars = new String(bytes, StandardCharsets.US_ASCII);

        List<ForegroundTile> currentRow = new ArrayList<>();
        for (int i = 0; i < chars.length(); i++){
            char c = chars.charAt(i);
            if (c == '\n') {
                foregroundTiles.add(currentRow);
                currentRow = new ArrayList<>();
            } else {
                ForegroundTile tile = TileData.FOREGROUND_TILES_BY_CHAR.get(c);
                assert(tile != null);
                currentRow.add(tile);
            }
        }

        assert(!foregroundTiles.isEmpty());

        int width = foregroundTiles.get(0).size();
        int height = foregroundTiles.size();

        // Validate that all rows are of equal length
        for (List<ForegroundTile> row : foregroundTiles) {
            assert(row.size() == width);
        }

        List<ForegroundTile> tiles = new ArrayList<>();
        for (List<ForegroundTile> row : foregroundTiles) {
            tiles.addAll(row);
        }

        Level level = new Level();
        level.setDimensions(width, height);
        level.setForegroundLayer(new ForegroundLayer(width, height, tiles));

        return level;
    }
}
