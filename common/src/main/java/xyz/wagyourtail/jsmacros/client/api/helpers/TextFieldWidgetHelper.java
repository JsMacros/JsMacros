package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import xyz.wagyourtail.jsmacros.client.access.IGuiTextField;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.concurrent.Semaphore;

/**F
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class TextFieldWidgetHelper extends BaseHelper<GuiTextField> implements RenderCommon.RenderElement {
    public int zIndex;

    public TextFieldWidgetHelper(GuiTextField t) {
        super(t);
        zIndex = 0;
    }

    public TextFieldWidgetHelper(GuiTextField t, int zIndex) {
        super(t);
        zIndex = zIndex;
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
    public TextFieldWidgetHelper setPos(int x, int y) {
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
        return base.getInnerWidth();
    }

    /**
     * @since 1.0.5
     *
     * @return button clickable state.
     */
    public boolean getActive() {
        return ((IGuiTextField)base).isEnabled();
    }

    /**
     * set the button clickable state.
     *
     * @since 1.0.5
     *
     * @param t
     * @return
     */
    public TextFieldWidgetHelper setActive(boolean t) {
        base.setEditable(t);
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
    public TextFieldWidgetHelper setWidth(int width) {
        base.width = width;
        return this;
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
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            base.setText(text);
        } else {
            final Semaphore waiter = new Semaphore(await ? 0 : 1);
            Minecraft.getInstance().execute(() -> {
                base.setText(text);
                waiter.release();
            });
            waiter.acquire();
        }
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

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        base.render();
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

}
