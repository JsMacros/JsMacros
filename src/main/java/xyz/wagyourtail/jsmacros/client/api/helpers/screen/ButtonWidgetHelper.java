package xyz.wagyourtail.jsmacros.client.api.helpers.screen;

import net.minecraft.client.gui.screen.ButtonTextures;
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
         * @since 1.8.4
         */
        @Override
        public ButtonBuilder size(int width, int height) {
            super.size(width, 20);
            return this;
        }

        /**
         * @return the action to run when the button is pressed.
         * @since 1.8.4
         */
        public MethodWrapper<ButtonWidgetHelper<ButtonWidget>, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the action to run when the button is pressed
         * @return self for chaining.
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
            b.set(new ButtonWidgetHelper<>(button, getZIndex()));
            return b.get();
        }

    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class TexturedButtonBuilder extends AbstractWidgetBuilder<TexturedButtonBuilder, TexturedButtonWidget, ButtonWidgetHelper<TexturedButtonWidget>> {

        private MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> action;

        private Identifier enabled;
        private Identifier disabled;
        private Identifier enabledFocused;
        private Identifier disabledFocused;

        public TexturedButtonBuilder(IScreen screen) {
            super(screen);
        }

        /**
         * @param height this argument is ignored and will always be set to 20
         * @return self for chaining.
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
         * @since 1.8.4
         */
        @Override
        public TexturedButtonBuilder size(int width, int height) {
            super.size(width, 20);
            return this;
        }

        /**
         * @return the action to run when the button is pressed.
         * @since 1.8.4
         */
        public MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the action to run when the button is pressed
         * @return self for chaining.
         * @since 1.8.4
         */
        public TexturedButtonBuilder action(MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        /**
         * @since 1.9.0
         * @return self for chaining.
         */
        public TexturedButtonBuilder enabledTexture(Identifier enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * @since 1.9.0
         * @return self for chaining.
         */
        public TexturedButtonBuilder disabledTexture(Identifier disabled) {
            this.disabled = disabled;
            return this;
        }

        /**
         * @since 1.9.0
         * @return self for chaining.
         */
        public TexturedButtonBuilder enabledFocusedTexture(Identifier enabledFocused) {
            this.enabledFocused = enabledFocused;
            return this;
        }

        /**
         * @since 1.9.0
         * @return self for chaining.
         */
        public TexturedButtonBuilder disabledFocusedTexture(Identifier disabledFocused) {
            this.disabledFocused = disabledFocused;
            return this;
        }

        @Override
        public ButtonWidgetHelper<TexturedButtonWidget> createWidget() {
            AtomicReference<ButtonWidgetHelper<TexturedButtonWidget>> b = new AtomicReference<>(null);
            TexturedButtonWidget button = new TexturedButtonWidget(getX(), getY(), getWidth(), getHeight(), new ButtonTextures(enabled, disabled, enabledFocused, disabledFocused), btn -> {
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

}
