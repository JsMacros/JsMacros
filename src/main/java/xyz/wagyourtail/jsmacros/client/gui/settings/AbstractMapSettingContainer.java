package xyz.wagyourtail.jsmacros.client.gui.settings;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.client.gui.overlays.SelectorDropdownOverlay;
import xyz.wagyourtail.jsmacros.client.gui.overlays.TextPrompt;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractMapSettingContainer<T> extends AbstractSettingGroupContainer {
    public SettingsOverlay.SettingField<Map<String, T>> setting;
    public final Map<String, MapSettingEntry> map = new HashMap<>();
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
        scroll = addButton(new Scrollbar(x + width - 10, y, 10, height - 10, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        addButton(new Button(x + width - 10, y + height - 10, 10, 10, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("+"), (btn) -> {
            if (setting.hasOptions()) {
                try {
                    List<String> options = ((List<String>) (List) setting.getOptions()).stream().filter(e -> !map.containsKey(e)).collect(Collectors.toList());
                    openOverlay(new SelectorDropdownOverlay(x + width - 10, y + height, width / 2, options.size() * textRenderer.fontHeight + 4, options.stream().map(LiteralText::new).collect(Collectors.toList()), textRenderer, getFirstOverlayParent(), (i) -> newField(options.get(i))));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                newField("");
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
            MapSettingEntry current = map.get(it.next());
            current.setPos(x, y + height - topScroll, width, current.height);
            current.setVisible(current.y >= y && current.y + current.height <= y + this.height);
            height += current.height;
        }
    }
    
    public void newField(String key) {
        addField(key, defaultValue.get());
    }
    
    public abstract void addField(String key, T value);
    
    public void removeField(String key) throws InvocationTargetException, IllegalAccessException {
        totalHeight -= map.remove(key).height;
        scroll.setScrollPages(totalHeight / (double) height);
        setting.get().remove(key);
    }
    
    public void changeValue(String key, T newValue) throws InvocationTargetException, IllegalAccessException {
        map.get(key).setValue(newValue);
        setting.get().put(key, newValue);
    }
    
    public void changeKey(String key, String newKey) throws InvocationTargetException, IllegalAccessException {
        map.get(key).setKey(newKey);
        Map<String, T> setting = this.setting.get();
        setting.put(newKey, setting.remove(key));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void addSetting(SettingsOverlay.SettingField<?> setting) {
        this.setting = (SettingsOverlay.SettingField<Map<String, T>>) setting;
        map.clear();
    }
    
    public abstract class MapSettingEntry extends MultiElementContainer<AbstractMapSettingContainer<T>> {
        protected String key;
        protected Button keyBtn;
        protected T value;
    
        public MapSettingEntry(int x, int y, int width, TextRenderer textRenderer, AbstractMapSettingContainer<T> parent, String key, T value) {
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
            keyBtn = addButton(new Button(x, y, w / 2, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(key), (btn) -> {
                if (setting.hasOptions()) {
                    try {
                        List<String> options = ((List<String>) (List) setting.getOptions()).stream().filter(e -> !map.containsKey(e)).collect(Collectors.toList());
                        openOverlay(new SelectorDropdownOverlay(x, y + height, w / 2, options.size() * textRenderer.fontHeight + 4, options.stream().map(LiteralText::new).collect(Collectors.toList()), textRenderer, getFirstOverlayParent(), (i) -> setKey(options.get(i))));
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    int x = AbstractMapSettingContainer.this.x;
                    int y = AbstractMapSettingContainer.this.y;
                    int width = AbstractMapSettingContainer.this.width;
                    int height = AbstractMapSettingContainer.this.height;
                    openOverlay(new TextPrompt(x + width / 4, y + height / 4, width / 2, height / 2, textRenderer, new TranslatableText("jsmacros.setkey"), key, getFirstOverlayParent(), (newKey) -> {
                        try {
                            changeKey(key, newKey);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }));
                }
            }));
            addButton(new Button(x + w, y, height, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
                try {
                    removeField(key);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }));
        }
    
        @Override
        public void setPos(int x, int y, int width, int height) {
            super.setPos(x, y, width, height);
            for (AbstractButtonWidget btn : buttons) {
                btn.y = y;
            }
        }
        
        public void setKey(String newKey) {
            map.remove(key);
            this.key = newKey;
            keyBtn.setMessage(new LiteralText(this.key));
            map.put(key, this);
        }
        
        public void setValue(T newValue) {
            this.value = newValue;
        }
    }
}
