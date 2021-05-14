package xyz.wagyourtail.doclet.webdoclet.parsers;

import com.sun.source.doctree.*;
import com.sun.source.util.DocTreePath;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.XMLBuilder;
import xyz.wagyourtail.doclet.webdoclet.Main;
import xyz.wagyourtail.doclet.webdoclet.options.Links;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.util.List;
import java.util.Objects;

public class ClassParser {
    private String group;
    private String alias;
    public TypeElement type;

    public ClassParser(TypeElement type, String group, String alias) {
        this.type = type;
        this.group = group;
        this.alias = alias;
    }

    /**
     * @return class name with $ for inner class
     */
    private static String getClassName(TypeElement type) {
        StringBuilder s = new StringBuilder(type.getSimpleName());
        Element t2 = type.getEnclosingElement();
        while (t2.getKind() == ElementKind.INTERFACE || t2.getKind() == ElementKind.CLASS) {
            s.insert(0, t2.getSimpleName() + "$");
            t2 = t2.getEnclosingElement();
        }
        return s.toString();
    }

    /**
     * @return package name with . separators
     */
    private static String getPackage(TypeElement type) {
        Element t2 = type;
        while (t2.getKind() != ElementKind.PACKAGE) t2 = t2.getEnclosingElement();

        return ((PackageElement) t2).getQualifiedName().toString();
    }

    public String getPathPart() {
        return getPackage(type).replaceAll("\\.", "/") + "/" + getClassName(type).replaceAll("\\$", ".") + ".html";
    }

    /**
     * nothing much
     * @return
     */
    private String getUpDir(int extra) {
        StringBuilder s = new StringBuilder();
        for (String s1 : getPackage(type).split("\\.")) {
            s.append("../");
        }
        s.append("../".repeat(Math.max(0, extra)));
        return s.toString();
    }

    /**
     * spec
     * C\\t<searchname>\\t<linkname>\\t<?group>\\t<?alias>
     * F\\t<fieldname>\\t<fieldlink>
     * M\\t<methodname>\\t<methodlink>
     */
    public String genSearchData() {
        StringBuilder s = new StringBuilder();

        return s.toString();
    }

    public String genXML() {
        StringBuilder s = new StringBuilder("<!DOCTYPE html\n");
        s.append(new XMLBuilder("html").append(
            new XMLBuilder("head").append(
                new XMLBuilder("link", true, true).addStringOption("rel", "stylesheet").addStringOption("href", getUpDir(1) + "classContent.css")
            ),
            new XMLBuilder("body").append(
                new XMLBuilder("header").append(
                    new XMLBuilder("a").addStringOption("href", getUpDir(1))).append(
                        "<----- Return to main JsMacros docs page."
                    )
                ),
                parseClass()
            )
        );
        return s.toString();
    }

    private XMLBuilder parseClass() {
        XMLBuilder builder = new XMLBuilder("main").setClass("classDoc");
        XMLBuilder constructors = null;
        XMLBuilder subClasses;
        builder.append(subClasses = new XMLBuilder("div").setID("subClasses"));
        for (TypeMirror subClass : type.getPermittedSubclasses()) {
            subClasses.append(parseType(subClass), " ");
        }
        XMLBuilder cname;
        builder.append(cname = new XMLBuilder("h2", true, true).setClass("classTitle").append(getClassName(type)));

        List<? extends TypeParameterElement> params = type.getTypeParameters();
        if (params != null && !params.isEmpty()) {
            cname.append("<");
            for (TypeParameterElement param : params) {
                cname.append(parseType(param.asType()), ", ");
            }
            cname.pop();
            cname.append(">");
        }

        builder.append(createFlags(type, false));
        TypeMirror sup = type.getSuperclass();
        List<? extends TypeMirror> ifaces = type.getInterfaces();
        if (sup != null || (ifaces != null && !ifaces.isEmpty())) {
            XMLBuilder ext;
            builder.append(ext = new XMLBuilder("h4", true, true).addStringOption("class", "classExtends"));
            if (sup != null) {
                ext.append("extends ", parseType(sup));
            }
            if (ifaces != null && !ifaces.isEmpty()) {
                ext.append(" implements ");
                for (TypeMirror iface : ifaces) {
                    ext.append(parseType(iface), " ");
                }
            }
        }

        builder.append(getSince(type));
        builder.append(getDescription(type));


        return builder;
    }

