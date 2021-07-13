package editor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AsciiLevelDecoder implements LevelDecoder {

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
                ForegroundTile tile = TileData.FOREGROUND_TILES_BY_INDEX.get(c);
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
