package zzzank.probejs.lang.java.base;

import lombok.val;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationHolder {
    private final Annotation[] annotations;

    public AnnotationHolder(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return Arrays.stream(annotations).anyMatch(annotation::isInstance);
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> List<T> getAnnotations(Class<T> type) {
        return Arrays.stream(annotations)
            .filter(type::isInstance)
            .map(a -> (T) a)
            .collect(Collectors.toList());
    }

    @Nullable
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        for (val annotation : annotations) {
            if (type.isInstance(annotation)) {
                return (T) annotation;
            }
        }
        return null;
    }
}
