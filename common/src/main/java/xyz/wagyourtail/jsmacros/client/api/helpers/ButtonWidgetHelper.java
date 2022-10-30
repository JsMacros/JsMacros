package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.concurrent.Semaphore;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class ButtonWidgetHelper<T extends ButtonWidget> extends BaseHelper<T> implements RenderCommon.RenderElement {
    public int zIndex;
    
    public ButtonWidgetHelper(T btn) {
        super(btn);
        zIndex = 0;
    }
    
    public ButtonWidgetHelper(T btn, int zIndex) {
        super(btn);
        this.zIndex = zIndex;
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
     * @since 1.0.5, renamed from {@code setText} in 1.3.1
     * @deprecated only deprecated in buttonWidgetHelper for confusing name.
     *
     * @param label
     * @return
     */
     @Deprecated
    public ButtonWidgetHelper<T> setLabel(String label) {
        base.message = label;
        return this;
    }
    
    /**
     * change the text.
     *
     * @since 1.3.1
     *
     * @param helper
     *
     * @return
     */
    public ButtonWidgetHelper<T> setLabel(TextHelper helper) {
        base.message = helper.getRaw().asFormattedString();
        return this;
    }
    
    /**
     * @since 1.2.3, renamed fro {@code getText} in 1.3.1
     * 
     * @return current button text.
     */
    public TextHelper getLabel() {
        return new TextHelper(new LiteralText(base.message));
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
    
    /**
     * clicks button
     * @since 1.3.1
     */
    public ButtonWidgetHelper<T> click() throws InterruptedException {
        click(true);
        return this;
    }
    
    /**
     * clicks button
     *
     * @param await should wait for button to finish clicking.
     * @since 1.3.1
     */
    public ButtonWidgetHelper<T> click(boolean await) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            base.isMouseOver(MinecraftClient.getInstance(), base.x, base.y);
            base.mouseReleased(base.x, base.y);
        } else {
            final Semaphore waiter = new Semaphore(await ? 0 : 1);
            MinecraftClient.getInstance().execute(() -> {
                base.isMouseOver(MinecraftClient.getInstance(), base.x, base.y);
                base.mouseReleased(base.x, base.y);
                waiter.release();
            });
            waiter.acquire();
        }
        return this;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        base.method_891(MinecraftClient.getInstance(), mouseX, mouseY, delta);
    }
    
    @Override
    public int getZIndex() {
        return zIndex;
    }
    
}
