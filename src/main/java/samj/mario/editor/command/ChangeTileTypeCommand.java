package samj.mario.editor.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.Tile;
import samj.mario.editor.data.TileType;

public class ChangeTileTypeCommand implements EditorCommand {

    private static final Logger logger = LoggerFactory.getLogger(ChangeTileTypeCommand.class);

    private final Tile tile;
    private final TileType oldType;
    private final TileType newType;
    private final LevelEditor levelEditor;

    public ChangeTileTypeCommand(Tile tile, TileType oldType, TileType newType, LevelEditor levelEditor) {
        this.tile = tile;
        this.oldType = oldType;
        this.newType = newType;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        logger.debug("Set type: {}", newType);
        tile.setType(newType);
        levelEditor.repaintLevel();
        levelEditor.refreshAttributeControls();
    }

    @Override
    public void undo() {
        logger.debug("Set type: {}", oldType);
        tile.setType(oldType);
        levelEditor.repaintLevel();
        levelEditor.refreshAttributeControls();
    }
}
