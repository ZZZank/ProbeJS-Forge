# ProbeJS Legacy 3.1.0 -> 3.2.0

Rhizo(not typo) support

## What's new?

-   Rhizo(not typo) support
  - ProbeJS Legacy now supports Rhizo, which adds method/field remapper for 1.16.5
  - With Rhizo, you can access methods/fields using readable MCP names like `getOpPermissionLevel()` instead of SRG names like `func_110455_j()`
  - ProbeJS Legacy specially supports Rhizo's remapper, and can dump mapped MCP name for typing.
  - If you're not using Rhizo, ProbeJS Legacy will automatically skip remapper accessing, so old Rhino is still compatible. 
- ProbeJS Legacy will now walk type parameters of superclass/interfaces more completely. 

---

# ProbeJS Legacy 3.0.1 -> 3.1.0

better type recognizing

## What's new?

-   lambda style type alias for Functional Interface
-   recognize any interfaces with one and only one abstract method as Functional Interface
-   fix tag snippet compiling
-   improve error message, making it red, with links to Github issue page
-   OnJavaMixin
    -   result of calling `java()` will now be touched by ProbeJS, so that next time dumping is triggered, it can be dumped.
-   "implements" keyword support for class documents
-   better line recognizing for documents
-   special assignment of MaterialJS
    -   e.g. `type MaterialJS_ = "sponge" | "explosive" | "wool" | "ice" | "air" | "vegetable" | "clay" | "slime" | "rock" | "lava" | "leaves" | "web" | "grass" | "coral" | "cake" | "honey" | "plants" | "wood" | "dragon_egg" | "portal" | "organic" | "gourd" | "glass" | "metal" | "dirt" | "water" | "lantern" | "stone" | "sand" | "snow" | "plant" | "earth" | "iron" | "tnt" | "berry_bush" | MaterialJS;`
-   touch contents of pre-defined constants
    -   e.g. `global.jeiRuntime`
-   var arg support for method params
-   class touching for generics in superclass/interfaces
    -   type hint when using PonderJS should be better now
-   (try to) add underscore to documented type when possible
-   generics in superclass/interfaces will now be formatted
-   make method/constructor param typed

---

# ProbeJS Legacy 3.0.0 -> 3.0.1

Fix `Internal.ItemStackJS_`

## What's new?

-   fix class assignment for ItemStackJS.
    -   use MCP class name instead of Official mapping class name.
    -   i hate inconsistent mapping, so much trouble.
-   entries in `java.d.ts` are now naturally sorted.
-   js config is tweaked to avoid triggering probe typing when not in `kubejs/{...}_script` folder.

---

# ProbeJS Legacy 2.6.1 -> 3.0.0

Rich display info dumping

## What's new?

-   ProbeJS Legacy will now dump rich display informations about items, fluids, blocks, tags, and lang keys, providing rich display in VSCode when ProbeJS(VSCode Extension) is installed.
    -   for items, it can display:
        -   item id,
        -   localized name(or lang key if no localization is avaliable),
        -   whether it's block or item or crop, max stack size, and
        -   special infomations for tools and foods and etc.
        -   If you have icons exported via [Icon Exporter](https://www.curseforge.com/minecraft/mc-mods/iconexporter), icons for such item can also be displayed.
    -   for fluids, it can display
        -   id,
        -   localized name(or lang key if no localization is avaliable),
        -   whether it has a fluid block, and
        -   whether it has a fluid bucket item.
    -   for lang kays, it can display:
        -   all valid localization under similar locale, and `en_us`
    -   it's really hard to name them all, please try it by yourself.
-   config "disabled" is now replaced by "enabled", to make ProbeJS VSCode extension happy.
    -   Old config will be automatically upgraded.
-   `.vscode` now will be generated at `.minecraft/` folder, aka game folder, making workspace more useful.
-   Snippets of registries now support all avaliable registry types, e.g. potion and enchantment
-   ProbeJS now fetch registries and tags from Minecraft internals, instead of relying on an external json file.
    -   This change prevents reloading and exporting data when `/probejs dump` is triggered, which makes dumping much much FASTER, especially on a large modded instance.
    -   Also, `autoExport` config is removed because of this.
-   Use different method filtering approaches for classes and interfaces, so we should be able to filter out duplicated methods more completely.
-   Set default values for configs, so that first-time users will not get a all-disabled config
-   ProbeJS Legacy now dump registries into `special.d.ts` instead of `registries.d.ts`
-   More datas, including tags and platform data, will be dumped into `special.d.ts`
-   dumping messages are improved now, roughly showing stages of dumping.
-   class `DamageSource` will now show accepted assginments in its type. E.g. `type DamageSource_ = "inFire"|"lightningBolt"|"onFire"|"lava"|"hotFloor"|"inWall"|"cramming"|"drown"|"starve"|"cactus"|"fall"|"flyIntoWall"|"outOfWorld"|"generic"|"magic"|"wither"|"anvil"|"fallingBlock"|"dragonBreath"|"dryout"|"sweetBerryBush"|DamageSource;`

---

# ProbeJS Legacy 2.6.0 -> 2.6.1

Fix "optional" class dumping crash

## What's new?

-   ProbeJS will now catch error thrown when classes to be looked into is missing.
    -   This means that "Optional" classes, like classes intended for cross-mod compatibility, should be able to be dumped without crashing the whole game
    -   This bug actually exists ever since the very first release of ProbeJS Legacy, so updating to this version is highly recommended.

---

# ProbeJS Legacy 2.5.0 -> 2.6.0

Registry Dumping

## What's new?

-   Registry Dumping!
    -   Registries, whether from vanilla(e.g. items, blocks) or mods(e.g. Mekanism Slurries), can now be dumped into `registries.d.ts`.
    -   Registries will be resolved into types, showing every avaliable names under such registry. e.g. `type schedule = "minecraft:empty"|"minecraft:simple"|"minecraft:villager_baby"|"minecraft:villager_default";`
    -   Types that are registry entries will now take cooresponding registry as its assignables, like `type Attribute_ = Registry.minecraft.attribute | Attribute;`
    -   This means in some cases, like `item.enchant(..., 1)`, using string in `...` will no longer confuses your IDE, if you enable type checks.
-   Minor performance tweaks to allow event listening mixin runs a little bit faster. After this change, changing config `disabled` will actually require a game restart to take effects
-   move generalized representation of `onEvent` and `onForgeEvent` lower to prefer sepcialized ones

---

# ProbeJS Legacy 2.4.1 -> 2.5.0

RecipeFilter\_ & FunctionalInterfaces

## What's new?

-   Functional Interfaces(Interfaces that accept Lambda as their instances) can now also display their original type, thus accepting document
    -   e.g. `event.replaceInput(filter, toReplace, replaceWith)` in RecipeEvent, where `filter` used to be a only lambda function, but now accepts Lambda, original type, and objects like `{mod: "minecraft", type: "minecraft:blasting"}`
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
    ```javascript
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
