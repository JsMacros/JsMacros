package xyz.wagyourtail.doclet.tsdoclet.parsers;

import com.sun.source.doctree.*;
import xyz.wagyourtail.StringHelpers;
import xyz.wagyourtail.doclet.DocletEnumType;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.tsdoclet.Main;
import xyz.wagyourtail.doclet.tsdoclet.parsers.ClassParser;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.lang.Deprecated;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public abstract class AbstractParser {
    static final public Set<String> javaShortifies = Set.of(
        "java.lang.Class",
        "java.lang.Array",
        "java.util.Collection",
        "java.util.List",
        "java.util.Set",
        "java.util.Map",
        "java.util.HashMap"
    );

    public Set<String> redirects = new HashSet<>();
    private static Set<String> loggedTypes = new HashSet<>();
    protected TypeElement type;

    public AbstractParser(TypeElement type) {
        this.type = type;
    }

    public String genFields(Set<Element> fields) {
        final StringBuilder s = new StringBuilder();
        for (Element field : fields) {
            if (!field.getModifiers().contains(Modifier.PUBLIC)) continue;
            if (field.getModifiers().contains(Modifier.STATIC)) continue;
            s.append(genField(field)).append("\n");
        }
        return s.toString();
    }

    public String genStaticFields(Set<Element> fields) {
        final StringBuilder s = new StringBuilder();
        for (Element field : fields) {
            if (!field.getModifiers().contains(Modifier.PUBLIC)) continue;
            if (!field.getModifiers().contains(Modifier.STATIC)) continue;
            s.append(genField(field)).append("\n");
        }
        return s.toString();
    }

    public String genMethods(Set<Element> methods) {
        final StringBuilder s = new StringBuilder();
        for (Element method : methods) {
            if (!method.getModifiers().contains(Modifier.PUBLIC)) continue;
            if (method.getModifiers().contains(Modifier.STATIC)) continue;
            s.append(genMethod((ExecutableElement) method)).append("\n");
        }
        return s.toString();
    }

    public String genStaticMethods(Set<Element> methods) {
        final StringBuilder s = new StringBuilder();
        for (Element method : methods) {
            if (!method.getModifiers().contains(Modifier.PUBLIC)) continue;
            if (!method.getModifiers().contains(Modifier.STATIC)) continue;
            s.append(genMethod((ExecutableElement) method)).append("\n");
        }
        return s.toString();
    }

    public String genConstructors(Set<Element> methods) {
        final StringBuilder s = new StringBuilder();
        for (Element method : methods) {
            if (!method.getModifiers().contains(Modifier.PUBLIC)) continue;
            s.append(genConstructor((ExecutableElement) method)).append("\n");
        }
        return s.toString();
    }

    public String genField(Element field) {
        DocletReplaceReturn replace = field.getAnnotation(DocletReplaceReturn.class);

        return genComment(field) + (field.getModifiers().contains(Modifier.FINAL) ? "readonly " : "") +
            field.getSimpleName() + ": " + (replace != null ? replace.value() : shortify(field)) + ";";
    }

    public String genMethod(ExecutableElement method) {
        final StringBuilder s = new StringBuilder();
        s.append(genComment(method));
        s.append(method.getSimpleName());

        DocletReplaceParams docletReplace = method.getAnnotation(DocletReplaceParams.class);
        String replace = (docletReplace != null ? docletReplace.value() : null);
        //diamondOperator
        List<? extends TypeParameterElement> typeParams = method.getTypeParameters();
        if (typeParams != null && !typeParams.isEmpty()) {
            s.append("<");
            for (TypeParameterElement param : typeParams) {
                s.append(shortify(param)).append(", ");
            }
            s.setLength(s.length() - 2);
            if (replace != null && replace.startsWith("<E> ")) {
                s.append(", E extends keyof Events");
                replace = replace.substring(4);
            }
            s.append(">");
        }else if (replace != null && replace.startsWith("<E> ")) {
            s.append("<E extends keyof Events>");
            replace = replace.substring(4);
        }
        s.append("(");
        if (replace != null) {
           s.append(replace);
        } else {
            List<? extends VariableElement> params = method.getParameters();
            if (params != null && !params.isEmpty()) {
                for (VariableElement param : params) {
                    s.append(param.getSimpleName()).append(": ").append(shortify(param)).append(", ");
                }
                s.setLength(s.length() - 2);
            }
        }
        s.append("): ");
        DocletReplaceReturn replace2 = method.getAnnotation(DocletReplaceReturn.class);
        if (replace2 != null) {
            s.append(replace2.value());
        } else {
            s.append(transformType(method.getReturnType(), true));
        }
        s.append(";");

        return s.toString();
    }

    public String genConstructor(ExecutableElement constructor) {
        final StringBuilder s = new StringBuilder();
        s.append(genComment(constructor));
        s.append("new ");

        //diamondOperator
        List<? extends TypeParameterElement> typeParams = type.getTypeParameters();
        if (typeParams != null && !typeParams.isEmpty()) {
            s.append("<");
            for (TypeParameterElement param : typeParams) {
                s.append(shortify(param)).append(", ");
            }
            s.setLength(s.length() - 2);
            s.append(">");
        }
        s.append("(");
        DocletReplaceParams replace = constructor.getAnnotation(DocletReplaceParams.class);
        List<? extends VariableElement> params = constructor.getParameters();
        if (replace != null) {
            s.append(replace.value());
        }else if (params != null && !params.isEmpty()) {
            for (VariableElement param : params) {
                s.append(param.getSimpleName()).append(": ").append(shortify(param)).append(", ");
            }
            s.setLength(s.length() - 2);
        }
        s.append("): ").append(shortify(type)).append(";");
        return s.toString();
    }

    public String transformType(TypeMirror type) {
        return transformType(type, false);
    }

    public String transformType(TypeMirror type, boolean shortify) {
        switch (type.getKind()) {
            case BOOLEAN -> {
                return "boolean";
            }
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, CHAR -> {
                return "number";
            }
            case VOID, NONE -> {
                return "void";
            }
            case DECLARED -> {
                Element typeElement = ((DeclaredType) type).asElement();
                StringBuilder rawType = new StringBuilder(typeElement.getSimpleName().toString());
                typeElement = typeElement.getEnclosingElement();
                while (typeElement.getKind() == ElementKind.CLASS || typeElement.getKind() == ElementKind.INTERFACE) {
                    rawType.insert(0, typeElement.getSimpleName().toString() + "$");
                    typeElement = typeElement.getEnclosingElement();
                }

                boolean shortified = false;
                String classpath = ((PackageElement) typeElement).getQualifiedName() + "";
                if (shortify && classpath.startsWith("xyz.")) {
                    if (classpath.equals("xyz.wagyourtail.jsmacros.core.event") &&
                        rawType.toString().equals("BaseEvent")) return "Events.BaseEvent";
                    shortified = true;
                    if (redirects.contains(rawType.toString())) {
                        Main.redirectNeeded.add(rawType.toString());
                        rawType.insert(0, "$");
                    }
                }else if (shortify && javaShortifies.contains(classpath + "." + rawType.toString())) {
                    shortified = true;
                    rawType.insert(0, "Java");
                }else rawType.insert(0, classpath + ".");

                List<? extends TypeMirror> params = ((DeclaredType) type).getTypeArguments();
                if (params != null && !params.isEmpty()) {
                    boolean skipAny = true;
                    if (shortified) for (TypeMirror param : params) {
                        if (!transformType(param).endsWith("any")) {
                            skipAny = false;
                            break;
                        }
                    }
                    if (!shortified || !skipAny) {
                        rawType.append("<");
                        for (TypeMirror param : params) {
                            rawType.append(transformType(param, shortify)).append(", ");
                        }
                        rawType.setLength(rawType.length() - 2);
                        rawType.append(">");
                    }
                }

                if (rawType.toString().startsWith("net.minecraft")) return "/* minecraft classes, as any, because obfuscation: */ any";
                AnnotationMirror mirror = type.getAnnotationMirrors().stream().filter(e -> e.getAnnotationType().asElement().getSimpleName().toString().equals("Event")).findFirst().orElse(null);

                if (mirror != null) {
                    return "Events." + Main.getAnnotationValue("value", mirror);
                }

                mirror = type.getAnnotationMirrors().stream().filter(e -> e.getAnnotationType().asElement().getSimpleName().toString().equals("Library")).findFirst().orElse(null);

                if (mirror != null) {
                    return Main.getAnnotationValue("value", mirror).toString();
                }

                if (rawType.toString().equals("xyz.wagyourtail.jsmacros.core.event.BaseEvent")) return "Events.BaseEvent";

                if (rawType.toString().startsWith("java.lang")) {
                    if (List.of("java.lang.Integer", "java.lang.Float", "java.lang.Long", "java.lang.Short", "java.lang.Character", "java.lang.Byte", "java.lang.Double").contains(rawType.toString())) {
                        return "number";
                    }

                    if ("java.lang.Boolean".equals(rawType.toString())) {
                        return "boolean";
                    }

                    if ("java.lang.String".equals(rawType.toString())) {
                        return "string";
                    }

                    if ("java.lang.Object".equals(rawType.toString())) {
                        return "any";
                    }

                    Main.classes.addClass(((DeclaredType) type).asElement());
                    return "_javatypes." + rawType.toString();
                } else {
                    Main.classes.addClass(((DeclaredType) type).asElement());
                    return (shortified || rawType.substring(0, 4).equals("xyz.") ? "" : "_javatypes.") +
                        rawType.toString().replace(".function.", "._function.");
                }
            }
            case TYPEVAR -> {
                return ((TypeVariable) type).asElement().getSimpleName().toString();
            }
            case ARRAY -> {
                return transformType(((ArrayType) type).getComponentType(), shortify) + "[]";
            }
            case WILDCARD -> {
                return "any";
            }
        }
        throw new UnsupportedOperationException(String.valueOf(type.getKind()));
    }

    public String genComment(Element comment) {
        checkEnumType(comment);

        DocCommentTree tree = Main.treeUtils.getDocCommentTree(comment);
        Deprecated dep = comment.getAnnotation(Deprecated.class);
        if (tree == null) {
            return dep != null ? "\n/** @deprecated */\n" : "";
        }
        final StringBuilder s = new StringBuilder();
        for (DocTree docTree : tree.getFullBody()) {
            switch (docTree.getKind()) {
                case LINK, LINK_PLAIN -> {
                    String referenceString = ((LinkTree) docTree).getReference().getSignature().split("\\(", 2)[0];
                    s.append("{@link ");
                    if (referenceString.startsWith("#") || !referenceString.contains(".")) {
                        s.append(referenceString);
                    } else {
                        if (!referenceString.startsWith("xyz.")) s.append("_javatypes.");
                        s.append(
                            referenceString
                            .replace(".function.", "._function.")
                        );
                    }
                    s.append("}");
                }
                case CODE -> s.append("`").append(((LiteralTree)docTree).getBody()).append("`");
                default -> s.append(docTree);
            }
        }

        for (DocTree blockTag : tree.getBlockTags()) {
            if (blockTag.getKind() == DocTree.Kind.SEE) {
                List<? extends DocTree> sees = ((SeeTree) blockTag).getReference();
                for (DocTree see : sees) {
                    if (see.getKind() == DocTree.Kind.REFERENCE) {
                        s.append("\n@see ").append(((ReferenceTree) see).getSignature());
                    } else {
                        s.append("\n@see ").append(see);
                    }
                }
            } else {
                s.append("\n").append(blockTag);
            }
        }
        if (dep != null && !s.toString().contains("@deprecated")) s.append("\n@deprecated");
        return "\n/**\n" +
            StringHelpers.addToLineStarts(s.toString(), " * ") +
            "\n */\n";
    }

    public abstract String genTSInterface();

    public TypeElement getType() {
        return type;
    }

    public String getTypeString() {
        return transformType(type.asType());
    }

    public String getShortifiedType() {
        return shortify(type);
    }

    public String shortify(Element type) {
        return transformType(type.asType(), true);
    }

    public static void checkEnumType(Element element) {
        DocletEnumType enumType = element.getAnnotation(DocletEnumType.class);
        if (enumType != null) {
            if (Main.enumTypes.containsKey(enumType.name()) &&
                !loggedTypes.contains(enumType.name()) &&
                Main.enumTypes.get(enumType.name()) != enumType.type()) {
                System.out.println("Duplicate enum type name: " + enumType.name());
                loggedTypes.add(enumType.name());
            }
            Main.enumTypes.put(enumType.name(), enumType.type());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractParser that)) return false;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
