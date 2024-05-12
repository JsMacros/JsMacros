package xyz.wagyourtail.jsmacros.core.classes;

/**
 * @param <R> the return value of the methods. it's usually the self.
 * @author aMelonRind
 * @since 1.9.1
 */
public interface Registrable<R> {

    R register();

    // throws exception because of CommandBuilder
    R unregister() throws Exception;

}
