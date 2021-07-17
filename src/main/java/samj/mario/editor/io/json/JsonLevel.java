package samj.mario.editor.io.json;

import java.util.List;

public class JsonLevel {
    public String name;
    public int seconds;
    public JsonColor backgroundColor;
    public List<List<JsonTile>> tiles;
}
