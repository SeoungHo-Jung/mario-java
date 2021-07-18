package samj.mario.editor;

import samj.mario.editor.data.TileDefinition;

public class ChangeForegroundTileCommand implements EditorCommand {

    private final int x;
    private final int y;
    private final TileDefinition newTile;
    private final TileDefinition oldTile;
    private final LevelEditor levelEditor;

    public ChangeForegroundTileCommand(int x, int y, TileDefinition newTile, TileDefinition oldTile, LevelEditor levelEditor) {
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
