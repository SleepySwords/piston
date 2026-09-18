

# piston

A Work-in-progress Minecraft server implementation written in Kotlin.

Supported version: 1.26.10

https://github.com/user-attachments/assets/4a1590e5-7911-465c-8ff9-dc1ffb626e08

Currently supports world loading and player joining

## Usage

To run use the following command:

```
./gradlew run
```

## Architecture

Piston uses a primarily ECS/Pipeline architecture. Events are collected from the network manager and sent to an EventBuffer. Systems can grab specific types of events from the EventBuffer and act upon them. Finally, these events are then sent to the network manager which forwards them to the corresponding client.

To resolve dependencies within systems, Piston uses something called Phases. A Phase is a period within an iteration of the game loop, there may be nested phases, an example `GAME_TICK` phase may also contain a `UPDATE_BLOCK` phase. These phases are then used to generate the ordering of the systems to be executed in. A system may require that a phase must be completed before it is run, it can specify this using `runBefore`, which ensures that the execution order enforces that this system is run before any other system that contains this phase in the `runIn` or `runAfter` variables.

Blocks can only carry their state, any update of a block must occur in a system rather than within block. A BlockState contains the state of a block according to the corresponding BlockDefinition. A Block State also has a separate Physical Block State which differs from the actual Block State, the Physical Block State is the actual value that is sent to the client. This enables some interesting functionality, such as:

```kotlin
class RedstoneWireState(blockDefinition: BlockDefinition, stateID: Int):
    BlockState(blockDefinition, stateID),
    RedstoneSide<RedstoneWireState>,
    RedstonePower<RedstoneWireState>
{

    override fun getPhysicalBlockState(): BlockState {
        if (getPower().toInt() > 5) {
            return Stone.DEFAULT_STATE
        } else {
            return this
        }
    }

    companion object Properties {
        val PROPERTIES = listOf(
            RedstoneSide.WEST_DIRECTION,
            RedstoneSide.SOUTH_DIRECTION,
            RedstonePower.POWER,
            RedstoneSide.NORTH_DIRECTION,
            RedstoneSide.EAST_DIRECTION,
        )
    }
}

```
where a Redstone Wire with a power greater than 5 is displayed as a stone block.

You can see the impacts of this change with the following video, it uses the same environment as the first video, with the change above.

https://github.com/user-attachments/assets/9e5f4902-b84f-4fe6-a9b4-a7770df1dc47
