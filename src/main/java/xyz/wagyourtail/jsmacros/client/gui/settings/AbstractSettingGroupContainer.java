package xyz.wagyourtail.jsmacros.client.gui.settings;

import net.minecraft.client.font.TextRenderer;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;

public abstract class AbstractSettingGroupContainer extends MultiElementContainer<SettingsOverlay> {
    public final String[] group;
    public Scrollbar scroll;
    public AbstractSettingGroupContainer(int x, int y, int width, int height, TextRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent);
        this.group = group;
        init();
    }
    
    public abstract void addSetting(SettingsOverlay.SettingField<?> setting);
    
}
