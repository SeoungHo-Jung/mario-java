package samj.mario.game;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public class Level {
    public List<List<Tile>> tiles;
    public Level(){}
}
