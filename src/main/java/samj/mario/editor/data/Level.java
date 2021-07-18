package samj.mario.editor.data;

public class Level {

    private int width;
    private int height;
    private TileMap tileMap;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public TileMap getForegroundLayer() {
        return tileMap;
    }

    public void setForegroundLayer(TileMap tileMap) {
        this.tileMap = tileMap;
    }
}
