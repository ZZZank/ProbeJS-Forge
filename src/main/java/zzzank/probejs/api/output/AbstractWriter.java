package zzzank.probejs.api.output;

import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.typescript.TypeScriptFile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author ZZZank
 */
public abstract class AbstractWriter implements TSFileWriter {
    protected int written = 0;
    public boolean writeAsModule = true;
    public boolean withIndex = true;

    @Override
    public TSFileWriter setWithIndex(boolean withIndex) {
        this.withIndex = withIndex;
        return this;
    }

    @Override
    public TSFileWriter setWriteAsModule(boolean writeAsModule) {
        this.writeAsModule = writeAsModule;
        return this;
    }

    protected void writeFile(TypeScriptFile file, BufferedWriter writer) throws IOException {
        if (this.writeAsModule) {
            writer.write("declare module ");
            writer.write(ProbeJS.GSON.toJson(file.path.getTSPath()));
            writer.write(" {\n");
            file.write(writer);
            writer.write("}\n");
        } else {
            file.write(writer);
        }
    }

    @Override
    public final void write(Path base) throws IOException {
        try {
            writeClasses(base);
            writeIndex(base);
        } finally {
            written = 0;
            clearAcceptedFiles();
        }
    }

    protected abstract void clearAcceptedFiles();

    protected abstract void writeClasses(Path base) throws IOException;

    protected abstract void writeIndex(Path base) throws IOException;

    @Override
    public int countWrittenFiles() {
        return written;
    }
}
