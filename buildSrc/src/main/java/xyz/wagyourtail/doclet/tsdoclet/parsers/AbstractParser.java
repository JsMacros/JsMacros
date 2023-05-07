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
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;

public abstract class AbstractParser {
    static final public Set<String> javaShortifies = Set.of(
        "java.lang.Class",
        "java.lang.Array",
        "java.util.Collection",
        "java.util.List",
        "java.util.Set",
        "java.util.Map"
    );
    static final public Map<String, String> javaNumberType = Map.of(
        "java.lang.Integer",   "int",
        "java.lang.Float",     "float",
        "java.lang.Long",      "long",
        "java.lang.Short",     "short",
        "java.lang.Character", "char",
        "java.lang.Byte",      "byte",
        "java.lang.Double",    "double"
    );

    private static Set<String> loggedTypes = new HashSet<>();
    private String path;
    protected TypeElement type;
    public boolean isPackage = true;

    public static TypeElement objectElement = null;
    public static Set<ExecutableElement> objectMethods = null;
    public static Set<Name> objectMethodNames = null;

    public static void initObjectElement() {
        objectElement = Main.elementUtils.getTypeElement("java.lang.Object");
        objectMethods = new HashSet<>();
        for (Element oel : objectElement.getEnclosedElements()) {
            if (oel.getKind() != ElementKind.METHOD) continue;
            if (!oel.getModifiers().contains(Modifier.PUBLIC)) continue;
            if (oel.getModifiers().contains(Modifier.STATIC)) continue;
            objectMethods.add((ExecutableElement) oel);
        }
        objectMethodNames = new HashSet<>();
        for (Element m : objectMethods) objectMethodNames.add(m.getSimpleName());
    }

    public AbstractParser(TypeElement type) {
        this.type = type;
        Element elem = type.getEnclosingElement();
        while (!(elem instanceof PackageElement)) elem = elem.getEnclosingElement();
        this.path = ((PackageElement) elem).getQualifiedName().toString();
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
        StringBuilder s = new StringBuilder();
        s.append(genComment(field));

        if (field.getModifiers().contains(Modifier.FINAL)) s.append("readonly ");
        s.append(field.getSimpleName()).append(": ");

        DocletReplaceReturn replace = field.getAnnotation(DocletReplaceReturn.class);
        if (replace != null) {
            s.append(replace.value());
        } else {
            s.append(transformType(field));
            if (isNullable(field)) s.append(" | null");
        }

        s.append(";");

        return s.toString();
    }

    public String genMethod(ExecutableElement method) {
        return genExecutable(method, false);
    }

    public String genConstructor(ExecutableElement constructor) {
        return genExecutable(constructor, true);
    }

    public String genExecutable(ExecutableElement e, boolean isConstructor) {
        final StringBuilder s = new StringBuilder();
        s.append(genComment(e));
        s.append(isConstructor ? "new " : e.getSimpleName());

        // diamondOperator
        DocletReplaceTypeParams replace = e.getAnnotation(DocletReplaceTypeParams.class);
        if (replace != null) {
            if (replace.value().length() > 0) s.append("<").append(replace.value()).append(">");
        } else {
            List<? extends TypeParameterElement> typeParams = isConstructor ? type.getTypeParameters() : e.getTypeParameters();
            if (typeParams != null && !typeParams.isEmpty()) {
                s.append("<");
                for (TypeParameterElement param : typeParams) {
                    s.append(transformType(param));
                    String ext = transformType(((TypeVariable) param.asType()).getUpperBound());
                    if (!ext.equals("any")) {
                        s.append(" extends ").append(ext);
                    }
                    s.append(", ");
                }
                s.setLength(s.length() - 2);
                s.append(">");
            }
        }

        s.append("(");
        DocletReplaceParams replace2 = e.getAnnotation(DocletReplaceParams.class);
        if (replace2 != null) {
            s.append(replace2.value());
        } else {
            List<? extends VariableElement> params = e.getParameters();
            if (params != null && !params.isEmpty()) {
                VariableElement restParam = e.isVarArgs() ? params.get(params.size() - 1) : null;
                for (VariableElement param : params) {
                    if (restParam != null && restParam.equals(param)) s.append("...");
                    s.append(param.getSimpleName()).append(": ")
                        .append(transformType(param, true));
                    if (isNullable(param)) s.append(" | null");
                    s.append(", ");
                }
                s.setLength(s.length() - 2);
            }
        }
        s.append("): ");

        DocletReplaceReturn replace3 = e.getAnnotation(DocletReplaceReturn.class);
        if (replace3 != null) {
            transformType(e.getReturnType()); // to add type to the Packages
            s.append(replace3.value());
        } else {
            s.append(transformType(isConstructor ? type.asType() : e.getReturnType()));
            if (isNullable(e)) s.append(" | null");
        }
        s.append(";");

        return s.toString();
    }

    public String transformType(Element elem) {
        return transformType(elem.asType(), false);
    }

    public String transformType(Element elem, boolean isParamType) {
        return transformType(elem.asType(), isParamType);
    }

    public String transformType(TypeMirror type) {
        return transformType(type, false);
    }

