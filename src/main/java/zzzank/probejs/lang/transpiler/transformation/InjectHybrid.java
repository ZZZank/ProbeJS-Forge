package zzzank.probejs.lang.transpiler.transformation;

import lombok.val;
import org.apache.commons.lang3.mutable.MutableInt;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.ParamDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Use hybrid to represent functional interfaces
 * <p>
 * {@code (a: SomeClass<number>, b: SomeClass<string>): void;}
 * <p>
 * barely useful now as we have type assignment
 * @author ZZZank
 */
public class InjectHybrid implements ClassTransformer {

    @Override
    public void transform(Clazz clazz, ClassDecl classDecl) {
        if (!clazz.attribute.isInterface) {
            return;
        }
        val count = new MutableInt(0);
        val hybrid = classDecl.methods
            .stream()
            .filter(method -> !method.isStatic)
            .filter(method -> method.isAbstract)
            .peek(c -> count.add(1))
            .findFirst()
            .orElse(null);
        if (count.getValue() != 1 || hybrid == null) {
            return;
        }
        classDecl.bodyCode.add(new Code() {
            @Override
            public Collection<ImportInfo> getImportInfos() {
                return Collections.emptyList();
            }

            @Override
            public List<String> format(Declaration declaration) {
                return Collections.singletonList(String.format(
                    "%s: %s;",
                    ParamDecl.formatParams(hybrid.params, declaration, BaseType.FormatType.RETURN),
                    hybrid.returnType.line(declaration, BaseType.FormatType.INPUT)
                ));
            }
        });
    }
}
