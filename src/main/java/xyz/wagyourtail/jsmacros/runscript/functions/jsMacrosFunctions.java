package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.runscript.RunScript;
import xyz.wagyourtail.jsmacros.runscript.RunScript.thread;

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

    public HashMap<RawMacro, ArrayList<thread>> getRunningThreads() {
        return RunScript.threads;
    }

    public String mcVersion() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.getGameVersion();
    }

    public void runScript(String file) {
        runScript(file, null);
    }

    public void runScript(String file, Consumer<String> callback) {
        if (callback != null) {
            RunScript.exec(new RawMacro(MacroEnum.EVENT, "", file, true), "", null, () -> {
                callback.accept(null);
            }, callback);
        } else {
            RunScript.exec(new RawMacro(MacroEnum.EVENT, "", file, true), "", null);
        }
    }
}