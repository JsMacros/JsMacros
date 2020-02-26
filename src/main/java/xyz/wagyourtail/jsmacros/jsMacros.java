package xyz.wagyourtail.jsmacros;


import java.util.Objects;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.gui.MacroListScreen;

public class jsMacros implements ClientModInitializer {
    public static final String MOD_ID = "jsmacros";
    public static ConfigManager config = new ConfigManager();
    public static Profile profile;
    public static MacroListScreen macroListScreen;

    @Override
    public void onInitializeClient() {
        // TODO Auto-generated method stub
        config.loadConfig();
        profile = new Profile(config.options.defaultProfile);
        macroListScreen = new MacroListScreen(null);
    }

    
    static public String getLocalizedName(InputUtil.KeyCode keyCode) {
        String string = keyCode.getName();
        int i = keyCode.getKeyCode();
        String string2 = null;
        switch(keyCode.getCategory()) {
        case KEYSYM:
           string2 = InputUtil.getKeycodeName(i);
           break;
        case SCANCODE:
           string2 = InputUtil.getScancodeName(i);
           break;
        case MOUSE:
           String string3 = I18n.translate(string);
           string2 = Objects.equals(string3, string) ? I18n.translate(InputUtil.Type.MOUSE.getName(), i + 1) : string3;
        }

        return string2 == null ? I18n.translate(string) : string2;
     }
    
    static public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }
}