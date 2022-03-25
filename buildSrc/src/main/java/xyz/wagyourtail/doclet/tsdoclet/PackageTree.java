package xyz.wagyourtail.doclet.tsdoclet;

import xyz.wagyourtail.StringHelpers;
import xyz.wagyourtail.doclet.tsdoclet.parsers.ClassParser;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.*;

public class PackageTree {
    public final static List<String> predefinedClasses = List.of("java.util.Collection", "java.util.List", "java.util.Map", "java.util.Set", "java.io.File", "java.net.URL", "java.net.URI", ".Object", ".Class", ".Throwable", "java.io.Serializable", ".StackTraceElement");
    private String pkgName;
    private Map<String, PackageTree> children = new LinkedHashMap<>();
    private Set<ClassParser> classes = new LinkedHashSet<>();
    private Map<ClassParser, String> compiledClasses = new LinkedHashMap<>();

    public boolean dirty = true;

    public PackageTree(String pkgName) {
        this.pkgName = pkgName;
    }

    public void addClass(Element clazz) {
        Stack<String> enclosing = new Stack<>();
        Element enclose = clazz;

        while (enclose != null && enclose.getKind() != ElementKind.PACKAGE) enclose = enclose.getEnclosingElement();

        if (enclose != null) {
            String[] pkg = ((PackageElement)enclose).getQualifiedName().toString().replace("java.lang", "").replace(".function.", "._function.").split("\\.");
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
            String enc = enclosing.pop();
            if (enc.equals("function")) enc = "_function";
            this.dirty = children.computeIfAbsent(enc, PackageTree::new)
                .addClassInternal(enclosing, clazz) || this.dirty;
        }
        return this.dirty;
    }

    private void prepareTSTree() {
        while (this.dirty) {
            this.dirty = false;
            for (ClassParser aClass : Set.copyOf(classes)) {
                compiledClasses.computeIfAbsent(aClass, ClassParser::genTSInterface);
            }
            for (PackageTree value : Set.copyOf(children.values())) {
                value.prepareTSTree();
            }
        }
    }

    public String genTSTree() {
        prepareTSTree();
        return genTSTreeIntern();
    }

    private String genTSTreeIntern() {
        if (classes.size() == 0 && children.size() == 1) {
            PackageTree onlyChild = children.values().stream().findFirst().get();
            onlyChild.pkgName = pkgName + "." + onlyChild.pkgName;
            return onlyChild.genTSTreeIntern();
        }
        StringBuilder s = new StringBuilder("export namespace ");
        s.append(pkgName).append(" {\n");
        for (String value : compiledClasses.values()) {
            s.append("\n").append(StringHelpers.tabIn(value));
        }
        for (PackageTree value : children.values()) {
            s.append("\n\n").append(StringHelpers.tabIn(value.genTSTreeIntern()));
        }
        s.append("\n}");
        return s.toString();
    }
}
