package xyz.wagyourtail.jsmacros.client;

import xyz.wagyourtail.jsmacros.client.api.helpers.ModContainerHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface ModLoader {

    boolean isDevEnv();

    String getName();

    List<? extends ModContainerHelper<?>> getLoadedMods();

}
