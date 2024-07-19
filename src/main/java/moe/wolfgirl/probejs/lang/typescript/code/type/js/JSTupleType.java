package moe.wolfgirl.probejs.lang.typescript.code.type.js;

import moe.wolfgirl.probejs.lang.typescript.Declaration;
import moe.wolfgirl.probejs.utils.NameUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JSTupleType extends JSMemberType {


    public JSTupleType(Collection<JSParam> members) {
        super(members);
    }

    @Override
    public List<String> format(Declaration declaration, FormatType input) {
        return Collections.singletonList(String.format("[%s]", formatMembers(declaration, input)));
    }

    @Override
    protected String getMemberName(String name) {
        return NameUtils.isNameSafe(name) ? name : "arg";
    }

    public static class Builder extends JSMemberType.Builder<Builder, JSTupleType> {

        @Override
        public JSTupleType build() {
            return new JSTupleType(members);
        }
    }
}
