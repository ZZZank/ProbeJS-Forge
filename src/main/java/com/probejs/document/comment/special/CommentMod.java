package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import me.shedaniel.architectury.platform.Platform;

public class CommentMod extends SpecialComment {

    public static final String MARK = "@mod";
    private final String mod;

    public CommentMod(String line) {
        super(line);
        mod = line.substring(MARK.length());
    }

    public boolean isLoaded() {
        return Platform.isModLoaded(mod);
    }
}
