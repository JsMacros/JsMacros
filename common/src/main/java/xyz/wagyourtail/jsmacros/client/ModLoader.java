package xyz.wagyourtail.jsmacros.client;

import xyz.wagyourtail.jsmacros.client.api.helpers.ModContainerHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface ModLoader {

    /**
     * @return {@code true} if the game is running in a development environment, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    boolean isDevEnv();

    /**
     * @return the name of the current mod loader.
     *
     * @since 1.8.4
     */
    String getName();

    /**
     * @return a list of all loaded mods.
     *
     * @since 1.8.4
     */
    List<? extends ModContainerHelper<?>> getLoadedMods();

}
