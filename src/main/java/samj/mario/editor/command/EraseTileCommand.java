package samj.mario.editor.command;

import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.Tile;

import static samj.mario.editor.data.Tile.EMPTY_TILE;

public class EraseTileCommand extends ChangeTileCommand {
    public EraseTileCommand(int x, int y, Tile oldTile, LevelEditor levelEditor) {
        super(x, y, EMPTY_TILE, oldTile, levelEditor);
    }
}
