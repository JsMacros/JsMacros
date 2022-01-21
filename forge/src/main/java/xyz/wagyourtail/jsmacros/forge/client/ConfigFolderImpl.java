package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.client.ConfigFolder;

import java.io.File;

public class ConfigFolderImpl implements ConfigFolder {
    @Override
    public File getFolder() {
        return new File(MinecraftClient.getInstance().runDirectory, "config/jsMacros");
    }

}
