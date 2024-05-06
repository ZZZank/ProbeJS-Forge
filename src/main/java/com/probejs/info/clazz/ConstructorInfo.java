package com.probejs.info.clazz;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
public class ConstructorInfo {

    private List<MethodInfo.ParamInfo> params;

    public ConstructorInfo(Constructor<?> constructor) {
        this.params = Arrays.stream(constructor.getParameters())
            .map(MethodInfo.ParamInfo::new)
            .collect(Collectors.toList());
    }
}
