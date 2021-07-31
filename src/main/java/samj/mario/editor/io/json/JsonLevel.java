package samj.mario.editor.io.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonLevel {
    public String name;
    public int seconds;
    public JsonColor backgroundColor;
    public List<List<JsonTile>> tiles;
}
