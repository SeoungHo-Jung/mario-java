package samj.mario.editor.io.json;

public enum JsonTileType {
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
}
