package xyz.wagyourtail.doclet.tsdoclet.parsers;

import xyz.wagyourtail.StringHelpers;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

public class LibraryParser extends AbstractParser {
    protected final String name;

    public LibraryParser(TypeElement type, String name) {
        super(type);
        this.name = name;
        this.isPackage = false;
    }

    @Override
    public String genComment(Element comment) {
        return super.genComment(comment) + "function ";
    }

    @Override
    public String genTSInterface() {
        String comment = super.genComment(type).trim()
            .replaceAll("\n \\*  An instance of this class is passed to scripts as the `\\w+` variable\\.", "");
        StringBuilder s = new StringBuilder(comment);
        if (!s.isEmpty()) s.append("\n");

        Set<Element> methods = new LinkedHashSet<>();

        for (Element el : type.getEnclosedElements()) {
            if (checkModifier(el, false) && el.getKind() == ElementKind.METHOD) {
                methods.add(el);
            }
        }

        s.append("declare namespace ").append(name).append(" {\n")
                .append(StringHelpers.tabIn(genMethods(methods)))
                .append("\n}");

        return s.toString();
    }

}
