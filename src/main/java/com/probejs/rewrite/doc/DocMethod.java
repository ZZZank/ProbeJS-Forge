package com.probejs.rewrite.doc;

import com.probejs.info.clazz.MethodInfo;
import com.probejs.info.type.IType;
import com.probejs.rewrite.doc.comments.CommentHolder;
import lombok.Getter;

import java.util.List;

@Getter
public class DocMethod implements CommentHolder {

    private final DocComment comment;
    private final List<MethodInfo.ParamInfo> params;
    private IType returnType;

    DocMethod(MethodInfo mInfo) {
        this.comment = new DocComment();
        //TODO: doc type
        this.params = mInfo.getParams();
        this.returnType = mInfo.getType();
    }

    @Override
    public void applyComment() {

    }
}
