package zzzank.probejs.lang.typescript.code.type.js;

import lombok.AllArgsConstructor;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;
import zzzank.probejs.lang.typescript.code.member.ParamDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code (a: A$$Type, b: B$$Type) => C} if format type is {@link zzzank.probejs.lang.typescript.code.type.BaseType.FormatType#RETURN}
 * <p>
 * {@code (a: A, b: B) => C$$Type} if format type is {@link zzzank.probejs.lang.typescript.code.type.BaseType.FormatType#INPUT}
 * @author ZZZank
 */
@AllArgsConstructor
public class JSLambdaType extends BaseType {
    public final List<ParamDecl> params;
    public final BaseType returnType;

    @Override
    public ImportInfos getImportInfos(@Nonnull FormatType type) {
        return ImportInfos.of(returnType.getImportInfos(type))
            .fromCodes(params.stream().map(p -> p.type), paramFormatType(type));
    }

    public static FormatType paramFormatType(FormatType formatType) {
        return switch (formatType) {
            case RETURN -> FormatType.INPUT;
            case INPUT -> FormatType.RETURN;
            case VARIABLE -> FormatType.VARIABLE;
        };
    }

    @Override
    public String line(Declaration declaration, FormatType formatType) {
        // (arg0: type, arg1: type...) => returnType
        return String.format(
            "(%s => %s)",
            //when formatType is INPUT, aka this lambda is a param itself, params of this lambda should be concrete
            ParamDecl.formatParams(
                params,
                declaration,
                paramFormatType(formatType)
            ),
            returnType.line(declaration, formatType)
        );
    }

    public String formatWithName(String name, Declaration declaration, FormatType formatType) {
        return String.format(
            "%s%s: %s",
            name,
            ParamDecl.formatParams(
                params,
                declaration,
                paramFormatType(formatType)
            ),
            returnType.line(declaration, formatType)
        );
    }

    public MethodDecl asMethod(String methodName) {
        return new MethodDecl(methodName, Collections.emptyList(), params, returnType);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<SELF extends Builder<SELF>> {
        public final List<ParamDecl> params = new ArrayList<>();
        public BaseType returnType = Types.VOID;

        public SELF returnType(BaseType type) {
            this.returnType = type;
            return (SELF) this;
        }

        public SELF param(String symbol, BaseType type) {
            return param(symbol, type, false);
        }

        public SELF param(String symbol, BaseType type, boolean isOptional) {
            return param(symbol, type, isOptional, false);
        }

        public SELF param(String symbol, BaseType type, boolean isOptional, boolean isVarArg) {
            params.add(new ParamDecl(symbol, type, isVarArg, isOptional));
            return (SELF) this;
        }

        public JSLambdaType build() {
            return new JSLambdaType(params, returnType);
        }
    }

    public static class Builder extends BuilderBase<Builder> {
    }
}
