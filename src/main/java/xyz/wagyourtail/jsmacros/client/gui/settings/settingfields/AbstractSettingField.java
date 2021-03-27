package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

public abstract class AbstractSettingField<T> extends MultiElementContainer<AbstractSettingContainer> {
    protected final SettingsOverlay.SettingField<T> setting;
    protected final Text settingName;
    public AbstractSettingField(int x, int y, int width, int height, TextRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<T> field) {
        super(x, y, width, height, textRenderer, parent);
        setting = field;
        settingName = new TranslatableText(field.option.translationKey());
        init();
    }
    
}
