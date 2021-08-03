package samj.mario.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.source.tree.ContinueTree;
@JsonIgnoreProperties(ignoreUnknown = true)

public class Tile {
    enum TileType{
        BACKGROUND, EMPTY, SOLID, BREAKABLE, BOUNCE, CONTAINER, COIN, TRANSPORT_ENTRANCE, TRANSPORT_EXIT, MARIO_SPAWN, ENEMY_SPAWN;
    }
    enum ContainerType{
        COIN, POWER_UP, STAR, ONE_UP
    }
    public TileType type;
    //ContainerType container_Type;
    public Integer x;
    public Integer y;
    public boolean isAnimated;

    public Tile(){}


    public int getTileX(){
        return x;
    }

    public int getTileY(){
        return y;
    }

}
