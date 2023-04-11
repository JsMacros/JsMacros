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
        StringBuilder s = new StringBuilder(" extends ");
        String sup = transformType(type.getSuperclass(), true);
        if (sup.equals("void") || sup.equals("any")) {
            s.append("JavaObject");
        } else if (sup.equals("/* minecraft class */ any")) {
            s.append("/* supressed minecraft class */ JavaObject");
        } else {
            s.append(sup);
        }

        List<? extends TypeMirror> iface = type.getInterfaces();
        if (iface != null && !iface.isEmpty()) {
            for (TypeMirror ifa : iface) {
                sup = transformType(ifa);
                if (!sup.equals("/* minecraft class */ any")) {
                    s.append(", ").append(sup);
                }
            }
        }

        if (s.toString().startsWith(" extends JavaObject, ")) s.delete(9, 21);

        DocletTypescriptExtends ext = type.getAnnotation(DocletTypescriptExtends.class);
        if (ext != null) s.append(", ").append(ext.value());

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

        StringBuilder s = new StringBuilder("const ").append(getClassName(false)).append(": Java");

        if (type.getKind().isInterface()) {
            s.append("InterfaceStatics");
            String statics = genStaticFields(fields) + "\n" + genStaticMethods(methods);
            if (!statics.equals("\n")) {
                s.append(" & {\n\n")
                    .append(StringHelpers.tabIn(statics)).append("\n")
                .append("}");
            }
            s.append(";\n");
        } else {
            String constrs = genConstructors(constructors);
            String statics = genStaticFields(fields) + "\n" + genStaticMethods(methods);

            s.append("ClassStatics<");
            if (constrs.length() == 0) {
                s.append("false");
            } else if (constrs.startsWith("new (") && constrs.indexOf("\n") == constrs.length() - 1) {
                s.append("[").append(constrs.substring(constrs.indexOf("):") + 3, constrs.length() - 2)).append("]");
                if (!constrs.startsWith("new ()")) {
                    s.append(", [").append(constrs.substring(5, constrs.indexOf("):"))).append("]");
                }
            } else {
                s.append("{\n\n").append(StringHelpers.tabIn(constrs)).append("\n")
                    .append(StringHelpers.tabIn(
                        """
                        /** @deprecated */ Symbol: unknown;
                        /** @deprecated */ apply: unknown;
                        /** @deprecated */ arguments: unknown;
                        /** @deprecated */ bind: unknown;
                        /** @deprecated */ call: unknown;
                        /** @deprecated */ caller: unknown;
                        /** @deprecated */ length: unknown;
                        /** @deprecated */ name: unknown;
                        /** @deprecated */ prototype: unknown;
                        """
                    )).append("\n}");
            }
            s.append(">");

            if (!statics.equals("\n")) {
                s.append(" & {\n\n")
                    .append(StringHelpers.tabIn(statics))
                .append("\n}");
            }
            s.append(";\n");
        }

        s.append("interface ").append(getClassName(true)).append(buildExtends()).append(" {\n\n")
            .append(StringHelpers.tabIn(genFields(fields))).append("\n")
            .append(StringHelpers.tabIn(genMethods(methods))).append("\n")
        .append("}");

        return s.toString().replaceAll("\\{[\n ]+\\}", "{}").replaceAll("\n\n\n+", "\n\n");
    }

}
