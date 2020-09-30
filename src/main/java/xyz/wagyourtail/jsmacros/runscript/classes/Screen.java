package xyz.wagyourtail.jsmacros.runscript.classes;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.reflector.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;
import xyz.wagyourtail.jsmacros.reflector.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.runscript.classes.ConsumerWrappers.BiConsumer;
import xyz.wagyourtail.jsmacros.runscript.classes.ConsumerWrappers.Consumer;
import xyz.wagyourtail.jsmacros.runscript.classes.common.PositionHelper;
import xyz.wagyourtail.jsmacros.runscript.classes.common.RenderCommon.image;
import xyz.wagyourtail.jsmacros.runscript.classes.common.RenderCommon.item;
import xyz.wagyourtail.jsmacros.runscript.classes.common.RenderCommon.rect;
import xyz.wagyourtail.jsmacros.runscript.classes.common.RenderCommon.text;

public class Screen extends net.minecraft.client.gui.screen.Screen {
    private int bgStyle = 0;
    protected List<TextFieldWidget> textFieldWidgets = new ArrayList<>();;
    protected List<text> textFields = new ArrayList<>();
    protected List<rect> rectFields = new ArrayList<>();
    protected List<item> itemFields = new ArrayList<>();
    protected List<image> imageFields = new ArrayList<>();
    public Consumer<Screen> onInit;
    public BiConsumer<PositionHelper.Pos2D, Integer> onMouseDown;
    public BiConsumer<PositionHelper.Vec2D, Integer> onMouseDrag;
    public BiConsumer<PositionHelper.Pos2D, Integer> onMouseUp;
    public BiConsumer<PositionHelper.Pos2D, Double> onScroll;
    public BiConsumer<Integer, Integer> onKeyPressed;
    public Consumer<String> catchInit;
    public Consumer<Screen> onClose;
    
    
    public Screen(String title, boolean dirt) {
        super(new LiteralText(title));
        this.bgStyle = dirt ? 0 : 1;
    }
    
    protected void init() {
        super.init();
        textFieldWidgets.clear();
        textFields.clear();
        itemFields.clear();
        rectFields.clear();
        client.keyboard.setRepeatEvents(true);
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
        List<ButtonWidgetHelper> list = new ArrayList<>();
        for (AbstractButtonWidget b : ImmutableList.copyOf(buttons)) {
            list.add(new ButtonWidgetHelper((ButtonWidget)b));
        }
        return list;
    }
    
    public List<TextFieldWidgetHelper> getTextFields() {
        List<TextFieldWidgetHelper> list = new ArrayList<>();
        for (TextFieldWidget t : ImmutableList.copyOf(textFieldWidgets)) {
            list.add(new TextFieldWidgetHelper(t));
        }
        return list;
    }
    

    public List<text> getTexts() {
        return ImmutableList.copyOf(textFields);
    }
    
    public List<rect> getRects() {
        return ImmutableList.copyOf(rectFields);
    }
    
    public List<item> getItems() {
        return ImmutableList.copyOf(itemFields);
    }

    public List<image> getImages() {
        return ImmutableList.copyOf(imageFields);
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
    
    public Screen removeButton(ButtonWidgetHelper btn) {
        this.buttons.remove(btn.getRaw());
        return this;
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
    
    public Screen removeTextInput(TextFieldWidgetHelper inp) {
        textFieldWidgets.remove(inp.getRaw());
        children.remove(inp.getRaw());
        return this;
    }
    
    public text addText(String text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, shadow, 1, 0);
        
    }
    
    public text addText(String text, int x, int y, int color, boolean shadow, double scale, float rotation) {
        text t = new text(text, x, y, color, shadow, scale, rotation);
        textFields.add(t);
        return t;
        
    }
    
    public Screen removeText(text t) {
        textFields.remove(t);
        return this;
    }
    

    public rect addRect(int x1, int y1, int x2, int y2, int color) {
        rect r = new rect(x1, y1, x2, y2, color, 0F);
        rectFields.add(r);
        return r;
    }
    
    public rect addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        return addRect(x1, y1, x2, y2, color, alpha, 0);
    }
    
    public rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, float rotation) {
        rect r = new rect(x1, y1, x2, y2, color, alpha, rotation);
        rectFields.add(r);
        return r;
    }
    
    public Screen removeRect(rect r) {
        rectFields.remove(r);
        return this;
    }
    
    public image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }
    
    public image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float rotation) {
        image i = new image(x, y, width, height, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
        imageFields.add(i);
        return i;
    }
    
    public Screen removeImage(image i) {
        imageFields.remove(i);
        return this;
    }
    
    public item addItem(int x, int y, String id, boolean overlay) {
        return addItem(y, y, id, overlay, 1, 0);
    }
    
    public item addItem(int x, int y, String id, boolean overlay, double scale, float rotation) {
        item i = new item(y, y, id, overlay, scale, rotation);
        itemFields.add(i);
        return i;
    }
    
    public item addItem(int x, int y, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, item, overlay, 1, 0);
    }
    
    public item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, float rotation) {
        item i = new item(x, y, item, overlay, scale, rotation);
        itemFields.add(i);
        return i;
    }
    
    public Screen removeItem(item i) {
        itemFields.remove(i);
        return this;
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (onMouseDown != null) onMouseDown.accept(new PositionHelper.Pos2D(mouseX, mouseY), button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (onMouseDrag != null) onMouseDrag.accept(new PositionHelper.Vec2D(mouseX, mouseY, deltaX, deltaY), button);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (onMouseUp != null) onMouseUp.accept(new PositionHelper.Pos2D(mouseX, mouseY), button);
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (onKeyPressed != null) onKeyPressed.accept(keyCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (onScroll != null) onScroll.accept(new PositionHelper.Pos2D(mouseX, mouseY), amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (matricies == null) return;
        if (bgStyle == 0) this.renderBackgroundTexture(0);
        else if (bgStyle == 1) this.renderBackground(matricies, 0);
        
        drawCenteredText(matricies, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        for (rect r : ImmutableList.copyOf(this.rectFields)) {
            r.render(matricies);
        }
        for (item i : ImmutableList.copyOf(this.itemFields)) {
            i.render(matricies);
        }
        for (image i : ImmutableList.copyOf(this.imageFields)) {
            i.render(matricies);
        }
        for (text t : ImmutableList.copyOf(this.textFields)) {
            t.render(matricies);
        }

        for (TextFieldWidget w : ImmutableList.copyOf(this.textFieldWidgets)) {
            w.render(matricies, mouseX, mouseY, delta);
        }
        
        super.render(matricies, mouseX, mouseY, delta);
    }
    
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        if (onClose != null) {
            try {
                onClose.accept(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void close() {
        onClose();
    }
}
