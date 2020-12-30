package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.gui.widget.TextFieldWidget;

/**F
 * @author Wagyourtail
 * @since 1.0.5
 */
public class TextFieldWidgetHelper extends ButtonWidgetHelper<TextFieldWidget> {
    public TextFieldWidgetHelper(TextFieldWidget t) {
        super(t);
    }
    
    /**
     * @since 1.0.5
     * @return the currently entered {@link java.lang.String String}.
     */
    public String getText() {
        return base.getText();
    }
    
    /**
     * set the currently entered {@link java.lang.String String}.
     * 
     * @since 1.0.5
     * @param text
     * @return
     */
    public TextFieldWidgetHelper setText(String text) {
        base.setText(text);
        return this;
    }
    
    
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setEditableColor(int color) {
        base.setEditableColor(color);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param edit
     * @return
     */
    public TextFieldWidgetHelper setEditable(boolean edit) {
        base.setEditable(edit);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setUneditableColor(int color) {
        base.setUneditableColor(color);
        return this;
    }
}
