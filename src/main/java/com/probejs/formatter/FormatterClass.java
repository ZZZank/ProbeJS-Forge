package com.probejs.formatter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.probejs.ProbeJS;
import com.probejs.document.DocManager;
import com.probejs.document.DocumentClass;
import com.probejs.document.DocumentComment;
import com.probejs.document.DocumentField;
import com.probejs.document.DocumentMethod;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.type.IType;
import com.probejs.formatter.api.DocumentReceiver;
import com.probejs.formatter.api.IFormatter;
import com.probejs.formatter.resolver.NameResolver;
import com.probejs.info.*;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.info.type.TypeInfoParameterized;
import com.probejs.info.type.TypeInfoVariable;
import com.probejs.info.type.TypeResolver;
import com.probejs.util.PUtil;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

public class FormatterClass extends DocumentReceiver<DocumentClass> implements IFormatter {

    private final ClassInfo classInfo;
    private final Map<String, FormatterField> fieldFormatters;
    private final Multimap<String, FormatterMethod> methodFormatters;
    private final List<DocumentField> fieldAdditions = new ArrayList<>();
    private final List<DocumentMethod> methodAdditions = new ArrayList<>();
    @Setter
    private boolean internal = false;

    public FormatterClass(ClassInfo classInfo) {
        this.classInfo = classInfo;
        this.methodFormatters = ArrayListMultimap.create();
        for (MethodInfo methodInfo : classInfo.getMethodInfos()) {
            methodFormatters.put(methodInfo.getName(), new FormatterMethod(methodInfo));
        }
        this.fieldFormatters = new HashMap<>();
        for (FieldInfo fieldInfo : classInfo.getFieldInfos()) {
            fieldFormatters.put(fieldInfo.getName(), new FormatterField(fieldInfo));
        }
    }

