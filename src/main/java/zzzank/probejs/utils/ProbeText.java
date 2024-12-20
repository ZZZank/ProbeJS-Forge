package zzzank.probejs.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author ZZZank
 */
public final class ProbeText implements MutableComponent {

    public static ProbeText of(@NotNull MutableComponent raw) {
        return new ProbeText(Objects.requireNonNull(raw));
    }

    public static ProbeText literal(String literal) {
        return of(new TextComponent(literal));
    }

    public static ProbeText translatable(String key, Object... args) {
        return of(new TranslatableComponent(key, args));
    }

    public static ProbeText pjs(String key, Object... args) {
        return translatable("probejs." + key, args);
    }

    private MutableComponent raw;

    public ProbeText(MutableComponent raw) {
        this.raw = raw;
    }

    @Override
    public ProbeText setStyle(Style style) {
        raw = raw.setStyle(style);
        return this;
    }

    @Override
    public ProbeText append(Component component) {
        this.raw = raw.append(component);
        return this;
    }

    public ProbeText append(ProbeText component) {
        return append(component.raw);
    }

    public ProbeText color(TextColor color) {
        return setStyle(getStyle().withColor(color));
    }

    public ProbeText color(ChatFormatting color) {
        return setStyle(getStyle().withColor(color));
    }

    public ProbeText click(ClickEvent clickEvent) {
        return setStyle(getStyle().withClickEvent(clickEvent));
    }

    public ProbeText hover(HoverEvent hoverEvent) {
        return setStyle(getStyle().withHoverEvent(hoverEvent));
    }

    public ProbeText font(ResourceLocation fontId) {
        return setStyle(getStyle().withFont(fontId));
    }

    public ProbeText insertion(String insertion) {
        return setStyle(getStyle().withInsertion(insertion));
    }

    public ProbeText bold(Boolean bold) {
        return setStyle(getStyle().withBold(bold));
    }

    public ProbeText italic(Boolean italic) {
        return setStyle(getStyle().withItalic(italic));
    }

    public ProbeText underlined(Boolean underlined) {
        return setStyle(getStyle().setUnderlined(underlined));
    }

    public ProbeText strikethrough(Boolean strikethrough) {
        return setStyle(getStyle().setStrikethrough(strikethrough));
    }

    public ProbeText obfuscated(Boolean obfuscated) {
        return setStyle(getStyle().setObfuscated(obfuscated));
    }

    public MutableComponent unwrap() {
        return raw;
    }

    @Override
    public Style getStyle() {
        return raw.getStyle();
    }

    @Override
    public String getContents() {
        return raw.getContents();
    }

    @Override
    public List<Component> getSiblings() {
        return raw.getSiblings();
    }

    @Override
    public MutableComponent plainCopy() {
        return raw.plainCopy();
    }

    @Override
    public ProbeText copy() {
        return of(raw.copy());
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        return raw.getVisualOrderText();
    }
}
