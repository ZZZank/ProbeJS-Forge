package com.probejs.rewrite.doc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.probejs.document.DocComment;
import com.probejs.document.type.DocType;
import com.probejs.info.clazz.ClassInfo;
import com.probejs.rewrite.ClazzPath;
import com.probejs.rewrite.PathResolver;
import com.probejs.rewrite.doc.comments.CommentHolder;
import dev.latvian.mods.rhino.Kit;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class DocClazz implements CommentHolder {

    private static final Map<String, DocClazz> ALL = new HashMap<>();
    private final boolean artificial;

    public DocClazz(ClazzPath path) {
        artificial = true;
        this.path = path;
        comment = new DocComment();
        info = null;
        methods = ArrayListMultimap.create(1,1);
        fields = new HashMap<>(1);
        assignables = new ArrayList<>(3);
    }

    public static DocClazz getOrCreate(String rawClassName) {
        var doc = ALL.get(rawClassName);
        if (doc != null) {
            return doc;
        }
        var clazz = Kit.classOrNull(rawClassName);
        if (clazz != null) {
            return new DocClazz(clazz);
        }
        return new DocClazz(PathResolver.resolve(rawClassName));
    }

    private final ListMultimap<String, DocMethod> methods;
    private final Map<String, DocField> fields;
    private final DocComment comment;
    private final ClazzPath path;
    private final List<DocType> assignables;
    private final ClassInfo info;

    public DocClazz(Class<?> clazz, ClazzPath path) {
        artificial = false;
        //doc properties
        val cInfo = ClassInfo.ofCache(clazz);
        this.path = path;
        ALL.put(clazz.getName(), this);
        this.assignables = new ArrayList<>(3);
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

    @Override
    public void applyComment() {

    }
}
