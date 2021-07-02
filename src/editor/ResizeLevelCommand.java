package editor;

public class ResizeLevelCommand implements EditorCommand {

    private final int oldWidth;
    private final int oldHeight;
    private final int newWidth;
    private final int newHeight;
    private final LevelEditor levelEditor;

    public ResizeLevelCommand(int oldWidth, int oldHeight, int newWidth, int newHeight, LevelEditor levelEditor) {
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        levelEditor.setLevelDimensions(newWidth, newHeight);
    }

    @Override
    public void undo() {
        levelEditor.setLevelDimensions(oldWidth, oldHeight);
    }
}