package samj.mario.editor;

public interface LevelFormat {
    byte[] encode(Level level);
    Level decode(byte[] bytes);
}
