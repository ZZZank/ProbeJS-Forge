package zzzank.probejs.docs.assignments;

import lombok.val;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.features.kubejs.SpecialData;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.snippet.Snippet;
import zzzank.probejs.lang.snippet.SnippetDump;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.FieldDecl;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.ts.Wrapped;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;
import zzzank.probejs.utils.NameUtils;
import zzzank.probejs.utils.registry.RegistryInfo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Assign types to all the registry types
 */
public class RegistryTypes extends ProbeJSPlugin {
    public static final String LITERAL_FIELD = "probejsInternal$$Literal";
    public static final String TAG_FIELD = "probejsInternal$$Tag";
    public static final String OF_TYPE_DECL = "T extends { %s: infer U } ? U : never";

    @Override
    public void assignType(ScriptDump scriptDump) {
        List<BaseType> registryNames = new ArrayList<>();
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return;
        }

        for (val info : SpecialData.instance().registries()) {
            val key = info.resKey();
            val typeName = NameUtils.rlToTitle(key.location().getPath());
            scriptDump.assignType(
                info.forgeRaw().getRegistrySuperType(),
                Types.primitive(String.format("Special.%s", typeName))
            );
            registryNames.add(Types.literal(key.location().toString()));
        }

        // ResourceKey<T> to Special.LiteralOf<T>
        scriptDump.assignType(
            ResourceKey.class,
            Types.parameterized(Types.primitive("Special.LiteralOf"), Types.generic("T"))
        );
        //Registries (why?)
        scriptDump.assignType(Registry.class, Types.or(registryNames.toArray(new BaseType[0])));
        assignRegistryType(scriptDump, ResourceKey.class, "Special.LiteralOf", "T");
        //TagKey<T> to Special.TagOf<T>
//        scriptDump.assignType(Tag.class, Types.parameterized(Types.primitive("Special.TagOf"), Types.generic("T")));
        assignRegistryType(scriptDump, Tag.class, "Special.TagOf", "T");

    }

    private static void assignRegistryType(ScriptDump scriptDump, Class<?> type, String literalType, String symbol) {
        scriptDump.assignType(type, Types.parameterized(Types.primitive(literalType), Types.generic(symbol)));
        scriptDump.assignType(type,
            Types.ignoreContext(
                Types.parameterized(Types.type(type), Types.generic(symbol)),
                BaseType.FormatType.RETURN
            )
        );
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return;
        }
        val special = new Wrapped.Namespace("Special");
        val enabled = ProbeJS.CONFIG.complete.get();

        for (val info : SpecialData.instance().registries()) {
            createTypes(special, info, enabled);
        }
//        createTypes(special, new RegistryInfo(Registry.REGISTRY), enabled);

        // Expose LiteralOf<T> and TagOf<T>
        val literalOf = new TypeDecl("LiteralOf<T>", Types.primitive(String.format(OF_TYPE_DECL, LITERAL_FIELD)));
        val tagOf = new TypeDecl("TagOf<T>", Types.primitive(String.format(OF_TYPE_DECL, TAG_FIELD)));
        special.addCode(literalOf);
        special.addCode(tagOf);

        scriptDump.addGlobal("registry_type", special);
    }

    private static void createTypes(
        Wrapped.Namespace special,
        RegistryInfo info,
        boolean enabled
    ) {
        val key = info.resKey();

        val types = enabled
            ? Types.or(info.names().stream().map(Types::literal).toArray(BaseType[]::new))
            : Types.STRING;
        val typeName = NameUtils.rlToTitle(key.location().getPath());

        val typeDecl = new TypeDecl(typeName, types);
        special.addCode(typeDecl);

        val tagNames = info.tagHelper() == null
            ? new BaseType[0]
            : info.tagHelper()
                .getAllTags()
                .getAvailableTags()
                .stream()
                .map(ResourceLocation::toString)
                .map(Types::literal)
                .toArray(BaseType[]::new);

        BaseType tagTypes = enabled ? Types.or(tagNames) : Types.STRING;
        String tagName = typeName + "Tag";

        TypeDecl tagDecl = new TypeDecl(tagName, tagTypes);
        special.addCode(tagDecl);
    }

    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return;
        }

        // We inject literal and tag into registry types
        for (val info : SpecialData.instance().registries()) {
            val key = info.resKey();
            makeClassModifications(globalClasses, key, info.forgeRaw().getRegistrySuperType());
        }
        makeClassModifications(globalClasses, Registry.REGISTRY.key(), Registry.class);
        makeClassModifications(globalClasses, Registry.DIMENSION_REGISTRY, Level.class);
    }

    private static void makeClassModifications(Map<ClassPath, TypeScriptFile> globalClasses, ResourceKey<? extends Registry<?>> key, Class<?> baseClass) {
        TypeScriptFile typeScriptFile = globalClasses.get(new ClassPath(baseClass));
        if (typeScriptFile == null) return;
        ClassDecl classDecl = typeScriptFile.findCode(ClassDecl.class).orElse(null);
        if (classDecl == null) return;

        String typeName = NameUtils.rlToTitle(key.location().getPath());
        String tagName = typeName + "Tag";

        var literalField = new FieldDecl(LITERAL_FIELD, Types.primitive(String.format("Special.%s", typeName)));
        literalField.addComment("This field is a type stub generated by ProbeJS and shall not be used in any sense.");
        classDecl.bodyCode.add(literalField);
        var tagField = new FieldDecl(TAG_FIELD, Types.primitive(String.format("Special.%s", tagName)));
        tagField.addComment("This field is a type stub generated by ProbeJS and shall not be used in any sense.");
        classDecl.bodyCode.add(tagField);
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        Set<Class<?>> registryObjectClasses = new HashSet<>();
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return registryObjectClasses;
        }

        for (val info : SpecialData.instance().registries()) {
            val registry = info.raw();
            if (registry == null) {
                continue;
            }
            for (val o : registry) {
                registryObjectClasses.add(o.getClass());
            }
            registryObjectClasses.add(info.forgeRaw().getRegistrySuperType());
        }
        return registryObjectClasses;
    }

    @Override
    public void addVSCodeSnippets(SnippetDump dump) {
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return;
        }

        for (val info : SpecialData.instance().registries()) {
            val registry = info.raw();
            val key = info.resKey();
            if (registry == null) {
                continue;
            }

            List<String> entries = registry.keySet()
                .stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
            if (entries.isEmpty()) {
                continue;
            }

            String registryName = key.location().getNamespace().equals("minecraft") ?
                key.location().getPath() :
                key.location().toString();

            Snippet registrySnippet = dump.snippet("probejs$$" + key.location());
            registrySnippet.prefix(String.format("@%s", registryName))
                .description(String.format("All available items in the registry \"%s\"", key.location()))
                .literal("\"")
                .choices(entries)
                .literal("\"");

            List<String> tags = info.tagHelper() == null ?
                Collections.emptyList() :
                info.tagHelper()
                    .getAllTags()
                    .getAvailableTags()
                    .stream()
                    .map(ResourceLocation::toString)
                    .map("#"::concat)
                    .collect(Collectors.toList());
            if (tags.isEmpty()) {
                continue;
            }

            Snippet tagSnippet = dump.snippet("probejs_tag$$" + key.location());
            tagSnippet.prefix(String.format("@%s_tag", registryName))
                .description(String.format("All available tags in the registry \"%s\", no # is added", key.location()))
                .literal("\"")
                .choices(tags)
                .literal("\"");
        }
    }
}
