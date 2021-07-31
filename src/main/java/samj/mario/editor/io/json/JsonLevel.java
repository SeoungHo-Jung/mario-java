package samj.mario.editor.io.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonLevel {
    public String name;
    public int seconds;
    public JsonColor backgroundColor;
    public List<List<JsonTile>> tiles;

    // Required for Object Deserialization
    public JsonLevel() {
    }
}
