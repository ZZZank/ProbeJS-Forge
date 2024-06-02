package com.probejs.document.type;

import com.probejs.info.type.JavaTypeVariable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * "T extends List"
 * @author ZZZank
 */
@AllArgsConstructor
@Getter
public class TypeVariable implements DocType {

    private final String name;
    private final List<DocType> bounds;

    public TypeVariable(JavaTypeVariable jVar) {
        this.name = jVar.getTypeName();
        this.bounds = DocTypeResolver.fromJava(jVar.getBounds());
    }

    @Override
    public String getTypeName() {
        if (bounds.isEmpty()) {
            return name;
        }
        return name;
//        return String.format("%s extends %s",
//            name,
//            //TODO: bounds formatting
//            null
//        );
    }
}
