# Mario Java Level Editor

## File Format (JSON)
### Example `Level`
```json
{
  "name": "World 1-1",
  "seconds": 300,
  "backgroundColor": {
    "r": 0,
    "g": 255,
    "b": 255
  },
  "tiles": [
    {
      "type": "SOLID",
      "x": 0,
      "y": 0
    },
    {
      "type": "CONTAINER",
      "containerType": "COIN",
      "itemCount": 7,
      "x": 24,
      "y": 0,
      "isAnimated": true
    },
    {
      "type": "BREAKABLE",
      "x": 1,
      "y": 6
    },
    {
      "type": "BACKGROUND",
      "enemySpawn": {
        "type": "BULLET_BILL"
      },
      "x": 9,
      "y": 0
    },
    {
      "type": "EMPTY"
    }
  ]
}
```

### Definitions

###### Level (Object)
| Property | Type | Nullability | Description |
| -------- | ---- | ----------- | ----------- |
| `name` | String |  _Optional_ | The name of the Level as it appears in-game. |
| `seconds` | Integer | _Optional_ | The time limit for the level allowed for the player in seconds. |
| `backgroundColor` | `Color` | _Required_ | The color of this level's background. |
| `tiles` | Array of Array of `Tile` | _Required_ | A two-dimensional array of Tile objects which represents the level in the form of a tile grid. Each inner array in `tiles` represents a row of tiles from left to right. The outer array's order is top to bottom. |

###### Tile (Object)
| Property | Type | Nullability | Description |
| -------- | ---- | ----------- | ----------- |
| `type` | `TileType` | _Required_ | The way this tile should behave. Each tile has a single `type`. |
| `containerType` | `ContainerType` | _Optional_ (unless `type` is `CONTAINER`) | The type of item this container dispenses. |
| `containerCount`| Integer | _Optional_ (unless `type` is `CONTAINER`) | The count of items this container dispenses (mainly used for coins). |
| `enemySpawn` | `EnemySpawn` | _Optional_ | How an enemy should be spawned from this tile. Only valid on `BACKGROUND` tiles. | 
| `x` | Integer | _Optional_ (only when `type` is `EMPTY`) | The x coordinate of this tile's graphic on the tile sheet. |
| `y` | Integer | _Optional_ (only when `type` is `EMPTY`) | The y coordinate of this tile's graphic on the tile sheet. |
| `isAnimated` | Boolean | _Required_ | Whether this particular tile should be animated. |

###### EnemySpawn (Object)
| Property | Type | Nullability | Description |
| `type` | `EnemyType` | _Required_ | The type of enemy to spawn from this tile - Eg. goomba, koopa, etc. |

###### Color (Object)
| Property | Type | Nullability | Description |
| -------- | ---- | ----------- | ----------- |
| `r` | Integer | _Required_ | The red component of the RGB color - Only values 0-255 are allowed. |
| `g` | Integer | _Required_ | The green component of the RGB color - Only values 0-255 are allowed. |
| `b` | Integer | _Required_ | The blue component of the RGB color - Only values 0-255 are allowed. |

###### TileType (Enum)
| Value | Description |
| ----- | ----------- |
| `BACKGROUND` | A Tile which is visual only and doesn't interact whatsoever with any objects in the game. | 
| `EMPTY` | Same as `BACKGROUND`, but with no image rendered from the tilesheet. Should appear as a solid tile of `Level.backgroundColor` |
| `SOLID` | A fixed block that has collision. | 
| `BREAKABLE` | Same as `SOLID` but breaks when head-bonked by Mario. |
| `BOUNCE` | Same as `SOLID` but bounces when head-bonked by Mario. This can result in killing enemies or collecting coins that are above this tile. |
| `CONTAINER` | Same as `BOUNCE`, but releases items (coins and power-ups) when head-bonked by Mario. Containers can also contain more than one item (of a single type), usually for coins. Once all the items have been collected by the player, | the container will chang| it's appearance and become solid. |
| `COIN` | This is a free-standing coin which can be collected when Mario comes in contact with. |

###### ContainerType (Enum)
| Value | Description |
| ----- | ----------- |
| `COIN` | https://www.mariowiki.com/Coin |
| `POWER_UP` | https://www.mariowiki.com/Super_Mushroom </br> https://www.mariowiki.com/Fire_Flower |
| `STAR` | https://www.mariowiki.com/Super_Star |
| `ONE_UP` | https://www.mariowiki.com/1-Up_Mushroom |

###### EnemyType (Enum)
| Value | Description |
| ----- | ----------- |
| `LITTLE_GOOMBA` | https://www.mariowiki.com/Goomba |
| `GREEN_KOOPA_TROOPA` | https://www.mariowiki.com/Koopa_Troopa |
| `BULLET_BILL` | https://www.mariowiki.com/Bullet_Bill |