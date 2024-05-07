package com.probejs.document.comment;

import com.probejs.document.comment.special.*;
import java.util.HashMap;
import java.util.function.Function;

public class CommentHandler {

    /**
     * key -> ((s: str)=>AbstractComment)
     * , where key should start with "@"
     */
    public static final HashMap<String, Function<String, SpecialComment>> specialCommentHandler = new HashMap<>();

    /**
     * determine if a line in comment block is special
     * @param line assumed to be a line in comment blocks, can have redundant empty chars or "*"
     * at the start of the line
     * @return true if this line may be recognized by special comment handlers
     * @see com.probejs.document.comment.CommentHandler#specialCommentHandler
     */
    public static boolean isCommentLineSpecial(String line) {
        //line = CommentUtil.removeStarMark(line).trim();
        if (!line.startsWith("@")) {
            return false;
        }
        return specialCommentHandler.containsKey(line.split(" ", 2)[0]);
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
