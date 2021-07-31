package samj.mario.editor.data;

public enum IconSheet {
    TILES(33, 28),
//    ITEMS(),
//    ENEMY()
    ;

    private int width;
    private int height;

    IconSheet(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
