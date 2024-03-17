package com.probejs.compiler;

import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRaw;
import com.probejs.formatter.formatter.IFormatter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

//TODO: import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes
public class RegistryCompiler {

    public static class RegistryInfo {

        private final Registry<?> raw;
        private final ResourceKey<? extends Registry<?>> resKey;
        private final ResourceLocation parentId;
        private final ResourceLocation id;
        private final Set<ResourceLocation> names;

        public RegistryInfo(Registry<?> registry) {
            this.raw = registry;
            this.resKey = raw.key();
            this.parentId = resKey.getRegistryName();
            this.id = resKey.location();
            this.names = raw.keySet();
        }

        public Registry<?> raw() {
            return this.raw;
        }

        public ResourceKey<? extends Registry<?>> resKey() {
            return this.resKey;
        }

        public ResourceLocation parentId() {
            return this.parentId;
        }

        public ResourceLocation id() {
            return this.id;
        }

        public Set<ResourceLocation> names() {
            return this.names;
        }
    }

    public static List<RegistryInfo> getAll() {
        return Registry.REGISTRY.stream().map(RegistryInfo::new).collect(Collectors.toList());
    }

    /*
    public static Set<Class<?>> getRegistryClasses() {
        Set<Class<?>> result = new HashSet<>();
        result.add(RegistryObjectBuilderTypes.class);
        result.add(RegistryObjectBuilderTypes.RegistryEventJS.class);
        RegistryObjectBuilderTypes.MAP
            .values()
            .forEach(v -> v.types.values().forEach(v1 -> result.add(v1.builderClass())));
        return result;
    }

    public static void compileEventRegistries(BufferedWriter writer) throws IOException {
        Gson stringG = new Gson();
        for (RegistryObjectBuilderTypes<?> types : RegistryObjectBuilderTypes.MAP.values()) {
            String fullName =
                types.registryKey.location().getNamespace() +
                "." +
                types.registryKey.location().getPath().replace('/', '.') +
                ".registry";
            String registryName = FormatterRegistry.getFormattedRegistryName(types);
            writer.write(
                "declare function onEvent(name: %s, handler: (event: Registry.%s) => void);\n".formatted(
                        stringG.toJson(fullName),
                        registryName
                    )
            );
            if (types.registryKey.location().getNamespace().equals("minecraft")) {
                String shortName = types.registryKey.location().getPath().replace('/', '.') + ".registry";
                writer.write(
                    "declare function onEvent(name: %s, handler: (event: Registry.%s) => void);\n".formatted(
                            stringG.toJson(shortName),
                            registryName
                        )
                );
            }
        }
    }
    */

    public static List<IFormatter> info2Formatters(Collection<RegistryInfo> infos) {
        Map<String, List<RegistryInfo>> infoByMods = new HashMap<>();
        for (RegistryInfo info : infos) {
            infoByMods.computeIfAbsent(info.id.getNamespace(), k -> new ArrayList<>()).add(info);
        }
        List<IFormatter> formatters = new ArrayList<>();
        infoByMods.forEach((namespace, rInfos) -> {
            List<IFormatter> formattersInside = rInfos
                .stream()
                .map(rInfo ->
                    String.format(
                        "type %s = %s;",
                        rInfo.id.getPath().replace('/', '$'),
                        rInfo
                            .names()
                            .stream()
                            .map(rl -> ProbeJS.GSON.toJson(rl.toString()))
                            .collect(Collectors.joining("|"))
                    )
                )
                .map(str -> {
                    FormatterRaw rawFmtr = new FormatterRaw(Arrays.asList(str));
                    rawFmtr.setCommentMark(false);
                    return rawFmtr;
                })
                .collect(Collectors.toList());
            formatters.add(new FormatterNamespace(namespace, formattersInside));
        });
        return formatters;
    }

    public static void compileRegistries() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(ProbePaths.GENERATED.resolve("registries.d.ts"));
        writer.write("/// <reference path=\"./globals.d.ts\" />\n");
        IFormatter namespace = new FormatterNamespace(
            "Registry",
            // RegistryObjectBuilderTypes.MAP
            //     .values()
            //     .stream()
            //     .map(FormatterRegistry::new)
            //     .collect(Collectors.toList())
            info2Formatters(getAll())
        );
        writer.write(String.join("\n", namespace.format(0, 4)));
        writer.flush();
        writer.close();
    }
    /*
    private static class FormatterRegistry implements IFormatter {

        RegistryObjectBuilderTypes<?> types;
        String name;

        private static String getFormattedRegistryName(RegistryObjectBuilderTypes<?> types) {
            return Arrays
                .stream(types.registryKey.location().getPath().split("/"))
                .map(str -> str.substring(0, 1).toUpperCase() + str.substring(1))
                .collect(Collectors.joining(""));
        }

        private FormatterRegistry(RegistryObjectBuilderTypes<?> types) {
            this.types = types;
            this.name = getFormattedRegistryName(types);
        }

        @Override
        public List<String> format(int indent, int stepIndent) {
            List<String> formatted = new ArrayList<>();
            int stepped = indent + stepIndent;
            Gson stringG = new Gson();
            formatted.add(
                PUtil.indent(indent) +
                "class %s extends %s {".formatted(
                        name,
                        FormatterClass.formatTypeParameterized(
                            new TypeInfoClass(RegistryObjectBuilderTypes.RegistryEventJS.class)
                        )
                    )
            );
            for (RegistryObjectBuilderTypes.BuilderType<?> builder : types.types.values()) {
                formatted.add(
                    PUtil.indent(stepped) +
                    "create(id: string, type: %s): %s;".formatted(
                            stringG.toJson(builder.type()),
                            FormatterClass.formatTypeParameterized(new TypeInfoClass(builder.builderClass()))
                        )
                );
            }
            if (types.getDefaultType() != null) {
                formatted.add(
                    PUtil.indent(stepped) +
                    "create(id: string): %s;".formatted(
                            FormatterClass.formatTypeParameterized(
                                new TypeInfoClass(types.getDefaultType().builderClass())
                            )
                        )
                );
            }
            formatted.add(PUtil.indent(indent) + "}");
            return formatted;
        }
    }
     */
}
