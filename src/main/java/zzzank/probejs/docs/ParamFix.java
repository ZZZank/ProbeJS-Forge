package zzzank.probejs.docs;

import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.text.Text;
import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;
import zzzank.probejs.utils.DocUtils;

import java.util.Map;

public class ParamFix extends ProbeJSPlugin {
    @Override
    public void modifyClasses(ScriptDump scriptDump, Map<ClassPath, TypeScriptFile> globalClasses) {
        val textWrapper = globalClasses.get(new ClassPath(TextWrapper.class));
        if (textWrapper == null) {
            return;
        }
        DocUtils.replaceParamType(
            textWrapper,
            m -> m.params.size() == 1 && m.name.equals("of"),
            0,
            Types.type(Text.class)
        );
    }
}
