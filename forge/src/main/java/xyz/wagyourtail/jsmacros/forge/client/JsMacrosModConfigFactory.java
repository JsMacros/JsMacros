package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.fml.client.IModGuiFactory;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.screens.KeyMacrosScreen;

import java.util.Set;

public class JsMacrosModConfigFactory implements IModGuiFactory {

    @Override
    public void initialize(MinecraftClient arg) {

    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public Screen createConfigGui(Screen arg) {
        if (JsMacros.prevScreen instanceof KeyMacrosScreen) {
            JsMacros.prevScreen.parent = arg;
        }
        return JsMacros.prevScreen;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
