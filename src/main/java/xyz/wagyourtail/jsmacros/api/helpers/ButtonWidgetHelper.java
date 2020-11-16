package xyz.wagyourtail.jsmacros.api.helpers;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
public class ButtonWidgetHelper implements Drawable {
    private ButtonWidget btn;
    
    public ButtonWidgetHelper(ButtonWidget btn) {
        this.btn = btn;
    }
    
    /**
     * @since 1.0.5
     * @return the {@code x} coordinate of the button.
     */
    public int getX() {
        return btn.x;
    }

    /**
     * @since 1.0.5
     * @return the {@code y} coordinate of the button.
     */
    public int getY() {
        return btn.y;
    }
    
    /**
     * Set the button position.
     * 
     * @since 1.0.5
     * 
     * @param x
     * @param y
     * @return
     */
    public ButtonWidgetHelper setPos(int x, int y) {
        btn.x = x;
        btn.y = y;
        return this;
    }
    
    /**
     * @since 1.0.5
     * 
     * @return
     */
    public int getWidth() {
        return btn.getWidth();
    }
    
    /**
     * change the text.
     * 
     * @since 1.0.5
     * 
     * @param message
     * @return
     */
    public ButtonWidgetHelper setText(String message) {
        btn.setMessage(new LiteralText(message));
        return this;
    }
    
    /**
     * @since 1.2.3
     * 
     * @return current button text.
     */
    public String getText() {
        return btn.getMessage().getString();
    }
    
    /**
     * @since 1.0.5
     * 
     * @return button clickable state.
     */
    public boolean getActive() {
        return btn.active;
    }
    
    /**
     * set the button clickable state.
     * 
     * @since 1.0.5
     * 
     * @param t
     * @return
     */
    public ButtonWidgetHelper setActive(boolean t) {
        btn.active = t;
        return this;
    }
    
    /**
     * set the button width.
     * 
     * @since 1.0.5
     * 
     * @param width
     * @return
     */
    public ButtonWidgetHelper setWidth(int width) {
        btn.setWidth(width);
        return this;
    }
    
    public ButtonWidget getRaw() {
        return btn;
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        btn.render(matrices, mouseX, mouseY, delta);
    }
    
}
