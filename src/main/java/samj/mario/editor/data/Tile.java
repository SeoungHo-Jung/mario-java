package samj.mario.editor.data;

import samj.mario.editor.io.json.JsonContainerType;
import samj.mario.editor.io.json.JsonDirection;
import samj.mario.editor.io.json.JsonEnemyType;
import samj.mario.editor.io.json.JsonTileType;

public class Tile {

    public static Tile EMPTY_TILE = Tile.builder()
            .setTileIndex((short) 0x0000)
            .setTileChar(' ')
            .setName("Empty Space")
            .build();

    private final Icon primaryDisplayIcon;
    private final Icon secondaryDisplayIcon;
    private final char tileChar;
    private final short tileIndex;
    private final Integer tileX;
    private final Integer tileY;
    private final String name;
    private final JsonTileType type;
    private final JsonContainerType containerType;
    private final JsonEnemyType enemyType;
    private final JsonDirection direction;
    private final Integer count;

    private Tile(Builder builder) {
        this.primaryDisplayIcon = builder.primaryDisplayIcon;
        this.secondaryDisplayIcon = builder.secondaryDisplayIcon;
        this.tileChar = builder.tileChar;
        this.tileIndex = builder.tileIndex;
        this.tileX = builder.tileX;
        this.tileY = builder.tileY;
        this.name = builder.name;
        this.type = builder.type;
        this.containerType = builder.containerType;
        this.enemyType = builder.enemyType;
        this.direction = builder.direction;
        this.count = builder.count;
    }

    public static class Builder {
        private Icon primaryDisplayIcon = null;
        private Icon secondaryDisplayIcon = null;
        private char tileChar = '\0';
        private short tileIndex = 0;
        private Integer tileX = null;
        private Integer tileY = null;
        private int paletteCount = 1;
        private boolean isEnabled = true;
        private String name = "";
        private JsonTileType type = JsonTileType.EMPTY;
        private JsonContainerType containerType = null;
        private JsonEnemyType enemyType = null;
        private JsonDirection direction = null;
        private Integer count = null;

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

        public Builder setTileX(Integer tileX) {
            this.tileX = tileX;
            return this;
        }

        public Builder setTileY(Integer tileY) {
            this.tileY = tileY;
            return this;
        }

        public Builder setType(JsonTileType type) {
            this.type = type;
            return this;
        }

        public Builder setContainerType(JsonContainerType containerType) {
            this.containerType = containerType;
            return this;
        }

        public Builder setEnemyType(JsonEnemyType enemyType) {
            this.enemyType = enemyType;
            return this;
        }

        public Builder setDirection(JsonDirection direction) {
            this.direction = direction;
            return this;
        }

        public Builder setCount(Integer count) {
            this.count = count;
            return this;
        }

        public Tile build() {
            return new Tile(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Icon getPrimaryDisplayIcon() {
        return primaryDisplayIcon;
    }

    public Icon getSecondaryDisplayIcon() {
        return secondaryDisplayIcon;
    }

    public char getTileChar() {
        return tileChar;
    }

    public short getTileIndex() {
        return tileIndex;
    }

    public Integer getTileX() {
        return tileX;
    }

    public Integer getTileY() {
        return tileY;
    }

    public String getName() {
        return name;
    }

    public JsonTileType getType() {
        return type;
    }

    public JsonContainerType getContainerType() {
        return containerType;
    }

    public JsonEnemyType getEnemyType() {
        return enemyType;
    }

    public JsonDirection getDirection() {
        return direction;
    }

    public Integer getCount() {
        return count;
    }
}
