package samj.mario.editor.data;

public class EditorIcon {
    private final IconSheet iconSheet;
    private final int xLocation;
    private final int yLocation;

    public EditorIcon(IconSheet iconSheet, int xLocation, int yLocation) {
        this.iconSheet = iconSheet;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }

    public IconSheet getSpriteSheet() {
        return iconSheet;
    }

    public int getxLocation() {
        return xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }
}
