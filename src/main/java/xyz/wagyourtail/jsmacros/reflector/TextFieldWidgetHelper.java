package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.client.gui.widget.TextFieldWidget;

public class TextFieldWidgetHelper {
    private TextFieldWidget t;
    
    public TextFieldWidgetHelper(TextFieldWidget t) {
        this.t = t;
    }
    
    public int getX() {
        return t.x;
    }
    
    public int getY() {
        return t.y;
    }
    
    public void setPos(int x, int y) {
        t.x = x;
        t.y = y;
    }
    
    public String getText() {
        return t.getText();
    }
    
    public TextFieldWidgetHelper setText(String text) {
        t.setText(text);
        return this;
    }
    
    public int getWidth() {
        return t.getWidth();
    }
    
    public TextFieldWidgetHelper setWidth(int width) {
        t.setWidth(width);
        return this;
    }
    
    public TextFieldWidgetHelper setEditableColor(int color) {
        t.setEditableColor(color);
        return this;
    }
    
    public TextFieldWidgetHelper setEditable(boolean edit) {
        t.setEditable(edit);
        return this;
    }
    
    public TextFieldWidgetHelper setUneditableColor(int color) {
        t.setUneditableColor(color);
        return this;
    }
    
    public TextFieldWidget getRaw() {
        return t;
    }
}
