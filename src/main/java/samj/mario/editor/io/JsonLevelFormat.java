package samj.mario.editor.io;

import samj.mario.editor.data.Level;

public class JsonLevelFormat implements LevelFormat {
    @Override
    public byte[] encode(Level level) {
        return new byte[0];
    }

    @Override
    public Level decode(byte[] bytes) {
        return null;
    }
}
