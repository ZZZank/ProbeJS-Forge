package zzzank.probejs.lang.typescript.code.type.js;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.type.BaseType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class JSMemberType extends BaseType {
    public final Collection<JSParam> members;


    protected JSMemberType(Collection<JSParam> members) {
        this.members = members;
    }

    @Override
    public Collection<ClassPath> getUsedClassPaths() {
        Set<ClassPath> paths = new HashSet<>();
        for (JSParam member : members) {
            paths.addAll(member.type().getUsedClassPaths());
        }
        return paths;
    }

    protected String formatMembers(Declaration declaration, FormatType type) {
        return members.stream()
                .map(m -> m.format(declaration, type, this::getMemberName))
                .collect(Collectors.joining(", "));
    }

    protected abstract String getMemberName(String name);

    public static abstract class Builder<T extends Builder<T, O>, O extends BaseType> {
        public final Collection<JSParam> members = new ArrayList<>(3);

        public T member(String name, BaseType type) {
            return member(name, false, type);
        }

        @SuppressWarnings("unchecked")
        public T member(JSParam param) {
            members.add(param);
            return (T) this;
        }

        public T member(String name, boolean optional, BaseType type) {
            return member(new JSParam(name, optional, type));
        }

        public abstract O build();
    }
}
