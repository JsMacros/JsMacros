package xyz.wagyourtail.doclet.tsdoclet.parsers;

import com.sun.source.doctree.*;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.StringHelpers;
import xyz.wagyourtail.doclet.*;
import xyz.wagyourtail.doclet.tsdoclet.Main;
import xyz.wagyourtail.doclet.tsdoclet.PackageTree;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.util.*;
import java.util.stream.Collectors;

import static xyz.wagyourtail.doclet.tsdoclet.PackageTree.tsReservedWords;

public abstract class AbstractParser {
    static final public Set<String> javaAliases = Set.of(
        "java.lang.Array",
        "java.lang.Class",
        "java.util.Collection",
        "java.util.List",
        "java.util.Map",
        "java.util.Set"
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
    static final public Map<String, String> functionalInterfaces = Map.of(
        "java.util.function.Consumer",    "MethodWrapper<$0>",
        "java.util.function.BiConsumer",  "MethodWrapper<$0, $1>",
        "java.util.function.Function",    "MethodWrapper<$0, any, $1>",
        "java.util.function.BiFunction",  "MethodWrapper<$0, $1, $2>",
        "java.util.function.Predicate",   "MethodWrapper<$0, any, boolean>",
        "java.util.function.BiPredicate", "MethodWrapper<$0, $1, boolean>",
        "java.util.function.Supplier",    "MethodWrapper<any, any, $0>",
        "java.util.Comparator",           "MethodWrapper<$0, $0, int>",
        "java.lang.Runnable",             "MethodWrapper"
    );

    private static final Set<String> loggedTypes = new HashSet<>();
    private final String path;
    protected final TypeElement type;
    public boolean isPackage = true;
    private transient boolean returnsSelf = false;

    public static TypeElement objectElement;
    public static Set<ExecutableElement> objectMethods;
    public static Set<Name> objectMethodNames;

    public static void initObjectElement() {
        objectElement = Main.elementUtils.getTypeElement("java.lang.Object");
        objectMethods = objectElement.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD && checkModifier(e, false))
                .map(e -> (ExecutableElement) e)
                .collect(Collectors.toUnmodifiableSet());
        objectMethodNames = objectMethods.stream()
                .map(ExecutableElement::getSimpleName)
                .collect(Collectors.toUnmodifiableSet());
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
            if (checkModifier(field, false) && !shouldIgnore(field)) {
                s.append(genField(field)).append("\n");
            }
        }
        return s.toString();
    }

    public String genStaticFields(Set<Element> fields) {
        final StringBuilder s = new StringBuilder();
        for (Element field : fields) {
            if (checkModifier(field, true) && !shouldIgnore(field)) {
                s.append(genField(field)).append("\n");
            }
        }
        return s.toString();
    }

    public String genMethods(Set<Element> methods) {
        final StringBuilder s = new StringBuilder();
        for (Element method : methods) {
            if (checkModifier(method, false) && !shouldIgnore(method)) {
                s.append(genMethod((ExecutableElement) method)).append("\n");
            }
        }
        return s.toString();
    }

    public String genStaticMethods(Set<Element> methods) {
        final StringBuilder s = new StringBuilder();
        for (Element method : methods) {
            if (checkModifier(method, true) && !shouldIgnore(method)) {
                s.append(genMethod((ExecutableElement) method)).append("\n");
            }
        }
        return s.toString();
    }

    public String genConstructors(Set<Element> methods) {
        final StringBuilder s = new StringBuilder();
        for (Element method : methods) {
            if (!method.getModifiers().contains(Modifier.PUBLIC)) continue;
            if (shouldIgnore(method)) continue;
            s.append(genConstructor((ExecutableElement) method)).append("\n");
        }
        return s.toString();
    }

    public String genField(Element field) {
        StringBuilder s = new StringBuilder();
        s.append(genComment(field));

        // modifiers
        Set<Modifier> mods = field.getModifiers();
        if (mods.contains(Modifier.STATIC)) s.append("static ");
        if (mods.contains(Modifier.FINAL)) s.append("readonly ");

        // name
        s.append(field.getSimpleName()).append(": ");

        // type
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
        if (!isConstructor && e.getModifiers().contains(Modifier.STATIC)) s.append("static ");

        // name
        s.append(isConstructor ? "constructor " : e.getSimpleName());

        // type params
        DocletReplaceTypeParams replace = e.getAnnotation(DocletReplaceTypeParams.class);
        if (replace != null) {
            if (!replace.value().isEmpty()) s.append("<").append(replace.value()).append(">");
        } else {
            List<? extends TypeParameterElement> typeParams = (isConstructor ? type : e).getTypeParameters();
            if (!typeParams.isEmpty()) {
                s.append("<");
                for (TypeParameterElement param : typeParams) {
                    s.append(transformType(param));
                    String ext = transformType(((TypeVariable) param.asType()).getUpperBound());
                    if (!ext.equals("any")) {
                        s.append(" extends ").append(ext);
                    }
                    s.append(", ");
                }
                s.setLength(s.length() - ", ".length());
                s.append(">");
            }
        }

        // params
        s.append("(");
        DocletReplaceParams replace2 = e.getAnnotation(DocletReplaceParams.class);
        if (replace2 != null) {
            s.append(replace2.value());
        } else {
            List<? extends VariableElement> params = e.getParameters();
            if (!params.isEmpty()) {
                VariableElement restParam = e.isVarArgs() ? params.get(params.size() - 1) : null;
                for (VariableElement param : params) {
                    String name = param.getSimpleName().toString();
                    if (restParam == param) {
                        s.append("...");
                        if (tsReservedWords.contains(name)) s.append("_");
                        s.append(name).append(": ").append("JavaVarArgs<").append(transformType(param, true));
                        int sl2 = s.length() - "[]".length();
                        if (s.substring(sl2).equals("[]")) s.setLength(sl2);
                        else System.out.println("varargs type is not array?? " + type.getSimpleName() + "." + e.getSimpleName());
                        if (isNullable(param)) s.append(" | null");
                        s.append(">");
                    } else {
                        if (tsReservedWords.contains(name)) s.append("_");
                        s.append(name).append(": ").append(transformType(param, true));
                        if (isNullable(param)) s.append(" | null");
                    }
                    s.append(", ");
                }
                s.setLength(s.length() - ", ".length());
            }
        }
        s.append(")");

        // return type
        if (!isConstructor) {
            s.append(": ");
            DocletReplaceReturn replace3 = e.getAnnotation(DocletReplaceReturn.class);
            if (replace3 != null) {
                transformType(e.getReturnType()); // to add type to the Packages
                s.append(replace3.value());
            } else if (returnsSelf && type.asType().equals(e.getReturnType())) {
                s.append("this");
            } else {
                s.append(transformType(e.getReturnType()));
                if (isNullable(e)) s.append(" | null");
            }
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

    @SuppressWarnings("unused")
    public String transformType(Element elem, boolean isParamType, boolean isExtends) {
        return transformType(elem.asType(), isParamType, isExtends);
    }

    public String transformType(TypeMirror type) {
        return transformType(type, false, false);
    }

    @SuppressWarnings("unused")
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
                final Element typeElement = ((DeclaredType) type).asElement();
                StringBuilder rawType = new StringBuilder(typeElement.getSimpleName().toString());
                Element enclosing = typeElement.getEnclosingElement();
                // full class name
                while (enclosing.getKind() == ElementKind.CLASS || enclosing.getKind() == ElementKind.INTERFACE) {
                    rawType.insert(0, enclosing.getSimpleName().toString() + "$");
                    enclosing = enclosing.getEnclosingElement();
                }

                String classpath = ((QualifiedNameable) enclosing).getQualifiedName().toString();
                // check Event and Library type (probably none Library)
                if (classpath.startsWith("xyz.wagyourtail.")) {
                    Optional<String> special = typeElement.getAnnotationMirrors().stream()
                            .map(a -> switch (a.getAnnotationType().asElement().getSimpleName().toString()) {
                                case "Event" -> "Events." + Main.getAnnotationValue(a);
                                case "Library" -> "typeof " + Main.getAnnotationValue(a);
                                default -> null;
                            })
                            .filter(Objects::nonNull)
                            .findFirst();
                    if (special.isPresent()) return special.get();
                }

                // detect types defined in Graal.d.ts
                boolean aliased = false;
                if (!isExtends && javaAliases.contains(classpath + "." + rawType)) {
                    aliased = true;
                    rawType.insert(0, "Java");
                    if (isParamType && rawType.toString().equals("JavaClass")) rawType.append("Arg");
                } else rawType.insert(0, classpath + ".");

                // type params
                List<? extends TypeMirror> params = ((DeclaredType) type).getTypeArguments();
                if (isParamType && functionalInterfaces.containsKey(rawType.toString())) {
                    // convert to MethodWrapper
                    String res = functionalInterfaces.get(rawType.toString());
                    if (!params.isEmpty()) {
                        int size = params.size();
                        for (int i = 0; i < size; i++) {
                            res = res.replace("$" + i, transformType(params.get(i), true, false));
                        }
                    }
                    return res;
                }

                if (!params.isEmpty()) {
                    rawType.append("<");
                    for (TypeMirror param : params) {
                        rawType.append(transformType(param, isParamType, isExtends)).append(", ");
                    }
                    rawType.setLength(rawType.length() - 2);
                    rawType.append(">");
                }

                String res = rawType.toString();

                // comment out minecraft types because it's obfuscated
                // + including minecraft types will make the file large asf
                // + don't even know how to get obfuscated names in doclet environment
                if (res.startsWith("net.minecraft.")) {
                    return "/* " + res.replaceAll("/\\* ", "").replaceAll(" \\*/(?: any)?", "") + " */ any";
                }

                // check BaseEvent
                if (res.equals("xyz.wagyourtail.jsmacros.core.event.BaseEvent")) {
                    return "Events.BaseEvent";
                }

                // register this type to the package tree for further type generation
                Main.classes.addClass(((DeclaredType) type).asElement());

                // primitive/aliased check
                if (!isExtends && res.startsWith("java.lang")) {
                    if (javaNumberType.containsKey(res)) {
                        return isParamType ? javaNumberType.get(res) : "number";
                    }

                    switch (res) {
                        case "java.lang.Boolean" -> { return "boolean"; }
                        case "java.lang.String"  -> { return "string";  }
                        case "java.lang.Object"  -> { return "any";     }
                    }
                } else {
                    if (aliased) return res;
                }

                // insert root name to be able to actually reference this type
                // if it's redundant, the regex in PackageTree#genTSTree() will take care of it
                if (!isPackage || !res.startsWith(this.path + ".")) return "Packages." + res;
                // at this point, res must be starts with this.path, because of the condition above

                String withoutTypeParams = res.contains("<") ? res.substring(0, res.indexOf("<")) : res;

                // if res isn't in the same package as this.type
                if (withoutTypeParams.substring(this.path.length() + 1).contains(".")) return "Packages." + res;
                // if res is defined in Graal.d.ts, don't trim the path (as the next line did)
                // because otherwise this won't be able to reference it
                if (PackageTree.predefinedClasses.contains(withoutTypeParams)) return res;
                // trim the path
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
                TypeMirror bound = ((WildcardType) type).getExtendsBound();
                if (bound == null) bound = ((WildcardType) type).getSuperBound();
                return bound == null ? "any" : transformType(bound, isParamType, isExtends);
            }
            case INTERSECTION -> {
                StringBuilder s = new StringBuilder("(");
                for (TypeMirror t : ((IntersectionType) type).getBounds()) {
                    s.append(transformType(t, isParamType, isExtends)).append(" & ");
                }
                s.setLength(s.length() - " & ".length());
                s.append(")");
                return s.toString();
            }
            case UNION -> {
                StringBuilder s = new StringBuilder("(");
                for (TypeMirror t : ((UnionType) type).getAlternatives()) {
                    s.append(transformType(t, isParamType, isExtends)).append(" | ");
                }
                s.setLength(s.length() - " | ".length());
                s.append(")");
                return s.toString();
            }
        }
        throw new UnsupportedOperationException(String.valueOf(type.getKind()));
    }

    public String genComment(Element element) {
        checkEnumType(element);
        returnsSelf = false;

        DocCommentTree tree = Main.treeUtils.getDocCommentTree(element);
        boolean isDeprecated = Main.elementUtils.isDeprecated(element);
        if (tree == null) return isDeprecated ? "/** @deprecated */\n" : "";

        StringBuilder b = new StringBuilder();
        b.append(genCommentDesc(tree.getFullBody()).replaceAll("(?<=[.,:;>]) ?\n", "  \n"));

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
                    List<? extends DocTree> description = param.getDescription();
                    if (!description.isEmpty()) {
                        b.append(param.isTypeParameter() ? "\n@template " : "\n@param ")
                                .append(param.getName().getName()).append(" ")
                                .append(genCommentDesc(description));
                    }
                }
                case RETURN -> {
                    List<? extends DocTree> description = ((ReturnTree) blockTag).getDescription();
                    if (!description.isEmpty()) {
                        String desc = genCommentDesc(description);
                        if (desc.startsWith("self") && (desc.length() == "self".length() || desc.charAt("self".length()) == ' ')) returnsSelf = true;
                        b.append("\n@return ");
                        // to prevent vscode from parsing the description as type
                        // typescript already provided the type, so assign it as any is fine
                        if (desc.startsWith("{")) b.append("{*} ");
                        b.append(desc);
                    }
                }
                case SINCE -> b.append("\n@since ").append(genCommentDesc(((SinceTree) blockTag).getBody()));
                case DEPRECATED -> b.append("\n@deprecated ").append(genCommentDesc(((DeprecatedTree) blockTag).getBody()));
                default -> b.append("\n").append(blockTag);
            }
        }

        String fin = b.toString().trim()
            .replaceAll("\n <p>", "\n")
            .replaceAll("</?pre>", "```")
            // is there any better way to parse html tag?
            .replaceAll("<a (?:\n|.)*?href=\"([^\"]*)\"(?:\n|.)*?>((?:\n|.)*?)</a>", "[$2]($1)")
            .replaceAll("&lt;", "<")
            .replaceAll("&gt;", ">");
        if (fin.isBlank()) return isDeprecated ? "/** @deprecated */\n" : "";

        if (isDeprecated && !b.toString().contains("@deprecated")) {
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
                    else {
                        String str = convertSignature(sig);
                        int i = str.indexOf("<");
                        if (i == -1) i = str.indexOf("(");
                        s.append("{@link ");
                        if (i == -1) {
                            s.append(str).append("}");
                        } else {
                            s.append(str, 0, i).append("}").append(str.substring(i));
                        }
                    }
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
//        sig = sig.replaceFirst("(?<=\\S)(?=[<(])", " ");
        return sig.startsWith("#")
            ? sig.substring(1)
            : sig.replaceFirst("^(?:xyz\\.wagyourtail\\.jsmacros\\.(?:client\\.api|core)\\.library\\.impl\\.)?F([A-Z]\\w+)#", "$1.").replaceFirst("#", ".");
    }

    public abstract String genTSInterface();

    public String getQualifiedType() {
        return isPackage ? this.path + "." + transformType(type) : transformType(type);
    }

    public static void checkEnumType(Element element) {
        DocletDeclareType enumType = element.getAnnotation(DocletDeclareType.class);
        if (enumType == null) return;

        if (Main.enumTypes.containsKey(enumType.name())
                && !loggedTypes.contains(enumType.name())
                && !Objects.equals(Main.enumTypes.get(enumType.name()), enumType.type())
        ) {
            System.out.println("Duplicate enum type name: " + enumType.name());
            loggedTypes.add(enumType.name());
        }
        Main.enumTypes.put(enumType.name(), enumType.type());
    }

    public static boolean checkModifier(@NotNull Element e, boolean shouldBeStatic) {
        Set<Modifier> mods = e.getModifiers();
        return mods.contains(Modifier.PUBLIC) && mods.contains(Modifier.STATIC) == shouldBeStatic;
    }

    public static boolean shouldIgnore(@NotNull Element e) {
        return e.getAnnotation(DocletIgnore.class) != null;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
        return e.getAnnotationMirrors().stream()
                .anyMatch(a -> a.getAnnotationType().asElement().getSimpleName().contentEquals("Nullable"));
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
