package xyz.wagyourtail.jsmacros.client.gui.settings.settingtypes;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.AbstractSettingGroupContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

public abstract class AbstractSettingType<T> extends MultiElementContainer<AbstractSettingGroupContainer> {
    protected final SettingsOverlay.SettingField<T> setting;
    protected final Text settingName;
    public AbstractSettingType(int x, int y, int width, int height, TextRenderer textRenderer, AbstractSettingGroupContainer parent, SettingsOverlay.SettingField<T> field) {
        super(x, y, width, height, textRenderer, parent);
        setting = field;
        settingName = new TranslatableText(field.option.translationKey());
        init();
    }
    
}
