package xyz.wagyourtail.jsmacros.gui2;

import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.profile.Profile;

import net.minecraft.client.gui.screen.Screen;

public class KeyMacrosScreen extends MacroScreen {

    public KeyMacrosScreen(Screen parent) {
        super(parent);
    }

    public void init() {
        super.init();
        eventScreen.onPress = (btn) -> {
            client.openScreen(new EventMacrosScreen(this));
        };

        profileScreen.onPress = (btn) -> {
            client.openScreen(new ProfileScreen(this));
        };

        for (RawMacro macro : Profile.registry.getMacros().get("KEY").keySet()) {
            addMacro(macro);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (MacroContainer macro : macros) {
            if (!macro.keyPressed(keyCode, scanCode, modifiers)) return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (MacroContainer macro : macros) {
            if (!macro.mouseClicked(mouseX, mouseY, button)) return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
