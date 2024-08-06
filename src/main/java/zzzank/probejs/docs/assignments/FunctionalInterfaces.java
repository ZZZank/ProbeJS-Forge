package zzzank.probejs.docs.assignments;

import lombok.val;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.stream.Collectors;

public class FunctionalInterfaces extends ProbeJSPlugin {

    @Override
    public void assignType(ScriptDump scriptDump) {
        val converter = scriptDump.transpiler.typeConverter;

        for (val recordedClass : scriptDump.recordedClasses) {
            if (!recordedClass.attribute.isInterface) {
                continue;
            }
            val abstracts = recordedClass.methods.stream()
                .filter(methodInfo -> methodInfo.attributes.isAbstract)
                .collect(Collectors.toList());
            if (abstracts.size() != 1) {
                continue;
            }
            val method = abstracts.get(0);
            val type = Types.lambda().methodStyle().returnType(converter.convertType(method.returnType));
            for (val param : method.params) {
                type.param(param.name, converter.convertType(param.type));
            }
            scriptDump.assignType(recordedClass.original, type.build());
        }
    }
}
