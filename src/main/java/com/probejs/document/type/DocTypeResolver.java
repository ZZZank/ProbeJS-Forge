package com.probejs.document.type;

import com.probejs.info.type.*;
import com.probejs.util.Pair;
import com.probejs.util.StringUtil;
import java.util.List;
import java.util.stream.Collectors;

public class DocTypeResolver {

    public static IDocType resolve(String type) {
        type = type.trim();

        //TODO: Resolve object type
        if (type.startsWith("{")) {
            // {[x in string]: string}
            return new DocTypeRaw(type);
        }

        Pair<String, String> splitUnion = StringUtil.splitFirst(type, "<", ">", "|");
        if (splitUnion != null) {
            return new DocTypeUnion(resolve(splitUnion.first()), resolve(splitUnion.second()));
        }

        Pair<String, String> splitIntersection = StringUtil.splitFirst(type, "<", ">", "&");
        if (splitIntersection != null) {
            return new DocTypeIntersection(
                resolve(splitIntersection.first()),
                resolve(splitIntersection.second())
            );
        }

        if (type.endsWith("[]")) {
            return new DocTypeArray(resolve(type.substring(0, type.length() - 2)));
        }

        if (type.endsWith(">")) {
            int indexLeft = type.indexOf("<");
            String rawType = type.substring(0, indexLeft);
            String typeParams = type.substring(indexLeft + 1, type.length() - 1);
            List<String> params = StringUtil.splitLayer(typeParams, "<", ">", ",");
            return new DocTypeParameterized(
                resolve(rawType),
                params.stream().map(DocTypeResolver::resolve).collect(Collectors.toList())
            );
        }
        return new DocTypeNamed(type);
    }

    public static boolean typeEquals(IDocType docType, com.probejs.info.type.IType param) {
        if (docType instanceof DocTypeUnion || docType instanceof DocTypeIntersection) {
            return false;
        }
        if (docType instanceof DocTypeArray && param instanceof com.probejs.info.type.TypeArray) {
            com.probejs.info.type.TypeArray array = (com.probejs.info.type.TypeArray) param;
            return typeEquals(((DocTypeArray) docType).getComponent(), array.getBaseType());
        }
        if (docType instanceof DocTypeParameterized && param instanceof com.probejs.info.type.TypeParameterized) {
            com.probejs.info.type.TypeParameterized parameterized = (com.probejs.info.type.TypeParameterized) param;
            List<com.probejs.info.type.IType> paramInfo = parameterized.getParamTypes();
            List<IDocType> paramDoc = ((DocTypeParameterized) docType).getParamTypes();
            if (paramDoc.size() != paramInfo.size()) {
                return false;
            }
            for (int i = 0; i < paramDoc.size(); i++) {
                if (!typeEquals(paramDoc.get(i), paramInfo.get(i))) return false;
            }
            return typeEquals(((DocTypeParameterized) docType).getRawType(), parameterized.getBaseType());
        }
        if (
            docType instanceof DocTypeNamed &&
            (param instanceof TypeVariable || param instanceof TypeClass)
        ) {
            return ((DocTypeNamed) docType).getRawTypeName().equals(param.getTypeName());
        }

        return false;
    }
}
