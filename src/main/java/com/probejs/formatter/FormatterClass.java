package com.probejs.formatter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.probejs.ProbeJS;
import com.probejs.document.DocManager;
import com.probejs.document.DocumentClass;
import com.probejs.document.DocumentField;
import com.probejs.document.DocumentMethod;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.type.IDocType;
import com.probejs.formatter.api.DocumentReceiver;
import com.probejs.formatter.api.MultiFormatter;
import com.probejs.formatter.resolver.NameResolver;
import com.probejs.formatter.resolver.SpecialTypes;
import com.probejs.info.clazz.ClassInfo;
import com.probejs.info.clazz.FieldInfo;
import com.probejs.info.clazz.MethodInfo;
import com.probejs.info.type.*;
import com.probejs.util.PUtil;
import lombok.Setter;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

public class FormatterClass extends DocumentReceiver<DocumentClass> implements MultiFormatter {

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
        for (MethodInfo methodInfo : classInfo.getMethods()) {
            methodFormatters.put(methodInfo.getName(), new FormatterMethod(methodInfo));
        }
        this.fieldFormatters = new HashMap<>();
        for (FieldInfo fieldInfo : classInfo.getFields()) {
            fieldFormatters.put(fieldInfo.getName(), new FormatterField(fieldInfo));
        }
    }

    /**
     * similar to {@code new FormatterType(info,false).format(0,4)}, but with additional
     * processing for TypeClass. If its getTypeVariables() is not returning an empty
     * list, a {@code <a,b,...>} style type variable representation will be added to
     * the end of formatted string
     */
    public static String formatParameterized(IType info) {
        return FormatterType.of(info, false)
            .format()
            .concat(info instanceof TypeClass clazz ? SpecialTypes.attachedTypeVar(clazz) : "");
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        val lines = new ArrayList<String>();
        val comment = document == null ? null : document.getComment();
        if (comment != null) {
            if (CommentUtil.isHidden(comment)) {
                return lines;
            }
            lines.addAll(comment.formatLines(indent, stepIndent));
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
                        .map(java.lang.reflect.TypeVariable::getName)
                        .collect(Collectors.joining(", "))
                )
            );
        }
        // super class
        if (classInfo.getSuperClass() != null) {
            firstLine.add("extends");
            val superC = classInfo.getSuperClass().getRaw() == Object.class
                ? "Document.Object"
                : formatParameterized(TypeResolver.resolveType(classInfo.getRaw().getGenericSuperclass()));
            firstLine.add(superC);
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

        // additions
        for (DocumentField fieldDoc : fieldAdditions) {
            lines.addAll(fieldDoc.formatLines(indent + stepIndent, stepIndent));
        }
        for (DocumentMethod methodDoc : methodAdditions) {
            lines.addAll(methodDoc.formatLines(indent + stepIndent, stepIndent));
        }
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
            .forEach(fmtrMethod -> lines.addAll(fmtrMethod.formatLines(indent + stepIndent, stepIndent)));
        //fields
        fieldFormatters
            .entrySet()
            .stream()
            .filter(e -> !methodFormatters.containsKey(e.getKey()))
            .filter(f -> !(classInfo.isInterface() && f.getValue().getFInfo().isStatic() && internal))
            .forEach(f -> {
                f.getValue().setFromInterface(classInfo.isInterface());
                lines.addAll(f.getValue().formatLines(indent + stepIndent, stepIndent));
            });

        // beans
        if (!classInfo.isInterface()) {
            val getters = new HashMap<String, FormatterMethod>();
            ListMultimap<String, FormatterMethod> setters = ArrayListMultimap.create();

            for (val m : methodFormatters.values()) {
                val beanName = m.getBeanedName();
                if (
                    beanName == null ||
                        !Character.isAlphabetic(beanName.charAt(0)) ||
                        fieldFormatters.containsKey(beanName) ||
                        methodFormatters.containsKey(beanName)
                ) {
                    continue;
                }
                if (m.isGetter()) {
                    getters.put(beanName, m);
                } else {
                    setters.put(beanName, m);
                }
            }

            for (val getter: getters.values()) {
                lines.addAll(getter.formatBean(indent+stepIndent, stepIndent));
            }
            for (val setter: setters.values()) {
                lines.addAll(setter.formatBean(indent+stepIndent, stepIndent));
            }
        }

        // constructors
        if (!classInfo.isInterface()) {
            if (internal && !classInfo.getConstructors().isEmpty()) {
                lines.addAll(
                    new FormatterComments("Internal constructor, not callable unless via `java()`.")
                        .setStyle(FormatterComments.CommentStyle.J_DOC)
                        .formatLines(indent + stepIndent, stepIndent)
                );
            }
            classInfo
                .getConstructors()
                .stream()
                .map(FormatterConstructor::new)
                .forEach(f -> lines.addAll(f.formatLines(indent + stepIndent, stepIndent)));
        }
        //end
        lines.add(PUtil.indent(indent) + "}");

        //type conversion
        String origName = NameResolver.getResolvedName(classInfo.getName()).getLastName();
        String underName = origName + "_";
        val params = classInfo.getTypeParameters();
        if (!params.isEmpty()) {
            val paramString = String.format(
                "<%s>",
                params.stream().map(IType::getTypeName).collect(Collectors.joining(", "))
            );
            underName += paramString;
            origName += paramString;
        }

        val assignables = new ArrayList<String>();
        assignables.add(origName);
        DocManager.typesAssignable
            .getOrDefault(classInfo.getRaw().getName(), Collections.emptyList())
            .stream()
            .map(t -> t.transform(IDocType.defaultTransformer))
            .forEach(assignables::add);
        if (NameResolver.specialTypeFormatters.containsKey(classInfo.getRaw())) {
            assignables.add(
                FormatterType.of(
                    new TypeParameterized(
                        new TypeClass(classInfo.getRaw()),
                        classInfo.getTypeParameters()
                    )
                )
                    .format()
            );
        }

        lines.add(String.format(
            "%stype %s = %s;",
            PUtil.indent(indent),
            underName,
            String.join(" | ", assignables)
        ));
        return lines;
    }

    @Override
    public void addDocument(DocumentClass document) {
        super.addDocument(document);
        for (val documentField : document.getFieldDocs()) {
            if (fieldFormatters.containsKey(documentField.getName())) {
                fieldFormatters.get(documentField.getName()).addDocument(documentField);
            } else {
                fieldAdditions.add(documentField);
            }
        }

        for (val documentMethod : document.getMethodDocs()) {
            if (methodFormatters.containsKey(documentMethod.getName())) {
                methodFormatters
                    .get(documentMethod.getName())
                    .stream()
                    .filter(formatterMethod -> documentMethod.testMethod(formatterMethod.getInfo()))
                    .forEach(formatterMethod -> formatterMethod.addDocument(documentMethod));
            } else {
                methodAdditions.add(documentMethod);
            }
        }
    }
}
