package samj.mario.editor.data;

import samj.mario.editor.io.json.JsonTileType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static samj.mario.editor.data.ForegroundTileCategory.INTERACTIVE;
import static samj.mario.editor.data.ForegroundTileCategory.PLATFORM;
import static samj.mario.editor.data.SpriteSheet.TILES;

public class TileData {

    public static List<TileDefinition> FOREGROUND_TILES;
    public static Map<Short, TileDefinition> FOREGROUND_TILES_BY_INDEX;
    public static Map<Character, TileDefinition> FOREGROUND_TILES_BY_CHAR;

    static {
        List<TileDefinition> fgTiles = new ArrayList<>();

        fgTiles.add(TileDefinition.EMPTY_TILE);

        // Rock
        fgTiles.add(TileDefinition.builder()
                .setPrimaryDisplayTileIcon(new Icon(TILES, 0, 0))
                .setTileIndex((short) 0x0010)
                .setPaletteCount(4)
                .setTileChar('#')
                .setName("Rock ?")
                .setAllowedTileTypes(Set.of(JsonTileType.SOLID))
                .setCategory(PLATFORM)
                .build());

        // Question Box
        fgTiles.add(TileDefinition.builder()
                .setPrimaryDisplayTileIcon(new Icon(TILES, 24, 0))
                .setTileIndex((short) 0x0020)
                .setPaletteCount(4)
                .setTileChar('?')
                .setName("Question Mark Box")
                .setAllowedTileTypes(Set.of(JsonTileType.CONTAINER))
                .setCategory(INTERACTIVE)
                .build());

        // Coin
        fgTiles.add(TileDefinition.builder()
                .setPrimaryDisplayTileIcon(new Icon(TILES, 24, 1))
                .setTileIndex((short) 0x0030)
                .setPaletteCount(4)
                .setTileChar('o')
                .setName("Coin")
                .setAllowedTileTypes(Set.of(JsonTileType.COIN))
                .setCategory(INTERACTIVE)
                .build());


        FOREGROUND_TILES = Collections.unmodifiableList(fgTiles);

        // Create a hashmap of tiles for quick lookup by char
        FOREGROUND_TILES_BY_CHAR = FOREGROUND_TILES.stream()
                .collect(Collectors.toUnmodifiableMap(TileDefinition::getTileChar, Function.identity()));

        // Create a hashmap of tiles for quick lookup by index
        FOREGROUND_TILES_BY_INDEX = FOREGROUND_TILES.stream()
                .collect(Collectors.toUnmodifiableMap(TileDefinition::getTileIndex, Function.identity()));
    }
}
