package samj.mario.editor.data;

import samj.mario.editor.io.json.JsonTileType;

import java.util.Collections;
import java.util.Set;

import static samj.mario.editor.data.ForegroundTileCategory.PLATFORM;
import static samj.mario.editor.data.ForegroundTileCategory.UNKNOWN;

public class TileDefinition {

    public static TileDefinition EMPTY_TILE = TileDefinition.builder()
            .setTileIndex((short) 0x0000)
            .setTileChar(' ')
            .setName("Empty Space")
            .setAllowedTileTypes(Set.of(JsonTileType.BACKGROUND))
            .setCategory(PLATFORM)
            .build();

    private final Icon primaryDisplayIcon;
    private final Icon secondaryDisplayIcon;
    private final char tileChar;
    private final short tileIndex;
    private final int paletteCount;
    private final boolean isEnabled;
    private final String name;
    private final Set<JsonTileType> allowedTileTypes;
    private final ForegroundTileCategory category;

    private TileDefinition(Builder builder) {
        this.primaryDisplayIcon = builder.primaryDisplayIcon;
        this.secondaryDisplayIcon = builder.secondaryDisplayIcon;
        this.tileChar = builder.tileChar;
        this.tileIndex = builder.tileIndex;
        this.paletteCount = builder.paletteCount;
        this.isEnabled = builder.isEnabled;
        this.name = builder.name;
        this.allowedTileTypes = builder.allowedTileTypes;
        this.category = builder.category;
    }

    public static class Builder {
        private Icon primaryDisplayIcon = null;
        private Icon secondaryDisplayIcon = null;
        private char tileChar = '\0';
        private short tileIndex = 0;
        private int paletteCount = 1;
        private boolean isEnabled = true;
        private String name = "";
        private Set<JsonTileType> allowedTileTypes = Collections.emptySet();
        private ForegroundTileCategory category = UNKNOWN;

        private Builder() {}

        public Builder setPrimaryDisplayTileIcon(Icon primaryDisplayIcon) {
            this.primaryDisplayIcon = primaryDisplayIcon;
            return this;
        }

        public Builder setSecondaryDisplayTileIcon(Icon secondaryDisplayIcon) {
            this.secondaryDisplayIcon = secondaryDisplayIcon;
            return this;
        }

        public Builder setTileChar(char tileChar) {
            this.tileChar = tileChar;
            return this;
        }

        public Builder setTileIndex(short tileIndex) {
            this.tileIndex = tileIndex;
            return this;
        }

        public Builder setPaletteCount(int paletteCount) {
            this.paletteCount = paletteCount;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            isEnabled = enabled;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAllowedTileTypes(Set<JsonTileType> allowedTileTypes) {
            this.allowedTileTypes = allowedTileTypes;
            return this;
        }

        public Builder setCategory(ForegroundTileCategory category) {
            this.category = category;
            return this;
        }

        public TileDefinition build() {
            return new TileDefinition(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Icon getPrimaryDisplayTileIcon() {
        return primaryDisplayIcon;
    }

    public Icon getSecondaryDisplayTileIcon() {
        return secondaryDisplayIcon;
    }

    public char getTileChar() {
        return tileChar;
    }

    public short getTileIndex() {
        return tileIndex;
    }

    public int getPaletteCount() {
        return paletteCount;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getName() {
        return name;
    }

    public Set<JsonTileType> getAllowedTileTypes() {
        return allowedTileTypes;
    }

    public ForegroundTileCategory getCategory() {
        return category;
    }
}
