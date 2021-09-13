package samj.mario.editor.command;

import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.ContainerType;
import samj.mario.editor.data.Tile;
import samj.mario.editor.data.TileType;

public class ChangeContainerTypeCommand implements EditorCommand {

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
        System.out.println("Set container type: " + newType);
        tile.setContainerType(newType);
        levelEditor.repaintLevel();
        levelEditor.refreshAttributeControls();
    }

    @Override
    public void undo() {
        System.out.println("Set container type: " + oldType);
        tile.setContainerType(oldType);
        levelEditor.repaintLevel();
        levelEditor.refreshAttributeControls();
    }
}
