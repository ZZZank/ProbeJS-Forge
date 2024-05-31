package com.probejs.info.clazz;

import com.probejs.info.type.IType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseMemberInfo {
    protected String name;
    protected IType type;
}
