package samj.mario.editor.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.LevelEditor;
import samj.mario.editor.LevelEditor.EditorMode;

public class ChangeEditorModeCommand implements EditorCommand {

    private static final Logger logger = LoggerFactory.getLogger(ChangeEditorModeCommand.class);

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
        logger.debug("Set editor mode: {}", newMode);
        levelEditor.setCurrentMode(newMode);
    }

    @Override
    public void undo() {
        logger.debug("Set editor mode: {}", oldMode);
        levelEditor.setCurrentMode(oldMode);
    }
}
