package samj.mario.editor.data;

public class IconResolver {

    public EditorIcon primaryDisplayIcon(Tile tile) {
        if (tile.getType() == TileType.COIN) {
            // Coin icon
            return new EditorIcon(IconSheet.ITEMS, 0, 6);
        }

        if (tile.getTileX() == null || tile.getTileY() == null) {
            // TODO: Give Invisible Container a dotted line border
            return null;
        }

        // TODO: Optimize
        return new EditorIcon(IconSheet.TILES, tile.getTileX(), tile.getTileY());
    }

    public EditorIcon secondaryDisplayIcon(Tile tile) {

        if (tile.getEnemyType() != null) {
            // return the enemy Icon for the selected enemy type
            switch (tile.getEnemyType()) {
                case LITTLE_GOOMBA -> {
                    return new EditorIcon(IconSheet.ENEMY, 0, 1);
                }
                case GREEN_KOOPA_TROOPA -> {
                    return new EditorIcon(IconSheet.ENEMY, 6, 1);
                }
                case BULLET_BILL -> {
                    return new EditorIcon(IconSheet.ENEMY, 35, 1);
                }
                default -> {
                    return null;
                }
            }

        } else {
            // return an Icon representing the Tile type if applicable
            if (tile.getType() == TileType.CONTAINER) {
                switch (tile.getContainerType()) {
                    case COIN -> {
                        return new EditorIcon(IconSheet.ITEMS, 0, 6);
                    }
                    case POWER_UP -> {
                        return new EditorIcon(IconSheet.ITEMS, 0, 0);
                    }
                    case STAR -> {
                        return new EditorIcon(IconSheet.ITEMS, 0, 3);
                    }
                    case ONE_UP -> {
                        return new EditorIcon(IconSheet.ITEMS, 1, 0);
                    }
                    default -> {
                        return null;
                    }
                }
            } else {
                return null;
            }
        }
    }
}
