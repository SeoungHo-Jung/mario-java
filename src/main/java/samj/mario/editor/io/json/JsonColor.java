package samj.mario.editor.io.json;

import java.awt.*;

public class JsonColor {
    public int r;
    public int g;
    public int b;

    // Required for Object Deserialization
    public JsonColor() {
    }

    public JsonColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color asAwtColor() {
        return new Color(r, g, b);
    }
}
