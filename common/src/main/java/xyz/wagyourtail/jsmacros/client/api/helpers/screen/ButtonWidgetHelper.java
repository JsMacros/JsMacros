package xyz.wagyourtail.jsmacros.client.api.helpers.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;

import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public class ButtonWidgetHelper<T extends ButtonWidget> extends ClickableWidgetHelper<ButtonWidgetHelper<T>, T> {

    public ButtonWidgetHelper(T btn) {
        super(btn);
    }

    public ButtonWidgetHelper(T btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class ButtonBuilder extends AbstractWidgetBuilder<ButtonBuilder, ButtonWidget, ButtonWidgetHelper<ButtonWidget>> {

        private MethodWrapper<ButtonWidgetHelper<ButtonWidget>, IScreen, Object, ?> action;

        public ButtonBuilder(IScreen screen) {
            super(screen);
        }

        /**
         * @param height this argument is ignored and will always be set to 20
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        @Override
        public ButtonBuilder height(int height) {
            super.height(20);
            return this;
        }

        /**
         * @param width  the width of the button
         * @param height this argument is ignored and will always be set to 20
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        @Override
        public ButtonBuilder size(int width, int height) {
            super.size(width, 20);
            return this;
        }

        /**
         * @return the action to run when the button is pressed.
         *
         * @since 1.8.4
         */
        public MethodWrapper<ButtonWidgetHelper<ButtonWidget>, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the action to run when the button is pressed
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public ButtonBuilder action(MethodWrapper<ButtonWidgetHelper<ButtonWidget>, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        @Override
        public ButtonWidgetHelper<ButtonWidget> createWidget() {
            AtomicReference<ButtonWidgetHelper<ButtonWidget>> b = new AtomicReference<>(null);
            ButtonWidget button = ButtonWidget.builder(getMessage().getRaw(), btn -> {
                try {
                    if (action != null) {
                        action.accept(b.get(), screen);
                    }
                } catch (Throwable e) {
                    Core.getInstance().profile.logError(e);
                }
            }).position(getX(), getY()).size(getWidth(), 20).build();
            b.set(new ButtonWidgetHelper(button, getZIndex()));
            return b.get();
        }
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class TexturedButtonBuilder extends AbstractWidgetBuilder<TexturedButtonBuilder, TexturedButtonWidget, ButtonWidgetHelper<TexturedButtonWidget>> {

        private MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> action;
        private int u = 0;
        private int v = 0;
        private int hoverOffset = 20;
        private Identifier texture;
        private int textureWidth = 256;
        private int textureHeight = 256;

        public TexturedButtonBuilder(IScreen screen) {
            super(screen);
        }

        /**
         * @param height this argument is ignored and will always be set to 20
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        @Override
        public TexturedButtonBuilder height(int height) {
            super.height(20);
            return this;
        }

        /**
         * @param width  the width of the button
         * @param height this argument is ignored and will always be set to 20
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        @Override
        public TexturedButtonBuilder size(int width, int height) {
            super.size(width, 20);
            return this;
        }

        /**
         * @return the action to run when the button is pressed.
         *
         * @since 1.8.4
         */
        public MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the action to run when the button is pressed
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public TexturedButtonBuilder action(MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        /**
         * @return the x position in the texture to start drawing from.
         *
         * @since 1.8.4
         */
        public int getU() {
            return u;
        }

        /**
         * @param u the x position in the texture to start drawing from
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public TexturedButtonBuilder u(int u) {
            this.u = u;
            return this;
        }

        /**
         * @return the y position in the texture to start drawing from.
         *
         * @since 1.8.4
         */
        public int getV() {
            return v;
        }

        /**
         * @param v the y position in the texture to start drawing from
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public TexturedButtonBuilder v(int v) {
            this.v = v;
            return this;
        }

        /**
         * @param u the x position in the texture to start drawing from
         * @param v the y position in the texture to start drawing from
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public TexturedButtonBuilder uv(int u, int v) {
            this.u = u;
            this.v = v;
            return this;
        }

        /**
         * @return the hover offset of the button.
         *
         * @since 1.8.4
         */
        public int getHoverOffset() {
            return hoverOffset;
        }

        /**
         * The hover offset is the vertical amount of pixels to offset the texture when the button
         * is hovered.
         *
         * @param hoverOffset the hover offset
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public TexturedButtonBuilder hoverOffset(int hoverOffset) {
            this.hoverOffset = hoverOffset;
            return this;
        }

        /**
         * @return the id of the texture to use or {@code null} if none is set.
         *
         * @since 1.8.4
         */
        public String getTexture() {
            return texture == null ? null : texture.toString();
        }

        /**
         * @param texture the texture id to use for the button
         * @return self for chaining.
         */
        public TexturedButtonBuilder texture(String texture) {
            this.texture = RegistryHelper.parseIdentifier(texture);
            return this;
        }

        /**
         * @return the width of the texture.
         *
         * @since 1.8.4
         */
        public int getTextureWidth() {
            return textureWidth;
        }

        /**
         * @param textureWidth the width of the texture
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public TexturedButtonBuilder textureWidth(int textureWidth) {
            this.textureWidth = textureWidth;
            return this;
        }

        /**
         * @return the height of the texture.
         *
         * @since 1.8.4
         */
        public int getTextureHeight() {
            return textureHeight;
        }

        /**
         * @param textureHeight the height of the texture
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public TexturedButtonBuilder textureHeight(int textureHeight) {
            this.textureHeight = textureHeight;
            return this;
        }

        /**
         * @param textureWidth  the width of the texture
         * @param textureHeight the height of the texture
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public TexturedButtonBuilder textureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        @Override
        public ButtonWidgetHelper<TexturedButtonWidget> createWidget() {
            AtomicReference<ButtonWidgetHelper<TexturedButtonWidget>> b = new AtomicReference<>(null);
            TexturedButtonWidget button = new TexturedButtonWidget(getX(), getY(), getWidth(), getHeight(), u, v, hoverOffset, texture, textureWidth, textureHeight, btn -> {
                try {
                    if (getAction() != null) {
                        getAction().accept(b.get(), screen);
                    }
                } catch (Throwable e) {
                    Core.getInstance().profile.logError(e);
                }
            }, getMessage().getRaw());
            b.set(new ButtonWidgetHelper(button, getZIndex()));
            return b.get();
        }
    }

}
