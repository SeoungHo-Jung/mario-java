package samj.mario.editor;

import static samj.mario.editor.ForegroundTileCategory.PLATFORM;
import static samj.mario.editor.SpriteSheet.TILES;

public class ForegroundTile {

    public static ForegroundTile EMPTY_TILE = new ForegroundTile(
            null,
            null,
            ' ',
            true,
            "Empty space",
            PLATFORM
    );

    public static ForegroundTile TEST_TILE = new ForegroundTile(
            new TileIcon(TILES, 0, 0),
            null,
            '#',
            true,
            "Rock ?",
            PLATFORM
    );

    private final TileIcon primaryDisplayTileIcon;
    private final TileIcon secondaryDisplayTileIcon;
    private final char tileIndex;
    private final boolean isEnabled;
    private final String name;
    private final ForegroundTileCategory category;

    public ForegroundTile(TileIcon primaryDisplayTileIcon, TileIcon secondaryDisplayTileIcon, char tileIndex, boolean isEnabled, String name, ForegroundTileCategory category) {
        this.primaryDisplayTileIcon = primaryDisplayTileIcon;
        this.secondaryDisplayTileIcon = secondaryDisplayTileIcon;
        this.tileIndex = tileIndex;
        this.isEnabled = isEnabled;
        this.name = name;
        this.category = category;
    }

    public TileIcon getPrimaryDisplayTileIcon() {
        return primaryDisplayTileIcon;
    }

    public TileIcon getSecondaryDisplayTileIcon() {
        return secondaryDisplayTileIcon;
    }

    public char getTileIndex() {
        return tileIndex;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getName() {
        return name;
    }

    public ForegroundTileCategory getCategory() {
        return category;
    }
}
