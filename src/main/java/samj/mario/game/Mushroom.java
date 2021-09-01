package samj.mario.game;

import java.awt.image.BufferedImage;

public class Mushroom {

    private float x;
    private float y;
    private BufferedImage image;

    public Mushroom(float x, float y) {
        this.x = x;
        this.y = y;
        this.image = Game.ITEM_SPRITE_SHEET.loadedSpriteSheet.getSubimage(0, 0, Game.BLOCK_WIDTH, Game.BLOCK_HEIGHT);
    }

    public void tick() {
        // update position
    }

    public void render(Screen screen) {
        // if is on screen.... draw
        screen.drawToScreen(image, (int)x, (int)y);
    }
}
