package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.concurrent.Semaphore;

/**F
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class TextFieldWidgetHelper extends ButtonWidgetHelper<TextFieldWidget> {
    public TextFieldWidgetHelper(TextFieldWidget t) {
        super(t);
    }
    
    public TextFieldWidgetHelper(TextFieldWidget t, int zIndex) {
        super(t, zIndex);
    }
    
    /**
     * @since 1.0.5
     * @return the currently entered {@link java.lang.String String}.
     */
    public String getText() {
        return base.getText();
    }
    
    /**
     *
     * @since 1.0.5
     * @param text
     * @return
     */
    public TextFieldWidgetHelper setText(String text) throws InterruptedException {
        setText(text, true);
        return this;
    }
    
    /**
     * set the currently entered {@link java.lang.String String}.
     *
     * @param text
     * @param await
     *
     * @return
     * @since 1.3.1
     *
     * @throws InterruptedException
     */
    public TextFieldWidgetHelper setText(String text, boolean await) throws InterruptedException {
        boolean joinedMain = MinecraftClient.getInstance().isOnThread() || Core.instance.profile.joinedThreadStack.contains(Thread.currentThread());
        if (joinedMain && await) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined!");
        }
        final Semaphore waiter = new Semaphore(await ? 0 : 1);
        MinecraftClient.getInstance().execute(() -> {
            base.setText(text);
            waiter.release();
        });
        waiter.acquire();
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
