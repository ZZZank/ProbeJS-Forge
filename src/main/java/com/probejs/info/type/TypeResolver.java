package com.probejs.info.type;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class TypeResolver {

    public static JavaType resolve(Type type) {
        if (type == null) {
            return null;
        }
        if (JavaTypeArray.test(type)) return new JavaTypeArray(type);
        if (JavaTypeVariable.test(type)) return new JavaTypeVariable(type);
        if (JavaTypeWildcard.test(type)) return new JavaTypeWildcard(type);
        if (JavaTypeParameterized.test(type)) return new JavaTypeParameterized(type);
        if (JavaTypeClass.test(type)) return new JavaTypeClass(type);
        return null;
    }

    /**
     * Returns a new modified typeInfo basing on the Map<String, IDocType>
     * If the typeInfo is immutable, a new TypeInfo will be returned.
     */
    public static JavaType mutateTypeMap(JavaType typeInfo, Map<String, JavaType> toMutate) {
        if (typeInfo instanceof JavaTypeClass || typeInfo instanceof JavaTypeVariable) {
            return toMutate.getOrDefault(typeInfo.getTypeName(), typeInfo).copy();
        }

        typeInfo = typeInfo.copy();

        if (typeInfo instanceof JavaTypeWildcard wild) {
            return mutateTypeMap(wild.getBase(), toMutate);
        }

        if (typeInfo instanceof JavaTypeArray array) {
            array.setBase(mutateTypeMap(array.getBase(), toMutate));
        }

        if (typeInfo instanceof JavaTypeParameterized parType) {
            parType.setRawType(mutateTypeMap(parType.getBase(), toMutate));
            parType.setParamTypes(
                parType
                    .getParamTypes()
                    .stream()
                    .map(info -> mutateTypeMap(info, toMutate))
                    .collect(Collectors.toList())
            );
        }

        return typeInfo;
    }

    public static JavaType getContainedTypeOrSelf(JavaType typeInfo) {
        if (typeInfo instanceof JavaTypeParameterized paramType) {
            JavaType baseType = paramType.getBase();
            if (
                baseType.assignableFrom(resolve(Collection.class)) && !paramType.getParamTypes().isEmpty()
            ) {
                return paramType.getParamTypes().get(0);
            }
            if (baseType.assignableFrom(resolve(Map.class)) && paramType.getParamTypes().size() > 1) {
                return paramType.getParamTypes().get(1);
            }
        }
        return typeInfo;
    }
}
