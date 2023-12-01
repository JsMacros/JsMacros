package xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.wagyourgui.elements.AnnotatedCheckBox;
import xyz.wagyourtail.wagyourgui.overlays.TextPrompt;

import java.lang.reflect.InvocationTargetException;

import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.literal;
import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.translatable;

public class ColorMapSetting extends AbstractMapSettingContainer<short[], ColorMapSetting.ColorEntry> {

    public ColorMapSetting(int x, int y, int width, int height, TextRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent, group);
        defaultValue = () -> new short[3];
    }

    @Override
    public void addField(String key, short[] value) {
        if (map.containsKey(key)) {
            return;
        }
        ColorEntry entry = new ColorEntry(x, y + 12 + totalHeight - topScroll, width - 12, textRenderer, this, key, value);
        map.put(key, entry);
        totalHeight += entry.height;
        scroll.setScrollPages(totalHeight / (double) height);
        if (scroll.active) {
            scroll.scrollToPercent(0);
        } else {
            onScrollbar(0);
        }
    }

    public static class ColorEntry extends AbstractMapSettingContainer.MapSettingEntry<short[]> {

        public ColorEntry(int x, int y, int width, TextRenderer textRenderer, ColorMapSetting parent, String key, short[] value) {
            super(x, y, width, textRenderer, (AbstractMapSettingContainer) parent, key, value);
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
            this.addDrawableChild(new AnnotatedCheckBox(x + w / 2, y, w / 2, height, textRenderer, convertColorToInt(value), 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, literal(convertColorToString(value)), true, (btn) -> {
                ((AnnotatedCheckBox) btn).value = true;
                int x = parent.x;
                int y = parent.y;
                int width = parent.width;
                int height = parent.height;
                TextPrompt prompt = new TextPrompt(x + width / 4, y + height / 4, width / 2, height / 2, textRenderer, translatable("jsmacros.setvalue"), convertColorToString(value), getFirstOverlayParent(), (str) -> {
                    try {
                        short[] newVal = convertStringToColor(str);
                        parent.changeValue(key, newVal);
                        btn.setColor(convertColorToInt(newVal));
                        btn.setMessage(literal(str));
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                openOverlay(prompt);
                prompt.ti.mask = "#[\\da-fA-F]{0,6}";
            }));
        }

    }

}
