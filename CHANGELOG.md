# ProbeJS Legacy 2.4.1 -> 2.5.0

RecipeFilter_ & FunctionalInterfaces

## What's new?

-   Functional Interfaces(Interfaces that accept Lambda as their instances) can now also display their original type, thus accepting document
    -  e.g. `event.replaceInput(filter, toReplace, replaceWith)` in RecipeEvent, where `filter` used to be a only lambda function, but now accepts Lambda, original type, and objects like `{mod: "minecraft", type: "minecraft:blasting"}`
-   Detailed doc for `RecipeFilter`, `ItemStackJS`, `IngredientJS`, and much more
-   Event doc will now have a generalized variant displayed, to handle events that are not exported by ProbeJS yet

---

# ProbeJS Legacy 2.4.0 -> 2.4.1

Fix type casting error

## What's new?

-   fix error caused by type casting in formatScriptable()
-   doc for `AttachedData` and `CompoundNBT`. It can now act like a regular JS object(which means accessing members in formats like `data.some_member` or `data["some_member"]` will no longer confuse your IDE)

---

# ProbeJS Legacy 2.3.1 -> 2.4.0

Event dump ++

## What's new?

-   events dumped in `events.d.ts` are now naturally sorted(in alphabet order).
-   dumped events will have more info displayed, e.g. if such event is cancellabe or not.
    ```js
    /**
     * @cancellable No
     * @at startup, server
     */
    declare function onEvent(name: "item.crafted", handler: (event: Internal.ItemCraftedEventJS) => void);
    ```
-   rawTS doc will now have no namespace wrapped. Instead, there will be two comments marking start and end.
-   fix Raw TS doc being cleared even before they are used for doc gen, so Raw TS doc can actually get generated.
-   fix tag snippets not writing into files.
-   enable special formatter for rhino::Scriptable.
-   recipe doc for Thermal Series.
-   constructors for classes in `Internal` namespaces will now be showed, with comments clarifying that you needs `java()` to actually use it.
-   remove `haunting` in Create doc, since it's not a thing in MC1.16.

---

# ProbeJS Legacy 2.3.0 -> 2.3.1

Fix crash caused by ConcurrentModificationException

## What's new?

-   use `containsKey` + `put` instead of `computeIfAbsent` to avoid CME crash.
-   fix events with sub-id not actually having sub-id.

---

# ProbeJS Legacy 2.2.0 -> 2.3.0

Better document system

NOTE: Due to changes in event cache for sub-id support, previous cache will be invalid for this version.

## What's new?

-   Fix param fetching of documents.
    -   This is a bug that has existed since 2.0.0, because the original document system from ProbeJS for MC 1.18 is already problematic.
    -   Try `event.shaped()` or `event.shapeless()` in recipe event, its type hint should be normal now.
-   ProbeJS Legacy can now catch every fired KubeJS event, without the needs of `onEvent`.
-   ProbeJS Legacy can catch events with sub id, e.g. those from FTB Quest.
    -   Because of
-   Hand-written recipe doc and auto-gen recipe doc will now be combined together, providing more accurate parameter info.
-   Documents for class now support `extends` .
-   RawTS doc will now has no namespace wrapped.
-   ProbeJS Legacy can now properly get the index of outter bracket in method documents.
-   Better documents for builtin types, like `CompoundNBT` .
-   Several performance tweaks to improve performance, especially on heavily modded instances.

---

# ProbeJS Legacy 2.1.0 -> 2.2.0

Dump trimming!

## What's new?

-   Dump trimming: probeJS will now make use of inherited Class, and avoid dumping a method/field if such method/field is already avaliable through inheritance. This can greatly reduce the size of `global.d.ts`
    -   On a slightly modded instance, dump trimming makes dump size decrease from 8.02MB to 2.55MB, less than 1/3 of the original dump size.
-   Better documents for builtin types, like `Text` or `Map`
-   Java type `Object` will now be showed, but in namespace `Document`, and every "complex" type will be it's subclass(for better dump trimming)

---

# ProbeJS Legacy 2.0.0 -> 2.0.1

## What's new?

-   ProbeJS will now listen to EVERY Forge events, so that users don't need to painstakingly search for names of Forge Events (which are usually very long).
-   `/probejs dump` command is now restricted to Singleplayer, to prevent freezing servers by accident.
-   A slightly better documents for builtin types, like `ResourceLocation`

---

# ProbeJS Legacy 1.6.0 -> 2.0.0

A huge update, adding support for Document, Forge event listening, full config with autosave, and much much more

## What's new?

-   Collected class/methods/... will now be dumped to `kubejs/probe/` folder, instead of `kubejs/kubetypings/` folder.
-   ProbeJS can now dump more classes/methods into `global.d.ts`
    -   On a slightly modded 1.16.5 Forge instance, ProbeJS can now dump about 164000 lines, 4.4 times of previously 37700 lines.
-   ProbeJS can now dump java access related data better.
    -   On a slightly modded 1.16.5 Forge instance, ProbeJS can now recognize and dump about 2000 method presets, 33% more than previously 1500.
-   An actual config file will be generated at `kubejs/config/probe.json`, and will be automatically saved on change(through command).
-   Event dumping now supports `onForgeEvent()`
-   Correctly resolves types used by List/Map
-   Types will have `_` appended to prevent conflict
-   better support for RecipeSerializer
-   Remove `dump.js` generation, because it seems completely useless
-   Suopprt getters
-   and many small changes, for a better experience

---

# ProbeJS Legacy 1.?.? -> 1.6.0

The first release of ProbeJS 1.16.5 unofficial continuation.

## What's new?

-   ProbeJS now mixins into event listening, so you no longer need captureEvent to capture fired events.
-   Fix command arguments. /probejs previously will do what /probejs dump does, that's fixed now.
-   Add semi-comma in dump.json to handle malformed tag key, like per-viam-invenire:replace_vanilla_navigator, whose `-` will causes troubles
-   Respect kubeJS annotations.
    -   Ported from https://github.com/Prunoideae/ProbeJS-Forge/commit/549118a44d07b5e7a10f0ff71ac8ce5338a7066e
-   You can use `/probejs config xxxx` to access configs. Currently you can use `/probejs config dump_export` to toggle dump.json generating on or off
