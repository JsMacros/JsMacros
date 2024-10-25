package xyz.wagyourtail.jsmacros.client;

import xyz.wagyourtail.jsmacros.api.helper.ModContainerHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface ModLoader {

    /**
     * @return {@code true} if the game is running in a development environment, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    boolean isDevEnv();

    /**
     * @return the name of the current mod loader.
     * @since 1.8.4
     */
    String getName();

    /**
     * @return a list of all loaded mods.
     * @since 1.8.4
     */
    List<? extends ModContainerHelper<?>> getLoadedMods();

    /**
     * @param modId the mod id to check
     * @return {@code true} if the mod with the given id is loaded, {@code false} otherwise.
     * @since 1.8.4
     */
    boolean isModLoaded(String modId);

    /**
     * @param modId the mod id
     * @return the mod container for the given id or {@code null} if the mod is not loaded.
     * @since 1.8.4
     */
    ModContainerHelper<?> getMod(String modId);

}
