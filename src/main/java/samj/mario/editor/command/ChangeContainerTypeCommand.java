package samj.mario.editor.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.ContainerType;
import samj.mario.editor.data.Tile;
import samj.mario.editor.data.TileType;

public class ChangeContainerTypeCommand implements EditorCommand {

    private static final Logger logger = LoggerFactory.getLogger(ChangeContainerTypeCommand.class);

    private final Tile tile;
    private final ContainerType oldType;
    private final ContainerType newType;
    private final LevelEditor levelEditor;

    public ChangeContainerTypeCommand(Tile tile, ContainerType oldType, ContainerType newType, LevelEditor levelEditor) {
        this.tile = tile;
        this.oldType = oldType;
        this.newType = newType;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        logger.debug("Set container type: {}", newType);
        tile.setContainerType(newType);
        levelEditor.repaintLevel();
        levelEditor.refreshAttributeControls();
    }

    @Override
    public void undo() {
        logger.debug("Set container type: {}", oldType);
        tile.setContainerType(oldType);
        levelEditor.repaintLevel();
        levelEditor.refreshAttributeControls();
    }
}
