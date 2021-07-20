package samj.mario.editor.io.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonTile {
    public JsonTileType type;
    public JsonContainerType containerType;
    public Integer containerCount;
    public JsonDirection direction; // for ENTRANCE/EXIT types
    public JsonEnemyType enemyType;
    public Integer x;
    public Integer y;
    public Boolean isAnimated;
}
