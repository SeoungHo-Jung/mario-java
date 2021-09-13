package samj.mario.editor.data;

public class IconResolver {

    public Icon primaryDisplayIcon(Tile tile) {
        if (tile.getTileX() == null || tile.getTileY() == null) {
            return null;
        }

        // TODO: Optimize
        return new Icon(IconSheet.TILES, tile.getTileX(), tile.getTileY());
    }

    public Icon secondaryDisplayIcon(Tile tile) {

        if (tile.getEnemyType() != null) {
            // return the enemy Icon for the selected enemy type
            switch (tile.getEnemyType()) {
                case LITTLE_GOOMBA -> {
                    return new Icon(IconSheet.ENEMY, 0, 1);
                }
                case GREEN_KOOPA_TROOPA -> {
                    return new Icon(IconSheet.ENEMY, 6, 1);
                }
                case BULLET_BILL -> {
                    return new Icon(IconSheet.ENEMY, 35, 1);
                }
                default -> {
                    return null;
                }
            }

        } else {
            // return an Icon representing the Tile type if applicable
            switch (tile.getType()) {
                case CONTAINER -> {
                    switch (tile.getContainerType()) {
                        case COIN -> {
                            return new Icon(IconSheet.ITEMS, 0, 6);
                        }
                        case POWER_UP -> {
                            return new Icon(IconSheet.ITEMS, 0, 0);
                        }
                        case STAR -> {
                            return new Icon(IconSheet.ITEMS, 0, 3);
                        }
                        case ONE_UP -> {
                            return new Icon(IconSheet.ITEMS, 1, 0);
                        }
                        default -> {
                            return null;
                        }
                    }
                }
                case COIN -> {
                    return new Icon(IconSheet.ITEMS, 0, 6);
                }
                default -> {
                    return null;
                }
            }
        }
    }
}
