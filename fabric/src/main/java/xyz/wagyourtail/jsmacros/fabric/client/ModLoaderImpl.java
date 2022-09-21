package xyz.wagyourtail.jsmacros.fabric.client;

import net.fabricmc.loader.api.FabricLoader;
import xyz.wagyourtail.jsmacros.client.ModLoader;
import xyz.wagyourtail.jsmacros.fabric.client.api.classes.FabricModContainer;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public class ModLoaderImpl implements ModLoader {
    @Override
    public boolean isDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public String getName() {
        return "Fabric";
    }

    @Override
    public List<FabricModContainer> getLoadedMods() {
        return FabricLoader.getInstance().getAllMods().stream().map(FabricModContainer::new).toList();
    }
}
