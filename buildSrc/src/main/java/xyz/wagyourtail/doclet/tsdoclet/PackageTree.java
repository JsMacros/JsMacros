package xyz.wagyourtail.doclet.tsdoclet;

import xyz.wagyourtail.StringHelpers;
import xyz.wagyourtail.doclet.tsdoclet.parsers.ClassParser;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.*;

public class PackageTree {
    public final static Set<String> predefinedClasses = Set.of(
        "java.util.Collection", "java.util.List", "java.util.Map", "java.util.Set",
        "java.io.File", "java.net.URL", "java.net.URI", "java.lang.Object", "java.lang.Class",
        "java.lang.Throwable", "java.io.Serializable", "java.lang.StackTraceElement",
        "java.lang.Iterable"
    );
    // List of reserved keywords #2536
    // https://github.com/microsoft/TypeScript/issues/2536
    public final static Set<String> tsReservedWords = Set.of(
        "break", "case", "catch", "class", "const", "continue", "debugger", "default",
        "delete", "do", "else", "enum", "export", "extends", "false", "finally", "for",
        "function", "if", "import", "in", "instanceof", "new", "null", "return", "super",
        "switch", "this", "throw", "true", "try", "typeof", "var", "void", "while", "with"
    );
    private String pkgName;
    private Map<String, PackageTree> children = new LinkedHashMap<>();
    private Set<ClassParser> classes = new LinkedHashSet<>();
    private Map<ClassParser, String> compiledClasses = new LinkedHashMap<>();
    private Set<String> redirects = new HashSet<>();

    public boolean dirty = true;

    public PackageTree(String pkgName) {
        this.pkgName = pkgName;
    }

    public void addClass(Element clazz) {
        Stack<String> enclosing = new Stack<>();
        Element enclose = clazz;

        while (enclose != null && enclose.getKind() != ElementKind.PACKAGE) enclose = enclose.getEnclosingElement();

        if (enclose != null) {
            String[] pkg = ((PackageElement)enclose).getQualifiedName().toString().split("\\.");
            for (int i = pkg.length - 1; i >= 0; --i) {
                if (pkg[i].equals("")) continue;
                enclosing.push(pkg[i]);
            }
            if (predefinedClasses.contains(String.join(".", pkg) + "." + clazz.getSimpleName())) return;
        }
        addClassInternal(enclosing, clazz);
    }

    private boolean addClassInternal(Stack<String> enclosing, Element clazz) {
        if (enclosing.empty()) {
            this.dirty = classes.add(new ClassParser((TypeElement) clazz)) || this.dirty;
        } else {
            this.dirty = children.computeIfAbsent(enclosing.pop(), PackageTree::new)
                .addClassInternal(enclosing, clazz) || this.dirty;
        }
        return this.dirty;
    }

    private void prepareTSTree() {
        while (this.dirty) {
            this.dirty = false;
            for (ClassParser aClass : Set.copyOf(classes)) {
                redirects.add(aClass.getClassName(false));
            }
            for (ClassParser aClass : Set.copyOf(classes)) {
                if (aClass.redirects.addAll(redirects)) {
                    compiledClasses.put(aClass, aClass.genTSInterface());
                } else {
                    compiledClasses.computeIfAbsent(aClass, ClassParser::genTSInterface);
                }
            }
            for (PackageTree value : Set.copyOf(children.values())) {
                if (value.redirects.addAll(redirects)) value.dirty = true;
                value.prepareTSTree();
            }
        }
    }

    public String genTSTree() {
        prepareTSTree();
        return genTSTreeIntern().replaceAll("\\bPackages\\.", "");
    }

    private String genTSTreeIntern() {
        if (classes.size() == 0 && children.size() == 1) {
            PackageTree onlyChild = children.values().stream().findFirst().get();
            if (!tsReservedWords.contains(onlyChild.pkgName)) {
                onlyChild.pkgName = pkgName + "." + onlyChild.pkgName;
                return onlyChild.genTSTreeIntern();
            }
        }

        StringBuilder s = new StringBuilder("namespace ");
        if (tsReservedWords.contains(pkgName)) {
            System.out.println("Escaped typescript reserved word " + pkgName + " -> _" + pkgName);
            s.append("_");
        }
        s.append(pkgName).append(" {");

        for (String value : compiledClasses.values()) {
            s.append("\n\n").append(StringHelpers.tabIn(value));
        }
        Set<PackageTree> escapes = new LinkedHashSet<>();
        for (PackageTree value : children.values()) {
            if (tsReservedWords.contains(value.pkgName)) {
                escapes.add(value);
                continue;
            }
            s.append("\n\n").append(StringHelpers.tabIn(value.genTSTreeIntern()));
        }

        if (s.charAt(s.length() - 1) == '{') s.setLength(0);
        else s.append("\n\n}");

        if (!escapes.isEmpty()) {
            if (s.length() > 0) s.append("\n");
            s.append("namespace ").append(pkgName).append(" {");
            for (PackageTree value : escapes) {
                s.append("\n\n").append("    export { _").append(value.pkgName).append(" as ")
                    .append(value.pkgName).append(" };\n")
                    .append(StringHelpers.tabIn(value.genTSTreeIntern()));
            }
            s.append("\n\n}");
        }

        return s.toString();
    }

    public List<ClassParser> getXyzClasses() {
        for (PackageTree value : children.values()) {
            if (value.pkgName.equals("xyz")) return value.getAllClasses();
        }
        return null;
    }

    public List<ClassParser> getAllClasses() {
        List<ClassParser> result = new ArrayList<>(classes);
        for (PackageTree value : children.values()) {
            result.addAll(value.getAllClasses());
        }
        return result;
    }
}
