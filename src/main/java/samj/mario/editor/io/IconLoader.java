package samj.mario.editor.io;

import samj.mario.editor.data.Icon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class IconLoader {

    private final int iconSize;

    public IconLoader(int iconSize) {
        this.iconSize = iconSize;

        // TODO: move somewhere else
        loadIcons();
    }

    private BufferedImage tileIcons;
    private BufferedImage editorIcons;

    public void loadIcons() {
        tileIcons = loadIconsFile("image/tiles.png");
        editorIcons = loadIconsFile("image/editor.png");
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
            // return a default "null" image
            return new BufferedImage(iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);
        }

        int x = icon.getxLocation() * iconSize;
        int y = icon.getyLocation() * iconSize;
        switch (icon.getSpriteSheet()) {
            case TILES -> {
                return tileIcons.getSubimage(x, y, iconSize, iconSize);
            }
            case EDITOR -> {
                return editorIcons.getSubimage(x, y, iconSize, iconSize);
            }
            default -> throw new UnsupportedOperationException(icon.getSpriteSheet() + " icons are not supported");
        }
    }
}
