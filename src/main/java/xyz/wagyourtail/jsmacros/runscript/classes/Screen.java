package xyz.wagyourtail.jsmacros.runscript.classes;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.reflector.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.reflector.TextFieldWidgetHelper;

public class Screen extends net.minecraft.client.gui.screen.Screen {
    private boolean dirt;
    protected ArrayList<TextFieldWidget> textFieldWidgets;
    protected ArrayList<text> textFields;
    public Consumer<Screen> onInit;
    
    public Screen(String title, boolean dirt) {
        super(new LiteralText(title));
        this.dirt = dirt;
        this.textFieldWidgets = new ArrayList<TextFieldWidget>();
        this.textFields = new ArrayList<text>();
    }
    
    protected void init() {
        super.init();
        textFieldWidgets.clear();
        textFields.clear();
        minecraft.keyboard.enableRepeatEvents(true);
        if (onInit != null) {
            try {
                onInit.accept(this);
            } catch (Exception e) {
                if (this.minecraft.inGameHud != null) {
                    LiteralText text = new LiteralText(e.toString());
                    this.minecraft.inGameHud.getChatHud().addMessage(text);
                }
                e.printStackTrace();
            }
        }
    }
    
    public String getTitleText() {
        return title.toString();
    }
    
    public ArrayList<ButtonWidgetHelper> getButtonWidgets() {
        ArrayList<ButtonWidgetHelper> list = new ArrayList<ButtonWidgetHelper>();
        for (AbstractButtonWidget b : buttons) {
            list.add(new ButtonWidgetHelper((ButtonWidget)b));
        }
        return list;
    }
    
    public ArrayList<TextFieldWidgetHelper> getTextFields() {
        ArrayList<TextFieldWidgetHelper> list = new ArrayList<TextFieldWidgetHelper>();
        for (TextFieldWidget t : textFieldWidgets) {
            list.add(new TextFieldWidgetHelper(t));
        }
        return list;
    }
    

    public ArrayList<text> getTexts() {
        return textFields;
    }
    
    public ButtonWidgetHelper addButton(int x, int y, int width, int height, String text, BiConsumer<ButtonWidgetHelper, Screen> callback) {
        ButtonWidget button = (ButtonWidget) super.addButton(new ButtonWidget(x, y, width, height, text, (btn) -> {
            try {
                callback.accept(new ButtonWidgetHelper(btn), this);
            } catch (Exception e) {
                if (this.minecraft.inGameHud != null) {
                    LiteralText te = new LiteralText(e.toString());
                    this.minecraft.inGameHud.getChatHud().addMessage(te);
                }
                e.printStackTrace();
            }
        }));
        return new ButtonWidgetHelper(button);
    }
    
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message, BiConsumer<String, Screen> onChange) {
        TextFieldWidget field = new TextFieldWidget(this.font, x, y, width, height, message);
        if (onChange != null) {
            field.setChangedListener(str -> {
                try {
                    onChange.accept(str, this);
                } catch (Exception e) {
                    if (this.minecraft.inGameHud != null) {
                        LiteralText text = new LiteralText(e.toString());
                        this.minecraft.inGameHud.getChatHud().addMessage(text);
                    }
                    e.printStackTrace();
                }
            });
        }
        textFieldWidgets.add(field);
        children.add(field);
        return new TextFieldWidgetHelper(field);
    }
    
    public text addText(String text, int x, int y, int color) {
        text t =  new text(text, x, y, color);
        textFields.add(t);
        return t;
    }
    
    public void render(int mouseX, int mouseY, float delta) {
        if (dirt) this.renderDirtBackground(0);
        else this.renderBackground(0);
        for (TextFieldWidget w : textFieldWidgets) {
            w.render(mouseX, mouseY, delta);
        }
        for (text t : textFields) {
            this.minecraft.textRenderer.draw(t.text, t.x, t.y, t.color);
        }
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }
    
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    
    public static class text {
        public String text;
        public int x;
        public int y;
        public int color;
        public int width;
        
        public text(String text, int x, int y, int color) {
            MinecraftClient mc = jsMacros.getMinecraft();
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
            this.width = mc.textRenderer.getStringWidth(text);
        }
        
        public void setPos(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void setText(String text) {
            MinecraftClient mc = jsMacros.getMinecraft();
            this.text = text;
            this.width = mc.textRenderer.getStringWidth(text);
        }
        
        public int getWidth() {
            return this.width;
        }
    }
}
