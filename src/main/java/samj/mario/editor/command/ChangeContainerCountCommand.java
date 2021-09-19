package samj.mario.editor.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.Tile;

public class ChangeContainerCountCommand implements EditorCommand {

    private static final Logger logger = LoggerFactory.getLogger(ChangeContainerCountCommand.class);

    private final Tile tile;
    private final Integer oldValue;
    private final Integer newValue;
    private final LevelEditor levelEditor;

    public ChangeContainerCountCommand(Tile tile, Integer oldValue, Integer newValue, LevelEditor levelEditor) {
        this.tile = tile;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        logger.debug("Set container count: {}", newValue);
        tile.setCount(newValue);
        levelEditor.refreshAttributeControls();
    }

    @Override
    public void undo() {
        logger.debug("Set container count: {}", oldValue);
        tile.setCount(oldValue);
        levelEditor.refreshAttributeControls();
    }
}
