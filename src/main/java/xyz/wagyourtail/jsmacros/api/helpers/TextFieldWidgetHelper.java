package xyz.wagyourtail.jsmacros.api.helpers;

import net.minecraft.client.gui.widget.TextFieldWidget;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
public class TextFieldWidgetHelper {
    private TextFieldWidget t;
    
    public TextFieldWidgetHelper(TextFieldWidget t) {
        this.t = t;
    }
    
    /**
     * @since 1.0.5
     * @return the {@code x} coordinate of the TextField.
     */
    public int getX() {
        return t.x;
    }
    
    /**
     * @since 1.0.5
     * @return the {@code y} coordinate of the TextField.
     */
    public int getY() {
        return t.y;
    }
    
    /**
     * set the TextField's position
     * 
     * @since 1.0.5
     * @param x
     * @param y
     */
    public void setPos(int x, int y) {
        t.x = x;
        t.y = y;
    }
    
    /**
     * @since 1.0.5
     * @return the currently entered {@link java.lang.String String}.
     */
    public String getText() {
        return t.getText();
    }
    
    /**
     * set the currently entered {@link java.lang.String String}.
     * 
     * @since 1.0.5
     * @param text
     * @return
     */
    public TextFieldWidgetHelper setText(String text) {
        t.setText(text);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @return
     */
    public int getWidth() {
        return t.getWidth();
    }
    
    /**
     * @since 1.0.5
     * @param width
     * @return
     */
    public TextFieldWidgetHelper setWidth(int width) {
        t.setWidth(width);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setEditableColor(int color) {
        t.setEditableColor(color);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param edit
     * @return
     */
    public TextFieldWidgetHelper setEditable(boolean edit) {
        t.setEditable(edit);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setUneditableColor(int color) {
        t.setUneditableColor(color);
        return this;
    }
    
    public TextFieldWidget getRaw() {
        return t;
    }
}
