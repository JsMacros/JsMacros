package xyz.wagyourtail.jsmacros.core.event;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Event {
    String value();

    String oldName() default "";

    boolean cancellable() default false;

    boolean joinable() default false;

    Class<? extends EventFilterer> filterer() default EventFilterer.class;

}
