package zzzank.probejs.lang.typescript;

import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.Reference;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeScriptFile {
    public final Declaration declaration;
    public final List<Code> codes;
    public final ClassPath path;

    public TypeScriptFile(ClassPath self) {
        this.declaration = new Declaration();
        this.codes = new ArrayList<>();

        if (self != null) {
            declaration.addImport(ImportInfo.of(self));
        }
        this.path = self;
    }

    public void excludeSymbol(String name) {
        declaration.exclude(name);
    }

    public void addCode(Code code) {
        codes.add(code);
        for (val info : code.getImportInfos()) {
            declaration.addImport(info);
        }
    }

    public List<String> format() {
        List<String> formatted = new ArrayList<>();

        for (Code code : codes) {
            formatted.addAll(code.format(declaration));
        }

        return formatted;
    }

    public void write(Path writeTo) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(writeTo)) {
            this.write(writer);
        }
    }

    public void write(BufferedWriter writer) throws IOException {
        boolean written = false;
        for (Reference value : declaration.references.values()) {
            if (value.info.path.equals(path)) {
                continue;
            }
            writer.write(value.getImport() + "\n");
            written = true;
        }
        if (!written) {
            writer.write("export {} // Mark the file as a module, do not remove unless there are other import/exports!");
        }

        writer.write("\n");
        for (val line : format()) {
            writer.write(line);
            writer.write('\n');
        }
    }

    public void writeAsModule(BufferedWriter writer) throws IOException {
        writer.write(String.format(
            "declare module %s {\n",
            ProbeJS.GSON.toJson(path.getTSPath())
        ));
        this.write(writer);
        writer.write("}\n");
    }

    @SuppressWarnings("unchecked")
    public <T extends Code> Optional<T> findCode(Class<T> type) {
        for (Code code : codes) {
            if (type.isInstance(code)) {
                return Optional.of((T) code);
            }
        }
        return Optional.empty();
    }
}
