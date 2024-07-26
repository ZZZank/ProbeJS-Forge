package zzzank.probejs.lang.typescript;

import com.github.bsideup.jabel.Desugar;
import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;

@Desugar
public record Reference(ClassPath classPath, String original, String input) {
    public String getImport() {
        val importOriginal = original.equals(classPath.getName()) ? original : String.format("%s as %s",classPath.getName(), original);
        val exportedInput = String.format(Declaration.INPUT_TEMPLATE, classPath.getName());
        val importInput = input.equals(exportedInput) ? input : String.format("%s as %s", exportedInput, input);

        // Underscores can be recognized by using a global export
        return String.format("import {%s, %s} from \"packages/%s\"",
            importOriginal, importInput, classPath.getTypeScriptPath()
        );
    }
}
