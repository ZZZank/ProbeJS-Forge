package com.probejs.util;

import net.minecraft.network.chat.*;

/**
 * @author ZZZank
 */
public interface PText {

    static TextComponent literal(String literal) {
        return new TextComponent(literal);
    }

    static TranslatableComponent translatable(String langKey) {
        return new TranslatableComponent(langKey);
    }

    static MutableComponent url(String name, String link) {
        return new TextComponent(name)
            .withStyle(
                Style.EMPTY
                    .withUnderlined(true)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link))
            );
    }
}
