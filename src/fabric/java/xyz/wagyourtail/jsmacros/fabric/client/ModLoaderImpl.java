package xyz.wagyourtail.jsmacros.fabric.client;

import net.fabricmc.loader.api.FabricLoader;
import xyz.wagyourtail.jsmacros.client.ModLoader;
import xyz.wagyourtail.jsmacros.fabric.client.api.classes.FabricModContainer;

import java.util.List;
import java.util.stream.Collectors;

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
        return FabricLoader.getInstance().getAllMods().stream().map(FabricModContainer::new).collect(Collectors.toList());
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public FabricModContainer getMod(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).map(FabricModContainer::new).orElse(null);
    }

}
