package xyz.wagyourtail.jsmacros;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.gui.macros.KeyMacrosScreen;
import xyz.wagyourtail.jsmacros.gui.macros.MacroScreen;

public class ModMenuEntry implements ModMenuApi {
    private final JsMacroScreen jsmacrosscreenfactory = new JsMacroScreen();
    
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return jsmacrosscreenfactory;
    }
    
    public class JsMacroScreen implements ConfigScreenFactory<MacroScreen> {
        @Override
        public MacroScreen create(Screen parent) {
            return new KeyMacrosScreen(parent);
        }
    }
}
