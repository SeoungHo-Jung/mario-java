package samj.mario.editor.data;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum ContainerType {
    COIN,
    POWER_UP,
    STAR,
    ONE_UP,
    @JsonEnumDefaultValue UNKNOWN
}
