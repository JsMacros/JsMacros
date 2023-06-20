package xyz.wagyourtail.jsmacros.core.config;

public @interface OptionType {
    String value() default "primitive";

    String[] options() default {};

}
