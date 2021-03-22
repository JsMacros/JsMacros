package xyz.wagyourtail.jsmacros.client.gui.settings.settingtypes;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.jsmacros.client.gui.elements.AnnotatedCheckBox;
import xyz.wagyourtail.jsmacros.client.gui.settings.AbstractSettingGroupContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

import java.lang.reflect.InvocationTargetException;

public class BooleanField  extends AbstractSettingType<Boolean> {
    
    
    public BooleanField(int x, int y, int width, TextRenderer textRenderer, AbstractSettingGroupContainer parent, SettingsOverlay.SettingField<Boolean> field) {
        super(x, y, width, textRenderer.fontHeight + 2, textRenderer, parent, field);
    }
    
    @Override
    public void init() {
        super.init();
        try {
            this.addButton(new AnnotatedCheckBox(x, y, width, height, textRenderer, 0xFFFFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, settingName, setting.get(), button -> {
                try {
                    setting.set(((AnnotatedCheckBox)button).value);
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
        for (AbstractButtonWidget btn : buttons) {
            btn.y = y;
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    
    }
    
}
