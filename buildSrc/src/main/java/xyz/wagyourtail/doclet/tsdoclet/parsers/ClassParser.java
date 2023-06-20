package xyz.wagyourtail.doclet.tsdoclet.parsers;

import xyz.wagyourtail.StringHelpers;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ClassParser extends AbstractParser {
    public ClassParser(TypeElement type) {
        super(type);
    }

    private String getClassName(boolean typeParams) {
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
        StringBuilder s = new StringBuilder();
        String sup = transformType(type.getSuperclass());
        if (sup.equals("void")) {
            if (type.getKind().isInterface()) {
                s.append("_javatypes.java.lang.Interface");
            } else {
                s.append("_javatypes.java.lang.Object");
            }
        } else if (!sup.equals("/* minecraft classes, as any, because obfuscation: */ any")) {
            s.append(sup);
        } else {
            s.append("/* supressed minecraft class */ _javatypes.java.lang.Object");
        }

        List<? extends TypeMirror> iface = type.getInterfaces();
        if (iface != null && !iface.isEmpty()) {
            for (TypeMirror ifa : iface) {
                sup = transformType(ifa);
                if (sup.equals("/* minecraft classes, as any, because obfuscation: */ any")) {
                    s.append(", ").append("/* supressed minecraft class */ _javatypes.java.lang.Interface");
                } else {
                    s.append(", ").append(sup);
                }
            }
        }
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
                    default -> {
                    }
                }
            }
        }

        StringBuilder s = new StringBuilder("interface ").append(getClassName(true)).append(" extends ").append(buildExtends()).append(" {\n")
                .append(StringHelpers.tabIn(genFields(fields))).append("\n")
                .append(StringHelpers.tabIn(genMethods(methods))).append("\n}");
        s.append("\nnamespace ").append(getClassName(false)).append(" {\n")
                .append(StringHelpers.tabIn("interface static {")).append("\n")
                .append(StringHelpers.tabIn(genConstructors(constructors), 2)).append("\n")
                .append(StringHelpers.tabIn(genStaticFields(fields), 2)).append("\n")
                .append(StringHelpers.tabIn(genStaticMethods(methods), 2)).append("\n    }\n}");

        return s.toString();
    }

}
