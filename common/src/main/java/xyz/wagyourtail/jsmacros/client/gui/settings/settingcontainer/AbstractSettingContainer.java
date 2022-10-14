package xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer;

import net.minecraft.client.gui.FontRenderer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;

public abstract class AbstractSettingContainer extends MultiElementContainer<SettingsOverlay> {
    public final String[] group;
    public Scrollbar scroll;
    public AbstractSettingContainer(int x, int y, int width, int height, FontRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent);
        this.group = group;
        init();
    }

    public abstract void addSetting(SettingsOverlay.SettingField<?> setting);

}
