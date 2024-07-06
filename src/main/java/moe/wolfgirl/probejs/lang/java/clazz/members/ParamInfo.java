package moe.wolfgirl.probejs.lang.java.clazz.members;

import dev.latvian.mods.rhino.annotations.typing.JSParam;
import lombok.val;
import moe.wolfgirl.probejs.lang.java.base.AnnotationHolder;
import moe.wolfgirl.probejs.lang.java.type.TypeAdapter;
import moe.wolfgirl.probejs.lang.java.type.TypeDescriptor;

import java.lang.reflect.Parameter;

public class ParamInfo extends AnnotationHolder {
    public String name;
    public TypeDescriptor type;
    public final boolean varArgs;

    public ParamInfo(Parameter parameter) {
        super(parameter.getAnnotations());
        this.name = parameter.getName();
        this.type = TypeAdapter.getTypeDescription(parameter.getAnnotatedType());
        this.varArgs = parameter.isVarArgs();

        val paramAnno = this.getAnnotation(JSParam.class);
        if (paramAnno != null) {
            if (!paramAnno.rename().isEmpty()) {
                this.name = paramAnno.rename();
            }
        }
    }
}
