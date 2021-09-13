package samj.mario.editor.io;

import samj.mario.editor.data.Icon;
import samj.mario.editor.data.IconSheet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IconLoader {

    private final int iconSize;

    public IconLoader(int iconSize) {
        this.iconSize = iconSize;

        // TODO: move somewhere else
        loadIcons();
    }

    private Map<IconSheet, BufferedImage> imageByIconSheet;

    public void loadIcons() {
        imageByIconSheet = Map.of(IconSheet.TILES, loadIconsFile("image/tiles.png"),
                IconSheet.ENEMY, loadIconsFile("image/enemy.png"),
                IconSheet.ITEMS, loadIconsFile("image/items.png"),
                IconSheet.EDITOR, loadIconsFile("image/editor.png"));
    }

    private BufferedImage loadIconsFile(String tilesFile) {
        URL imageURL = getClass().getClassLoader().getResource(tilesFile);
        if (imageURL == null) {
            throw new RuntimeException("Couldn't find icon file: " + tilesFile);
        } else {
            try {
                BufferedImage in = ImageIO.read(imageURL);
                BufferedImage icons = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
                icons.getGraphics().drawImage(in, 0, 0, null);
                return icons;
            } catch (IOException e) {
                throw new RuntimeException("Failed to load icon file: " + tilesFile, e);
            }
        }
    }

    public Image getImageForIcon(Icon icon) {
        if (icon == null) {
            return defaultImage();
        }

        int x = icon.getxLocation() * iconSize;
        int y = icon.getyLocation() * iconSize;
        BufferedImage image = imageByIconSheet.get(icon.getSpriteSheet());

        if (image == null) {
            System.out.println("Could not find image for IconSheet " + icon.getSpriteSheet());
            return defaultImage();
        }

        if (x + iconSize > image.getWidth()) {
            System.out.println("Icon location out of bounds for Image " + icon.getSpriteSheet());
            return defaultImage();
        }

        if (y + iconSize > image.getHeight()) {
            System.out.println("Icon location out of bounds for Image " + icon.getSpriteSheet());
            return defaultImage();
        }

        return imageByIconSheet.get(icon.getSpriteSheet()).getSubimage(x, y, iconSize, iconSize);
    }

    private BufferedImage defaultImage() {
        // return a default "null" image
        return new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
    }
}
