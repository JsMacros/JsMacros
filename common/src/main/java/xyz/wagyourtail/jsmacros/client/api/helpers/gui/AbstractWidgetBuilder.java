package xyz.wagyourtail.jsmacros.client.api.helpers.gui;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public abstract class AbstractWidgetBuilder<T extends ClickableWidget, U extends ButtonWidgetHelper<T>> {

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
     *
     * @since 1.8.4
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> width(int width) {
        this.width = width;
        return this;
    }

    /**
     * @return the height of the widget.
     *
     * @since 1.8.4
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> height(int height) {
        this.height = height;
        return this;
    }

    /**
     * @param width  the width of the widget
     * @param height the height of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * @return the z-index of the widget.
     *
     * @since 1.8.4
     */
    public int getZIndex() {
        return zIndex;
    }

    /**
     * @param zIndex the z-index of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> zIndex(int zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    /**
     * @return the x position of the widget.
     *
     * @since 1.8.4
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x position of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> x(int x) {
        this.x = x;
        return this;
    }

    /**
     * @return the y position of the widget.
     *
     * @since 1.8.4
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y position of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> y(int y) {
        this.y = y;
        return this;
    }

    /**
     * @param x the x position of the widget
     * @param y the y position of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> pos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * @return the message of the widget or an empty text if none is set.
     *
     * @since 1.8.4
     */
    public TextHelper getMessage() {
        return new TextHelper(message);
    }

    /**
     * @param message the message of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> message(String message) {
        if (message != null) {
            this.message = Text.literal(message);
        }
        return this;
    }

    /**
     * @param message the message of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> message(TextHelper message) {
        if (message != null) {
            this.message = message.getRaw();
        }
        return this;
    }

    /**
     * An inactive widget can not be interacted with and may have a different appearance.
     *
     * @return {@code true} if the widget is active, {@code false} otherwise.
     *
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
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> active(boolean active) {
        this.active = active;
        return this;
    }

    /**
     * @return {@code true} if the widget is visible, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible whether the widget should be visible or not
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /**
     * @return the alpha value of the widget.
     *
     * @since 1.8.4
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * @param alpha the alpha value of the widget
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public AbstractWidgetBuilder<T, U> alpha(float alpha) {
        this.alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
        return this;
    }

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

}
