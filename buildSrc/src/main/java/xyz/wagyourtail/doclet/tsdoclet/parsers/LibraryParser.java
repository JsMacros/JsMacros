package xyz.wagyourtail.doclet.tsdoclet.parsers;

import xyz.wagyourtail.StringHelpers;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

public class LibraryParser extends AbstractParser {
    protected String name;

    public LibraryParser(TypeElement type, String name) {
        super(type);
        this.name = name;
    }

    @Override
    public String genComment(Element comment) {
        return super.genComment(comment) + "function ";
    }

    @Override
    public String genTSInterface() {
        Set<Element> methods = new LinkedHashSet<>();

        for (Element el : type.getEnclosedElements()) {
            if (el.getModifiers().contains(Modifier.PUBLIC) && el.getKind() == ElementKind.METHOD) {
                methods.add(el);
            }
        }

        return "namespace " + name + " {\n" +
            StringHelpers.tabIn(genMethods(methods)) +
            "\n}";
    }

}
