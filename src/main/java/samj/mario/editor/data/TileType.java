package samj.mario.editor.data;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum TileType {
    EMPTY,
    BACKGROUND,
    SOLID,
    BREAKABLE,
    BOUNCE,
    CONTAINER,  // COIN (+ NUMBER), POWER UP, STAR, ONE-UP (Animated for [?] block)
    COIN, // Animated
    TRANSPORT_ENTRANCE, // Param: Index, UP/DOWN/LEFT/RIGHT
    TRANSPORT_EXIT,
    MARIO_SPAWN,
    ENEMY_SPAWN, // GOOMBA, KOOPA, ETC.
    @JsonEnumDefaultValue UNKNOWN
}
