package zzzank.probejs.lang.typescript.code.type.utility;

import lombok.val;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ZZZank
 */
public class WithFormatType extends BaseType {
    private final String format;
    private final BaseType[] types;

    public WithFormatType(String format, BaseType... types) {
        this.format = format;
        this.types = types;
    }

    @Override
    public ImportInfos getImportInfos() {
        return ImportInfos.of().fromCodes(Arrays.asList(types));
    }

    @Override
    public List<String> format(Declaration declaration, FormatType formatType) {
        val additions = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            additions[i] = types[i].line(declaration, formatType);
        }
        return Collections.singletonList(String.format(format, additions));
    }
}
