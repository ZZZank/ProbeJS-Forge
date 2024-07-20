package zzzank.probejs.lang.typescript.code.member;

import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class FieldDecl extends CommentableCode {
    public boolean isFinal = false;
    public boolean isStatic = false;
    public String name;
    public BaseType type;

    public FieldDecl(String name, BaseType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public Collection<ClassPath> getUsedClassPaths() {
        return type.getUsedClassPaths();
    }

    @Override
    public List<String> formatRaw(Declaration declaration) {
        List<String> modifiers = new ArrayList<>();
        if (isStatic) modifiers.add("static");
        if (isFinal) modifiers.add("readonly");


        return Arrays.asList(String.format("%s %s: %s",
            String.join(" ", modifiers), ProbeJS.GSON.toJson(name), type.line(declaration, BaseType.FormatType.RETURN)
        ));
    }
}
