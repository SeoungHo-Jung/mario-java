package editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static editor.ForegroundTileCategory.INTERACTIVE;
import static editor.ForegroundTileCategory.PLATFORM;
import static editor.SpriteSheet.TILES;

public class TileData {

    public static List<ForegroundTile> FOREGROUND_TILES;
    public static Map<Character, ForegroundTile> FOREGROUND_TILES_BY_INDEX;

    static {
        List<ForegroundTile> fgTiles = new ArrayList<>();

        fgTiles.add(ForegroundTile.EMPTY_TILE);

        // Rock
        fgTiles.add(ForegroundTile.builder()
                .setPrimaryDisplayTileIcon(new TileIcon(TILES, 0, 0))
                .setTileIndex((short) 0x0010)
                .setTileChar('#')
                .setName("Rock ?")
                .setCategory(PLATFORM)
                .build());

        // Question Box
        fgTiles.add(ForegroundTile.builder()
                .setPrimaryDisplayTileIcon(new TileIcon(TILES, 24, 0))
                .setTileIndex((short) 0x0020)
                .setTileChar('?')
                .setName("Question Mark Box")
                .setCategory(INTERACTIVE)
                .build());

        // Coin
        fgTiles.add(ForegroundTile.builder()
                .setPrimaryDisplayTileIcon(new TileIcon(TILES, 24, 1))
                .setTileIndex((short) 0x0030)
                .setTileChar('o')
                .setName("Coin")
                .setCategory(INTERACTIVE)
                .build());


        FOREGROUND_TILES = Collections.unmodifiableList(fgTiles);

        // Create a hashmap of tiles for quick lookup by char index
        FOREGROUND_TILES_BY_INDEX = FOREGROUND_TILES.stream()
                .collect(Collectors.toUnmodifiableMap(ForegroundTile::getTileChar, Function.identity()));
    }
}
