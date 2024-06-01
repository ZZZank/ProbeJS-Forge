package com.probejs.document.comment;

import com.probejs.document.comment.special.*;
import lombok.val;

import java.util.HashMap;
import java.util.function.Function;

public class CommentHandler {

    /**
     * key -> ((s: str)=>AbstractComment)
     * , where key should start with "@"
     */
    private static final HashMap<String, Function<String, SpecialComment>> REGISTRIES = new HashMap<>();

    /**
     * determine if a line in comment block is special
     * @param line assumed to be a line in comment blocks, can have redundant empty chars or "*"
     * at the start of the line
     * @return true if this line may be recognized by special comment handlers
     * @see com.probejs.document.comment.CommentHandler#REGISTRIES
     */
    public static boolean isCommentLineSpecial(String line) {
        //line = CommentUtil.removeStarMark(line).trim();
        if (!line.startsWith("@")) {
            return false;
        }
        return REGISTRIES.containsKey(line.split(" ", 2)[0]);
    }

    public static SpecialComment tryParseSpecialComment(String line) {
        line = line.trim();
        if (!line.startsWith("@")) {
            return null;
        }
        val handler = REGISTRIES.get(line.split(" ")[0]);
        if (handler == null) {
            return null;
        }
        return handler.apply(line);
    }

    public static void init() {
        REGISTRIES.put("@hidden", CommentHidden::new);
        REGISTRIES.put("@modify", CommentModify::new);
        REGISTRIES.put("@target", CommentTarget::new);
        REGISTRIES.put("@assign", CommentAssign::new);
        REGISTRIES.put("@mod", CommentMod::new);
        REGISTRIES.put("@returns", CommentReturns::new);
        REGISTRIES.put("@rename", CommentRename::new);
    }
}
