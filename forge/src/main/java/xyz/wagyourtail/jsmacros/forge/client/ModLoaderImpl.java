package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import xyz.wagyourtail.jsmacros.client.ModLoader;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.ForgeModContainer;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
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
        return ModList.get().getMods().stream().map(ForgeModContainer::new).toList();
    }

}
