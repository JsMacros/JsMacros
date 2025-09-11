package xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.wagyourgui.BaseScreen;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;
import xyz.wagyourtail.wagyourgui.overlays.SelectorDropdownOverlay;
import xyz.wagyourtail.wagyourgui.overlays.TextPrompt;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractMapSettingContainer<T, U extends AbstractMapSettingContainer.MapSettingEntry<T>> extends AbstractSettingContainer {
    public SettingsOverlay.SettingField<Map<String, T>> setting;
    public OrderedText settingName;
    public final Map<String, U> map = new HashMap<>();
    public int topScroll = 0;
    public int totalHeight = 0;
    public Supplier<T> defaultValue = () -> null;

    public AbstractMapSettingContainer(int x, int y, int width, int height, TextRenderer textRenderer, SettingsOverlay parent, String[] group) {
        super(x, y, width, height, textRenderer, parent, group);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void init() {
        super.init();
        scroll = addDrawableChild(new Scrollbar(x + width - 10, y + 12, 10, height - 12, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        addDrawableChild(new Button(x, y, 40, 10, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.add"), (btn) -> {
            if (setting.hasOptions()) {
                try {
                    List<String> options = ((List<String>) (List) setting.getOptions()).stream().filter(e -> !map.containsKey(e)).collect(Collectors.toList());
                    openOverlay(new SelectorDropdownOverlay(x, y + 10, width / 2, options.size() * textRenderer.fontHeight + 4, options.stream().map(Text::literal).collect(Collectors.toList()), textRenderer, getFirstOverlayParent(), (i) -> {
                        try {
                            newField(options.get(i));
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    newField("");
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    public void onScrollbar(double pages) {
        topScroll = (int) (pages * height);
        Iterator<String> it = map.keySet().stream().sorted().iterator();
        int height = 0;
        int x = this.x + 1;
        int width = this.width - 12;
        while (it.hasNext()) {
            MapSettingEntry<T> current = map.get(it.next());
            current.setPos(x, y + 12 + height - topScroll, width, current.height);
            current.setVisible(current.y >= y + 12 && current.y + current.height <= y + this.height + 2);
            height += current.height;
        }
    }

    public void newField(String key) throws InvocationTargetException, IllegalAccessException {
        setting.get().put(key, defaultValue.get());
        addField(key, defaultValue.get());
    }

    public abstract void addField(String key, T value);

    public void removeField(String key) throws InvocationTargetException, IllegalAccessException {
        setting.get().remove(key);
        MapSettingEntry<T> ent = map.remove(key);
        ent.getButtons().forEach(this::remove);
        totalHeight -= ent.height;
        scroll.setScrollPages(totalHeight / (double) height);
        if (scroll.active) {
            scroll.scrollToPercent(0);
        } else {
            onScrollbar(0);
        }
    }

    public void changeValue(String key, T newValue) throws InvocationTargetException, IllegalAccessException {
        setting.get().put(key, newValue);
        map.get(key).setValue(newValue);
    }

    public void changeKey(String key, String newKey) throws InvocationTargetException, IllegalAccessException {
        Map<String, T> setting = this.setting.get();
        setting.put(newKey, setting.remove(key));
        map.get(key).setKey(newKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addSetting(SettingsOverlay.SettingField<?> setting) {
        this.setting = (SettingsOverlay.SettingField<Map<String, T>>) setting;
        this.settingName = BaseScreen.trimmed(textRenderer, Text.translatable(setting.option.translationKey()), width - 40);
        map.clear();
        try {
            this.setting.get().forEach(this::addField);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        drawContext.drawText(textRenderer, settingName, (int) (x + width / 2F - textRenderer.getWidth(settingName) / 2F + 20), y + 1, 0xFFFFFFFF, false);
        drawContext.fill(x, y + 10, x + width, y + 11, 0xFFFFFFFF);
    }

    public static abstract class MapSettingEntry<T> extends MultiElementContainer<AbstractMapSettingContainer<T, MapSettingEntry<T>>> {
        protected String key;
        protected Button keyBtn;
        protected T value;

        public MapSettingEntry(int x, int y, int width, TextRenderer textRenderer, AbstractMapSettingContainer<T, MapSettingEntry<T>> parent, String key, T value) {
            super(x, y, width, textRenderer.fontHeight + 2, textRenderer, parent);
            this.key = key;
            this.value = value;
            init();
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void init() {
            super.init();
            int w = width - height;
            keyBtn = addDrawableChild(new Button(x, y, w / 2, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFFFF, Text.literal(key), (btn) -> {
                if (parent.setting.hasOptions()) {
                    try {
                        List<String> options = ((List<String>) (List) parent.setting.getOptions()).stream().filter(e -> !parent.map.containsKey(e)).collect(Collectors.toList());
                        openOverlay(new SelectorDropdownOverlay(x, y + height, w / 2, options.size() * textRenderer.fontHeight + 4, options.stream().map(Text::literal).collect(Collectors.toList()), textRenderer, getFirstOverlayParent(), (i) -> setKey(options.get(i))));
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    int x = parent.x;
                    int y = parent.y;
                    int width = parent.width;
                    int height = parent.height;
                    openOverlay(new TextPrompt(x + width / 4, y + height / 4, width / 2, height / 2, textRenderer, Text.translatable("jsmacros.setprofilename"), key, getFirstOverlayParent(), (newKey) -> {
                        try {
                            parent.changeKey(key, newKey);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }));
                }
            }));
            addDrawableChild(new Button(x + w, y, height, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFFFF, Text.literal("X"), (btn) -> {
                try {
                    parent.removeField(key);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }));
        }

        @Override
        public void setPos(int x, int y, int width, int height) {
            super.setPos(x, y, width, height);
            for (ClickableWidget btn : buttons) {
                btn.setY(y);
            }
        }

        public void setKey(String newKey) {
            parent.map.remove(key);
            this.key = newKey;
            keyBtn.setMessage(Text.literal(this.key));
            parent.map.put(key, this);
        }

        public void setValue(T newValue) {
            this.value = newValue;
        }

        @Override
        public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {

        }

    }

}
