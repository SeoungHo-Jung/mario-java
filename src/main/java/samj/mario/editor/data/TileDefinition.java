package samj.mario.editor.data;

import samj.mario.editor.io.json.JsonContainerType;
import samj.mario.editor.io.json.JsonEnemyType;
import samj.mario.editor.io.json.JsonTileType;

import java.util.List;

public class TileDefinition {
    public int x;
    public int y;
    public int paletteCount;
    public boolean isAnimated;
    public List<JsonTileType> allowedTypes;
    public List<JsonContainerType> allowedContainerTypes;
    public JsonEnemyType enemyType;

    // Required for deserialization
    public TileDefinition() {
    }

    public TileDefinition(int x, int y, int paletteCount, boolean isAnimated, List<JsonTileType> allowedTypes, List<JsonContainerType> allowedContainerTypes, JsonEnemyType enemyType) {
        this.x = x;
        this.y = y;
        this.paletteCount = paletteCount;
        this.isAnimated = isAnimated;
        this.allowedTypes = allowedTypes;
        this.allowedContainerTypes = allowedContainerTypes;
        this.enemyType = enemyType;
    }
}