    /**
     * similar to {@code new FormatterType(info,false).format(0,4)}, but with additional
     * processing for TypeInfoClass. If its getTypeVariables() is not returning an empty
     * list, a {@code <any,any,...>} style type variable representation will be added to
     * the end of formatted string
     */
    public static String formatParameterized(ITypeInfo info) {
        StringBuilder sb = new StringBuilder(FormatterType.of(info, false).format());
        if (info instanceof TypeInfoClass) {
            TypeInfoClass clazz = (TypeInfoClass) info;
            if (!clazz.getTypeVariables().isEmpty()) {
                sb.append(
                    String.format(
                        "<%s>",
                        clazz
                            .getTypeVariables()
                            .stream()
                            .map(ITypeInfo::getTypeName)
                            .map(NameResolver::getResolvedName)
                            .map(NameResolver.ResolvedName::getFullName)
                            .collect(Collectors.joining(","))
                    )
                );
            }
        }
        return sb.toString();
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
        List<String> lines = new ArrayList<>();
        DocumentComment comment = document == null ? null : document.getComment();
        if (comment != null) {
            if (CommentUtil.isHidden(comment)) {
                return lines;
            }
            lines.addAll(comment.format(indent, stepIndent));
        }

        List<String> assignableTypes = DocManager.typesAssignable
            .getOrDefault(classInfo.getRaw().getName(), new ArrayList<>())
            .stream()
            .map(t -> t.transform(IType.defaultTransformer))
            .collect(Collectors.toList());

        if (classInfo.isEnum()) {
            //TODO: add special processing for KubeJS
            Class<?> clazz = classInfo.getRaw();
            try {
                Method values = clazz.getMethod("values");
                values.setAccessible(true);
                Object[] enumValues = (Object[]) values.invoke(null);
                //Use the name() here so won't be affected by overrides
                Method name = Enum.class.getMethod("name");
                for (Object enumValue : enumValues) {
                    assignableTypes.add(
                        ProbeJS.GSON.toJson(name.invoke(enumValue).toString().toLowerCase(Locale.ROOT))
                    );
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // First line
        List<String> firstLine = new ArrayList<>();
        if (!internal) {
            firstLine.add("declare");
        }
        if (classInfo.isInterface()) {
            firstLine.add("interface");
        } else if (classInfo.isAbstract()) {
            firstLine.add("abstract class");
        } else {
            firstLine.add("class");
        }
        firstLine.add(NameResolver.getResolvedName(classInfo.getName()).getLastName());
        if (classInfo.getRaw().getTypeParameters().length != 0) {
            firstLine.add(
                String.format(
                    "<%s>",
                    Arrays
                        .stream(classInfo.getRaw().getTypeParameters())
                        .map(TypeVariable::getName)
                        .collect(Collectors.joining(", "))
                )
            );
        }
        // super class
        if (classInfo.getSuperClass() != null) {
            firstLine.add("extends");
            if (classInfo.getSuperClass().getRaw() == Object.class) {
                // redirect to another `Object` so that we can bypass replacement of original `Object`
                firstLine.add("Document.Object");
            } else {
                firstLine.add(
                    formatParameterized(
                        TypeResolver.resolveType(classInfo.getRaw().getGenericSuperclass())
                    )
                );
            }
        }
        // interface
        if (!classInfo.getInterfaces().isEmpty()) {
            firstLine.add(classInfo.isInterface() ? "extends" : "implements");
            firstLine.add(
                Arrays
                    .stream(classInfo.getRaw().getGenericInterfaces())
                    .map(TypeResolver::resolveType)
                    .map(FormatterClass::formatParameterized)
                    .collect(Collectors.joining(", "))
            );
        }
        firstLine.add("{");
        lines.add(PUtil.indent(indent) + String.join(" ", firstLine));
        // first line processing, end

        // methods
        methodFormatters
            .values()
            .stream()
            .filter(fmtrMethod ->
                ProbeJS.CONFIG.keepBeaned || //want to keep, or
                    fmtrMethod.getBeanedName() == null || //cannot be beaned when not wanting to keep
                    fieldFormatters.containsKey(fmtrMethod.getBeanedName()) || //beaning will cause conflict
                    methodFormatters.containsKey(fmtrMethod.getBeanedName()) //also conflict
            )
            .filter(fmtrMethod ->
                //not static interface in namespace `Internal`
                !(classInfo.isInterface() && fmtrMethod.getInfo().isStatic() && internal)
            )
            .forEach(fmtrMethod -> lines.addAll(fmtrMethod.format(indent + stepIndent, stepIndent)));
        //fields
        fieldFormatters
            .entrySet()
            .stream()
            .filter(e -> !methodFormatters.containsKey(e.getKey()))
            .filter(f -> !(classInfo.isInterface() && f.getValue().getFieldInfo().isStatic() && internal))
            .forEach(f -> {
                f.getValue().setFromInterface(classInfo.isInterface());
                lines.addAll(f.getValue().format(indent + stepIndent, stepIndent));
            });

        // beans
        if (!classInfo.isInterface()) {
            Map<String, FormatterMethod> getterMap = new HashMap<>();
            ListMultimap<String, FormatterMethod> setterMap = ArrayListMultimap.create();

            for (FormatterMethod m : methodFormatters.values()) {
                String beanName = m.getBeanedName();
                if (
                    beanName == null ||
                        !Character.isAlphabetic(beanName.charAt(0)) ||
                        fieldFormatters.containsKey(beanName) ||
                        methodFormatters.containsKey(beanName)
                ) {
                    continue;
                }
                if (m.isGetter()) {
                    getterMap.put(beanName, m);
                } else {
                    setterMap.put(beanName, m);
                }
            }

            getterMap.forEach((k, v) -> lines.addAll(v.formatBean(indent + stepIndent, stepIndent)));
            setterMap.values()
                .stream()
                .filter(m ->
                    !getterMap.containsKey(m.getBeanedName()) ||
                        getterMap.get(m.getBeanedName()).getBeanTypeString().equals(m.getBeanTypeString())
                )
                .findFirst()
                .ifPresent(fmtr -> lines.addAll(fmtr.formatBean(indent + stepIndent, stepIndent)));
        }
        //special processing for FunctionalInterface
        if (classInfo.isFunctionalInterface()) {
            Optional<MethodInfo> fnTargets = classInfo
                .getMethodInfos()
                .stream()
                .filter(MethodInfo::isAbstract)
                .findFirst();
            if (fnTargets.isPresent()) {
                FormatterMethod fnFormatter = new FormatterMethod(fnTargets.get());
                DocumentMethod doc = fnFormatter.document;
                lines.add(
                    String.format(
                        "%s(%s): %s;",
                        PUtil.indent(indent + stepIndent),
                        fnFormatter.formatParams(
                            CommentUtil.getRenames(doc == null ? null : doc.getComment()),
                            true
                        ),
                        fnFormatter.formatReturn()
                    )
                );
            }
        }

        // constructors
        if (!classInfo.isInterface()) {
            if (internal && !classInfo.getConstructorInfos().isEmpty()) {
                lines.addAll(
                    new FormatterComments("Internal constructor, not callable unless via `java()`.")
                        .setStyle(true)
                        .format(indent + stepIndent, stepIndent)
                );
            }
            classInfo
                .getConstructorInfos()
                .stream()
                .map(FormatterConstructor::new)
                .forEach(f -> lines.addAll(f.format(indent + stepIndent, stepIndent)));
        }
        // additions
        for (DocumentField fieldDoc : fieldAdditions) {
            lines.addAll(fieldDoc.format(indent + stepIndent, stepIndent));
        }
        for (DocumentMethod methodDoc : methodAdditions) {
            lines.addAll(methodDoc.format(indent + stepIndent, stepIndent));
        }
        //end
        lines.add(PUtil.indent(indent) + "}");
        //type conversion
        String origName = NameResolver.getResolvedName(classInfo.getName()).getLastName();
        String underName = origName + "_";
        if (NameResolver.specialTypeFormatters.containsKey(classInfo.getRaw())) {
            assignableTypes.add(
                FormatterType.of(
                    new TypeInfoParameterized(
                        new TypeInfoClass(classInfo.getRaw()),
                        classInfo.getTypeParameters()
                    )
                )
                    .format()
            );
        }
        List<TypeInfoVariable> params = classInfo.getTypeParameters();
        if (!params.isEmpty()) {
            String paramString = String.format(
                "<%s>",
                params.stream().map(ITypeInfo::getTypeName).collect(Collectors.joining(", "))
            );
            underName += paramString;
            origName += paramString;
        }

        assignableTypes.add(origName);
        lines.add(
            PUtil.indent(indent) +
            String.format("type %s = %s;", underName, String.join(" | ", assignableTypes))
        );
        return lines;
    }

    @Override
    public void addDocument(DocumentClass document) {
        super.addDocument(document);
        document
            .getFieldDocs()
            .forEach(documentField -> {
                if (fieldFormatters.containsKey(documentField.getName())) {
                    fieldFormatters.get(documentField.getName()).addDocument(documentField);
                } else {
                    fieldAdditions.add(documentField);
                }
            });

        document
            .getMethodDocs()
            .forEach(documentMethod -> {
                if (methodFormatters.containsKey(documentMethod.getName())) {
                    methodFormatters
                        .get(documentMethod.getName())
                        .forEach(formatterMethod -> {
                            if (documentMethod.testMethod(formatterMethod.getInfo())) {
                                formatterMethod.addDocument(documentMethod);
                            }
                        });
                } else {
                    methodAdditions.add(documentMethod);
                }
            });
    }
}
