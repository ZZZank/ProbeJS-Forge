package com.probejs.rewrite.doc;

import com.probejs.document.DocComment;
import com.probejs.document.type.DocType;
import com.probejs.document.type.DocTypeResolver;
import com.probejs.info.clazz.ConstructorInfo;
import com.probejs.info.clazz.MethodInfo;
import com.probejs.rewrite.doc.comments.CommentHolder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DocMethod implements CommentHolder {

    private final DocComment comment;
    private final List<DocParam> params;
    private String name;
    private DocType returnType;

    DocMethod(MethodInfo mInfo) {
        this.comment = new DocComment();
        this.name = mInfo.getName();
        this.params = mInfo.getParams().stream().map(DocParam::new).collect(Collectors.toList());
        this.returnType = DocTypeResolver.fromJava(mInfo.getType());
    }

    @Override
    public void applyComment() {
    }

    @Getter
    @Setter
    public static class DocParam {

        private String name;
        private DocType type;

        DocParam(MethodInfo.ParamInfo pInfo) {
            this.name = pInfo.getName();
            this.type = DocTypeResolver.fromJava(pInfo.getType());
        }
    }
}
