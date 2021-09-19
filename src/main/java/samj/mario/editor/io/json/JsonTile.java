package samj.mario.editor.io.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import samj.mario.editor.data.ContainerType;
import samj.mario.editor.data.Direction;
import samj.mario.editor.data.TileType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonTile {
    public TileType type;
    public ContainerType containerType;
    public Integer containerCount;
    public Direction direction; // for ENTRANCE/EXIT types
    public JsonEnemySpawn enemySpawn;
    public Integer x;
    public Integer y;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Boolean isAnimated;

    // Required for Object Deserialization
    public JsonTile() {
    }
}
