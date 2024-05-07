package com.probejs.document.comment.special;

import com.probejs.document.comment.SpecialComment;
import me.shedaniel.architectury.platform.Platform;

public class CommentMod extends SpecialComment {

    private static final int MARK_LEN = "@mod ".length();
    private final String mod;

    public CommentMod(String line) {
        super(line);
        mod = line.substring(MARK_LEN);
    }

    public boolean isLoaded() {
        return Platform.isModLoaded(mod);
    }
}
