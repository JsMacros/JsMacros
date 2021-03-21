package xyz.wagyourtail.jsmacros.client.gui.settings.settingtypes;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.jsmacros.client.gui.elements.TextInput;
import xyz.wagyourtail.jsmacros.client.gui.settings.AbstractSettingGroupContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

import java.lang.reflect.InvocationTargetException;

public class IntField extends AbstractSettingType<Integer> {
    
    public IntField(int x, int y, int width, TextRenderer textRenderer, AbstractSettingGroupContainer parent, SettingsOverlay.SettingField<Integer> field) {
        super(x, y, width, textRenderer.fontHeight + 2, textRenderer, parent, field);
    }
    
    @Override
    public void init() {
        super.init();
        try {
            TextInput intIn = addButton(new TextInput(x + width / 2, y, width / 2, height, textRenderer, 0xFF101010, 0, 0xFF4040FF, 0xFFFFFF, setting.get().toString(), null, (value) -> {
                try {
                    setting.set(Integer.parseInt(value));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }));
            intIn.mask = "\\d*";
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        textRenderer.drawTrimmed(settingName, x, y + 1, width / 2, 0xFFFFFF);
    }
    
}
