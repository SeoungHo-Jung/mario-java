package samj.mario.editor.io;

import samj.mario.editor.data.Level;

public interface LevelFormat {
    byte[] encode(Level level);
    Level decode(byte[] bytes);
}
