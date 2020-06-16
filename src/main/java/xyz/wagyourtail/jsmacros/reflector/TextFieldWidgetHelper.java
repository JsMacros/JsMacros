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
    
    public void setText(String text) {
        t.setText(text);
    }
    
    public int getWidth() {
        return t.getWidth();
    }
    
    public void setWidth(int width) {
        t.setWidth(width);
    }
    
    public void setEditableColor(int color) {
        t.setEditableColor(color);
    }
    
    public void setEditable(boolean edit) {
        t.setEditable(edit);
    }
    
    public void setUneditableColor(int color) {
        t.setUneditableColor(color);
    }
    
    public TextFieldWidget getRaw() {
        return t;
    }
}
