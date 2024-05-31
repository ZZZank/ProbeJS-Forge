package com.probejs.info.clazz;

import com.probejs.info.type.JavaType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseMemberInfo {
    protected String name;
    protected JavaType type;
}
