package com.probejs.rewrite.doc.type.java;

import com.probejs.info.clazz.ClassInfo;
import com.probejs.info.type.IType;
import com.probejs.info.type.TypeClass;
import com.probejs.rewrite.ClazzPath;
import com.probejs.rewrite.doc.DocClazz;
import com.probejs.rewrite.doc.type.DocType;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@Getter
public class TypeClazz implements JavaType {

    private final ClazzPath path;
    private final DocClazz doc;
    private final boolean assigned;

    TypeClazz(Class<?> clazz) {
        this.doc = DocClazz.of(clazz);
        this.path = this.doc.getPath();
        this.assigned = !this.doc.getAssignables().isEmpty();
    }

    TypeClazz(ClassInfo clazz) {
        this(clazz.getRaw());
    }

    public TypeClazz(Type type) {

        this(ClassInfo.ofCache((Class<?>) type));
    }

    @Override
    public Type raw() {
        return doc.getInfo().getRaw();
    }

    @Override
    public JavaType base() {
        return this;
    }

    @Override
    public Collection<Class<?>> relatedClasses() {
        //TODO: could be related to ClassWalker
        return null;
    }
}
