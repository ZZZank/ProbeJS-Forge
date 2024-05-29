package com.probejs.rewrite.doc;

import com.probejs.document.comment.SpecialComment;
import com.probejs.document.comment.special.CommentModify;
import com.probejs.document.comment.special.CommentRename;
import com.probejs.document.type.IDocType;
import com.probejs.info.clazz.MethodInfo;
import com.probejs.rewrite.doc.comments.CommentHolder;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DocMethod implements CommentHolder {

    private final DocComment comment;
    private final List<DocParam> params;
    private IDocType returnType;

    DocMethod(MethodInfo mInfo) {
        this.comment = new DocComment();
        this.params = mInfo.getParams().stream().map(DocParam::new).collect(Collectors.toList());
        //TODO: doc type
        this.returnType = (IDocType) mInfo.getType();
    }

    @Override
    public void applyComment() {
        //rename
        for (val rename : this.comment.getSpecialComment(CommentRename.class)) {
            for (val param : this.params) {
                if (param.applyComment(rename)) {
                    break;
                }
            }
        }
        //type modify
        val modifys = this.comment.getSpecialComment(CommentModify.class);
        if (!modifys.isEmpty()) {
            val modify = modifys.get(0);
            if (modify.getName().equals(this.returnType.getTypeName())) {
                this.returnType = modify.getType();
            }
        }
    }

    @Getter
    @Setter
    public static class DocParam {

        private String name;
        private IDocType type;

        DocParam(MethodInfo.ParamInfo pInfo) {
            this.name = pInfo.getName();
            //TODO: doc type
            this.type = (IDocType) pInfo.getType();
        }

        public boolean applyComment(SpecialComment comment) {
            //TODO: totally mess
            if (comment instanceof CommentRename rename) {
                if (rename.getName().equals(this.name)) {
                    this.name = rename.getTo();
                }
            } else if (comment instanceof CommentModify modify) {
                if (modify.getName().equals(this.type.getTypeName())) {
                    this.type = modify.getType();
                }
            } else {
                return false;
            }
            return true;
        }
    }
}
