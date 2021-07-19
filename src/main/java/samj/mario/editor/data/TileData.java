package samj.mario.editor.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TileData {

    public static List<Tile> TILES;
    public static Map<Short, Tile> TILES_BY_INDEX;
    public static Map<Character, Tile> TILES_BY_CHAR;

    static {
        List<Tile> fgTiles = new ArrayList<>();

        fgTiles.add(Tile.EMPTY_TILE);

        // Rock
        fgTiles.add(Tile.builder()
                .setPrimaryDisplayTileIcon(new Icon(IconSheet.TILES, 0, 0))
                .setTileIndex((short) 0x0010)
                .setPaletteCount(4)
                .setTileChar('#')
                .setName("Rock ?")
                .build());

        // Question Box
        fgTiles.add(Tile.builder()
                .setPrimaryDisplayTileIcon(new Icon(IconSheet.TILES, 24, 0))
                .setTileIndex((short) 0x0020)
                .setPaletteCount(4)
                .setTileChar('?')
                .setName("Question Mark Box")
                .build());

        // Coin
        fgTiles.add(Tile.builder()
                .setPrimaryDisplayTileIcon(new Icon(IconSheet.TILES, 24, 1))
                .setTileIndex((short) 0x0030)
                .setPaletteCount(4)
                .setTileChar('o')
                .setName("Coin")
                .build());


        TILES = Collections.unmodifiableList(fgTiles);

        // Create a hashmap of tiles for quick lookup by char
        TILES_BY_CHAR = TILES.stream()
                .collect(Collectors.toUnmodifiableMap(Tile::getTileChar, Function.identity()));

        // Create a hashmap of tiles for quick lookup by index
        TILES_BY_INDEX = TILES.stream()
                .collect(Collectors.toUnmodifiableMap(Tile::getTileIndex, Function.identity()));
    }
}
