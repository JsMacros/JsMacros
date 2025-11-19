package xyz.wagyourtail.jsmacros.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.wagyourgui.BaseScreen;

public class ModMenuEntry implements ModMenuApi {
    private final JsMacroScreen jsmacrosscreenfactory = new JsMacroScreen();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return jsmacrosscreenfactory;
    }

    public static class JsMacroScreen implements ConfigScreenFactory<BaseScreen> {
        @Override
        public BaseScreen create(Screen parent) {
            return JsMacrosClient.prevScreen;
        }

    }

}