    private XMLBuilder getSince(Element element) {
        DocCommentTree tree = Main.treeUtils.getDocCommentTree(element);
        SinceTree since = tree == null ? null : (SinceTree) tree.getBlockTags().stream().filter(e -> e.getKind().equals(DocTree.Kind.SINCE)).findFirst().orElse(null);
        if (since == null) {
            switch (element.getKind()) {
                case ENUM, CLASS, INTERFACE, ANNOTATION_TYPE -> {
                    return new XMLBuilder("p").setClass("classSince since");
                }
                case ENUM_CONSTANT, FIELD -> {
                    return new XMLBuilder("p").setClass("fieldSince since");
                }
                case METHOD -> {
                    return new XMLBuilder("p").setClass("methodSince since");
                }
                case CONSTRUCTOR -> {
                    return new XMLBuilder("p").setClass("constructorSince since");
                }
                default -> throw new UnsupportedOperationException(element.getKind().toString());
            }
        } else {
            XMLBuilder s = createDescription(element, since.getBody());
            switch (element.getKind()) {
                case ENUM, CLASS, INTERFACE, ANNOTATION_TYPE -> {
                    return s.setClass("classSince since");
                }
                case ENUM_CONSTANT, FIELD -> {
                    return s.setClass("fieldSince since");
                }
                case METHOD -> {
                    return s.setClass("methodSince since");
                }
                case CONSTRUCTOR -> {
                    return s.setClass("constructorSince since");
                }
                default -> throw new UnsupportedOperationException(element.getKind().toString());
            }
        }
    }

    private XMLBuilder getDescription(Element element) {
        DocCommentTree tree = Main.treeUtils.getDocCommentTree(element);

        return createDescription(element, tree == null ? List.of() : tree.getFullBody());
    }

    private XMLBuilder createDescription(Element el, List<? extends DocTree> inlinedoc) {
        XMLBuilder s = new XMLBuilder("p", true, true).setClass("description");
        for (DocTree docTree : inlinedoc) {
            switch (docTree.getKind()) {
                case LINK, LINK_PLAIN -> {
                    Element ele = Main.treeUtils.getElement(new DocTreePath(new DocTreePath(Main.treeUtils.getPath(el), Main.treeUtils.getDocCommentTree(el)), ((LinkTree) docTree).getReference()));
                    if (ele != null) {
                        XMLBuilder link;
                        Pair<String, Boolean> url = getURL(ele);

                        s.append(link = new XMLBuilder("a", true).addStringOption("href", url.getKey()));

                        if (List.of(ElementKind.INTERFACE, ElementKind.CLASS, ElementKind.ANNOTATION_TYPE, ElementKind.ENUM).contains(ele.getKind())) {
                            link.append(getClassName((TypeElement) ele));
                        } else {
                            link.append(getClassName((TypeElement) ele.getEnclosingElement()), "#", ele.toString());
                        }

                        if (url.getValue()) {
                            link.addStringOption("target", "_blank");
                        }
                        if (link.options.get("href").equals("\"\"")) link.setClass("type deadType");
                        else link.setClass("type");

                    } else {
                        s.append(((LinkTree) docTree).getReference().getSignature());
                    }
                }
                case CODE -> s.append(new XMLBuilder("code", true).setClass("inlineCode").append(((LiteralTree)docTree).getBody()));
                default -> s.append(docTree);
            }
        }
        return s;
    }

    private XMLBuilder parseType(TypeMirror type) {
        XMLBuilder builder = new XMLBuilder("div", true).setClass("typeParameter");
        XMLBuilder typeLink;
        switch (type.getKind()) {
            case BOOLEAN, BYTE, SHORT, INT, LONG, CHAR, FLOAT, DOUBLE, VOID, NONE -> {
                //isPrimitive
                builder.append(typeLink = new XMLBuilder("p", true).append(type));
                typeLink.setClass("type primitiveType");
            }
            case ARRAY -> {
                return parseType(((ArrayType) type).getComponentType()).append("[]");
            }
            case DECLARED -> {
                Pair<String, Boolean> url = getURL(((DeclaredType) type).asElement());
                builder.append(typeLink = new XMLBuilder("a", true).addStringOption("href", url.getKey()).append(getClassName((TypeElement) ((DeclaredType) type).asElement())));

                if (url.getValue()) {
                    typeLink.addStringOption("target", "_blank");
                }
                if (typeLink.options.get("href").equals("\"\"")) typeLink.setClass("type deadType");
                else typeLink.setClass("type");

                List<? extends TypeParameterElement> params = ((TypeElement) ((DeclaredType) type).asElement()).getTypeParameters();
                if (params != null && !params.isEmpty()) {
                    builder.append("<");
                    for (TypeParameterElement param : params) {
                        builder.append(parseType(param.asType()), ", ");
                    }
                    builder.pop();
                    builder.append(">");
                }
            }
            case TYPEVAR -> {
                builder.append(typeLink = new XMLBuilder("p", true));
                typeLink.setClass("type primitiveType");
                typeLink.append(((TypeVariable)type).asElement().getSimpleName());
            }
            case WILDCARD -> {
                builder.append(typeLink = new XMLBuilder("p", true));
                typeLink.setClass("type primitiveType");
                typeLink.append("?");
            }
        }
        return builder;
    }

