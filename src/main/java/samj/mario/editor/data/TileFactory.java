package samj.mario.editor.data;

import samj.mario.editor.io.json.JsonTileType;

import java.util.ArrayList;
import java.util.List;

public class TileFactory {

    public static List<Tile> getTiles(TileDefinition td) {
        List<Tile> tiles = new ArrayList<>();
        for (JsonTileType tileType : td.getAllowedTileTypes()) {
            for (int palette = 0; palette < td.getPaletteCount(); palette++) {
                Tile tile = new Tile(
                        td.getPrimaryDisplayTileIcon(),
                        td.getSecondaryDisplayTileIcon(),
                        td.getTileChar(),
                        td.getTileIndex(),
                        0, // todo
                        0, // todo
                        palette,
                        false, // todo
                        td.getName(),
                        tileType,
                        null, // todo
                        null // todo
                );
                tiles.add(tile);
            }
        }
        return tiles;
    }
}
