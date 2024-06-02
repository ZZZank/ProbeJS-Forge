package com.probejs.document.type;

import com.probejs.info.type.JavaTypeWildcard;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * "? extends List"
 * but as of TypeScript, there's no wildcard, so should be formatted into "List"
 * @author ZZZank
 */
@AllArgsConstructor
@Getter
public class TypeWildcard implements DocType {

    private final DocType bound;

    public TypeWildcard(JavaTypeWildcard jWild) {
        bound = DocTypeResolver.fromJava(jWild.getBase());
    }

    @Override
    public String getTypeName() {
        return bound.getTypeName();
    }
}
