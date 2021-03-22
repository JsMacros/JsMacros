package xyz.wagyourtail.jsmacros.client.gui.settings;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
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
    public Text settingName;
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
        scroll = addButton(new Scrollbar(x + width - 10, y + 12, 10, height - 22, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
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
            current.setPos(x, y + 12 + height - topScroll, width, current.height);
            current.setVisible(current.y >= y + 12 && current.y + current.height <= y + this.height + 2);
            height += current.height;
        }
    }
    
    public void newField(String key) {
        addField(key, defaultValue.get());
    }
    
    public abstract void addField(String key, T value);
    
    public void removeField(String key) throws InvocationTargetException, IllegalAccessException {
        setting.get().remove(key);
        MapSettingEntry ent = map.remove(key);
        ent.getButtons().forEach(this::removeButton);
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
        this.settingName = new TranslatableText(setting.option.translationKey());
        map.clear();
        try {
            this.setting.get().forEach(this::addField);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, textRenderer, settingName, x + width / 2, y + 1, 0xFFFFFF);
        fill(matrices, x, y + 10, x+width,y + 11,0xFFFFFFFF);
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
