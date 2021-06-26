package editor;

public class TileIcon {
    private final SpriteSheet spriteSheet;
    private final int xLocation;
    private final int yLocation;

    public TileIcon(SpriteSheet spriteSheet, int xLocation, int yLocation) {
        this.spriteSheet = spriteSheet;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }

    public int getxLocation() {
        return xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }
}
