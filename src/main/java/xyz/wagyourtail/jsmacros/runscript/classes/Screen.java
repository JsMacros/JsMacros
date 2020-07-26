package xyz.wagyourtail.jsmacros.runscript.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.reflector.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;
import xyz.wagyourtail.jsmacros.reflector.TextFieldWidgetHelper;

public class Screen extends net.minecraft.client.gui.screen.Screen {
    private boolean dirt;
    protected ArrayList<TextFieldWidget> textFieldWidgets = new ArrayList<>();;
    protected ArrayList<text> textFields = new ArrayList<>();
    public ArrayList<rect> rectFields = new ArrayList<>();
    public ArrayList<item> itemFields = new ArrayList<>();
    public Consumer<Screen> onInit;
    public BiConsumer<Pos2D, Integer> onMouseDown;
    public BiConsumer<Vec2D, Integer> onMouseDrag;
    public BiConsumer<Pos2D, Integer> onMouseUp;
    public BiConsumer<Pos2D, Double> onScroll;
    public BiConsumer<Integer, Integer> onKeyPressed;
    public Consumer<String> catchInit;
    public Consumer<Screen> onClose;
    
    public Screen(String title, boolean dirt) {
        super(new LiteralText(title));
        this.dirt = dirt;
    }
    
    protected void init() {
        super.init();
        textFieldWidgets.clear();
        textFields.clear();
        client.keyboard.enableRepeatEvents(true);
        if (onInit != null) {
            try {
                onInit.accept(this);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (catchInit != null) catchInit.accept(e.toString());
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        }
    }
    
    public String getTitleText() {
        return title.toString();
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public List<ButtonWidgetHelper> getButtonWidgets() {
        ArrayList<ButtonWidgetHelper> list = new ArrayList<ButtonWidgetHelper>();
        for (AbstractButtonWidget b : buttons) {
            list.add(new ButtonWidgetHelper((ButtonWidget)b));
        }
        return list;
    }
    
    public List<TextFieldWidgetHelper> getTextFields() {
        ArrayList<TextFieldWidgetHelper> list = new ArrayList<TextFieldWidgetHelper>();
        for (TextFieldWidget t : textFieldWidgets) {
            list.add(new TextFieldWidgetHelper(t));
        }
        return list;
    }
    

    public List<text> getTexts() {
        return textFields;
    }
    
    public List<rect> getRects() {
        return rectFields;
    }
    
    public List<item> getItems() {
        return itemFields;
    }
    
    public ButtonWidgetHelper addButton(int x, int y, int width, int height, String text, BiConsumer<ButtonWidgetHelper, Screen> callback) {
        ButtonWidget button = (ButtonWidget) super.addButton(new ButtonWidget(x, y, width, height, new LiteralText(text), (btn) -> {
            try {
                callback.accept(new ButtonWidgetHelper(btn), this);
            } catch (Exception e) {
                if (this.client.inGameHud != null) {
                    LiteralText te = new LiteralText(e.toString());
                    this.client.inGameHud.getChatHud().addMessage(te);
                }
                e.printStackTrace();
            }
        }));
        return new ButtonWidgetHelper(button);
    }
    
    public void removeButton(ButtonWidgetHelper btn) {
        this.buttons.remove(btn.getRaw());
    }
    
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message, BiConsumer<String, Screen> onChange) {
        TextFieldWidget field = new TextFieldWidget(this.textRenderer, x, y, width, height, new LiteralText(message));
        if (onChange != null) {
            field.setChangedListener(str -> {
                try {
                    onChange.accept(str, this);
                } catch (Exception e) {
                    if (this.client.inGameHud != null) {
                        LiteralText text = new LiteralText(e.toString());
                        this.client.inGameHud.getChatHud().addMessage(text);
                    }
                    e.printStackTrace();
                }
            });
        }
        textFieldWidgets.add(field);
        children.add(field);
        return new TextFieldWidgetHelper(field);
    }
    
    public void removeTextInput(TextFieldWidgetHelper inp) {
        textFieldWidgets.remove(inp.getRaw());
        children.remove(inp.getRaw());
    }
    
    public text addText(String text, int x, int y, int color, boolean shadow) {
        text t =  new text(text, x, y, color, shadow);
        textFields.add(t);
        return t;
    }
    
    public void removeText(text t) {
        textFields.remove(t);
    }
    

    public rect addRect(int x1, int y1, int x2, int y2, int color) {
        rect r = new rect(x1, y1, x2, y2, color);
        rectFields.add(r);
        return r;
    }
    
    public rect addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        rect r = new rect(x1, y1, x2, y2, color, alpha);
        rectFields.add(r);
        return r;
    }
    
