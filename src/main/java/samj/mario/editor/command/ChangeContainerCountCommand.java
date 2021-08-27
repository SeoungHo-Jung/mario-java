package samj.mario.editor.command;

import samj.mario.editor.LevelEditor;
import samj.mario.editor.data.ContainerType;
import samj.mario.editor.data.Tile;

public class ChangeContainerCountCommand implements EditorCommand {

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
        System.out.println("Set container count: " + newValue);
        tile.setCount(newValue);
        levelEditor.refreshAttributeControls();
    }

    @Override
    public void undo() {
        System.out.println("Set container count: " + oldValue);
        tile.setCount(oldValue);
        levelEditor.refreshAttributeControls();
    }
}
