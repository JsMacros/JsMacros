package xyz.wagyourtail.jsmacros.core.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @author aMelonRind
 * @since 1.9.0
 */
public interface EventFilterer extends Predicate<BaseEvent> {

    @NotNull
    Class<? extends BaseEvent> dedicatedFor();

    @Nullable
    default String getDedicatedEventName() {
        Event a = dedicatedFor().getAnnotation(Event.class);
        return a == null ? null : a.value();
    }

}
