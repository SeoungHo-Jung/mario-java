package samj.mario.editor.data;

import samj.mario.editor.io.json.JsonContainerType;
import samj.mario.editor.io.json.JsonDirection;
import samj.mario.editor.io.json.JsonEnemyType;
import samj.mario.editor.io.json.JsonTileType;

public class Tile {

    private final Icon primaryDisplayIcon;
    private final Icon secondaryDisplayIcon;
    private final char tileChar;
    private final short tileIndex;
    private final int tileX;
    private final int tileY;
    private final int palette;
    private final boolean isAnimated;
    private final String name;
    private final JsonTileType type;
    private final JsonContainerType containerType;
    private final JsonEnemyType enemyType;

    // Modifiable properties
    private JsonDirection direction = JsonDirection.DOWNWARD;
    private int count = 0;

    public Tile(Icon primaryDisplayIcon, Icon secondaryDisplayIcon, char tileChar, short tileIndex, int tileX, int tileY, int palette, boolean isAnimated, String name, JsonTileType type, JsonContainerType containerType, JsonEnemyType enemyType) {
        this.primaryDisplayIcon = primaryDisplayIcon;
        this.secondaryDisplayIcon = secondaryDisplayIcon;
        this.tileChar = tileChar;
        this.tileIndex = tileIndex;
        this.tileX = tileX;
        this.tileY = tileY;
        this.palette = palette;
        this.isAnimated = isAnimated;
        this.name = name;
        this.type = type;
        this.containerType = containerType;
        this.enemyType = enemyType;
    }

    public Tile setDirection(JsonDirection direction) {
        this.direction = direction;
        return this;
    }

    public Tile setCount(int count) {
        this.count = count;
        return this;
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

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public int getPalette() {
        return palette;
    }

    public boolean isAnimated() {
        return isAnimated;
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

    public int getCount() {
        return count;
    }
}
