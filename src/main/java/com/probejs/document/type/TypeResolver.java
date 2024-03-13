package com.probejs.document.type;

import com.probejs.info.type.*;
import com.probejs.util.Pair;
import com.probejs.util.StringUtil;
import java.util.List;
import java.util.stream.Collectors;

public class TypeResolver {

    public static IType resolve(String type) {
        type = type.trim();

        //TODO: Resolve object type
        if (type.startsWith("{")) {
            // {[x in string]: string}
            return new TypeNamed(type);
        }

        Pair<String, String> splitUnion = StringUtil.splitFirst(type, "<", ">", "|");
        if (splitUnion != null) {
            return new TypeUnion(resolve(splitUnion.getFirst()), resolve(splitUnion.getSecond()));
        }

        Pair<String, String> splitIntersection = StringUtil.splitFirst(type, "<", ">", "&");
        if (splitIntersection != null) {
            return new TypeIntersection(
                resolve(splitIntersection.getFirst()),
                resolve(splitIntersection.getSecond())
            );
        }

        if (type.endsWith("[]")) {
            return new TypeArray(resolve(type.substring(0, type.length() - 2)));
        }

        if (type.endsWith(">")) {
            int indexLeft = type.indexOf("<");
            String rawType = type.substring(0, indexLeft);
            String typeParams = type.substring(indexLeft + 1, type.length() - 1);
            List<String> params = StringUtil.splitLayer(typeParams, "<", ">", ",");
            return new TypeParameterized(
                resolve(rawType),
                params.stream().map(TypeResolver::resolve).collect(Collectors.toList())
            );
        }
        return new TypeNamed(type);
    }

    public static boolean typeEquals(IType docType, ITypeInfo param) {
        if (docType instanceof TypeUnion || docType instanceof TypeIntersection) {
            return false;
        }
        if (docType instanceof TypeArray && param instanceof TypeInfoArray) {
            TypeInfoArray array = (TypeInfoArray) docType;
            return typeEquals(((TypeArray) docType).getComponent(), array.getBaseType());
        }
        if (docType instanceof TypeParameterized && param instanceof TypeInfoParameterized) {
            TypeInfoParameterized parameterized = (TypeInfoParameterized) param;
            List<ITypeInfo> paramInfo = parameterized.getParamTypes();
            List<IType> paramDoc = ((TypeParameterized) docType).getParamTypes();
            if (paramDoc.size() != paramInfo.size()) {
                return false;
            }
            for (int i = 0; i < paramDoc.size(); i++) {
                if (!typeEquals(paramDoc.get(i), paramInfo.get(i))) return false;
            }
            return typeEquals(((TypeParameterized) docType).getRawType(), parameterized.getBaseType());
        }
        if (
            docType instanceof TypeNamed &&
            (param instanceof TypeInfoVariable || param instanceof TypeInfoClass)
        ) {
            return ((TypeNamed) docType).getRawTypeName().equals(param.getTypeName());
        }

        return false;
    }
}
