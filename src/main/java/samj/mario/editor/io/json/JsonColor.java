package samj.mario.editor.io.json;

public class JsonColor {
    public float r;
    public float g;
    public float b;

    // Required for Object Deserialization
    public JsonColor() {
    }

    public JsonColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
