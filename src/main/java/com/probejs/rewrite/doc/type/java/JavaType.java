package com.probejs.rewrite.doc.type.java;

import com.probejs.rewrite.doc.type.DocType;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author ZZZank
 */
public interface JavaType extends DocType {

    Type raw();

    JavaType base();

    Collection<Class<?>> relatedClasses();
}
