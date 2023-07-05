package com.probejs.recipe.component;

import com.probejs.jdoc.property.PropertyType;
import dev.latvian.mods.kubejs.typings.desc.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ComponentConverter {
    public static PropertyType<?> fromDescription(TypeDescJS description) {
        if (description instanceof ArrayDescJS array) {
            return new PropertyType.Array(fromDescription(array.type()));
        } else if (description instanceof FixedArrayDescJS fixedArray) {
            return new PropertyType.JSArray(Arrays.stream(fixedArray.types()).map(ComponentConverter::fromDescription).collect(Collectors.toList()));
        } else if (description instanceof GenericDescJS parameterized) {
            if (parameterized.type() instanceof PrimitiveDescJS primitive && primitive.type().equals("Map") && fromDescription(parameterized.types()[0]) instanceof PropertyType.Native keyType) {
                // Map is not an object in JS, so we need to use a special type `{[key: keyType]: valueType}`
                // In case of Special.XXXX or whatever, you can still have good mappings
                return new PropertyType.JSObject()
                        .add(new PropertyType.JSObjectKey().withType(keyType),
                                fromDescription(parameterized.types()[1])
                        );
            }
            return new PropertyType.Parameterized(
                    fromDescription(parameterized.type()),
                    Arrays.stream(parameterized.types())
                            .map(ComponentConverter::fromDescription)
                            .collect(Collectors.toList())
            );
        } else if (description instanceof ObjectDescJS object) {
            return new PropertyType.JSObject(
                    object.types().stream()
                            .map(pair -> Pair.of(
                                    new PropertyType.JSObjectKey().withName(pair.getLeft()),
                                    fromDescription(pair.getRight())
                            ))
                            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight))
            );
        } else if (description instanceof OrDescJS or) {
            return new PropertyType.Union(
                    Arrays.stream(or.types())
                            .map(ComponentConverter::fromDescription)
                            .collect(Collectors.toList())
            );
        } else if (description instanceof PrimitiveDescJS primitive) {
            String name = primitive.type();
            if (name.equals("null")) // return any here because we don't know what to do with null
                return new PropertyType.Clazz(Object.class);
            if (name.startsWith("$probejs$")) {
                name = name.substring(9);
                return new PropertyType.Clazz(name);
            } else {
                return new PropertyType.Native(name);
            }
        } else {
            return new PropertyType.Clazz(Object.class);
        }
    }

    // Prefixes the class name with "$probejs$" to avoid conflicts with other types
    public static DescriptionContext PROBEJS_CONTEXT = new DescriptionContext() {
        @Override
        public String typeName(Class<?> type) {
            return "$probejs$%s".formatted(type.getName());
        }
    };
}
