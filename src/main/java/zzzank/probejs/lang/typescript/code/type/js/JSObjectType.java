package zzzank.probejs.lang.typescript.code.type.js;

import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JSObjectType extends JSMemberType {

    public JSObjectType(Collection<JSParam> members) {
        super(members);
    }

    @Override
    protected String getMemberName(String name) {
        return ProbeJS.GSON.toJson(name);
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(String.format("{%s}", String.join(", ", formatMembers(declaration, input))));
    }

    public static class Builder extends JSMemberType.Builder<Builder, JSObjectType> {
        public JSObjectType.Builder indexParam(BaseType type) {
            this.members.add(new JSParam.ObjIndex(type));
            return this;
        }

        public JSObjectType build() {
            return new JSObjectType(members);
        }
    }
}
