package xyz.wagyourtail.doclet.tsdoclet.parsers;

import com.sun.source.doctree.*;
import xyz.wagyourtail.StringHelpers;
import xyz.wagyourtail.doclet.DocletEnumType;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.DocletReplaceTypeParams;
import xyz.wagyourtail.doclet.tsdoclet.Main;

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
        "java.lang.Double",    "double",
        "java.lang.Number",    "number"
    );

    private static final Set<String> loggedTypes = new HashSet<>();
    private final String path;
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
            if (!field.getModifiers().contains(Modifier.PUBLIC)) {
                continue;
            }
            if (field.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }
            s.append(genField(field)).append("\n");
        }
        return s.toString();
    }

    public String genStaticFields(Set<Element> fields) {
        final StringBuilder s = new StringBuilder();
        for (Element field : fields) {
            if (!field.getModifiers().contains(Modifier.PUBLIC)) {
                continue;
            }
            if (!field.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }
            s.append(genField(field)).append("\n");
        }
        return s.toString();
    }

    public String genMethods(Set<Element> methods) {
        final StringBuilder s = new StringBuilder();
        for (Element method : methods) {
            if (!method.getModifiers().contains(Modifier.PUBLIC)) {
                continue;
            }
            if (method.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }
            s.append(genMethod((ExecutableElement) method)).append("\n");
        }
        return s.toString();
    }

    public String genStaticMethods(Set<Element> methods) {
        final StringBuilder s = new StringBuilder();
        for (Element method : methods) {
            if (!method.getModifiers().contains(Modifier.PUBLIC)) {
                continue;
            }
            if (!method.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }
            s.append(genMethod((ExecutableElement) method)).append("\n");
        }
        return s.toString();
    }

    public String genConstructors(Set<Element> methods) {
        final StringBuilder s = new StringBuilder();
        for (Element method : methods) {
            if (!method.getModifiers().contains(Modifier.PUBLIC)) {
                continue;
            }
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
            if (!replace.value().isEmpty()) s.append("<").append(replace.value()).append(">");
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
        return transformType(elem.asType(), false, false);
    }

    public String transformType(Element elem, boolean isParamType) {
        return transformType(elem.asType(), isParamType, false);
    }

    public String transformType(Element elem, boolean isParamType, boolean isExtends) {
        return transformType(elem.asType(), isParamType, isExtends);
    }

    public String transformType(TypeMirror type) {
        return transformType(type, false, false);
    }

    public String transformType(TypeMirror type, boolean isParamType) {
        return transformType(type, isParamType, false);
    }

    public String transformType(TypeMirror type, boolean isParamType, boolean isExtends) {
        switch (type.getKind()) {
            case BOOLEAN -> {
                return "boolean";
            }
            case BYTE -> {
                return isParamType ? "byte" : "number";
            }
            case SHORT -> {
                return isParamType ? "short" : "number";
            }
            case INT -> {
                return isParamType ? "int" : "number";
            }
            case LONG -> {
                return isParamType ? "long" : "number";
            }
            case FLOAT -> {
                return isParamType ? "float" : "number";
            }
            case DOUBLE -> {
                return isParamType ? "double" : "number";
            }
            case CHAR -> {
                return isParamType ? "char" : "number";
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
                if (javaShortifies.contains(classpath + "." + rawType)) {
                    shortified = true;
                    rawType.insert(0, "Java");
                    if (isParamType && rawType.toString().equals("JavaClass")) rawType.append("Arg");
                } else rawType.insert(0, classpath + ".");

                List<? extends TypeMirror> params = ((DeclaredType) type).getTypeArguments();
                if (params != null && !params.isEmpty()) {
                    rawType.append("<");
                    for (TypeMirror param : params) {
                        rawType.append(transformType(param, isParamType, isExtends)).append(", ");
                    }
                    rawType.setLength(rawType.length() - 2);
                    rawType.append(">");
                }

                if (rawType.toString().startsWith("net.minecraft")) {
                    return "/* " + rawType.toString().replaceAll("/\\* ", "").replaceAll(" \\*/(?: any)?", "") + " */ any";
                }

                AnnotationMirror mirror = type.getAnnotationMirrors().stream().filter(e -> e.getAnnotationType().asElement().getSimpleName().toString().equals("Event")).findFirst().orElse(null);
                if (mirror != null) {
                    return "Events." + Main.getAnnotationValue("value", mirror);
                }

                mirror = type.getAnnotationMirrors().stream().filter(e -> e.getAnnotationType().asElement().getSimpleName().toString().equals("Library")).findFirst().orElse(null);
                if (mirror != null) {
                    return Main.getAnnotationValue("value", mirror).toString();
                }

                if (rawType.toString().equals("xyz.wagyourtail.jsmacros.core.event.BaseEvent")) {
                    return "Events.BaseEvent";
                }

                Main.classes.addClass(((DeclaredType) type).asElement());
                if (!isExtends && rawType.toString().startsWith("java.lang")) {
                    if (javaNumberType.containsKey(rawType.toString())) {
                        return isParamType ? javaNumberType.get(rawType.toString()) : "number";
                    }

                    switch (rawType.toString()) {
                        case "java.lang.Boolean" -> { return "boolean"; }
                        case "java.lang.String"  -> { return "string";  }
                        case "java.lang.Object"  -> { return "any";     }
                    }
                } else {
                    if (shortified) return rawType.toString();
                }

                String res = rawType.toString();
                if (!isPackage
                ||  !res.startsWith(this.path + ".")
                ||  (res.contains("<") ?
                    res.substring(this.path.length() + 1, res.indexOf("<")) :
                    res.substring(this.path.length() + 1)
                    ).contains(".")
                ) return "Packages." + res;
                return res.substring(this.path.length() + 1);
            }
            case TYPEVAR -> {
                return ((TypeVariable) type).asElement().getSimpleName().toString();
            }
            case ARRAY -> {
                String component = transformType(((ArrayType) type).getComponentType(), isParamType, isExtends);
                return isParamType ? component + "[]" : "JavaArray<" + component + ">";
            }
            case WILDCARD -> {
                return "any";
            }
            case INTERSECTION -> {
                StringBuilder s = new StringBuilder("(");
                for (TypeMirror t : ((IntersectionType) type).getBounds()) {
                    s.append(transformType(t, isParamType, isExtends)).append(" & ");
                }
                s.setLength(s.length() - 3);
                s.append(")");
                return s.toString();
            }
            case UNION -> {
                StringBuilder s = new StringBuilder("(");
                for (TypeMirror t : ((UnionType) type).getAlternatives()) {
                    s.append(transformType(t, isParamType, isExtends)).append(" | ");
                }
                s.setLength(s.length() - 3);
                s.append(")");
                return s.toString();
            }
        }
        throw new UnsupportedOperationException(String.valueOf(type.getKind()));
    }

    public String genComment(Element element) {
        checkEnumType(element);

        DocCommentTree tree = Main.treeUtils.getDocCommentTree(element);
        if (tree == null) {
            return Main.elementUtils.isDeprecated(element) ? "/** @deprecated */\n" : "";
        }
        final StringBuilder b = new StringBuilder();

        for (DocTree blockTag : tree.getBlockTags()) {
            switch (blockTag.getKind()) {
                case SEE -> {
                    for (DocTree see : ((SeeTree) blockTag).getReference()) {
                        b.append("\n@see ");
                        if (see.getKind() == DocTree.Kind.REFERENCE) {
                            b.append(convertSignature(((ReferenceTree) see).getSignature()));
                        } else {
                            b.append(see);
                        }
                    }
                }
                case PARAM -> {
                    ParamTree param = (ParamTree) blockTag;
                    if (!param.getDescription().isEmpty()) {
                        b.append(param.isTypeParameter() ? "\n@template " : "\n@param ")
                                .append(param.getName().getName()).append(" ")
                                .append(genCommentDesc(param.getDescription()));
                    }
                }
                case RETURN -> {
                    if (!((ReturnTree) blockTag).getDescription().isEmpty()) {
                        String desc = genCommentDesc(((ReturnTree) blockTag).getDescription());
                        b.append("\n@return ").append(desc.startsWith("{") ? "{*} " : "").append(desc);
                    }
                }
                case SINCE -> b.append("\n@since ").append(genCommentDesc(((SinceTree) blockTag).getBody()));
                case DEPRECATED -> b.append("\n@deprecated ").append(genCommentDesc(((DeprecatedTree) blockTag).getBody()));
                default -> b.append("\n").append(blockTag);
            }
        }

        String fin = (genCommentDesc(tree.getFullBody()).replaceAll("(?<=[.,:;>]) ?\n", "  \n") + b).trim()
            .replaceAll("\n <p>", "\n")
            .replaceAll("</?pre>", "```")
            // is there any better way to parse html tag?
            .replaceAll("<a (?:\n|.)*?href=\"([^\"]*)\"(?:\n|.)*?>((?:\n|.)*?)</a>", "[$2]($1)")
            .replaceAll("&lt;", "<")
            .replaceAll("&gt;", ">");
        if (fin.isBlank()) return Main.elementUtils.isDeprecated(element) ? "/** @deprecated */\n" : "";

        if (Main.elementUtils.isDeprecated(element) && !b.toString().contains("@deprecated")) {
            fin += "\n@deprecated";
        }

        if (fin.startsWith("@since") && !fin.contains("\n")) return "/** " + fin + " */\n";

        return ("\n/**\n" +
            StringHelpers.addToLineStarts(fin, " * ") +
            "\n */\n").replaceAll("\n \\* +\n", "\n *\n");
    }

    private String genCommentDesc(List<? extends DocTree> desc) {
        final StringBuilder s = new StringBuilder();
        for (DocTree docTree : desc) {
            switch (docTree.getKind()) {
                case LINK, LINK_PLAIN -> {
                    String sig = ((LinkTree) docTree).getReference().getSignature();
                    if (javaNumberType.containsKey(sig)) s.append(javaNumberType.get(sig));
                    else if (sig.equals("java.lang.String")) s.append("string");
                    else if (sig.equals("java.lang.Boolean")) s.append("boolean");
                    else s.append("{@link ").append(convertSignature(sig)).append("}");
                }
                case CODE -> s.append("`").append(((LiteralTree) docTree).getBody()).append("`");
                default -> s.append(docTree);
            }
        }
        return s.toString();
    }

    private String convertSignature(String sig) {
        if (sig.matches("^xyz\\.wagyourtail\\.[^#]+\\w$")) return sig.replaceFirst("^.+\\.(?=[^.]+$)", "");
        if (sig.matches("^\\w+\\.(?:\\w+\\.)+[\\w$_]+$")) return "Packages." + sig;
        sig = sig.replaceFirst("(?<=\\S)(?=[<(])", " ");
        return sig.startsWith("#")
            ? sig.substring(1)
            : sig.replaceFirst("^(?:xyz\\.wagyourtail\\.jsmacros\\.(?:client\\.api|core)\\.library\\.impl\\.)?F([A-Z]\\w+)#", "$1.");
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
            if (Main.enumTypes.containsKey(enumType.name())
                && !loggedTypes.contains(enumType.name())
                && !Objects.equals(Main.enumTypes.get(enumType.name()), enumType.type())
            ) {
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractParser that)) {
            return false;
        }
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
