package samj.mario.editor.data;

import java.util.List;

public class TileDefinition {
    public Integer x;
    public Integer y;
    public int paletteCount;
    public boolean isAnimated;
    public List<TileType> allowedTypes;
    public List<ContainerType> allowedContainerTypes;
    public EnemyType enemyType;
    public Tile prototype;

    // Required for deserialization
    public TileDefinition() {
    }
}
