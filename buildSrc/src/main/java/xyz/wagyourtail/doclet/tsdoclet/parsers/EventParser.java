package xyz.wagyourtail.doclet.tsdoclet.parsers;

import xyz.wagyourtail.StringHelpers;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

public class EventParser extends AbstractParser {
    protected String name;

    public EventParser(TypeElement type, String name) {
        super(type);
        this.name = name;
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

        return "interface " + name + " extends BaseEvent {\n" +
            StringHelpers.tabIn(genFields(fields)) + "\n" +
            StringHelpers.tabIn(genMethods(methods)) +
            "\n}";
    }

}
