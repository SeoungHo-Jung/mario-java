package samj.mario.editor.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.ContainerType;
import samj.mario.editor.data.EnemyType;
import samj.mario.editor.data.Tile;

public class SetEnemySpawnCommand implements EditorCommand {

    private static final Logger logger = LoggerFactory.getLogger(SetEnemySpawnCommand.class);

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
        logger.debug("Set enemy type: {}", newType);
        tile.setEnemyType(newType);
        levelEditor.repaintLevel();
        levelEditor.refreshAttributeControls();
    }

    @Override
    public void undo() {
        logger.debug("Set enemy type: {}", oldType);
        tile.setEnemyType(oldType);
        levelEditor.repaintLevel();
        levelEditor.refreshAttributeControls();
    }
}
