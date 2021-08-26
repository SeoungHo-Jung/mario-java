package samj.mario.editor.command;

import samj.mario.editor.LevelEditor;
import samj.mario.editor.LevelEditor.EditorMode;

public class ChangeEditorModeCommand implements EditorCommand {

    private final EditorMode oldMode;
    private final EditorMode newMode;
    private final LevelEditor levelEditor;

    public ChangeEditorModeCommand(EditorMode oldMode, EditorMode newMode, LevelEditor levelEditor) {
        this.oldMode = oldMode;
        this.newMode = newMode;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        levelEditor.setCurrentMode(newMode);
    }

    @Override
    public void undo() {
        levelEditor.setCurrentMode(oldMode);
    }
}
