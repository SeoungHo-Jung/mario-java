package samj.mario.editor.command;

import samj.mario.editor.LevelEditor;

public class SelectGridTileCommand implements EditorCommand {

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
        levelEditor.setSelectedGridTile(newX, newY);
    }

    @Override
    public void undo() {
        levelEditor.setSelectedGridTile(oldX, oldY);
    }
}
