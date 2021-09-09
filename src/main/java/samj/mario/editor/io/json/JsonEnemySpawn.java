package samj.mario.editor.io.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import samj.mario.editor.data.EnemyType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonEnemySpawn {
    public EnemyType type;

    public JsonEnemySpawn() {
    }

    public JsonEnemySpawn(EnemyType type) {
        this.type = type;
    }
}
