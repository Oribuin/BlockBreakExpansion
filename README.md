# Block Break Statistics Expansion

A PlaceholderAPI Expansion to calculate the number of blocks broken by a player from the statistics data.

## Why not use Statistics Expansion?

The statistics expansion is very inaccurate when it comes to the global block break statistic (`%statistic_mine_block%`).

It counts blocks that are not shown in the statistics page and fabricates the number of blocks broken by a player.

## Usage

All materials provided must be considered "blocks", Here are the placeholders that you can use:

- `blockbroken_total` - The total number of blocks broken by the player.
- `blockbroken_<material>` - The number of blocks of the specified material broken by the player.
- `blockbroken_<material>,<material>,...` - Allows calculating multiple materials at once.


