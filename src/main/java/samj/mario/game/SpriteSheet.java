package samj.mario.game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class SpriteSheet {
    BufferedImage loadedSpriteSheet;
    public SpriteSheet(String spriteSheetName){
        loadedSpriteSheet = new SpriteSheetLoader(getClass().getClassLoader().getResource("image/" + spriteSheetName + ".png")).loadedSheet;
    }

}
