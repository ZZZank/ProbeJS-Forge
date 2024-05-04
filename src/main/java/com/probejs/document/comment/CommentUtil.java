package com.probejs.document.comment;

import com.probejs.document.DocumentComment;
import com.probejs.document.comment.special.CommentHidden;
import com.probejs.document.comment.special.CommentMod;
import com.probejs.document.comment.special.CommentModify;
import com.probejs.document.comment.special.CommentRename;
import com.probejs.document.type.IType;
import java.util.HashMap;
import java.util.Map;

public class CommentUtil {

    public static boolean isLoaded(DocumentComment comment) {
        if (comment == null) {
            return true;
        }
        return comment
            .getSpecialComments(CommentMod.class)
            .stream()
            .allMatch(CommentMod::isLoaded);
    }

    public static boolean isHidden(DocumentComment comment) {
        if (comment == null) {
            return false;
        }
        return comment.getSpecialComment(CommentHidden.class) != null;
    }

    public static Map<String, IType> getTypeModifiers(DocumentComment comment) {
        Map<String, IType> modifiers = new HashMap<>();
        if (comment != null) {
            comment
                .getSpecialComments(CommentModify.class)
                .forEach(modify -> modifiers.put(modify.getName(), modify.getType()));
        }
        return modifiers;
    }

    public static Map<String, String> getRenames(DocumentComment comment) {
        Map<String, String> renames = new HashMap<>();
        if (comment == null) {
            return renames;
        }
        comment
            .getSpecialComments(CommentRename.class)
            .forEach(rename -> renames.put(rename.getName(), rename.getTo()));
        return renames;
    }

    /**
     * remove one star mark("*") at the front if any.
     * @return processed string, or itself if it has no "*" at the front.
     */
    public static String removeStarMark(String line) {
        if (!line.startsWith("*")) {
            return line;
        }
        return line.substring(1).trim();
    }
}
