package xyz.wagyourtail.jsmacros.core.library;

import java.lang.annotation.*;

/**
 * Base Function interface.
 * 
 * @author Wagyourtail
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Library {
    String value();
    String[] onlyAllow() default {};
}
