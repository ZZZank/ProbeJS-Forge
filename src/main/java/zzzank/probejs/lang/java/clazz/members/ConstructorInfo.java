package zzzank.probejs.lang.java.clazz.members;

import zzzank.probejs.lang.java.base.TypeVariableHolder;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConstructorInfo extends TypeVariableHolder  {

    public final List<ParamInfo> params;

    public ConstructorInfo(Constructor<?> constructor) {
        super(constructor.getTypeParameters(), constructor.getAnnotations());
        this.params = Arrays.stream(constructor.getParameters()).map(ParamInfo::new).collect(Collectors.toList());
    }

}
