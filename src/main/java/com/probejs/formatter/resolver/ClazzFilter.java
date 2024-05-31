package com.probejs.formatter.resolver;

import com.probejs.ProbeJS;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ClazzFilter {

    public static final Set<Class<?>> skipped = new HashSet<>();
    private static final Pattern methodPattern = Pattern.compile("^func_\\d+_\\w+$");
    private static final Pattern fieldPattern = Pattern.compile("^field_\\d+_\\w+$");

    public static boolean shouldSkip(Class<?> clazz) {
        return skipped.contains(clazz);
    }

    public static void skipClass(Class<?>... clazz) {
        skipped.addAll(Arrays.asList(clazz));
    }

    public static boolean acceptMethod(String methodName) {
        if (!ProbeJS.isRhizoLoaded()) {
            //fallback for Rhino
            return !methodName.equals("constructor");
        }
        //we can filter out unmapped method because not being mapped means the unmapped method is mapped in a class that
        //should be the superclass of current class
        return !methodName.equals("constructor") && !methodPattern.matcher(methodName).matches();
    }

    public static boolean acceptField(String fieldName) {
        if (!ProbeJS.isRhizoLoaded()) {
            //fallback for Rhino
            return !fieldName.equals("constructor");
        }
        //we can filter out unmapped field because 'not being mapped' means the unmapped field is mapped in a class that
        //should be the superclass of current class
        return !fieldName.equals("constructor") && !fieldPattern.matcher(fieldName).matches();
    }

    public static void init() {
        skipClass(Object.class);
        skipClass(Void.class, Void.TYPE);
        skipClass(String.class, Character.class, Character.TYPE);
        skipClass(Long.class, Long.TYPE);
        skipClass(Integer.class, Integer.TYPE);
        skipClass(Short.class, Short.TYPE);
        skipClass(Byte.class, Byte.TYPE);
        skipClass(Double.class, Double.TYPE, Float.class, Float.TYPE);
        skipClass(Boolean.class, Boolean.TYPE);
    }
}