    public String transformType(TypeMirror type, boolean isParamType) {
        switch (type.getKind()) {
            case BOOLEAN -> {
                return "boolean";
            }
            case BYTE -> {
                return "byte";
            }
            case SHORT -> {
                return "short";
            }
            case INT -> {
                return "int";
            }
            case LONG -> {
                return "long";
            }
            case FLOAT -> {
                return "float";
            }
            case DOUBLE -> {
                return "double";
            }
            case CHAR -> {
                return "char";
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
                String classpath = ((PackageElement) typeElement).getQualifiedName().toString();
                if (javaShortifies.contains(classpath + "." + rawType.toString())) {
                    shortified = true;
                    rawType.insert(0, "Java");
                    if (isParamType && rawType.toString().equals("JavaClass")) rawType.append("Arg");
                } else if (isPackage && classpath.equals(this.path)) {
                    shortified = true;
                } else rawType.insert(0, classpath + ".");

                List<? extends TypeMirror> params = ((DeclaredType) type).getTypeArguments();
                if (params != null && !params.isEmpty()) {
                    rawType.append("<");
                    for (TypeMirror param : params) {
                        rawType.append(transformType(param, isParamType)).append(", ");
                    }
                    rawType.setLength(rawType.length() - 2);
                    rawType.append(">");
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
                    if (javaNumberType.containsKey(rawType.toString())) {
                        return isParamType ? javaNumberType.get(rawType.toString()) : "number";
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
                    return rawType.toString();
                }
            }
            case TYPEVAR -> {
                return ((TypeVariable) type).asElement().getSimpleName().toString();
            }
            case ARRAY -> {
                String component = transformType(((ArrayType) type).getComponentType(), isParamType);
                return isParamType ? component + "[]" : "JavaArray<" + component + ">";
            }
            case WILDCARD -> {
                return "any";
            }
            case INTERSECTION -> {
                StringBuilder s = new StringBuilder("(");
                for (TypeMirror t : ((IntersectionType) type).getBounds()) {
                    s.append(transformType(t, isParamType)).append(" & ");
                }
                s.setLength(s.length() - 3);
                s.append(")");
                return s.toString();
            }
            case UNION -> {
                StringBuilder s = new StringBuilder("(");
                for (TypeMirror t : ((UnionType) type).getAlternatives()) {
                    s.append(transformType(t, isParamType)).append(" | ");
                }
                s.setLength(s.length() - 3);
                s.append(")");
                return s.toString();
            }
        }
        throw new UnsupportedOperationException(String.valueOf(type.getKind()));
    }

    public String genComment(Element comment) {
        checkEnumType(comment);

        DocCommentTree tree = Main.treeUtils.getDocCommentTree(comment);
        if (tree == null) {
            return Main.elementUtils.isDeprecated(comment) ? "/** @deprecated */\n" : "";
        }
        final StringBuilder a = new StringBuilder();
        final StringBuilder b = new StringBuilder();
        for (DocTree docTree : tree.getFullBody()) {
            switch (docTree.getKind()) {
                case LINK, LINK_PLAIN -> {
                    a.append("{@link ")
                        .append(((LinkTree) docTree).getReference().getSignature().split("\\(", 2)[0])
                    .append("}");
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

        String fin = (a.toString().replaceAll("(?<=[\\.,:;>]) ?\n", "  \n") + b.toString()).trim()
            .replaceAll("\n <p>", "\n")
            .replaceAll("<\\/?pre>", "```")
            .replaceAll("<a (?:\n|.)*?href=\"([^\"]*)\"(?:\n|.)*?>((?:\n|.)*?)</a>", "[$2]($1)")
            .replaceAll("\n@param <\\w>(?! )", "")
            .replaceAll("&lt;", "<")
            .replaceAll("&gt;", ">");
        if (fin.isBlank()) return Main.elementUtils.isDeprecated(comment) ? "/** @deprecated */\n" : "";

        if (Main.elementUtils.isDeprecated(comment) && !b.toString().contains("@deprecated")) {
            fin += "\n@deprecated";
        }

        if (fin.startsWith("@since") && !fin.contains("\n")) return "/** " + fin + " */\n";

        return ("\n/**\n" +
            StringHelpers.addToLineStarts(fin, " * ") +
            "\n */\n").replaceAll("\n \\* +\n", "\n *\n");
    }

    public abstract String genTSInterface();

    public TypeElement getType() {
        return type;
    }

    public String getQualifiedType() {
        return isPackage ? this.path + "." + transformType(type) : transformType(type);
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

    protected boolean isObjectMethod(Element m) {
        if (!objectMethodNames.contains(m.getSimpleName())
        ||  m.getKind() != ElementKind.METHOD
        ||  Main.treeUtils.getDocCommentTree(m) != null) return false;
        for (ExecutableElement om : objectMethods) {
            if (Main.elementUtils.overrides((ExecutableElement) m, om, type)) return true;
        }
        return false;
    }

    public boolean isNullable(Element e) {
        for (AnnotationMirror annotationMirror : e.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().asElement().getSimpleName().toString().equals("Nullable")) {
                return true;
            }
        }
        return false;
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
