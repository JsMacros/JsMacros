package xyz.wagyourtail.doclet;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface DocletDeclareType {
    String name();
    String type();
}
