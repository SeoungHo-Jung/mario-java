package samj.mario.editor.data;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum TileType {
    EMPTY("Empty"),
    BACKGROUND("Background"),
    SOLID("Solid"),
    BREAKABLE("Breakable"),
    BOUNCE("Bouncing"),
    CONTAINER("Containers"),  // COIN (+ NUMBER), POWER UP, STAR, ONE-UP (Animated for [?] block)
    COIN("Coins"), // Animated
    TRANSPORT_ENTRANCE("Transports"), // Param: Index, UP/DOWN/LEFT/RIGHT
    TRANSPORT_EXIT("Transports"),
    MARIO_SPAWN("Spawns"),
    ENEMY_SPAWN("Spawns"), // GOOMBA, KOOPA, ETC.
    @JsonEnumDefaultValue UNKNOWN("UNKNOWN");

    private final String displayName;

    TileType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
