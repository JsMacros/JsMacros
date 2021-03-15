package xyz.wagyourtail.jsmacros.client.gui.settings;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.overlays.IOverlayParent;
import xyz.wagyourtail.jsmacros.client.gui.overlays.OverlayContainer;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.Option;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SettingsOverlay extends OverlayContainer {
    private final Text title = new TranslatableText("jsmacros.settings");
    private CategoryTreeContainer sections;
    private final SettingTree settings = new SettingTree();
    public SettingsOverlay(int x, int y, int width, int height, TextRenderer textRenderer, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
    
        for (Class<?> clazz : Core.instance.config.optionClasses.values()) {
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(Option.class)) {
                    try {
                        Option option = f.getAnnotation(Option.class);
                        Method getter = null;
                        Method setter = null;
                        if (!option.getter().equals("")) {
                            getter = clazz.getDeclaredMethod(option.getter());
                        }
                        if (!option.setter().equals("")) {
                            setter = clazz.getDeclaredMethod(option.setter(), f.getType());
                        }
                        settings.addChild(option.group(), new SettingField(option, Core.instance.config.getOptions(clazz), f, getter, setter));
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
            //synthetics
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Option.class)) {
                    try {
                        Option option = m.getAnnotation(Option.class);
                        Method setter = null;
                        if (!option.setter().equals("")) {
                            setter = clazz.getDeclaredMethod(option.setter(), m.getReturnType());
                        }
                        settings.addChild(option.group(), new SettingField(option, Core.instance.config.getOptions(clazz), null, m, setter));
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    @Override
    public void init() {
        super.init();
        int w = width - 4;
    
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> this.close()));
        sections = new CategoryTreeContainer(x + 2, y + 13, w / 3, height - 17, textRenderer, this);
        
        for (String[] group : settings.groups()) {
            sections.addCategory(group);
        }
    }
    
    public void selectCategory(String[] category) {
        System.out.println(String.join(", ", category));
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        int w = width - 4;
        
        sections.render(matrices, mouseX, mouseY, delta);
        
        textRenderer.drawTrimmed(title, x + 3, y + 3, width - 14, 0xFFFFFF);
        fill(matrices, x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        
        //sep
        fill(matrices, x + w / 3, y + 13, x + w / 3 + 1, y + height, 0xFFFFFFFF);
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    static class SettingTree {
        Map<String, SettingTree> children = new HashMap<>();
        List<SettingField> settings = new LinkedList<>();
        
        void addChild(String[] group, SettingField field) {
            if (group.length > 0) {
                String[] childGroup = new String[group.length - 1];
                System.arraycopy(group, 1, childGroup, 0, childGroup.length);
                addChild(childGroup, field);
            }
            settings.add(field);
        }
        
        public List<String[]> groups() {
            if (children.size() > 0) {
                List<String[]> groups = new LinkedList<>();
                for (Map.Entry<String, SettingTree> child : children.entrySet()) {
                    for (String[] childGroup : child.getValue().groups()) {
                        String[] group = new String[childGroup.length + 1];
                        System.arraycopy(childGroup, 0, group, 1, childGroup.length);
                        group[0] = child.getKey();
                        groups.add(group);
                    }
                    groups.add(new String[] {child.getKey()});
                }
                return groups;
            }
            return new LinkedList<>();
        }
    }
    
    static class SettingField {
        final Option option;
        final Object containingClass;
        final Field field;
        final Method getter;
        final Method setter;
        
        public SettingField(Option option, Object containingClass, Field f, Method getter, Method setter) {
            this.option = option;
            this.containingClass = containingClass;
            this.field = f;
            this.getter = getter;
            this.setter = setter;
        }
        
        public void set(Object o) throws IllegalAccessException, InvocationTargetException {
            if (setter == null) {
                field.set(containingClass, o);
            } else {
                setter.invoke(containingClass, o);
            }
        }
        
        public Object get() throws IllegalAccessException, InvocationTargetException {
            if (getter == null) {
                return field.get(containingClass);
            } else {
                return getter.invoke(containingClass);
            }
        }
    }
}
