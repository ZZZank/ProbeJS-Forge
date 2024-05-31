package com.probejs.formatter.resolver;

import com.probejs.document.DocManager;
import com.probejs.document.type.DocTypeRaw;
import com.probejs.formatter.FormatterClass;
import com.probejs.formatter.FormatterType;
import com.probejs.info.SpecialData;
import com.probejs.info.type.IType;
import com.probejs.info.type.TypeClass;
import com.probejs.info.type.TypeParameterized;
import dev.latvian.mods.rhino.*;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpecialTypes {

    public static final Set<Class<?>> skippedSpecials = new HashSet<>();

    public static String formatClassLike(IType obj) {
        IType inner = null;
        if (obj instanceof TypeParameterized cls) {
            inner = cls.getParamTypes().get(0);
        } else if (obj instanceof TypeClass cls) {
            inner = cls;
        }
        if (inner == null) {
            return "any";
        }
        return String.format("typeof %s", FormatterType.of(inner.getBase(), false).format());
    }

    private static String formatValueOrType(Object obj) {
        String formattedValue = NameResolver.formatValue(obj);
        if (formattedValue == null) {
            if (
                !NameResolver.resolvedNames.containsKey(obj.getClass().getName()) &&
                !obj.getClass().getName().contains("$Lambda")
            ) {
                NameResolver.resolveName(obj.getClass());
            }
            formattedValue = FormatterClass.formatParameterized(new TypeClass(obj.getClass()));
        }
        return formattedValue;
    }

    public static String formatMap(Object obj) {
        if (!(obj instanceof Map<?, ?> map)) {
            return "{}";
        }
        List<String> values = new ArrayList<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            String formattedKey = NameResolver.formatValue(key);
            if (formattedKey == null) {
                continue;
            }
            String formattedValue = formatValueOrType(value);
            values.add(String.format("%s:%s", formattedKey, formattedValue));
        }
        return String.format("{%s}", String.join(",", values));
    }

    public static String formatList(Object obj) {
        if (!(obj instanceof List<?> list)) {
            return "[]";
        }
        List<String> values = new ArrayList<>();
        for (Object o : list) {
            String formattedValue = NameResolver.formatValue(o);
            if (formattedValue == null) formattedValue = "undefined";
            values.add(formattedValue);
        }
        return String.format("[%s]", String.join(", ", values));
    }

    public static String formatScriptable(Object obj) {
        if (!(obj instanceof ScriptableObject scriptable)) {
            // if not Scriptable, why call this
            return "{}";
        }
        List<String> values = new ArrayList<>();

        Scriptable prototype = scriptable.getPrototype();
        if (prototype.get("constructor", prototype) instanceof BaseFunction func) {
            //Resolves Object since they're not typed
            if (!func.getFunctionName().isEmpty() && !func.getFunctionName().equals("Object")) {
                return func.getFunctionName();
            }
        }

        for (Object id : scriptable.getIds()) {
            String formattedKey = NameResolver.formatValue(id);
            Object value;
            if (id instanceof Number) {
                value = scriptable.get((Integer) id, scriptable);
            } else {
                value = scriptable.get((String) id, scriptable);
            }
            String formattedValue = formatValueOrType(value);
            values.add(String.format("%s:%s", formattedKey, formattedValue));
        }

        Scriptable proto = scriptable.getPrototype();
        for (Object id : proto.getIds()) {
            String formattedKey = NameResolver.formatValue(id);
            Object value;
            if (id instanceof Number) {
                value = proto.get((Integer) id, scriptable);
            } else {
                value = proto.get((String) id, scriptable);
            }
            String formattedValue = formatValueOrType(value);
            values.add(String.format("%s:%s", formattedKey, formattedValue));
        }
        return String.format("{%s}", String.join(",", values));
    }

    public static String formatFunction(Object obj) {
        if (!(obj instanceof BaseFunction function)) {
            return null;
        }
        return String.format(
            "(%s) => any",
            IntStream
                .range(0, function.getLength())
                .mapToObj(i -> "arg" + i)
                .collect(Collectors.joining(", "))
        );
    }

    public static String formatNJO(Object obj) {
        if (!(obj instanceof NativeJavaObject njo)) {
            return null;
        }
        return formatValueOrType(njo.unwrap());
    }

    public static void processSpecialAssignments() {
        //specialClassAssigner
        NameResolver.specialClassAssigner.forEach((clazzName, assignProvider) -> {
            String name = clazzName.getName();
            for (String assignTo : assignProvider.get()) {
                DocManager.addAssignable(name, new DocTypeRaw(assignTo));
            }
        });
        //registry
        SpecialData.instance()
            .registries()
            .stream()
            .filter(info -> !info.names.isEmpty())
            .forEach(info -> {
                Class<?> registrySuperType = info.forgeRaw.getRegistrySuperType();
                String name = String.format(
                    "Registry.%s.%s",
                    info.id.getNamespace(),
                    info.id.getPath().replace('/', '$')
                );
                DocManager.addAssignable(registrySuperType.getName(), new DocTypeRaw(name));
            });
    }

    public static String attachedTypeVar(TypeClass type) {
        val typeVariables = type.getTypeVariables();
        if (typeVariables.isEmpty()) {
            return "";
        }
        return typeVariables
            .stream()
            .map(IType::getTypeName)
            .map(NameResolver::getResolvedName)
            .map(NameResolver.ResolvedName::getFullName)
            .collect(Collectors.joining(","));
    }
}
