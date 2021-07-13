package editor;

import static editor.ForegroundTileCategory.PLATFORM;
import static editor.ForegroundTileCategory.UNKNOWN;

public class ForegroundTile {

    public static ForegroundTile EMPTY_TILE = ForegroundTile.builder()
            .setTileIndex(' ')
            .setName("Empty Space")
            .setCategory(PLATFORM)
            .build();

    private final TileIcon primaryDisplayTileIcon;
    private final TileIcon secondaryDisplayTileIcon;
    private final char tileIndex;
    private final boolean isEnabled;
    private final String name;
    private final ForegroundTileCategory category;

    private ForegroundTile(Builder builder) {
        this.primaryDisplayTileIcon = builder.primaryDisplayTileIcon;
        this.secondaryDisplayTileIcon = builder.secondaryDisplayTileIcon;
        this.tileIndex = builder.tileIndex;
        this.isEnabled = builder.isEnabled;
        this.name = builder.name;
        this.category = builder.category;
    }

    public static class Builder {
        private TileIcon primaryDisplayTileIcon = null;
        private TileIcon secondaryDisplayTileIcon = null;
        private char tileIndex = '\0';
        private boolean isEnabled = true;
        private String name = "";
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

        public Builder setTileIndex(char tileIndex) {
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
