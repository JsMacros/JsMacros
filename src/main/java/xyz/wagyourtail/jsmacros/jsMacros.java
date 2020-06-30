package xyz.wagyourtail.jsmacros;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.python.util.PythonInterpreter;

import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.gui.MacroListScreen;
import xyz.wagyourtail.jsmacros.gui2.KeyMacrosScreen;

public class jsMacros implements ClientModInitializer {
    public static final String MOD_ID = "jsmacros";
    public static ConfigManager config = new ConfigManager();
    public static Profile profile;
    public static MacroListScreen macroListScreen;
    public static KeyMacrosScreen keyMacrosScreen;
    
    @Override
    public void onInitializeClient() {
        config.loadConfig();
        profile = new Profile(config.options.defaultProfile);
        macroListScreen = new MacroListScreen(null);
        keyMacrosScreen = new KeyMacrosScreen(null);
        
        Builder build = Context.newBuilder("js");
        Context con = build.build();
        con.eval("js", "console.log('js loaded.')");
        
        PythonInterpreter interp = new PythonInterpreter();
        interp.eval("print('py loaded.')");
        interp.close();
        
    }

    static public Text getKeyText(String translationKey) {
        try {
            return InputUtil.fromTranslationKey(translationKey).getLocalizedText();
        } catch(Exception e) {
            return new LiteralText(translationKey);
        }
    }
    
    @Deprecated
    static public String getLocalizedName(InputUtil.Key keyCode) {
        return I18n.translate(keyCode.getTranslationKey());
     }
    
    @Deprecated
    static public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }
}