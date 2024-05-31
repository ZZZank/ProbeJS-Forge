package com.probejs.document.type;

import com.probejs.info.type.*;
import com.probejs.util.StringUtil;
import lombok.val;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DocTypeResolver {

    public static DocType fromJava(JavaType type) {
        if (type == null) {
            return null;
        } else if (type instanceof JavaTypeClass c) {
            return new TypeClazz(c);
        } else if (type instanceof JavaTypeArray a) {
            return new TypeArray(a);
        } else if (type instanceof JavaTypeParameterized p) {
            return new TypeParameterized(p);
        } else if (type instanceof JavaTypeVariable v) {
            return new TypeVariable(v);
        } else if (type instanceof JavaTypeWildcard w) {
            return new TypeWildcard(w);
        } else if (type instanceof com.probejs.info.type.TypeLiteral) {
            throw new IllegalArgumentException("");
        }
        throw new IllegalStateException("Not instance of JavaType: " + type);
    }

    public static List<DocType> fromJava(Collection<JavaType> types) {
        val resolved = new ArrayList<DocType>(types.size());
        for (val jType : types) {
            resolved.add(fromJava(jType));
        }
        return resolved;
    }

    public static DocType resolve(String type) {
        type = type.trim();

        //TODO: Resolve object type
        if (type.startsWith("{") || type.startsWith("[")) {
            // {[x in string]: string}, or [int, int, int]
            return new TypeLiteral(type);
        }

        val splitUnion = StringUtil.splitFirst(type, "|");
        if (splitUnion != null) {
            return new TypeUnion(resolve(splitUnion.first()), resolve(splitUnion.second()));
        }

        val splitIntersection = StringUtil.splitFirst(type, "&");
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
            val indexLeft = type.indexOf("<");
            val rawType = type.substring(0, indexLeft);
            val typeParams = type.substring(indexLeft + 1, type.length() - 1);
            val params = StringUtil.splitLayer(typeParams, ",");
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
            val paramInfo = parameterized.getParamTypes();
            val paramDoc = ((TypeParameterized) docType).getParamTypes();
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
