package samj.mario.editor.command;

import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.ContainerType;
import samj.mario.editor.data.EnemyType;
import samj.mario.editor.data.Tile;

public class SetEnemySpawnCommand implements EditorCommand {

    private final Tile tile;
    private final EnemyType oldType;
    private final EnemyType newType;
    private final LevelEditor levelEditor;

    public SetEnemySpawnCommand(Tile tile, EnemyType oldType, EnemyType newType, LevelEditor levelEditor) {
        this.tile = tile;
        this.oldType = oldType;
        this.newType = newType;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        System.out.println("Set enemy type: " + newType);
        tile.setEnemyType(newType);
        levelEditor.refreshAttributeControls();
    }

    @Override
    public void undo() {
        System.out.println("Set enemy type: " + oldType);
        tile.setEnemyType(oldType);
        levelEditor.refreshAttributeControls();
    }
}