    public void removeRect(rect r) {
        rectFields.remove(r);
    }
    
    public item addItem(int x, int y, String id) {
        item i = new item(y, y, id);
        itemFields.add(i);
        return i;
    }
    
    public item addItem(int x, int y, ItemStackHelper item) {
        item i = new item(y, y, item);
        itemFields.add(i);
        return i;
    }
    
    public void removeItem(item i) {
        itemFields.remove(i);
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (onMouseDown != null) onMouseDown.accept(new Pos2D(mouseX, mouseY), button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (onMouseDrag != null) onMouseDrag.accept(new Vec2D(mouseX, mouseY, deltaX, deltaY), button);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (onMouseUp != null) onMouseUp.accept(new Pos2D(mouseX, mouseY), button);
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (onKeyPressed != null) onKeyPressed.accept(keyCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (onScroll != null) onScroll.accept(new Pos2D(mouseX, mouseY), amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (matricies == null) return;
        if (dirt) this.renderBackgroundTexture(0);
        else this.renderBackground(matricies, 0);
        
        this.drawCenteredText(matricies, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        ArrayList<TextFieldWidget> textFieldWidgets;
        ArrayList<text> textFields;
        ArrayList<rect> rectFields;
        ArrayList<item> itemFields;
        
        try {
            rectFields = new ArrayList<>(this.rectFields);
            itemFields = new ArrayList<>(this.itemFields);
            textFieldWidgets = new ArrayList<>(this.textFieldWidgets);
            textFields = new ArrayList<>(this.textFields);
        } catch (Exception e) {
            return;
        }
        
        for (rect r : rectFields) {
            r.render(matricies);
        }
        for (item i : itemFields) {
            i.render(matricies);
        }
        for (text t : textFields) {
            t.render(matricies);
        }

        for (TextFieldWidget w : textFieldWidgets) {
            w.render(matricies, mouseX, mouseY, delta);
        }
        
        super.render(matricies, mouseX, mouseY, delta);
    }
    
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
        if (onClose != null) onClose.accept(this);
    }
    
    public void close() {
        onClose();
    }
    
    public static class item {
        public int x;
        public int y;
        public ItemStack item;
        
        public item(int x, int y, String id) {
            this.x = x;
            this.y = y;
            this.setItem(id, 1);
        }
        
        public item(int x, int y, ItemStackHelper i) {
            this.x = x;
            this.y = y;
            this.item = i.getRaw();
        }
        
        public void setPos(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void setItem(ItemStackHelper i) {
            this.item = i.getRaw();
        }
        
        public void setItem(String id, int count) {
            Item it = (Item)Registry.ITEM.get(new Identifier(id));
            if (it != null) this.item = new ItemStack(it, count);
        }
        
        public void render(MatrixStack matrixStack) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                i.renderGuiItemIcon(item, x, y);
                i.renderGuiItemOverlay(mc.textRenderer, item, x, y);
            }
        }
    }
    
    public static class rect {
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int color;
        
        public rect(int x1, int y1, int x2, int y2, int color) {
            setPos(x1, y1, x2, y2);
            setColor(color);
        }
        
        public rect(int x1, int y1, int x2, int y2, int color, int alpha) {
            setPos(x1, y1, x2, y2);
            setColor(color, alpha);
        }
        
        public void setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
        }
        
        public void setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
        }
        
        public void setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
        }
        
        public void setPos(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        public void render(MatrixStack matrixStack) {
            Screen.fill(matrixStack, x1, y1, x2, y2, color);
        }
    }
    
    public static class text {
        public String text;
        public int x;
        public int y;
        public int color;
        public int width;
        public boolean shadow;
        
        public text(String text, int x, int y, int color, boolean shadow) {
            MinecraftClient mc = MinecraftClient.getInstance();
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
            this.width = mc.textRenderer.getWidth(text);
            this.shadow = shadow;
        }
        
        public void setPos(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void setText(String text) {
            MinecraftClient mc = MinecraftClient.getInstance();
            this.text = text;
            this.width = mc.textRenderer.getWidth(text);
        }
        
        public int getWidth() {
            return this.width;
        }
        
        public void render(MatrixStack matricies) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (shadow) mc.textRenderer.drawWithShadow(matricies, text, x, y, color);
            else mc.textRenderer.draw(matricies, text, x, y, color);
        }
    }
    
    public class Pos2D {
        public double x;
        public double y;
        
        public Pos2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
    
    public class Vec2D {
        public double x1;
        public double y1;
        public double x2;
        public double y2;
        
        
        public Vec2D(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
}
