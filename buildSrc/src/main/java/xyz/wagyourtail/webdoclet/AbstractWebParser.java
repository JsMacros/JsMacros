package xyz.wagyourtail.webdoclet;

import com.sun.javadoc.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AbstractWebParser {
    public final ClassDoc clazz;
    
    public AbstractWebParser(ClassDoc clazz) {
        this.clazz = clazz;
    }
    
    public XMLBuilder parseClass() {
        XMLBuilder builder = new XMLBuilder("body");
        
        
        return builder;
    }
    
    public static String getURL(Type type) {
        return "";
    }
    
    public static XMLBuilder parseType(Type type) {
        XMLBuilder builder = new XMLBuilder("div", true).addStringOption("class", "typeParameter");
        XMLBuilder typeLink;
        builder.append(typeLink = new XMLBuilder("a", true).addStringOption("href", getURL(type))).append(type.typeName());
        
        if (typeLink.options.get("href") == null) typeLink.addOption("class", "type deadType");
        else typeLink.addOption("class", "Type");
        
        ParameterizedType ptype = type.asParameterizedType();
        if (ptype != null && !ptype.qualifiedTypeName().startsWith("net.minecraft")) {
            builder.append("<");
            Type[] types = ptype.typeArguments();
            int i;
            for (i = 0; i < types.length - 1; ++i) {
                builder.append(parseType(type));
                builder.append(", ");
            }
            builder.append(parseType(types[i]));
            builder.append(">");
        }
        return builder;
    }
    
    public static XMLBuilder createDescription(AnnotationDesc[] annotations) {
        XMLBuilder builder = new XMLBuilder("p").addStringOption("class", "description");
        
        return builder;
    }
    
    public XMLBuilder parseMethod(MethodDoc methodDoc) {
        XMLBuilder method = new XMLBuilder("div").addStringOption("class", "method classItem");
        XMLBuilder methodTitle;
        method.append(methodTitle = new XMLBuilder("h2", true).addStringOption("class", "methodTitle classItemTitle"));
        methodTitle.append(".", methodDoc.name(), "(",
            new XMLBuilder("div", true).addStringOption("class", "methodParams").append(Arrays.stream(methodDoc.parameters()).map(Parameter::name).collect(Collectors.joining(", "))),
        ")");
        XMLBuilder paramTable;
        method.append(new XMLBuilder("table").addStringOption("class", "paramTable").append(
            new XMLBuilder("thead").append(
                new XMLBuilder("th").append("Parameter"),
                new XMLBuilder("th").append("Type"),
                new XMLBuilder("th").append("Description")
            ),
            paramTable = new XMLBuilder("tbody")
        ));
        for (Parameter param : methodDoc.parameters()) {
            paramTable.append(new XMLBuilder("tr").append(
                new XMLBuilder("td").append(param.name()),
                new XMLBuilder("td").append(parseType(param.type())),
                new XMLBuilder("td").append(createDescription(param.annotations()))
            ));
        }
        return method;
    }
    
    public XMLBuilder parseField(FieldDoc fieldDoc) {
        XMLBuilder field = new XMLBuilder("div").addStringOption("class", "field classItem");
        XMLBuilder fieldTitle;
        field.append(fieldTitle = new XMLBuilder("h2").addStringOption("class", "fieldTitle classItemTitle"));
        
        
        return field;
    }
}
