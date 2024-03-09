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
    private static final Set<String> objectAliases = Set.of("void", "any", "JavaObject", "Object");
    public static Map<TypeElement, Set<TypeElement>> mixinInterfaceMap = new LinkedHashMap<>();
    public final String className;
    private Set<TypeElement> superMcClasses;
    private boolean doesDirectExtendMc = false;
    private Set<TypeElement> mixinInterfaces;

    public ClassParser(TypeElement type) {
        super(type);
        StringBuilder s = new StringBuilder(type.getSimpleName());
        Element enclosing = type.getEnclosingElement();
        while (enclosing.getKind() == ElementKind.INTERFACE || enclosing.getKind() == ElementKind.CLASS) {
            s.insert(0, enclosing.getSimpleName() + "$");
            enclosing = enclosing.getEnclosingElement();
        }
        className = s.toString();
    }

    public String getClassName(boolean typeParams) {
        return getClassName(typeParams, false);
    }

    public String getClassName(boolean typeParams, boolean defaultToAny) {
        if (!typeParams) return className;
        StringBuilder s = new StringBuilder(className);
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
        return s.toString();
    }

    private String getClassHeader() {
        StringBuilder s = new StringBuilder("static readonly class: JavaClass<").append(getClassName(false));
        int params = type.getTypeParameters().size();
        if (params > 0) {
            s.append("<").append(", any".repeat(params).substring(2)).append(">");
        }
        s.append(">;\n/** @deprecated */ static prototype: undefined;\n");
        return s.toString();
    }

    private String buildExtends() {
        StringBuilder s = new StringBuilder(" extends ");
        String sup = transformType(type.getSuperclass(), false, true);
        if (objectAliases.contains(sup)) {
            s.append("java.lang.Object");
        } else if (sup.startsWith("/* net.minecraft")) {
            s.append(sup, 0, sup.length() - 3).append("java.lang.Object");
        } else {
            s.append(sup);
        }
        return s.toString();
    }

    private String buildImplements() {
        Set<TypeMirror> interfaces = new HashSet<>(type.getInterfaces());
        if (doesDirectExtendMc) for (TypeElement e : mixinInterfaces) {
            interfaces.add(e.asType());
            System.out.println("Added mixin interface " + e.getSimpleName() + " on class " + type.getSimpleName());
        }
        if (interfaces.isEmpty()) return "";

        Set<String> strings = new TreeSet<>();
        for (TypeMirror ifa : interfaces) {
            strings.add(transformType(ifa, false, true));
        }

        StringBuilder s = new StringBuilder(" extends ");
        for (String sup : strings) {
            if (sup.startsWith("/* net.minecraft")) {
                s.append(sup, 0, sup.length() - 3).append("JavaObject");
            } else {
                s.append(sup);
            }
            s.append(", ");
        }
        s.setLength(s.length() - 2);
        return s.toString();
    }

    private void getSuperClasses(Set<TypeElement> set, TypeElement c) {
        if (!c.getKind().isInterface()) {
            TypeMirror t = c.getSuperclass();
            if (t instanceof DeclaredType) {
                TypeElement e = (TypeElement) ((DeclaredType) t).asElement();
                if (transformType(t).startsWith("/* net.minecraft")) {
                    superMcClasses.add(e);
                    if (doesDirectExtendMc && mixinInterfaceMap.containsKey(e)) {
                        Set<TypeElement> ifs = mixinInterfaceMap.get(e);
                        set.addAll(ifs);
                        mixinInterfaces.addAll(ifs);
                    }
                } else {
                    set.add(e);
                }
                getSuperClasses(set, e);
            }
        }

        for (TypeMirror t : c.getInterfaces()) {
            TypeElement e = (TypeElement) ((DeclaredType) t).asElement();
            if (transformType(t).startsWith("/* net.minecraft")) {
                superMcClasses.add(e);
            } else {
                set.add(e);
            }
            getSuperClasses(set, e);
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
        getSuperClasses(superClasses, type);

        for (Element el : type.getEnclosedElements()) {
            if (el.getModifiers().contains(Modifier.PUBLIC)) {
                switch (el.getKind()) {
                    case METHOD -> {
                        if (!isObfuscated(el) && !isObjectMethod(el)) methods.add(el);
                    }
                    case FIELD, ENUM_CONSTANT -> fields.add(el);
                    case CONSTRUCTOR -> constructors.add(el);
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

        StringBuilder s = new StringBuilder(genComment(type));

        String className = getClassName(true);
        String temp;
        if (type.getKind().isInterface()) {
            s.append("abstract class ").append(className).append(" extends java.lang.Interface {\n")
                    .append(StringHelpers.tabIn(getClassHeader()));
            if (!(temp = genStaticFields(fields)).isEmpty()) s.append("\n").append(StringHelpers.tabIn(temp));
            if (!(temp = genStaticMethods(methods)).isEmpty()) s.append("\n").append(StringHelpers.tabIn(temp)).append("\n");
            s.append("}\ninterface ").append(className).append(buildImplements()).append(" {\n");
            int len = s.length();
            if (!(temp = genFields(fields)).isEmpty()) s.append(StringHelpers.tabIn(temp));
            if (!(temp = genMethods(methods)).isEmpty()) s.append("\n").append(StringHelpers.tabIn(temp)).append("\n");
            if (len == s.length()) s.delete(len - 1, len);
            s.append("}");
        } else {
            //noinspection SpellCheckingInspection
            String constrs = genConstructors(constructors);
            String implementS = buildImplements();
            if (!implementS.isEmpty()) s.append("interface ").append(className).append(implementS).append(" {}\n");
            if (constrs.isBlank()) s.append("abstract ");
            s.append("class ").append(className).append(buildExtends()).append(" {\n")
                    .append(StringHelpers.tabIn(getClassHeader()));
            if (!(temp = genStaticFields(fields)).isEmpty()) s.append("\n").append(StringHelpers.tabIn(temp));
            int len = s.length();
            if (!(temp = genStaticMethods(methods)).isEmpty()) s.append("\n").append(StringHelpers.tabIn(temp));
            if (!constrs.isEmpty()) s.append("\n").append(StringHelpers.tabIn(constrs));
            if (!(temp = genFields(fields)).isEmpty()) {
                s.append("\n").append(StringHelpers.tabIn(temp));
                len = s.length();
            }
            if (!(temp = genMethods(methods)).isEmpty()) s.append("\n").append(StringHelpers.tabIn(temp));
            if (len != s.length()) s.append("\n");
            s.append("}");
        }

        return s.toString().replaceAll("\n\n\n+", "\n\n");
    }

}
