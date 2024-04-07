package com.probejs.document.comment;

import com.probejs.document.comment.special.*;
import java.util.HashMap;
import java.util.function.Function;

public class CommentHandler {

    public static final HashMap<String, Function<String, AbstractComment>> specialCommentHandler = new HashMap<>();

    /**
     * determine if a line in comment block is special
     * @param line assumed to be a line in comment blocks, can have redundant empty chars or "*"
     * at the start of the line
     * @return true if this line may be recongnized by special comment handlers
     * @see com.probejs.document.comment.CommentHandler#specialCommentHandler
     */
    public static boolean isCommentLineSecial(String line) {
        line = CommentUtil.removeStarMark(line);
        int end = line.indexOf(" ");
        if (end == -1) {
            return false;
        }
        return specialCommentHandler.containsKey(line.substring(0, end));
    }

    public static void init() {
        specialCommentHandler.put("@hidden", CommentHidden::new);
        specialCommentHandler.put("@modify", CommentModify::new);
        specialCommentHandler.put("@target", CommentTarget::new);
        specialCommentHandler.put("@assign", CommentAssign::new);
        specialCommentHandler.put("@mod", CommentMod::new);
        specialCommentHandler.put("@returns", CommentReturns::new);
        specialCommentHandler.put("@rename", CommentRename::new);
    }
}
