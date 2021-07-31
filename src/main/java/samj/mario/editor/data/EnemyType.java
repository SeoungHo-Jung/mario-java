package samj.mario.editor.data;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum EnemyType {
    LITTLE_GOOMBA,
    GREEN_KOOPA_TROOPA,
    BULLET_BILL,
    @JsonEnumDefaultValue UNKNOWN
}
