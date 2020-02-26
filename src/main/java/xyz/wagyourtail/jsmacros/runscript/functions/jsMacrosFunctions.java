package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.profile.Profile;

public class jsMacrosFunctions {
    
    public MinecraftClient getMinecraft() {
        return jsMacros.getMinecraft();
    }
    
    public Profile getProfile() {
        return jsMacros.profile;
    }
    
    public ConfigManager getConfig() {
        return jsMacros.config;
    }
}