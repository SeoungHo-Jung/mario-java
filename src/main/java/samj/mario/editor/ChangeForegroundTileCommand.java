package samj.mario.editor;

import samj.mario.editor.data.ForegroundTile;

public class ChangeForegroundTileCommand implements EditorCommand {

    private final int x;
    private final int y;
    private final ForegroundTile newTile;
    private final ForegroundTile oldTile;
    private final LevelEditor levelEditor;

    public ChangeForegroundTileCommand(int x, int y, ForegroundTile newTile, ForegroundTile oldTile, LevelEditor levelEditor) {
        this.x = x;
        this.y = y;
        this.newTile = newTile;
        this.oldTile = oldTile;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        levelEditor.getLevel().getForegroundLayer().setTile(x, y, newTile);
        levelEditor.getLevelPanel().repaint();
    }

    @Override
    public void undo() {
        levelEditor.getLevel().getForegroundLayer().setTile(x, y, oldTile);
        levelEditor.getLevelPanel().repaint();
    }
}
