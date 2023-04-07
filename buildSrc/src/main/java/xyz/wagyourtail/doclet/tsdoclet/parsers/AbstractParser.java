package xyz.wagyourtail.doclet.tsdoclet.parsers;

import com.sun.source.doctree.*;
import xyz.wagyourtail.StringHelpers;
import xyz.wagyourtail.doclet.DocletEnumType;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.DocletReplaceTypeParams;
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
    static final public Set<String> javaNumberType = Set.of(
        "java.lang.Integer",
        "java.lang.Float",
        "java.lang.Long",
        "java.lang.Short",
        "java.lang.Character",
        "java.lang.Byte",
        "java.lang.Double"
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

        // diamondOperator
        DocletReplaceTypeParams replace = method.getAnnotation(DocletReplaceTypeParams.class);
        if (replace != null) {
           s.append("<").append(replace.value()).append(">");
        } else {
            List<? extends TypeParameterElement> typeParams = method.getTypeParameters();
            if (typeParams != null && !typeParams.isEmpty()) {
                s.append("<");
                for (TypeParameterElement param : typeParams) {
                    s.append(shortify(param)).append(", ");
                }
                s.setLength(s.length() - 2);
                s.append(">");
            }
        }

        s.append("(");
        DocletReplaceParams replace2 = method.getAnnotation(DocletReplaceParams.class);
        if (replace2 != null) {
           s.append(replace2.value());
        } else {
            List<? extends VariableElement> params = method.getParameters();
            if (params != null && !params.isEmpty()) {
                for (VariableElement param : params) {
                    if (isRestParameter(param)) s.append("...");
                    s.append(param.getSimpleName()).append(": ").append(shortify(param)).append(", ");
                }
                s.setLength(s.length() - 2);
            }
        }
        s.append("): ");
        DocletReplaceReturn replace3 = method.getAnnotation(DocletReplaceReturn.class);
        if (replace3 != null) {
            s.append(replace3.value());
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

        // diamondOperator
        DocletReplaceTypeParams replace = constructor.getAnnotation(DocletReplaceTypeParams.class);
        if (replace != null) {
           s.append("<").append(replace.value()).append(">");
        } else {
            List<? extends TypeParameterElement> typeParams = type.getTypeParameters();
            if (typeParams != null && !typeParams.isEmpty()) {
                s.append("<");
                for (TypeParameterElement param : typeParams) {
                    s.append(shortify(param)).append(", ");
                }
                s.setLength(s.length() - 2);
                s.append(">");
            }
        }

        s.append("(");
        DocletReplaceParams replace2 = constructor.getAnnotation(DocletReplaceParams.class);
        List<? extends VariableElement> params = constructor.getParameters();
        if (replace2 != null) {
            s.append(replace2.value());
        } else if (params != null && !params.isEmpty()) {
            for (VariableElement param : params) {
                s.append(param.getSimpleName()).append(": ").append(shortify(param)).append(", ");
            }
            s.setLength(s.length() - 2);
        }
        s.append("): ").append(getShortifiedType()).append(";");
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
                } else if (shortify && javaShortifies.contains(classpath + "." + rawType.toString())) {
                    shortified = true;
                    rawType.insert(0, "Java");
                } else rawType.insert(0, classpath + ".");

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

                if (rawType.toString().startsWith("net.minecraft")) return "/* minecraft class */ any";
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
                    if (javaNumberType.contains(rawType.toString())) {
                        return "number";
                    }

                    switch (rawType.toString()) {
                        case "java.lang.Boolean" -> { return "boolean"; }
                        case "java.lang.String"  -> { return "string";  }
                        case "java.lang.Object"  -> { return "any";     }
                    }

                    Main.classes.addClass(((DeclaredType) type).asElement());
                    return rawType.insert(0, "Packages.").toString();
                } else {
                    Main.classes.addClass(((DeclaredType) type).asElement());
                    if (!shortified) rawType.insert(0, "Packages.");
                    return rawType.toString().replace(".function.", "._function.");
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
            return dep != null ? "/** @deprecated */\n" : "";
        }
        final StringBuilder a = new StringBuilder();
        final StringBuilder b = new StringBuilder();
        for (DocTree docTree : tree.getFullBody()) {
            switch (docTree.getKind()) {
                case LINK, LINK_PLAIN -> {
                    String referenceString = ((LinkTree) docTree).getReference().getSignature().split("\\(", 2)[0];
                    a.append("{@link ");
                    if (referenceString.startsWith("#") || !referenceString.contains(".")) {
                        a.append(referenceString);
                    } else {
                        a.append(
                            referenceString
                            .replace(".function.", "._function.")
                        );
                    }
                    a.append("}");
                }
                case CODE -> a.append("`").append(((LiteralTree)docTree).getBody()).append("`");
                default -> a.append(docTree);
            }
        }

        for (DocTree blockTag : tree.getBlockTags()) {
            if (blockTag.getKind() == DocTree.Kind.SEE) {
                List<? extends DocTree> sees = ((SeeTree) blockTag).getReference();
                for (DocTree see : sees) {
                    if (see.getKind() == DocTree.Kind.REFERENCE) {
                        b.append("\n@see ").append(((ReferenceTree) see).getSignature());
                    } else {
                        b.append("\n@see ").append(see);
                    }
                }
            } else {
                b.append("\n").append(blockTag);
            }
        }

        if (dep != null && !b.toString().contains("@deprecated")) b.append("\n@deprecated");

        return ("\n/**\n" +
            StringHelpers.addToLineStarts(
                a.toString().replaceAll("<\\/?pre>", "```").replaceAll("(?<=[\\.,:;>]) ?\n", "  \n") +
                b.toString()
            , " * ") +
            "\n */\n").replaceAll("\n \\* +\n", "\n *\n");
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

    private static boolean isRestParameter(VariableElement v) { // by chatGPT
        // Get the enclosing executable element
        ExecutableElement executableElement = (ExecutableElement) v.getEnclosingElement();

        // Check if the variable element is a varargs parameter
        return executableElement.isVarArgs() &&
            executableElement.getParameters().indexOf(v) == executableElement.getParameters().size() - 1;
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
