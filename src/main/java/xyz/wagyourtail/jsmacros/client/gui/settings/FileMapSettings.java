package xyz.wagyourtail.jsmacros.client.gui.settings;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingtypes.FileField;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class FileMapSettings extends AbstractMapSettingContainer<String> {
    
    public FileMapSettings(int x, int y, int width, int height, TextRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent, group);
        defaultValue = () -> "./";
    }
    
    @Override
    public void addField(String key, String value) {
        if (map.containsKey(key)) return;
        FileEntry entry = new FileEntry(x, y + 12 + totalHeight - topScroll, width - 12, textRenderer, this, key, value);
        map.put(key, entry);
        totalHeight +=  entry.height;
        scroll.setScrollPages(totalHeight / (double) height);
        if (scroll.active) {
            scroll.scrollToPercent(0);
        } else {
            onScrollbar(0);
        }
    }
    
    public class FileEntry extends AbstractMapSettingContainer<String>.MapSettingEntry {
    
        public FileEntry(int x, int y, int width, TextRenderer textRenderer, AbstractMapSettingContainer<String> parent, String key, String value) {
            super(x, y, width, textRenderer, parent, key, value);
        }
    
        @Override
        public void init() {
            super.init();
            int w = width - height;
            addButton(new Button(x + w / 2, y, w / 2, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(value), (btn) -> {
                parent.openOverlay(new FileChooser(parent.x, parent.y, parent.width, parent.height, textRenderer, FileField.getTopLevel(setting), new File(FileField.getTopLevel(setting), value), getFirstOverlayParent(), (file) -> {
                    String newVal = "." + file.getAbsolutePath().substring(FileField.getTopLevel(setting).getAbsolutePath().length()).replaceAll("\\\\", "/");
                    try {
                        changeValue(key, newVal);
                        btn.setMessage(new LiteralText(newVal));
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }, file -> {}));
            }));
        }
    
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        
        }
    
    }
}
