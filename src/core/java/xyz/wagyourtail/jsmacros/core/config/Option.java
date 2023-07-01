package xyz.wagyourtail.jsmacros.core.config;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Option {
    String translationKey();

    String[] group();

    String setter() default "";

    String getter() default "";

    String options() default "";

    OptionType type() default @OptionType;

}
