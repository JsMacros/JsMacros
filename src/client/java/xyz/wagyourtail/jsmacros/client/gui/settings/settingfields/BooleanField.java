package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;
import xyz.wagyourtail.wagyourgui.elements.AnnotatedCheckBox;

import java.lang.reflect.InvocationTargetException;

public class BooleanField extends AbstractSettingField<Boolean> {

    public BooleanField(int x, int y, int width, TextRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<Boolean> field) {
        super(x, y, width, textRenderer.fontHeight + 2, textRenderer, parent, field);
    }

    @Override
    public void init() {
        super.init();
        try {
            this.addDrawableChild(new AnnotatedCheckBox(x, y, width, height, textRenderer, 0xFFFFFFFF, 0xFF242424, 0x7FFFFFFF, 0xFFFFFFFF, settingName, setting.get(), button -> {
                try {
                    setting.set(((AnnotatedCheckBox) button).value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }));
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
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {

    }

}
