package samj.mario.editor.io.json;

public enum JsonTileType {
    BACKGROUND,
    SOLID,
    BREAK,
    BOUNCE,
    DISPENSE,  // COIN (+ NUMBER), POWER UP, STAR, ONE-UP (Animated for [?] block)
    COIN, // Animated
    TRANSPORT_ENTRANCE, // Param: Index, UP/DOWN/LEFT/RIGHT
    TRANSPORT_EXIT,
    SPAWN,
    ENEMY, // GOOMBA, KOOPA, ETC.
}
