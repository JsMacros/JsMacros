package xyz.wagyourtail.jsmacros.client.api.helper.screen;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.render.components.Alignable;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public abstract class AbstractWidgetBuilder<B extends AbstractWidgetBuilder<B, T, U>, T extends ClickableWidget, U extends ClickableWidgetHelper<U, T>> implements Alignable<B> {

    protected final IScreen screen;

    private int zIndex;
    private int width;
    private int height = 20;
    private int x;
    private int y;
    private Text message = Text.empty();
    private boolean active = true;
    private boolean visible = true;
    private float alpha = 1.0F;

    protected AbstractWidgetBuilder(IScreen screen) {
        this.screen = screen;
    }

    /**
     * @return the width of the widget.
     * @since 1.8.4
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B width(int width) {
        this.width = width;
        return (B) this;
    }

    /**
     * @return the height of the widget.
     * @since 1.8.4
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B height(int height) {
        this.height = height;
        return (B) this;
    }

    /**
     * @param width  the width of the widget
     * @param height the height of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B size(int width, int height) {
        this.width = width;
        this.height = height;
        return (B) this;
    }

    /**
     * @return the z-index of the widget.
     * @since 1.8.4
     */
    public int getZIndex() {
        return zIndex;
    }

    /**
     * @param zIndex the z-index of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B zIndex(int zIndex) {
        this.zIndex = zIndex;
        return (B) this;
    }

    /**
     * @return the x position of the widget.
     * @since 1.8.4
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x position of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B x(int x) {
        this.x = x;
        return (B) this;
    }

    /**
     * @return the y position of the widget.
     * @since 1.8.4
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y position of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B y(int y) {
        this.y = y;
        return (B) this;
    }

    /**
     * @param x the x position of the widget
     * @param y the y position of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B pos(int x, int y) {
        this.x = x;
        this.y = y;
        return (B) this;
    }

    /**
     * @return the message of the widget or an empty text if none is set.
     * @since 1.8.4
     */
    public TextHelper getMessage() {
        return TextHelper.wrap(message);
    }

    /**
     * @param message the message of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B message(@Nullable String message) {
        if (message != null) {
            this.message = Text.literal(message);
        }
        return (B) this;
    }

    /**
     * @param message the message of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B message(@Nullable TextHelper message) {
        if (message != null) {
            this.message = message.getRaw();
        }
        return (B) this;
    }

    /**
     * An inactive widget can not be interacted with and may have a different appearance.
     *
     * @return {@code true} if the widget is active, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isActive() {
        return active;
    }

    /**
     * An inactive widget can not be interacted with and may have a different appearance.
     *
     * @param active whether the widget should be active or not
     * @return self for chaining.
     * @since 1.8.4
     */
    public B active(boolean active) {
        this.active = active;
        return (B) this;
    }

    /**
     * @return {@code true} if the widget is visible, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible whether the widget should be visible or not
     * @return self for chaining.
     * @since 1.8.4
     */
    public B visible(boolean visible) {
        this.visible = visible;
        return (B) this;
    }

    /**
     * @return the alpha value of the widget.
     * @since 1.8.4
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha value of the widget
     * @return self for chaining.
     * @since 1.8.4
     */
    public B alpha(double alpha) {
        this.alpha = (float) MathHelper.clamp(alpha, 0.0F, 1.0F);
        return (B) this;
    }

    /**
     * @return the build widget for the set properties.
     * @since 1.8.4
     */
    public final U build() {
        U helper = createWidget();
        T widget = helper.getRaw();
        widget.setAlpha(alpha);
        widget.active = active;
        widget.visible = visible;
        screen.reAddElement(helper);
        return helper;
    }

    protected abstract U createWidget();

    @Override
    public int getScaledWidth() {
        return width;
    }

    @Override
    public int getParentWidth() {
        return screen.getWidth();
    }

    @Override
    public int getScaledHeight() {
        return height;
    }

    @Override
    public int getParentHeight() {
        return screen.getHeight();
    }

    @Override
    public int getScaledLeft() {
        return x;
    }

    @Override
    public int getScaledTop() {
        return y;
    }

    @Override
    public B moveTo(int x, int y) {
        return pos(x, y);
    }

}
