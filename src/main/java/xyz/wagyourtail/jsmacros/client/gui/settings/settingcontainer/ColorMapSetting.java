package xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.elements.AnnotatedCheckBox;
import xyz.wagyourtail.jsmacros.client.gui.overlays.TextPrompt;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;

import java.lang.reflect.InvocationTargetException;

public class ColorMapSetting extends AbstractMapSettingContainer<short[]> {
    
    public ColorMapSetting(int x, int y, int width, int height, TextRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent, group);
        defaultValue = () -> new short[3];
    }
    
    @Override
    public void addField(String key, short[] value) {
        if (map.containsKey(key)) return;
        ColorEntry entry = new ColorEntry(x, y + 12 + totalHeight - topScroll, width - 12, textRenderer, this, key, value);
        map.put(key, entry);
        totalHeight +=  entry.height;
        scroll.setScrollPages(totalHeight / (double) height);
        if (scroll.active) {
            scroll.scrollToPercent(0);
        } else {
            onScrollbar(0);
        }
    }
    
    public class ColorEntry extends AbstractMapSettingContainer<short[]>.MapSettingEntry {
        
        public ColorEntry(int x, int y, int width, TextRenderer textRenderer, AbstractMapSettingContainer<short[]> parent, String key, short[] value) {
            super(x, y, width, textRenderer, parent, key, value);
        }
    
        public String convertColorToString(short[] color) {
            return String.format("#%02X%02X%02X", color[0], color[1], color[2]);
        }
        
        public short[] convertStringToColor(String color) {
            short[] retVal = new short[3];
            long val = Long.parseLong(color.replace("#", ""), 16);
            retVal[2] = (short) (val & 255);
            retVal[1] = (short) ((val >> 8) & 255);
            retVal[0] = (short) ((val >> 16) & 255);
            return retVal;
        }
        
        public int convertColorToInt(short[] color) {
            return 0xFF000000 | (int) color[0] << 16 | (int) color[1] << 8 | color[2];
        }
    
        @Override
        public void init() {
            super.init();
            int w = width - height;
            this.addButton(new AnnotatedCheckBox(x + w / 2, y, w / 2, height, textRenderer, convertColorToInt(value), 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(convertColorToString(value)), true, (btn) -> {
                ((AnnotatedCheckBox)btn).value = true;
                int x = ColorMapSetting.this.x;
                int y = ColorMapSetting.this.y;
                int width = ColorMapSetting.this.width;
                int height = ColorMapSetting.this.height;
                TextPrompt prompt = new TextPrompt(x + width / 4, y + height / 4, width / 2, height / 2, textRenderer, new TranslatableText("jsmacros.setvalue"), convertColorToString(value), getFirstOverlayParent(), (str) -> {
                    try {
                        short[] newVal = convertStringToColor(str);
                        changeValue(key, newVal);
                        btn.setColor(convertColorToInt(newVal));
                        btn.setMessage(new LiteralText(str));
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                openOverlay(prompt);
                prompt.ti.mask = "#[\\da-fA-F]{0,6}";
            }));
        }
    
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        
        }
    
    }
    
}
