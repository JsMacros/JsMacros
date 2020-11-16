package xyz.wagyourtail.jsmacros.api.helpers;

import net.minecraft.client.gui.widget.TextFieldWidget;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
public class TextFieldWidgetHelper extends ButtonWidgetHelper {
    public TextFieldWidgetHelper(TextFieldWidget t) {
        super(t);
    }
    
    /**
     * @since 1.0.5
     * @return the currently entered {@link java.lang.String String}.
     */
    public String getText() {
        return ((TextFieldWidget)btn).getText();
    }
    
    /**
     * set the currently entered {@link java.lang.String String}.
     * 
     * @since 1.0.5
     * @param text
     * @return
     */
    public TextFieldWidgetHelper setText(String text) {
        ((TextFieldWidget)btn).setText(text);
        return this;
    }
    
    
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setEditableColor(int color) {
        ((TextFieldWidget)btn).setEditableColor(color);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param edit
     * @return
     */
    public TextFieldWidgetHelper setEditable(boolean edit) {
        ((TextFieldWidget)btn).setEditable(edit);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setUneditableColor(int color) {
        ((TextFieldWidget)btn).setUneditableColor(color);
        return this;
    }
}
