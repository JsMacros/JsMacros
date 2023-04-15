package xyz.wagyourtail.doclet.tsdoclet.parsers;

import xyz.wagyourtail.StringHelpers;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class EventParser extends AbstractParser {
    protected String name;

    public EventParser(TypeElement type, String name) {
        super(type);
        this.name = name;
        this.isPackage = false;
    }

    public String getName() {
        return name;
    }

    @Override
    public String genTSInterface() {
        Set<Element> fields = new LinkedHashSet<>();
        Set<Element> methods = new LinkedHashSet<>();

        for (Element el : type.getEnclosedElements()) {
            if (el.getModifiers().contains(Modifier.PUBLIC)) {
                switch (el.getKind()) {
                    case FIELD, ENUM_CONSTANT -> fields.add(el);
                    case METHOD -> methods.add(el);
                    default -> {}
                }
            }
        }

        Map<Name, Set<Element>> methodMap = new LinkedHashMap<>();
        for (Element m : methods) {
            methodMap.computeIfAbsent(m.getSimpleName(), k -> new HashSet<>()).add(m);
        }
        outer:
        for (Name name : methodMap.keySet()) {
            if (!objectMethodNames.contains(name)) continue;
            for (Element m : methodMap.get(name)) if (!isObjectMethod(m)) continue outer;
            methods.removeAll(methodMap.get(name));
        }

        return ("interface " + name + " extends BaseEvent {\n\n" +
                StringHelpers.tabIn(genFields(fields)) + "\n" +
                StringHelpers.tabIn(genMethods(methods)) +
            "\n}").replaceAll("\\{[\n ]+\\}", "{}").replaceAll("\n\n\n+", "\n\n");
    }

}
