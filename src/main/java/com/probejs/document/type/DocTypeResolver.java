package com.probejs.document.type;

import com.probejs.info.type.*;
import com.probejs.util.Pair;
import com.probejs.util.StringUtil;
import java.util.List;
import java.util.stream.Collectors;

public class DocTypeResolver {

    public static IType resolve(String type) {
        type = type.trim();

        //TODO: Resolve object type
        if (type.startsWith("{")) {
            // {[x in string]: string}
            return new TypeRaw(type);
        }

        Pair<String, String> splitUnion = StringUtil.splitFirst(type, "<", ">", "|");
        if (splitUnion != null) {
            return new TypeUnion(resolve(splitUnion.first()), resolve(splitUnion.second()));
        }

        Pair<String, String> splitIntersection = StringUtil.splitFirst(type, "<", ">", "&");
        if (splitIntersection != null) {
            return new TypeIntersection(
                resolve(splitIntersection.first()),
                resolve(splitIntersection.second())
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
                params.stream().map(DocTypeResolver::resolve).collect(Collectors.toList())
            );
        }
        return new TypeNamed(type);
    }

    public static boolean typeEquals(IType docType, ITypeInfo param) {
        if (docType instanceof TypeUnion || docType instanceof TypeIntersection) {
            return false;
        }
        if (docType instanceof TypeArray && param instanceof TypeInfoArray) {
            //TODO: sus type casting, but somehow not causing a crash
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
