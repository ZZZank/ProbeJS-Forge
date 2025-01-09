package zzzank.probejs.lang.transpiler.transformation.impl;

import lombok.val;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.docs.Primitives;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.lang.transpiler.transformation.ClassTransformer;
import zzzank.probejs.lang.typescript.code.member.ConstructorDecl;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;
import zzzank.probejs.lang.typescript.code.member.ParamDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;
import zzzank.probejs.lang.typescript.code.type.ts.TSArrayType;
import zzzank.probejs.lang.typescript.code.type.ts.TSClassType;
import zzzank.probejs.lang.typescript.code.type.ts.TSParamType;
import zzzank.probejs.utils.NameUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * @author ZZZank
 */
public class AutoParamRename implements ClassTransformer {

    public static final Pattern ARG_N = Pattern.compile("^arg(\\d+)$");
    private static final Map<JSPrimitiveType, String> PRIMITIVES;

    static {
        PRIMITIVES = new HashMap<>();
        val fn = (Consumer<JSPrimitiveType>) t -> PRIMITIVES.put(t, t.content);
        fn.accept(Primitives.FLOAT);
        fn.accept(Primitives.LONG);
        fn.accept(Primitives.INTEGER);
        fn.accept(Primitives.SHORT);
        fn.accept(Primitives.BYTE);
        fn.accept(Primitives.DOUBLE);
        fn.accept(Primitives.CHAR_SEQUENCE);
        PRIMITIVES.put(Primitives.CHARACTER, "char");
        PRIMITIVES.put(Types.NUMBER, "num");
        PRIMITIVES.put(Types.BOOLEAN, "bl");
    }

    @Override
    public void transformMethod(Clazz clazz, MethodInfo methodInfo, MethodDecl methodDecl) {
        for (val param : methodDecl.params) {
            autoRename(param);
        }
    }

    @Override
    public void transformConstructor(Clazz clazz, ConstructorInfo constructorInfo, ConstructorDecl constructorDecl) {
        for (val param : constructorDecl.params) {
            autoRename(param);
        }
    }

    public static void autoRename(ParamDecl param) {
        val match = ARG_N.matcher(param.name);
        if (match.find()) {
            val index = match.group(1);
            val autoName = autoParamName(param.type);
            if (autoName != null) {
                param.name = autoName + index;
            }
        }
    }

    @Nullable
    public static String autoParamName(BaseType type) {
        if (type instanceof JSPrimitiveType primitive) {
            return PRIMITIVES.get(primitive);
        } else if (type instanceof TSClassType c) {
            return NameUtils.firstLower(c.classPath.getJavaName());
        } else if (type instanceof TSArrayType arr) {
            return autoParamName(arr.component) + "s";
        } else if (type instanceof TSParamType param) {
            return autoParamName(param.baseType);
        }
        return null;
    }
}
