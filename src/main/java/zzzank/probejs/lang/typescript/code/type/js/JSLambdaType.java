package zzzank.probejs.lang.typescript.code.type.js;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;
import zzzank.probejs.lang.typescript.code.member.ParamDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.ContextShield;
import zzzank.probejs.lang.typescript.code.type.Types;

import java.util.*;

public class JSLambdaType extends BaseType {
    public final List<ParamDecl> params;
    public final BaseType returnType;

    public JSLambdaType(List<ParamDecl> params, BaseType returnType) {
        this.params = params;
        this.returnType = returnType;
    }

    @Override
    public Collection<ClassPath> getUsedClassPaths() {
        Set<ClassPath> classPaths = new HashSet<>(returnType.getUsedClassPaths());
        for (ParamDecl param : params) {
            classPaths.addAll(param.type.getUsedClassPaths());
        }
        return classPaths;
    }

    @Override
    public List<String> format(Declaration declaration, FormatType formatType) {
        // (arg0: type, arg1: type...) => returnType
        return Collections.singletonList(String.format(
            "(%s => %s)",
            //when formatType is INPUT, aka this lambda is a param itself, params of this lambda should be concrete
            ParamDecl.formatParams(
                params,
                declaration,
                formatType == FormatType.INPUT ? FormatType.RETURN : FormatType.INPUT
            ),
            returnType.line(declaration, formatType)
        ));
    }

    public String formatWithName(String name, Declaration declaration, FormatType input) {
        return String.format(
            "%s%s: %s",
            name,
            ParamDecl.formatParams(params, declaration),
            returnType.line(declaration, FormatType.RETURN)
        );
    }

    public MethodDecl asMethod(String methodName) {
        return new MethodDecl(methodName, Collections.emptyList(), params, returnType);
    }

    public static class Builder {
        public final List<ParamDecl> params = new ArrayList<>();
        public BaseType returnType = Types.VOID;
        public FormatType forceFormatType = null;

        public Builder returnType(BaseType type) {
            if (forceFormatType != null) {
                type = type.contextShield(forceFormatType == FormatType.INPUT ? FormatType.RETURN : FormatType.INPUT);
            }
            this.returnType = type;
            return this;
        }

        public Builder param(String symbol, BaseType type) {
            return param(symbol, type, false);
        }

        public Builder param(String symbol, BaseType type, boolean isOptional) {
            return param(symbol, type, isOptional, false);
        }

        public Builder param(String symbol, BaseType type, boolean isOptional, boolean isVarArg) {
            if (forceFormatType != null) {
                type = type.contextShield(forceFormatType);
            }
            params.add(new ParamDecl(symbol, type, isVarArg, isOptional));
            return this;
        }

        public Builder forceFormatType(FormatType formatType) {
            this.forceFormatType = formatType;
            return this;
        }

        public Builder methodStyle() {
            forceFormatType = FormatType.RETURN;
            return this;
        }

        public Builder lambdaStyle() {
            forceFormatType = FormatType.INPUT;
            return this;
        }

        public JSLambdaType build() {
            return new JSLambdaType(params, returnType);
        }
    }
}
