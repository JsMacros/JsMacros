package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.client.gui.widget.ButtonWidget;

public class ButtonWidgetHelper {
    private ButtonWidget btn;
    
    public ButtonWidgetHelper(ButtonWidget btn) {
        this.btn = btn;
    }
    
    public int getX() {
        return btn.x;
    }
    
    public int getY() {
        return btn.y;
    }
    
    public void setPos(int x, int y) {
        btn.x = x;
        btn.y = y;
    }
    
    public int getWidth() {
        return btn.getWidth();
    }
    
    public void setText(String message) {
        btn.setMessage(message);
    }
    
    public boolean getActive() {
        return btn.active;
    }
    
    public void setActive(boolean t) {
        btn.active = t;
    }
    
    public void setWidth(int width) {
        btn.setWidth(width);
    }
    
    public ButtonWidget getRaw() {
        return btn;
    }
}
