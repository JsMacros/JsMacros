package xyz.wagyourtail.doclet.tsdoclet.parsers;

import com.sun.source.doctree.*;
import xyz.wagyourtail.StringHelpers;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.tsdoclet.Main;

import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractParser {
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
        return genComment(field) +
        (field.getModifiers().contains(Modifier.FINAL) ? "readonly " : "") +
        field.getSimpleName() + ": " + transformType(field.asType()) + ";";
    }

    public String genMethod(ExecutableElement method) {
        final StringBuilder s = new StringBuilder();
        s.append(genComment(method));
        s.append(method.getSimpleName());
        //diamondOperator
        List<? extends TypeParameterElement> typeParams = method.getTypeParameters();
        if (typeParams != null && !typeParams.isEmpty()) {
            s.append("<");
            for (TypeParameterElement param : typeParams) {
                s.append(transformType(param.asType())).append(", ");
            }
            s.setLength(s.length() - 2);
            s.append(">");
        }
        s.append("(");
        DocletReplaceParams replace = method.getAnnotation(DocletReplaceParams.class);
        if (replace != null) {
           s.append(replace.value());
        } else {
            List<? extends VariableElement> params = method.getParameters();
            if (params != null && !params.isEmpty()) {
                for (VariableElement param : params) {
                    s.append(param.getSimpleName()).append(": ").append(transformType(param.asType())).append(", ");
                }
                s.setLength(s.length() - 2);
            }
        }
        s.append("): ");
        DocletReplaceReturn replace2 = method.getAnnotation(DocletReplaceReturn.class);
        if (replace2 != null) {
            s.append(replace2.value());
        } else {
            s.append(transformType(method.getReturnType()));
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
                s.append(transformType(param.asType())).append(", ");
            }
            s.setLength(s.length() - 2);
            s.append(">");
        }
        s.append("(");
        List<? extends VariableElement> params = constructor.getParameters();
        if (params != null && !params.isEmpty()) {
            for (VariableElement param : params) {
                s.append(param.getSimpleName()).append(": ").append(transformType(param.asType())).append(", ");
            }
            s.setLength(s.length() - 2);
        }
        s.append("): ").append(transformType(type.asType())).append(";");
        return s.toString();
    }

    public String transformType(TypeMirror type) {
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
                rawType.insert(0, ((PackageElement) typeElement).getQualifiedName() + ".");

                List<? extends TypeMirror> params = ((DeclaredType) type).getTypeArguments();
                if (params != null && !params.isEmpty()) {
                    rawType.append("<");
                    for (TypeMirror param : params) {
                        rawType.append(transformType(param)).append(", ");
                    }
                    rawType.setLength(rawType.length() - 2);
                    rawType.append(">");
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

                    Main.classes.addClass(((DeclaredType) type).asElement());
                    return "_javatypes." + rawType.toString();
                } else {
                    Main.classes.addClass(((DeclaredType) type).asElement());
                    return "_javatypes." + rawType.toString().replace(".function.", "._function.");
                }
            }
            case TYPEVAR -> {
                return ((TypeVariable) type).asElement().getSimpleName().toString();
            }
            case ARRAY -> {
                return transformType(((ArrayType) type).getComponentType()) + "[]";
            }
            case WILDCARD -> {
                return "any";
            }
        }
        throw new UnsupportedOperationException(String.valueOf(type.getKind()));
    }

    public String genComment(Element comment) {
        DocCommentTree tree = Main.treeUtils.getDocCommentTree(comment);
        if (tree == null) {
            return "";
        }
        final StringBuilder s = new StringBuilder();
        for (DocTree docTree : tree.getFullBody()) {
            switch (docTree.getKind()) {
                case LINK, LINK_PLAIN -> {
                    String referenceString = ((LinkTree) docTree).getReference().getSignature().split("\\(")[0];
                    s.append("{@link ");
                    if (referenceString.startsWith("#")) {
                        s.append(referenceString);
                    } else {
                        s.append("_javatypes.").append(
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
        return "\n/**\n" +
            StringHelpers.addToLineStarts(s.toString(), " * ") +
            "\n */\n";
    }

    public abstract String genTSInterface();

    public TypeElement getType() {
        return type;
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
