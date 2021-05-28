package xyz.wagyourtail.doclet.tsdoclet.parsers;

import xyz.wagyourtail.StringHelpers;

import javax.lang.model.element.*;
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

        return "declare namespace " + name + " {\n\n" +
            StringHelpers.tabIn(genMethods(methods)) +
            "\n}";
    }

}
