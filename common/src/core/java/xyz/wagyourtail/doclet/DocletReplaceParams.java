package xyz.wagyourtail.doclet;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface DocletReplaceParams {
    String value();
}
