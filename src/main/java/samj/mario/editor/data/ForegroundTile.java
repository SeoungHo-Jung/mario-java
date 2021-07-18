package samj.mario.editor.data;

import samj.mario.editor.io.json.JsonTileType;

import java.util.Collections;
import java.util.Set;

import static samj.mario.editor.data.ForegroundTileCategory.PLATFORM;
import static samj.mario.editor.data.ForegroundTileCategory.UNKNOWN;

public class ForegroundTile {

    public static ForegroundTile EMPTY_TILE = ForegroundTile.builder()
            .setTileIndex((short) 0x0000)
            .setTileChar(' ')
            .setName("Empty Space")
            .setAllowedTileTypes(Set.of(JsonTileType.BACKGROUND))
            .setCategory(PLATFORM)
            .build();

    private final TileIcon primaryDisplayTileIcon;
    private final TileIcon secondaryDisplayTileIcon;
    private final char tileChar;
    private final short tileIndex;
    private final boolean isEnabled;
    private final String name;
    private final Set<JsonTileType> allowedTileTypes;
    private final ForegroundTileCategory category;

    private ForegroundTile(Builder builder) {
        this.primaryDisplayTileIcon = builder.primaryDisplayTileIcon;
        this.secondaryDisplayTileIcon = builder.secondaryDisplayTileIcon;
        this.tileChar = builder.tileChar;
        this.tileIndex = builder.tileIndex;
        this.isEnabled = builder.isEnabled;
        this.name = builder.name;
        this.allowedTileTypes = builder.allowedTileTypes;
        this.category = builder.category;
    }

    public static class Builder {
        private TileIcon primaryDisplayTileIcon = null;
        private TileIcon secondaryDisplayTileIcon = null;
        private char tileChar = '\0';
        private short tileIndex = 0;
        private boolean isEnabled = true;
        private String name = "";
        private Set<JsonTileType> allowedTileTypes = Collections.emptySet();
        private ForegroundTileCategory category = UNKNOWN;

        private Builder() {}

        public Builder setPrimaryDisplayTileIcon(TileIcon primaryDisplayTileIcon) {
            this.primaryDisplayTileIcon = primaryDisplayTileIcon;
            return this;
        }

        public Builder setSecondaryDisplayTileIcon(TileIcon secondaryDisplayTileIcon) {
            this.secondaryDisplayTileIcon = secondaryDisplayTileIcon;
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

        public ForegroundTile build() {
            return new ForegroundTile(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public TileIcon getPrimaryDisplayTileIcon() {
        return primaryDisplayTileIcon;
    }

    public TileIcon getSecondaryDisplayTileIcon() {
        return secondaryDisplayTileIcon;
    }

    public char getTileChar() {
        return tileChar;
    }

    public short getTileIndex() {
        return tileIndex;
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