    private Pair<String, Boolean> getURL(Element type) {
        if (type.asType().getKind().isPrimitive()) return new Pair<>("", false);
        Element clazz = type;
        while (!(type instanceof TypeElement)) {
            clazz = clazz.getEnclosingElement();
        }
        if (!clazz.equals(this.type)) {
            String pkg = getPackage((TypeElement) clazz);
            if (Main.internalClasses.containsKey(clazz)) {
                StringBuilder s = new StringBuilder(getUpDir(0));
                s.append(Main.internalClasses.get(clazz).getPathPart());
                if (type != clazz) {
                    s.append("#").append(memberId(type));
                }
                return new Pair<>(s.toString(), false);
            } else if (Links.externalPackages.containsKey(pkg)) {
                return new Pair<>(Links.externalPackages.get(pkg) + getClassName((TypeElement) clazz) + ".html", true);
            } else if (pkg.startsWith("com.mojang") || pkg.startsWith("net.minecraft")) {
                return new Pair<>(Main.mappingViewerURL + pkg.replaceAll("\\.", "/") + "/" + getClassName((TypeElement) clazz), true);
            } else {
                return new Pair<>("", false);
            }
        } else {
            StringBuilder s = new StringBuilder();
            s.append("#");
            if (type != clazz) {
                s.append(memberId(type));
            }
            return new Pair<>(s.toString(), false);
        }
    }

    private static String memberId(Element member) {
        StringBuilder s = new StringBuilder();
        switch (member.getKind()) {
            case ENUM_CONSTANT, FIELD:
                s.append(member.getSimpleName());
                break;
            case CONSTRUCTOR:
                s.append("constructor");
            case METHOD:
                for (VariableElement parameter : ((ExecutableElement) member).getParameters()) {
                    s.append("-").append(parameter.getSimpleName());
                }
                s.append("-");
                break;
            default:
                throw new UnsupportedOperationException(String.valueOf(member.getKind()));
        }

        return s.toString();
    }

    private static XMLBuilder createFlags(Element member, boolean shortFlags) {
        XMLBuilder flags = new XMLBuilder("div").setClass(shortFlags ? "shortFlags" : "flags");
        for (Modifier modifier : member.getModifiers()) {
            switch (modifier) {
                case ABSTRACT -> {
                    if (member.getKind() != ElementKind.INTERFACE && member.getEnclosingElement().getKind() != ElementKind.INTERFACE) {
                        flags.append(
                            new XMLBuilder("div", true, true).setClass("flag abstractFlag").append(shortFlags ? "A" : "Abstract")
                        );
                    }
                }
                case STATIC -> flags.append(
                    new XMLBuilder("div", true, true).addStringOption("class", "flag staticFlag").append(shortFlags ? "S" : "Static")
                );
                case FINAL -> flags.append(
                    new XMLBuilder("div", true, true).addStringOption("class", "flag finalFlag").append(shortFlags ? "F" : "Final")
                );
                default -> {}
            }
        }
        if (member.getKind() == ElementKind.ENUM || member.getKind() == ElementKind.ENUM_CONSTANT) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag enumFlag").append(shortFlags ? "E" : "Enum")
            );
        }
        if (member.getKind() == ElementKind.INTERFACE) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag interfaceFlag").append(shortFlags ? "I" : "Interface")
            );
        }
        if (member.getAnnotation(Deprecated.class) != null) {
            flags.append(
                new XMLBuilder("div", true, true).addStringOption("class", "flag deprecatedFlag").append(shortFlags ? "D" : "Deprecated")
            );
        }
        return flags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassParser)) return false;
        ClassParser that = (ClassParser) o;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

}
