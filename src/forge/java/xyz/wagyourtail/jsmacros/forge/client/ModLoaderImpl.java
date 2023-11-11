package xyz.wagyourtail.jsmacros.forge.client;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import xyz.wagyourtail.jsmacros.client.ModLoader;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.ForgeModContainer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public class ModLoaderImpl implements ModLoader {

    @Override
    public boolean isDevEnv() {
        return !FMLEnvironment.production;
    }

    @Override
    public String getName() {
        return "Forge";
    }

    @Override
    public List<ForgeModContainer> getLoadedMods() {
        return ModList.get().getMods().stream().map(ForgeModContainer::new).collect(Collectors.toList());
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public ForgeModContainer getMod(String modId) {
        return ModList.get().getModContainerById(modId).map(c -> new ForgeModContainer(c.getModInfo())).orElse(null);
    }

}
