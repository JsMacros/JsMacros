package xyz.wagyourtail.jsmacros.client;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.client.gui.screens.KeyMacrosScreen;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;

public class ModMenuEntry implements ModMenuApi {
    private final JsMacroScreen jsmacrosscreenfactory = new JsMacroScreen();
    
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return jsmacrosscreenfactory;
    }
    
    public static class JsMacroScreen implements ConfigScreenFactory<MacroScreen> {
        @Override
        public MacroScreen create(Screen parent) {
            return new KeyMacrosScreen(parent);
        }
    }
}
