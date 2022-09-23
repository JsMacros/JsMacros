package xyz.wagyourtail.jsmacros.client.api.helpers.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class ButtonWidgetHelper<T extends ClickableWidget> extends BaseHelper<T> implements RenderCommon.RenderElement {
    public int zIndex;
    public List<Text> tooltips;

    public ButtonWidgetHelper(T btn) {
        this(btn, 0);
    }

    public ButtonWidgetHelper(T btn, int zIndex) {
        super(btn);
        this.zIndex = zIndex;
        this.tooltips = new ArrayList<>();
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
        base.setMessage(Text.literal(label));
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
        base.setMessage(helper.getRaw());
        return this;
    }
    
    /**
     * @since 1.2.3, renamed fro {@code getText} in 1.3.1
     * 
     * @return current button text.
     */
    public TextHelper getLabel() {
        return new TextHelper(base.getMessage());
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
            base.mouseClicked(base.x, base.y, 0);
            base.mouseReleased(base.x, base.y, 0);
        } else {
            final Semaphore waiter = new Semaphore(await ? 0 : 1);
            MinecraftClient.getInstance().execute(() -> {
                base.mouseClicked(base.x, base.y, 0);
                base.mouseReleased(base.x, base.y, 0);
                waiter.release();
            });
            waiter.acquire();
        }
        return this;
    }

    /**
     * @param tooltips the tooltips to set
     * @since 1.8.4
     */
    public void setTooltip(Object... tooltips) {
        this.tooltips = new ArrayList<>();
        for (Object text : tooltips) {
            addTooltip(text);
        }
    }

    /**
     * @param tooltip the tooltips to add
     * @since 1.8.4
     */
    public void addTooltip(Object tooltip) {
        if (tooltip instanceof TextBuilder) {
            tooltips.add(((TextBuilder) tooltip).build().getRaw());
        } else if (tooltip instanceof TextHelper) {
            tooltips.add(((TextHelper) tooltip).getRaw());
        } else if (tooltip instanceof String) {
            tooltips.add(Text.literal((String) tooltip));
        } else {
            tooltips.add(Text.literal(tooltip.toString()));
        }
    }

    /**
     * @param index the index of the tooltip to remove
     * @return
     *
     * @since 1.8.4
     */
    public boolean removeTooltip(int index) {
        if (index >= 0 && index < tooltips.size()) {
            tooltips.remove(index);
            return true;
        }
        return false;
    }

    /**
     * @param tooltip the tooltip to remove
     * @return {@code true} if the tooltip was removed successfully, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean removeTooltip(TextHelper tooltip) {
        return tooltips.remove(tooltip.getRaw());
    }

    /**
     * @return a copy of the tooltips
     *
     * @since 1.8.4
     */
    public List<TextHelper> getTooltips() {
        return tooltips.stream().map(TextHelper::new).toList();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        base.render(matrices, mouseX, mouseY, delta);
        if (base.isMouseOver(mouseX, mouseY) && tooltips.size() > 0) {
            MinecraftClient.getInstance().currentScreen.renderTooltip(matrices, tooltips, mouseX, mouseY);
        }
    }
    
    @Override
    public int getZIndex() {
        return zIndex;
    }


    public static class ButtonBuilder extends AbstractWidgetBuilder<ButtonWidget, ButtonWidgetHelper<ButtonWidget>> {

        private MethodWrapper<ButtonWidgetHelper<ButtonWidget>, IScreen, Object, ?> action;

        public ButtonBuilder(IScreen screen) {
            super(screen);
        }

        public MethodWrapper<ButtonWidgetHelper<ButtonWidget>, IScreen, Object, ?> getAction() {
            return action;
        }

        public ButtonBuilder action(MethodWrapper<ButtonWidgetHelper<ButtonWidget>, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        @Override
        public ButtonWidgetHelper<ButtonWidget> createWidget() {
            AtomicReference<ButtonWidgetHelper<ButtonWidget>> b = new AtomicReference<>(null);
            ButtonWidget button = new ButtonWidget(getX(), getY(), getWidth(), getHeight(), getMessage().getRaw(), btn -> {
                try {
                    if (action != null) {
                        action.accept(b.get(), screen);
                    }
                } catch (Throwable e) {
                    Core.getInstance().profile.logError(e);
                }
            });
            b.set(new ButtonWidgetHelper<>(button, getZIndex()));
            return b.get();
        }
    }

    public static class TexturedButtonBuilder extends ButtonBuilder {

        private int u = 0;
        private int v = 0;
        private int hoverOffset = 20;
        private Identifier texture;
        private int textureWidth = 256;
        private int textureHeight = 256;

        public TexturedButtonBuilder(IScreen screen) {
            super(screen);
        }

        public int getU() {
            return u;
        }

        public TexturedButtonBuilder u(int u) {
            this.u = u;
            return this;
        }

        public int getV() {
            return v;
        }

        public TexturedButtonBuilder v(int v) {
            this.v = v;
            return this;
        }

        public TexturedButtonBuilder uv(int u, int v) {
            this.u = u;
            this.v = v;
            return this;
        }

        public int getHoverOffset() {
            return hoverOffset;
        }

        public TexturedButtonBuilder hoverOffset(int hoverOffset) {
            this.hoverOffset = hoverOffset;
            return this;
        }

        public Identifier getTexture() {
            return texture;
        }

        public TexturedButtonBuilder texture(String texture) {
            this.texture = new Identifier(texture);
            return this;
        }

        public int getTextureWidth() {
            return textureWidth;
        }

        public TexturedButtonBuilder textureWidth(int textureWidth) {
            this.textureWidth = textureWidth;
            return this;
        }

        public int getTextureHeight() {
            return textureHeight;
        }

        public TexturedButtonBuilder textureHeight(int textureHeight) {
            this.textureHeight = textureHeight;
            return this;
        }

        public TexturedButtonBuilder textureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        @Override
        public ButtonWidgetHelper<ButtonWidget> createWidget() {
            AtomicReference<ButtonWidgetHelper<ButtonWidget>> b = new AtomicReference<>(null);
            TexturedButtonWidget button = new TexturedButtonWidget(getX(), getY(), getWidth(), getHeight(), u, v, hoverOffset, texture, textureWidth, textureHeight, btn -> {
                try {
                    if (getAction() != null) {
                        getAction().accept(b.get(), screen);
                    }
                } catch (Throwable e) {
                    Core.getInstance().profile.logError(e);
                }
            }, getMessage().getRaw());
            b.set(new ButtonWidgetHelper<>(button, getZIndex()));
            return b.get();
        }
    }
    
    @Override
    public String toString() {
        return String.format("ButtonWidgetHelper:{\"message\": \"%s\"}", base.getMessage().getString());
    }
    
}
