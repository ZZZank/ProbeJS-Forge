# ProbeJS Legacy 1.6.0 -> 2.0.0

A huge update, adding support for Document, Forge event listening, full config with autosave, and much much more

## What's new?

-   Collected class/methods/... will now be dumped to `kubejs/probe/` folder, instead of `kubejs/kubetypings/` folder.
-   ProbeJS can now dump more classes/methods into `global.d.ts`
    - On a slihtly modded 1.16.5 Forge instance, ProbeJS can now dump about 164000 lines, 4.4 times of previously 37700 lines.
-   ProbeJS can now dump java access related data better.
    -   On a slihtly modded 1.16.5 Forge instance, ProbeJS can now recognize and dump about 2000 method presets, 33% more than previously 1500.
-   An actual config file will be generated at `kubejs/config/probe.json`, and will be automatically saved on change(through command).
-   Correctly resolves types used by List/Map
-   Types will have `_` appended to prevent conflict
-   better support for RecipeSerializer
-   Remove `dump.js` generation, because it seems completely useless
-   Suopprt getters
-   and many small changes, for a better experience

# ProbeJS Legacy 1.?.? -> 1.6.0

The first release of ProbeJS 1.16.5 unofficial continuation.

## What's new?

-   ProbeJS now mixins into event listening, so you no longer need captureEvent to capture fired events.
-   Fix command arguments. /probejs previously will do what /probejs dump does, that's fixed now.
-   Add semi-comma in dump.json to handle malformed tag key, like per-viam-invenire:replace_vanilla_navigator, whose `-` will causes troubles
-   Respect kubeJS annotations.
    -   Ported from https://github.com/Prunoideae/ProbeJS-Forge/commit/549118a44d07b5e7a10f0ff71ac8ce5338a7066e
-   You can use `/probejs config xxxx` to access configs. Currently you can use `/probejs config dump_export` to toggle dump.json generating on or off
