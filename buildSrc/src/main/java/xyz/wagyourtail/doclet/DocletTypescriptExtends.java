package xyz.wagyourtail.doclet;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface DocletTypescriptExtends {
    String value();
}
