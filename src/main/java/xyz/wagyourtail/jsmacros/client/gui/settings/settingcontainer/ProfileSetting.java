package xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileSetting extends AbstractMapSettingContainer<List<ScriptTrigger>> {
    
    
    public ProfileSetting(int x, int y, int width, int height, TextRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent, group);
        defaultValue = ArrayList::new;
    }
    
    @Override
    public void addField(String key, List<ScriptTrigger> value) {
        if (map.containsKey(key)) return;
        ProfileEntry entry = new ProfileEntry(x, y + 12 + totalHeight - topScroll, width - 12, textRenderer, this, key, value);
        map.put(key, entry);
        totalHeight +=  entry.height;
        scroll.setScrollPages(totalHeight / (double) height);
        if (scroll.active) {
            scroll.scrollToPercent(0);
        } else {
            onScrollbar(0);
        }
    }
    
    @Override
    public void removeField(String key) throws InvocationTargetException, IllegalAccessException {
        Map<String, List<ScriptTrigger>> settings = setting.get();
        if (settings.size() > 1) {
            super.removeField(key);
            if (Core.instance.profile.getCurrentProfileName().equals(key)) {
                Core.instance.profile.loadOrCreateProfile(settings.keySet().stream().sorted().findFirst().orElse("default"));
            }
            if (Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile.equals(key)) {
                Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile = settings.keySet().stream().sorted().findFirst().orElse("default");
            }
        }
    }
    
    @Override
    public void changeKey(String key, String newKey) throws InvocationTargetException, IllegalAccessException {
        super.changeKey(key, newKey);
        if (Core.instance.profile.getCurrentProfileName().equals(key)) {
            Core.instance.profile.renameCurrentProfile(newKey);
        }
        if (Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile.equals(key)) {
            Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile = newKey;
        }
    }
    
    public class ProfileEntry extends AbstractMapSettingContainer<List<ScriptTrigger>>.MapSettingEntry {
    
        public ProfileEntry(int x, int y, int width, TextRenderer textRenderer, AbstractMapSettingContainer<List<ScriptTrigger>> parent, String key, List<ScriptTrigger> value) {
            super(x, y, width, textRenderer, parent, key, value);
        }
    
        @Override
        public void init() {
            super.init();
            int w = width - height;
            buttons.get(0).setWidth(w);
        }
    
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        
        }
    
    }
}
