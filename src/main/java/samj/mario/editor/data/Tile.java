package samj.mario.editor.data;

import java.util.Collections;
import java.util.List;

public class Tile {

    public static Tile EMPTY_TILE = Tile.builder()
            .setType(TileType.EMPTY)
            .setName("Empty Space")
            .setAllowedTileTypes(List.of(TileType.EMPTY))
            .build();

    private final Icon primaryDisplayIcon;
    private final Icon secondaryDisplayIcon;
    private final Integer tileX;
    private final Integer tileY;
    private final Integer tilePalette;
    private final boolean isAnimated;
    private final String name;
    private TileType type;
    private final ContainerType containerType;
    private final EnemyType enemyType;
    private final Direction direction;
    private final Integer count;
    private final List<TileType> allowedTileTypes;

    private Tile(Builder builder) {
        this.primaryDisplayIcon = builder.primaryDisplayIcon;
        this.secondaryDisplayIcon = builder.secondaryDisplayIcon;
        this.tileX = builder.tileX;
        this.tileY = builder.tileY;
        this.tilePalette = builder.tilePalette;
        this.isAnimated = builder.isAnimated;
        this.name = builder.name;
        this.type = builder.type;
        this.containerType = builder.containerType;
        this.enemyType = builder.enemyType;
        this.direction = builder.direction;
        this.count = builder.count;
        this.allowedTileTypes = builder.allowedTileTypes;
    }

    public static class Builder {
        private Icon primaryDisplayIcon = null;
        private Icon secondaryDisplayIcon = null;
        private Integer tileX = null;
        private Integer tileY = null;
        private Integer tilePalette = null;
        private boolean isAnimated = false;
        private String name = "";
        private TileType type = TileType.EMPTY;
        private ContainerType containerType = null;
        private EnemyType enemyType = null;
        private Direction direction = null;
        private Integer count = null;
        private List<TileType> allowedTileTypes = Collections.emptyList();

        private Builder() {}

        public Builder setPrimaryDisplayTileIcon(Icon primaryDisplayIcon) {
            this.primaryDisplayIcon = primaryDisplayIcon;
            return this;
        }

        public Builder setSecondaryDisplayTileIcon(Icon secondaryDisplayIcon) {
            this.secondaryDisplayIcon = secondaryDisplayIcon;
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

        public Builder setTilePalette(Integer tilePalette) {
            this.tilePalette = tilePalette;
            return this;
        }

        public Builder setAnimated(boolean animated) {
            isAnimated = animated;
            return this;
        }

        public Builder setType(TileType type) {
            this.type = type;
            return this;
        }

        public Builder setContainerType(ContainerType containerType) {
            this.containerType = containerType;
            return this;
        }

        public Builder setEnemyType(EnemyType enemyType) {
            this.enemyType = enemyType;
            return this;
        }

        public Builder setDirection(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder setCount(Integer count) {
            this.count = count;
            return this;
        }

        public Builder setAllowedTileTypes(List<TileType> allowedTileTypes) {
            this.allowedTileTypes = allowedTileTypes;
            return this;
        }

        public Icon getPrimaryDisplayIcon() {
            return primaryDisplayIcon;
        }

        public Icon getSecondaryDisplayIcon() {
            return secondaryDisplayIcon;
        }

        public Integer getTileX() {
            return tileX;
        }

        public Integer getTileY() {
            return tileY;
        }

        public Integer getTilePalette() {
            return tilePalette;
        }

        public boolean isAnimated() {
            return isAnimated;
        }

        public String getName() {
            return name;
        }

        public TileType getType() {
            return type;
        }

        public ContainerType getContainerType() {
            return containerType;
        }

        public EnemyType getEnemyType() {
            return enemyType;
        }

        public Direction getDirection() {
            return direction;
        }

        public Integer getCount() {
            return count;
        }

        public List<TileType> getAllowedTileTypes() {
            return allowedTileTypes;
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

    public Integer getTileX() {
        return tileX;
    }

    public Integer getTileY() {
        return tileY;
    }

    public Integer getTilePalette() {
        return tilePalette;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public String getName() {
        return name;
    }

    public TileType getType() {
        return type;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }

    public Direction getDirection() {
        return direction;
    }

    public Integer getCount() {
        return count;
    }

    public List<TileType> getAllowedTileTypes() {
        return allowedTileTypes;
    }

    public Tile setType(TileType type) {
        this.type = type;
        return this;
    }
}
