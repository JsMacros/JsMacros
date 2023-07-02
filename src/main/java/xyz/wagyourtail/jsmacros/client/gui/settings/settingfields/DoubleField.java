package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;
import xyz.wagyourtail.wagyourgui.BaseScreen;
import xyz.wagyourtail.wagyourgui.elements.TextInput;

import java.lang.reflect.InvocationTargetException;

public class DoubleField extends AbstractSettingField<Double> {

    public DoubleField(int x, int y, int width, TextRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<Double> field) {
        super(x, y, width, textRenderer.fontHeight + 2, textRenderer, parent, field);
    }

    @Override
    public void init() {
        super.init();
        try {
            TextInput doubleIn = addDrawableChild(new TextInput(x + width / 2, y, width / 2, height, textRenderer, 0xFF101010, 0, 0xFF4040FF, 0xFFFFFF, setting.get().toString(), null, (value) -> {
                try {
                    if (value.equals("")) {
                        value = "0";
                    }
                    setting.set(Double.parseDouble(value));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }));
            doubleIn.mask = "\\d*(\\.\\d*)";
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        for (ClickableWidget btn : buttons) {
            btn.setY(y);
        }
    }

    @Override
    public void render(MatrixStack drawContext, int mouseX, int mouseY, float delta) {
        textRenderer.draw(drawContext, BaseScreen.trimmed(textRenderer, settingName, width / 2), x, y + 1, 0xFFFFFF);
    }

}
