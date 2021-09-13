package samj.mario.editor.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.LevelEditor;

public class SelectGridTileCommand implements EditorCommand {

    private static final Logger logger = LoggerFactory.getLogger(SelectGridTileCommand.class);

    private final int oldX;
    private final int oldY;
    private final int newX;
    private final int newY;
    private final LevelEditor levelEditor;

    public SelectGridTileCommand(int oldX, int oldY, int newX, int newY, LevelEditor levelEditor) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        logger.debug("Select tile {},{}", newX, newY);
        levelEditor.setSelectedGridTile(newX, newY);
    }

    @Override
    public void undo() {
        logger.debug("Select tile {},{}", oldX, oldY);
        levelEditor.setSelectedGridTile(oldX, oldY);
    }
}
