package samj.mario.editor.io.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonTile {
    public JsonTileType type;
    public JsonContainerType dispenseType;
    public int count; // for DISPENSE type
    public JsonDirection direction; // for ENTRANCE/EXIT types
    public JsonEnemyType enemyType;
    public int x;
    public int y;
    public boolean isAnimated;
}
