package com.probejs.rewrite.doc;

import com.probejs.info.clazz.ClassInfo;
import com.probejs.rewrite.ClazzPath;
import com.probejs.rewrite.PathResolver;
import com.probejs.rewrite.doc.comments.CommentHolder;
import com.probejs.rewrite.doc.type.DocType;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class DocClazz implements CommentHolder {

    private static final Map<Class<?>, DocClazz> REGISTRIES = new HashMap<>();

    private final List<DocMethod> methods;
    private final List<DocField> fields;
    private final DocComment comment;
    private final ClazzPath path;
    private final List<DocType> assignables;
    private final ClassInfo info;

    private DocClazz(Class<?> clazz) {
        //doc properties
        val cInfo = ClassInfo.ofCache(clazz);
        this.path = PathResolver.resolve(cInfo.getRaw());
        REGISTRIES.put(clazz, this);
        this.assignables = new ArrayList<>();
        this.comment = new DocComment();
        //properties from ClassInfo
        this.info = cInfo;
        this.fields = cInfo.getFields().stream().map(DocField::new).collect(Collectors.toList());
        this.methods = cInfo.getMethods().stream().map(DocMethod::new).collect(Collectors.toList());
    }

    public static DocClazz of(Class<?> clazz) {
        val doc = REGISTRIES.get(clazz);
        if (doc != null) {
            return doc;
        }
        return new DocClazz(clazz);
    }

    @Override
    public void applyComment() {

    }
}
