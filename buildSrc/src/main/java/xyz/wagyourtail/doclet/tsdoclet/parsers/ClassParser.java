package xyz.wagyourtail.doclet.tsdoclet.parsers;

import xyz.wagyourtail.doclet.DocletTypescriptExtends;
import xyz.wagyourtail.StringHelpers;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClassParser extends AbstractParser {
    public ClassParser(TypeElement type) {
        super(type);
        super.checkEnumType(type);
    }

    public String getClassName(boolean typeParams) {
        StringBuilder s = new StringBuilder(type.getSimpleName());
        Element type = this.type.getEnclosingElement();
        while (type.getKind() == ElementKind.INTERFACE || type.getKind() == ElementKind.CLASS) {
            s.insert(0, type.getSimpleName() + "$");
            type = type.getEnclosingElement();
        }
        if (typeParams) {
            List<? extends TypeParameterElement> params = this.type.getTypeParameters();
            if (params != null && !params.isEmpty()) {
                s.append("<");
                for (TypeParameterElement param : params) {
                    s.append(transformType(param.asType())).append(", ");
                }
                s.setLength(s.length() - 2);
                s.append(">");
            }
        }
        return s.toString();
    }

    private String buildExtends() {
        StringBuilder s = new StringBuilder();//.append(" extends ");
        String sep = type.getKind().isInterface() ? ", " : " & ";
        String sup = transformType(type.getSuperclass());
        if (sup.equals("void")) {
            if (!type.getKind().isInterface()) {
                s.append("java.lang.Object");
            }
        } else if (sup.equals("/* minecraft class */ any")) {
            s.append("/* supressed minecraft class */ java.lang.Object");
        } else {
            if (sup.contains(".") &&
                    redirects.contains(sup.substring(sup.lastIndexOf(".") + 1)) &&
                    getTypeString().startsWith(sup.substring(0, sup.lastIndexOf(".") + 1))) {
                sup = sup.substring(sup.lastIndexOf(".") + 1);
            }
            s.append(sup);
        }

        List<? extends TypeMirror> iface = type.getInterfaces();
        if (iface != null && !iface.isEmpty()) {
            for (TypeMirror ifa : iface) {
                sup = transformType(ifa);
                if (!sup.equals("/* minecraft class */ any")) {
                    if (s.length() > 0) s.append(sep);
                    s.append(sup);
                }
            }
        }

        DocletTypescriptExtends ext = type.getAnnotation(DocletTypescriptExtends.class);
        if (ext != null) {
            if (s.length() > 0) s.append(sep);
            s.append(ext.value());
        }

        if (getClassName(false).equals("Iterable")) s.append("JsIterable<T>");

        if (sep.equals(" & ") && s.toString().contains(" & ")) {
            s.insert(0, "(0 as any as MergeClass<").append(">)");
        }

        if (s.length() > 0) s.insert(0, " extends ");

        return s.toString();
    }

    @Override
    public String genTSInterface() {
        Set<Element> fields = new LinkedHashSet<>();
        Set<Element> methods = new LinkedHashSet<>();
        Set<Element> constructors = new LinkedHashSet<>();

        for (Element el : type.getEnclosedElements()) {
            if (el.getModifiers().contains(Modifier.PUBLIC)) {
                switch (el.getKind()) {
                    case FIELD, ENUM_CONSTANT -> fields.add(el);
                    case METHOD -> methods.add(el);
                    case CONSTRUCTOR -> constructors.add(el);
                    default -> {}
                }
            }
        }

        StringBuilder s = new StringBuilder();

        if (type.getKind().isInterface()) {
            s.append("const ").append(getClassName(false)).append(": {\n\n")
                .append("    new (interface: never): ").append(getClassName(true)).append(";\n")
                .append(StringHelpers.tabIn(("\n" + genStaticFields(fields)).replaceAll("\nstatic ", "\n")))
                .append(StringHelpers.tabIn(("\n" + genStaticMethods(methods)).replaceAll("\nstatic ", "\n")))
            .append("\n};\n")
            .append("interface ").append(getClassName(true)).append(buildExtends()).append(" {\n\n")
                .append(StringHelpers.tabIn(genFields(fields))).append("\n")
                .append(StringHelpers.tabIn(genMethods(methods))).append("\n")
            .append("}");
        } else {
            s.append("class ").append(getClassName(true)).append(buildExtends()).append(" {\n\n")
                .append(StringHelpers.tabIn(genConstructors(constructors))).append("\n")
                .append(StringHelpers.tabIn(genStaticFields(fields))).append("\n")
                .append(StringHelpers.tabIn(genStaticMethods(methods))).append("\n")
                .append(StringHelpers.tabIn(genFields(fields))).append("\n")
                .append(StringHelpers.tabIn(genMethods(methods))).append("\n")
            .append("}");
        }

        return s.toString().replaceAll("\\{[\n ]+\\}", "{}").replaceAll("\n\n\n+", "\n\n");
    }

}
