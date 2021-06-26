package editor;

import java.util.ArrayList;
import java.util.List;

import static editor.ForegroundTileCategory.INTERACTIVE;
import static editor.ForegroundTileCategory.PLATFORM;
import static editor.SpriteSheet.TILES;

public class TileData {

    public static List<ForegroundTile> FOREGROUND_TILES = new ArrayList<>();

    static {
        FOREGROUND_TILES.add(ForegroundTile.EMPTY_TILE);

        // Rock
        FOREGROUND_TILES.add(new ForegroundTile(
                        new TileIcon(TILES, 0, 0),
                        null,
                        '#',
                        true,
                        "Rock ?",
                        PLATFORM
                )
        );

        // Question Box
        FOREGROUND_TILES.add(new ForegroundTile(
                new TileIcon(TILES, 24, 0),
                null,
                '?',
                true,
                "Question Mark Box",
                INTERACTIVE
        ));

        // Coin
        FOREGROUND_TILES.add(new ForegroundTile(
                new TileIcon(TILES, 24, 1),
                null,
                'o',
                true,
                "Coin",
                INTERACTIVE
        ));

    }
}
