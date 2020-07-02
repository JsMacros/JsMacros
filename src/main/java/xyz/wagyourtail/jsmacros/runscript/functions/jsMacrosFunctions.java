package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

public class jsMacrosFunctions {
    
    public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }
    
    public Profile getProfile() {
        return jsMacros.profile;
    }
    
    public ConfigManager getConfig() {
        return jsMacros.config;
    }
    
    public HashMap<RawMacro, ArrayList<Thread>> getRunningThreads() {
        return RunScript.threads;
    }
    
    public String mcVersion() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.getGameVersion();
    }
}