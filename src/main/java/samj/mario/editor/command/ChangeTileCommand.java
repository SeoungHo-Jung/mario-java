package samj.mario.editor.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.Tile;

public class ChangeTileCommand implements EditorCommand {

    private static final Logger logger = LoggerFactory.getLogger(ChangeTileCommand.class);

    private final int x;
    private final int y;
    private final Tile newTile;
    private final Tile oldTile;
    private final LevelEditor levelEditor;

    public ChangeTileCommand(int x, int y, Tile newTile, Tile oldTile, LevelEditor levelEditor) {
        this.x = x;
        this.y = y;
        this.newTile = newTile;
        this.oldTile = oldTile;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        logger.debug("Set tile {},{}", x, y);
        levelEditor.getLevel().getTileMatrix().setTile(x, y, newTile);
        levelEditor.repaintLevel();
    }

    @Override
    public void undo() {
        logger.debug("Set tile {},{}", x, y);
        levelEditor.getLevel().getTileMatrix().setTile(x, y, oldTile);
        levelEditor.repaintLevel();
    }
}
