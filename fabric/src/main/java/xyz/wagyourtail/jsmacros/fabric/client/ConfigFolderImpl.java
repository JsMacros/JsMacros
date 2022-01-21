package xyz.wagyourtail.jsmacros.fabric.client;

import net.fabricmc.loader.api.FabricLoader;
import xyz.wagyourtail.jsmacros.client.ConfigFolder;

import java.io.File;

public class ConfigFolderImpl implements ConfigFolder {

    @Override
    public File getFolder() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), "jsMacros");
    }

}
