package xyz.wagyourtail.doclet.tsdoclet.parsers;

import xyz.wagyourtail.doclet.tsdoclet.Main;
import xyz.wagyourtail.StringHelpers;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.lang.Override;
import java.util.*;

public class ClassParser extends AbstractParser {
    public static Map<TypeElement, Set<TypeElement>> mixinInterfaceMap = new LinkedHashMap<>();
    private Set<TypeElement> superMcClasses;
    private boolean doesDirectExtendMc = false;
    private Set<TypeElement> mixinInterfaces;

    public ClassParser(TypeElement type) {
        super(type);
    }

    public String getClassName(boolean typeParams) {
        return getClassName(typeParams, false);
    }

    public String getClassName(boolean typeParams, boolean defaultToAny) {
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
                    s.append(transformType(param));
                    String ext = transformType(((TypeVariable) param.asType()).getUpperBound());
                    if (!ext.endsWith("any")) {
                        s.append(" extends ").append(ext);
                        if (defaultToAny) s.append(" = any");
                    } else if (ext.startsWith("/* net.minecraft")) {
                        s.append(" = ").append(ext);
                    } else if (defaultToAny) s.append(" = any");
                    s.append(", ");
                }
                s.setLength(s.length() - 2);
                s.append(">");
            }
        }
        return s.toString();
    }

    private String buildExtends() {
        StringBuilder s = new StringBuilder(" extends ");
        String sup = transformType(type.getSuperclass(), false, true);
        if (sup.equals("void") || sup.equals("any")) {
            s.append("JavaObject");
        } else if (sup.startsWith("/* net.minecraft")) {
            s.append(sup, 0, sup.length() - 3).append("JavaObject");
        } else {
            s.append(sup);
        }


        Set<TypeMirror> iface = new HashSet<>(type.getInterfaces());
        if (doesDirectExtendMc) for (TypeElement e : mixinInterfaces) {
            iface.add(e.asType());
            System.out.println("Added mixin interface " + e.getSimpleName() + " on class " + type.getSimpleName());
        }
        if (!iface.isEmpty()) {
            for (TypeMirror ifa : iface) {
                sup = transformType(ifa, false, true);
                s.append(", ");
                if (sup.startsWith("/* net.minecraft")) {
                    s.append(sup, 0, sup.length() - 3).append("JavaObject");
                } else {
                    s.append(sup);
                }
            }
        }

        if (s.toString().startsWith(" extends JavaObject, ")) s.delete(9, 21);

        return s.toString();
    }

    private void getSuperMcClasses(Set<TypeElement> set, TypeElement c) {
        if (!c.getKind().isInterface()) {
            TypeMirror sup = c.getSuperclass();
            if (sup instanceof DeclaredType) {
                TypeElement supe = (TypeElement) ((DeclaredType) sup).asElement();
                if (transformType(sup).startsWith("/* net.minecraft")) {
                    superMcClasses.add(supe);
                    if (doesDirectExtendMc && mixinInterfaceMap.containsKey(supe)) {
                        Set<TypeElement> ifs = mixinInterfaceMap.get(supe);
                        set.addAll(ifs);
                        mixinInterfaces.addAll(ifs);
                    }
                } else {
                    set.add(supe);
                }
                getSuperMcClasses(set, supe);
            }
        }

        List<? extends TypeMirror> ifaces = c.getInterfaces();
        if (ifaces != null && !ifaces.isEmpty()) {
            for (TypeMirror ifa : ifaces) {
                TypeElement ifae = (TypeElement) ((DeclaredType) ifa).asElement();
                if (transformType(ifa).startsWith("/* net.minecraft")) {
                    superMcClasses.add(ifae);
                } else {
                    set.add(ifae);
                }
                getSuperMcClasses(set, ifae);
            }
        }
    }

    private boolean isObfuscated(Element m) {
        return isObfuscated(m, type);
    }

    private boolean isObfuscated(Element m, TypeElement type) {
        if (m.getAnnotation(Override.class) == null) return false;
        if (m.getKind() != ElementKind.METHOD) return false;
        for (TypeElement clz : superMcClasses) {
            for (Element sel : clz.getEnclosedElements()) {
                if (sel.getKind() != ElementKind.METHOD) continue;
                if (Main.elementUtils.overrides(
                    (ExecutableElement) m,
                    (ExecutableElement) sel,
                    type
                )) return true;
            }
        }
        return false;
    }

    public String gapper(String fields, String methods) {
        return methods.isEmpty() ? fields : fields + "\n" + methods + "\n";
    }

    @Override
    public String genTSInterface() {
        superMcClasses = new LinkedHashSet<>();
        Set<TypeElement> superClasses = new LinkedHashSet<>();
        Set<Element> fields = new LinkedHashSet<>();
        Set<Element> methods = new LinkedHashSet<>();
        Set<Element> constructors = new LinkedHashSet<>();

        if (transformType(type.getSuperclass()).startsWith("/* net.minecraft")) {
            doesDirectExtendMc = true;
            mixinInterfaces = new LinkedHashSet<>();
        }
        getSuperMcClasses(superClasses, type);

        for (Element el : type.getEnclosedElements()) {
            if (el.getModifiers().contains(Modifier.PUBLIC)) {
                switch (el.getKind()) {
                    case METHOD -> {
                        if (!isObfuscated(el) && !isObjectMethod(el)) methods.add(el);
                    }
                    case FIELD, ENUM_CONSTANT -> fields.add(el);
                    case CONSTRUCTOR -> constructors.add(el);
                    default -> {
                    }
                }
            }
        }

        Set<Name> methodNames = new LinkedHashSet<>();
        for (Element m : methods) {
            if (!m.getModifiers().contains(Modifier.STATIC)) {
                methodNames.add(m.getSimpleName());
            }
        }
        // add super methods with same name to this class because js extending works a bit different
        if (!methodNames.isEmpty()) {
            Set<Element> superMethods = new LinkedHashSet<>();
            for (TypeElement clz : superClasses) {
                outer:
                for (Element sel : clz.getEnclosedElements()) {
                    if (sel.getKind() != ElementKind.METHOD) continue;
                    if (sel.getModifiers().contains(Modifier.STATIC)) continue;
                    Name name = sel.getSimpleName();
                    if (!methodNames.contains(name)) continue;
                    for (Element m : methods) {
                        if (!m.getSimpleName().equals(name)) continue;
                        if (Main.elementUtils.overrides(
                            (ExecutableElement) m,
                            (ExecutableElement) sel,
                            type
                        )) continue outer;
                    }
                    for (Element m : superMethods) {
                        if (!m.getSimpleName().equals(name)) continue;
                        if (Main.elementUtils.overrides(
                            (ExecutableElement) m,
                            (ExecutableElement) sel,
                            type
                        )) continue outer;
                    }
                    if (!isObfuscated(sel, clz)) superMethods.add(sel);
                }
            }
            if (!superMethods.isEmpty()) {
                Set<Element> merged = new LinkedHashSet<>();
                Set<Name> superMethodNames = new LinkedHashSet<>();
                Name next = null;
                for (Element m : superMethods) superMethodNames.add(m.getSimpleName());
                for (Element m : methods) {
                    if (next != null) {
                        if (m.getSimpleName().equals(next)) {
                            merged.add(m);
                            continue;
                        }
                        for (Element sm : superMethods) {
                            if (sm.getSimpleName().equals(next)) merged.add(sm);
                        }
                        superMethodNames.remove(next);
                        next = null;
                    }
                    Name name = m.getSimpleName();
                    if (superMethodNames.contains(name)) next = name;
                    merged.add(m);
                }
                if (next != null) {
                    for (Element sm : superMethods) {
                        if (sm.getSimpleName().equals(next)) merged.add(sm);
                    }
                }
                methods = merged;
            }
        }

        StringBuilder s = new StringBuilder(genComment(type))
            .append("const ").append(getClassName(false)).append(": Java");

        String statics = gapper(genStaticFields(fields), genStaticMethods(methods));
        if (type.getKind().isInterface()) {
            s.append("InterfaceStatics<").append(getClassName(false));
            List<? extends TypeParameterElement> params = this.type.getTypeParameters();
            if (params != null && !params.isEmpty()) {
                s.append("<");
                for (TypeParameterElement param : params) s.append("any, ");
                s.setLength(s.length() - 2);
                s.append(">");
            }
            s.append(">");
            if (!statics.isEmpty()) {
                s.append(" & {\n")
                    .append(StringHelpers.tabIn(statics))
                .append("}\n");
            } else s.append(";\n");
        } else {
            String constrs = genConstructors(constructors);

            s.append("ClassStatics<").append(getClassName(false));
            List<? extends TypeParameterElement> params = this.type.getTypeParameters();
            if (params != null && !params.isEmpty()) {
                s.append("<");
                for (TypeParameterElement param : params) s.append("any, ");
                s.setLength(s.length() - 2);
                s.append(">");
            }
            if (constrs.isEmpty()) s.append("> & NoConstructor");
            else s.append(", ").append(getClassName(false)).append("$$constructor>");

            if (!statics.isEmpty()) {
                s.append(" & {\n")
                    .append(StringHelpers.tabIn(statics))
                .append("}\n");
            } else s.append(";\n");

            if (!constrs.isEmpty()) {
                s.append("interface ").append(getClassName(false))
                        .append("$$constructor extends SuppressProperties {\n\n")
                    .append(StringHelpers.tabIn(constrs)).append("\n")
                .append("}\n");
            }
        }

        s.append("interface ").append(getClassName(true)).append(buildExtends()).append(" {\n")
            .append(StringHelpers.tabIn(gapper(genFields(fields), genMethods(methods))))
        .append("}");

        return s.toString().trim().replaceAll("\\{[\n ]+\\}", "{}").replaceAll("\n\n\n+", "\n\n");
    }

}
