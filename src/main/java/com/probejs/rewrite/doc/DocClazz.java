package com.probejs.rewrite.doc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.probejs.document.DocComment;
import com.probejs.document.type.DocType;
import com.probejs.info.clazz.ClassInfo;
import com.probejs.rewrite.ClazzPath;
import com.probejs.rewrite.PathResolver;
import com.probejs.rewrite.doc.comments.CommentHolder;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class DocClazz implements CommentHolder {

    private static final Map<String, DocClazz> ALL = new HashMap<>();

    private final ListMultimap<String, DocMethod> methods;
    private final Map<String, DocField> fields;
    private final DocComment comment;
    private final ClazzPath path;
    private final List<DocType> assignables;
    private final ClassInfo info;

    public DocClazz(Class<?> clazz, ClazzPath path) {
        //doc properties
        val cInfo = ClassInfo.ofCache(clazz);
        this.path = path;
        ALL.put(clazz.getName(), this);
        this.assignables = new ArrayList<>();
        this.comment = new DocComment();
        //properties from ClassInfo
        info = cInfo;
        fields = new HashMap<>();
        for (val field : cInfo.getFields()) {
            val doc = new DocField(field);
            fields.put(doc.getName(), doc);
        }
        methods = ArrayListMultimap.create();
        for (val method : cInfo.getMethods()) {
            val doc = new DocMethod(method);
            methods.put(doc.getName(), doc);
        }
    }

    public DocClazz(Class<?> clazz) {
        this(clazz, PathResolver.resolve(clazz));
    }

    public static DocClazz of(Class<?> clazz) {
        val doc = ALL.get(clazz.getName());
        if (doc != null) {
            return doc;
        }
        return new DocClazz(clazz);
    }

    @Override
    public void applyComment() {

    }
}
