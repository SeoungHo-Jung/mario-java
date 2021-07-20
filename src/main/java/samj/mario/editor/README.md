# SMB Java Level Editor

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
      "y": 0,
      "isAnimated": false
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
      "y": 6,
      "isAnimated": false,
    },
    {
      "type": "ENEMY_SPAWN",
      "enemyType": "BULLET_BILL",
      "x": 9,
      "y": 0,
      "isAnimated": false
    },
    {
      "type": "EMPTY",
      "isAnimated": false
    }
  ]
}
```

### Definitions

###### Level (Object)

* `name` - String -  _Optional_ : The name of the Level as it appears in-game.
* `seconds` - Integer - _Optional_ : The time limit for the level allowed for the player in seconds.
* `backgroundColor` - `Color` - _Required_ : The color of this level's background.
* `tiles` - Array of Array of `Tile` - _Required_ : A two-dimensional array of Tile objects which represents the level in the form of a tile grid.
    Each inner array in `tiles` represents a row of tiles from left to right. The outer array's order is top to bottom.

###### Tile (Object)
* `type` - `TileType` - _Required_
* `containerType` - `ContainerType` - _Optional_ (unless `type` is `CONTAINER`)
* `containerCount`- Integer - _Optional_ (unless `type` is `CONTAINER`)
* `direction` - `Direction` - _Optional_ (unless `type` is `TRANSPORT_ENTRANCE` or `TRANSPORT_EXIT`)
* `enemyType` - `EnemyType` - _Optional_ (unless `type` is `ENEMY_SPAWN`)
* `x` - Integer - _Optional_ (only when `type` is `EMPTY`)
* `y` - Integer - _Optional_ (only when `type` is `EMPTY`) 
* `isAnimated` - Boolean - _Optional_

###### Color (Object)
* `r` - Integer - _Required_ : The red component of the RGB color - Only values 0-255 are allowed.
* `g` - Integer - _Required_ : The green component of the RGB color - Only values 0-255 are allowed.
* `b` - Integer - _Required_ : The blue component of the RGB color - Only values 0-255 are allowed.

###### TileType (Enum)
* `BACKGROUND` : A Tile which is visual only and doesn't interact whatsoever with any objects in the game. 
* `EMPTY` : Same as `BACKGROUND`, but with no image rendered from the tilesheet. Should appear as a solid tile of `Level.backgroundColor`
* `SOLID` : A fixed block that has collision. 
* `BREAKABLE` : Same as `SOLID` but breaks when head-bonked by Mario.
* `BOUNCE` : Same as `SOLID` but bounces when head-bonked by Mario. This can result in killing enemies or collecting coins
    that are above this tile.
* `CONTAINER` : Same as `BOUNCE`, but releases items (coins and power-ups) when head-bonked by Mario. Containers can also
    contain more than one item (of a single type), usually for coins. Once all the items have been collected by the player,
    the container will change it's appearance and become solid.
* `COIN` : This is a free-standing coin which can be collected when Mario comes in contact with.
* `TRANSPORT_ENTRANCE` : Transports Mario to a corresponding `TRANSPORT_EXIT` when Mario collides with it in the specified `Direction`.
    Usually, this tile type is used in conjunction with the "pipe" tiles.
* `TRANSPORT_EXIT` : Destination when Mario enters a `TRANSPORT_ENTRANCE`. In this case, the `Direction` indicates the direction in
    which Mario emerges from the exit.
* `MARIO_SPAWN` : Indicates the tile on which Mario should spawn at the start of a level. Can be used multiple times within
    a single level to re-spawn mario after he reaches a checkpoint.
* `ENEMY_SPAWN` : Location where an enemy should spawn on the level. This can be any type of enemy as indicated by the `enemyType`.

###### ContainerType (Enum)
* `COIN`
* `POWER_UP`
* `STAR`
* `ONE_UP`

###### EnemyType (Enum)
* `LITTLE_GOOMBA`
* `GREEN_KOOPA_TROOPA`
* `BULLET_BILL`

###### Direction (Enum)
* `UPWARD`
* `RIGHTWARD`
* `DOWNWARD`
* `LEFTWARD`