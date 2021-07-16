package samj.mario.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static samj.mario.editor.ForegroundTileCategory.INTERACTIVE;
import static samj.mario.editor.ForegroundTileCategory.PLATFORM;
import static samj.mario.editor.SpriteSheet.TILES;

public class TileData {

    public static List<ForegroundTile> FOREGROUND_TILES;
    public static Map<Character, ForegroundTile> FOREGROUND_TILES_BY_INDEX;

    static {
        List<ForegroundTile> fgTiles = new ArrayList<>();

        fgTiles.add(ForegroundTile.EMPTY_TILE);

        // Rock
        fgTiles.add(new ForegroundTile(
                        new TileIcon(TILES, 0, 0),
                        null,
                        '#',
                        true,
                        "Rock ?",
                        PLATFORM
                )
        );

        // Question Box
        fgTiles.add(new ForegroundTile(
                new TileIcon(TILES, 24, 0),
                null,
                '?',
                true,
                "Question Mark Box",
                INTERACTIVE
        ));

        // Coin
        fgTiles.add(new ForegroundTile(
                new TileIcon(TILES, 24, 1),
                null,
                'o',
                true,
                "Coin",
                INTERACTIVE
        ));

        FOREGROUND_TILES = Collections.unmodifiableList(fgTiles);

        // Create a hashmap of tiles for quick lookup by char index
        FOREGROUND_TILES_BY_INDEX = FOREGROUND_TILES.stream()
                .collect(Collectors.toUnmodifiableMap(ForegroundTile::getTileIndex, Function.identity()));
    }
}
