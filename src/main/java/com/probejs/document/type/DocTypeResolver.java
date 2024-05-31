package com.probejs.document.type;

import com.probejs.info.type.*;
import com.probejs.util.Pair;
import com.probejs.util.StringUtil;
import java.util.List;
import java.util.stream.Collectors;

public class DocTypeResolver {

    public static DocType fromJava(JavaType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case JavaTypeClass c -> new TypeClazz(c);
            case JavaTypeArray a -> new TypeArray(a);
            case JavaTypeParameterized p -> new TypeParameterized(p);
            case JavaTypeVariable v -> null;
            case JavaTypeWildcard w -> null;
            case com.probejs.info.type.TypeLiteral l -> throw new IllegalArgumentException("");
            default -> throw new IllegalStateException("Not instance of JavaType: " + type);
        };
    }

    public static DocType resolve(String type) {
        type = type.trim();

        //TODO: Resolve object type
        if (type.startsWith("{")) {
            // {[x in string]: string}
            return new TypeLiteral(type);
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

    public static boolean typeEquals(DocType docType, JavaType param) {
        if (docType instanceof TypeUnion || docType instanceof TypeIntersection) {
            return false;
        }
        if (docType instanceof TypeArray && param instanceof JavaTypeArray array) {
            return typeEquals(((TypeArray) docType).getBase(), array.getBase());
        }
        if (docType instanceof TypeParameterized && param instanceof JavaTypeParameterized parameterized) {
            List<JavaType> paramInfo = parameterized.getParamTypes();
            List<DocType> paramDoc = ((TypeParameterized) docType).getParamTypes();
            if (paramDoc.size() != paramInfo.size()) {
                return false;
            }
            for (int i = 0; i < paramDoc.size(); i++) {
                if (!typeEquals(paramDoc.get(i), paramInfo.get(i))) return false;
            }
            return typeEquals(((TypeParameterized) docType).getRawType(), parameterized.getBase());
        }
        if (
            docType instanceof TypeNamed &&
            (param instanceof JavaTypeVariable || param instanceof JavaTypeClass)
        ) {
            return ((TypeNamed) docType).getRawTypeName().equals(param.getTypeName());
        }

        return false;
    }
}
