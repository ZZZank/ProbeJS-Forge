package com.probejs.util;

import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class PUtil {

    private static final String[] INDENT_CACHE;

    static {
        INDENT_CACHE = new String[12 + 1];
        for (int i = 0; i < INDENT_CACHE.length; i++) {
            INDENT_CACHE[i] = String.join("", Collections.nCopies(i, " "));
        }
    }

    public static void writeLines(Writer writer, List<String> lines) throws IOException {
        for (val line : lines) {
            writer.write(line);
            writer.write('\n');
        }
    }

    public static void mergeJsonRecursive(JsonObject base, JsonObject addition) {
        for (val entry : addition.entrySet()) {
            val key = entry.getKey();
            val baseChild = base.get(key);
            val additionChild = entry.getValue();
            if (baseChild == null || !baseChild.isJsonObject() || !additionChild.isJsonObject()) {
                //add or overwrite
                base.add(key, additionChild);
            } else {
                mergeJsonRecursive(baseChild.getAsJsonObject(), additionChild.getAsJsonObject());
            }
        }
    }

    public static <T> T tryOrDefault(TrySupplier<T> toEval, T defaultValue) {
        try {
            return toEval.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * @param message The message you want to send
     * @param context The command context, usually available in Command.executes() callback
     * @return Will always be `Command.SINGLE_SUCCESS`
     */
    public static int sendSuccess(Component message, CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(message, true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * @param message The message you want to send
     * @param context The command context, usually available in Command.executes() callback
     * @return Will always be `Command.SINGLE_SUCCESS`
     */
    public static int sendSuccess(String message, CommandContext<CommandSourceStack> context) {
        return sendSuccess(PText.literal(message), context);
    }

    public interface TrySupplier<T> {
        T get() throws Exception;
    }

    public static String indent(int indentLength) {
        if (indentLength < INDENT_CACHE.length) {
            return INDENT_CACHE[indentLength];
        }
        return String.join("", Collections.nCopies(indentLength, " "));
    }

    @SuppressWarnings("unchecked")
    public static <E> E castedGetOrDef(Object key, Map<?, ?> values, E defaultValue) {
        val v = values.get(key);
        return v == null ? defaultValue : (E) v;
    }

    @SuppressWarnings("unchecked")
    public static <T> T castedGetField(Field f, Object o, T defaultVal) {
        try {
            return (T) f.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultVal;
    }

    public static Class<?> classOrNull(String name) {
        return classOrNull(name, false);
    }

    public static Class<?> classOrNull(String name, boolean printError) {
        try {
            return Class.forName(name);
        } catch (Throwable e) {
            if (printError) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean classExist(String name) {
        return classOrNull(name) != null;
    }
}
