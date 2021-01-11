package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class ButtonWidgetHelper<T extends AbstractButtonWidget> extends BaseHelper<T> implements Drawable {
    
    public ButtonWidgetHelper(T btn) {
        super(btn);
    }
    
    /**
     * @since 1.0.5
     * @return the {@code x} coordinate of the button.
     */
    public int getX() {
        return base.x;
    }

    /**
     * @since 1.0.5
     * @return the {@code y} coordinate of the button.
     */
    public int getY() {
        return base.y;
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
    public ButtonWidgetHelper<T> setPos(int x, int y) {
        base.x = x;
        base.y = y;
        return this;
    }
    
    /**
     * @since 1.0.5
     * 
     * @return
     */
    public int getWidth() {
        return base.getWidth();
    }
    
    /**
     * change the text.
     * 
     * @since 1.0.5
     * 
     * @param message
     * @return
     */
    public ButtonWidgetHelper<T> setText(String message) {
        base.setMessage(new LiteralText(message));
        return this;
    }
    
    /**
     * @since 1.2.3
     * 
     * @return current button text.
     */
    public String getText() {
        return base.getMessage().getString();
    }
    
    /**
     * @since 1.0.5
     * 
     * @return button clickable state.
     */
    public boolean getActive() {
        return base.active;
    }
    
    /**
     * set the button clickable state.
     * 
     * @since 1.0.5
     * 
     * @param t
     * @return
     */
    public ButtonWidgetHelper<T> setActive(boolean t) {
        base.active = t;
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
    public ButtonWidgetHelper<T> setWidth(int width) {
        base.setWidth(width);
        return this;
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        base.render(matrices, mouseX, mouseY, delta);
    }
    
}
