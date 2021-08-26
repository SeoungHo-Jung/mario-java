package samj.mario.editor.command;

import samj.mario.editor.LevelEditor;

public class SelectGridTileCommand implements EditorCommand {

    private int oldX;
    private int oldY;
    private int newX;
    private int newY;
    private LevelEditor levelEditor;

    public SelectGridTileCommand(int oldX, int oldY, int newX, int newY, LevelEditor levelEditor) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        levelEditor.setSelectedGridTileX(newX);
        levelEditor.setSelectedGridTileY(newY);
        handleSelectionChange(newX, newY);
    }

    @Override
    public void undo() {
        levelEditor.setSelectedGridTileX(oldX);
        levelEditor.setSelectedGridTileY(oldY);
        handleSelectionChange(oldX, oldY);
    }

    private void handleSelectionChange(int x, int y) {
        System.out.println("Selected grid (" + x + "," + y + ")");
        levelEditor.getLevelPanel().repaint();
    }
}
