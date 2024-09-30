package zzzank.probejs.lang.typescript.refer;

import lombok.*;
import zzzank.probejs.lang.java.clazz.ClassPath;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public final class Reference {
    public final ClassPath classPath;
    public final String original;
    public final String input;

    public String getImport() {
        val noAltName = original.equals(classPath.getName());
        val importOriginal = original.equals(classPath.getName())
            ? original
            : String.format("%s as %s", classPath.getName(), original);
        val exportedInput = ImportType.TYPE.fmt(classPath.getName());
        val importInput = input.equals(exportedInput)
            ? input
            : String.format("%s as %s", exportedInput, input);

        // Underscores can be recognized by using a global export
        return String.format("import {%s, %s} from \"packages/%s\"",
            importOriginal, importInput, classPath.getTypeScriptPath()
        );
    }
}
