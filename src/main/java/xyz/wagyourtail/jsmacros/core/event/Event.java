package xyz.wagyourtail.jsmacros.core.event;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Event {
    String value();
    String oldName() default "";
}
