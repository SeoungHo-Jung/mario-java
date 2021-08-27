package samj.mario.editor.command;

import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.Tile;
import samj.mario.editor.data.TileType;

public class ChangeTileTypeCommand implements EditorCommand {

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
        System.out.println("Set type: " + newType);
        tile.setType(newType);
        levelEditor.refreshAttributeControls();
    }

    @Override
    public void undo() {
        System.out.println("Set type: " + oldType);
        tile.setType(oldType);
        levelEditor.refreshAttributeControls();
    }
}
