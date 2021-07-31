package samj.mario.editor.data;

import java.util.List;

public class TileDefinition {
    public int x;
    public int y;
    public int paletteCount;
    public boolean isAnimated;
    public List<TileType> allowedTypes;
    public List<ContainerType> allowedContainerTypes;
    public EnemyType enemyType;

    // Required for deserialization
    public TileDefinition() {
    }

    public TileDefinition(int x, int y, int paletteCount, boolean isAnimated, List<TileType> allowedTypes, List<ContainerType> allowedContainerTypes, EnemyType enemyType) {
        this.x = x;
        this.y = y;
        this.paletteCount = paletteCount;
        this.isAnimated = isAnimated;
        this.allowedTypes = allowedTypes;
        this.allowedContainerTypes = allowedContainerTypes;
        this.enemyType = enemyType;
    }
}
