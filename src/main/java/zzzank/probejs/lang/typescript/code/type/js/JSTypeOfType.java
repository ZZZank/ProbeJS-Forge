package zzzank.probejs.lang.typescript.code.type.js;

import zzzank.probejs.lang.java.ClassRegistry;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.ts.TSClassType;
import zzzank.probejs.lang.typescript.code.type.utility.StaticType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import javax.annotation.Nonnull;

public class JSTypeOfType extends BaseType {

    public final BaseType inner;

    public JSTypeOfType(BaseType inner) {
        Clazz c;
        if (inner instanceof TSClassType cType
            && (c = cType.classPath.toClazz(ClassRegistry.REGISTRY)) != null
            && c.attribute.isInterface
        ) {
            this.inner = new StaticType(cType.classPath);
        } else {
            this.inner = inner;
        }
    }

    @Override
    public ImportInfos getImportInfos(@Nonnull FormatType type) {
        return inner.getImportInfos(type);
    }

    @Override
    public String line(Declaration declaration, FormatType formatType) {
        return String.format("(typeof %s)", inner.line(declaration, FormatType.RETURN));
    }
}
