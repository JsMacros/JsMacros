package xyz.wagyourtail.jsmacros.client.api.helpers.gui;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;

/**
 * @author Etheradon
 * @since 1.9.0
 */
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

    public int getWidth() {
        return width;
    }

    public int getZIndex() {
        return zIndex;
    }

    public AbstractWidgetBuilder<T, U> zIndex(int zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    public AbstractWidgetBuilder<T, U> width(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public AbstractWidgetBuilder<T, U> height(int height) {
        this.height = height;
        return this;
    }

    public AbstractWidgetBuilder<T, U> size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }
    
    public int getX() {
        return x;
    }

    public AbstractWidgetBuilder<T, U> x(int x) {
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public AbstractWidgetBuilder<T, U> y(int y) {
        this.y = y;
        return this;
    }

    public AbstractWidgetBuilder<T, U> pos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public TextHelper getMessage() {
        return new TextHelper(message);
    }

    public AbstractWidgetBuilder<T, U> message(String message) {
        this.message = Text.literal(message);
        return this;
    }

    public AbstractWidgetBuilder<T, U> message(TextHelper message) {
        this.message = message.getRaw();
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public AbstractWidgetBuilder<T, U> active(boolean active) {
        this.active = active;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public AbstractWidgetBuilder<T, U> visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public float getAlpha() {
        return alpha;
    }

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
