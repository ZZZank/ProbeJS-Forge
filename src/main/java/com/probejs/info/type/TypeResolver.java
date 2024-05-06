package com.probejs.info.type;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class TypeResolver {

    public static IType resolveType(Type type) {
        if (type == null) {
            return null;
        }
        if (TypeArray.test(type)) return new TypeArray(type);
        if (TypeVariable.test(type)) return new TypeVariable(type);
        if (TypeWildcard.test(type)) return new TypeWildcard(type);
        if (TypeParameterized.test(type)) return new TypeParameterized(type);
        if (TypeClass.test(type)) return new TypeClass(type);
        return null;
    }

    /**
     * Returns a new modified typeInfo basing on the Map<String, IDocType>
     * If the typeInfo is immutable, a new TypeInfo will be returned.
     */
    public static IType mutateTypeMap(IType typeInfo, Map<String, IType> toMutate) {
        if (typeInfo instanceof TypeClass || typeInfo instanceof TypeVariable) {
            return toMutate.getOrDefault(typeInfo.getTypeName(), typeInfo).copy();
        }

        typeInfo = typeInfo.copy();

        if (typeInfo instanceof TypeWildcard) {
            TypeWildcard wild = (TypeWildcard) typeInfo;
            return mutateTypeMap(wild.getBaseType(), toMutate);
        }

        if (typeInfo instanceof TypeArray) {
            TypeArray array = (TypeArray) typeInfo;
            array.setBase(mutateTypeMap(array.getBaseType(), toMutate));
        }

        if (typeInfo instanceof TypeParameterized) {
            TypeParameterized parType = (TypeParameterized) typeInfo;
            parType.setRawType(mutateTypeMap(parType.getBaseType(), toMutate));
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

    public static IType getContainedTypeOrSelf(IType typeInfo) {
        if (typeInfo instanceof TypeParameterized) {
            TypeParameterized paramType = (TypeParameterized) typeInfo;
            IType baseType = paramType.getBaseType();
            if (
                baseType.assignableFrom(resolveType(Collection.class)) && !paramType.getParamTypes().isEmpty()
            ) {
                return paramType.getParamTypes().get(0);
            }
            if (baseType.assignableFrom(resolveType(Map.class)) && paramType.getParamTypes().size() > 1) {
                return paramType.getParamTypes().get(1);
            }
        }
        return typeInfo;
    }
}
