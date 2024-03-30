# ProbeJS

A data dumper and typing generator for the KubeJS functions, constants and classes.

Great thanks to @DAmNRelentless, @LatvianModder and @yesterday17 for invaluable suggestions during the development!

## 1. Showcase

Auto-completion snippets for Items, Blocks, Fluids, Entities and Tags:

![image](./examples/2.gif)

Auto-completion, type-hinting for most of the functions and classes:

![image](./examples/3.gif)

## 2. Installation

1. Get VSCode.
2. Install the mod.
3. In game, use `/probejs dump` and wait for the typings to be generated.
4. Open the your game folder in VSCode, you should see snippets and typing functioning.
5. Use `/probejs dump` in case of you want to refresh the generated typing. If VSCode is not responding to file changes,
   press F1 and execute `TypeScript: Restart TS server` to force a refresh in Code.

## 3. Event Dump

1. Run the game, and use the `/probejs dump` commmand **only** after the events of interest are fired, then dump and regenerate typings as before.
2. Reload your IDE if your IDE doesn't know about the changes of typings, you will see those `onEvent` with correct typings now.
3. v1.4 allows dumped events to be persisted between dumps, no matter actually they're fired or not in current dump, if an event is missing (mostly from the removal of mods), cached events will be automatically remove too. If you want to clear the cache manually, use `/probejs clear_cache`.

## 4. Beaning

Bean conversions are added now, however, to make generated typing information comfort with VSCode's TS language server
while applying beaning as much as possible, a few rules are added:

1. Beans will not have naming conflicts with methods/fields existed, if a bean has a conflicted name, the bean will not be compiled to declaration.
2. For beans with `.getX()` implemented, `get x()` will be added. 
   For beans with `.setX(arg1)` implemented, `set x(arg1)` will be added. `setX` must accepts exactly one arg, otherwise such method will not be beaned. 
3. If `.isX()` has a `.setX()` or `.getX()` part with same name, the latter will override `.isX()` to prevent loss of information.
4. Original getter and setter methods are NOT hidden by default, but this behaviour can be toggle on  via command `/probejs config toggle_bean`.
