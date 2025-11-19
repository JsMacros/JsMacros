package xyz.wagyourtail.jsmacros.client.api.helper.screen;

import net.minecraft.client.gui.widget.LockButtonWidget;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class LockButtonWidgetHelper extends ClickableWidgetHelper<LockButtonWidgetHelper, LockButtonWidget> {

    public LockButtonWidgetHelper(LockButtonWidget btn) {
        super(btn);
    }

    public LockButtonWidgetHelper(LockButtonWidget btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @return {@code true} if the button is locked, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isLocked() {
        return base.isLocked();
    }

    /**
     * @param locked whether to lock the button or not
     * @return self for chaining.
     * @since 1.8.4
     */
    public LockButtonWidgetHelper setLocked(boolean locked) {
        base.setLocked(locked);
        return this;
    }

    @Override
    public String toString() {
        return String.format("LockButtonWidgetHelper:{\"message\": \"%s\", \"locked\": %b}", base.getMessage().getString(), isLocked());
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class LockButtonBuilder extends AbstractWidgetBuilder<LockButtonBuilder, LockButtonWidget, LockButtonWidgetHelper> {

        private boolean locked = false;
        @Nullable
        private MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> action;

        public LockButtonBuilder(IScreen screen) {
            super(screen);
        }

        /**
         * @return the initial state of the lock button.
         * @since 1.8.4
         */
        public boolean isLocked() {
            return locked;
        }

        /**
         * @param locked whether to initially lock the button or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public LockButtonBuilder locked(boolean locked) {
            this.locked = locked;
            return this;
        }

        /**
         * @return the action to run when the button is pressed.
         * @since 1.8.4
         */
        @Nullable
        public MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the action to run when the button is pressed
         * @return self for chaining.
         * @since 1.8.4
         */
        public LockButtonBuilder action(@Nullable MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        @Override
        public LockButtonWidgetHelper createWidget() {
            AtomicReference<LockButtonWidgetHelper> b = new AtomicReference<>(null);
            LockButtonWidget lockButton = new LockButtonWidget(getX(), getY(), btn -> {
                try {
                    if (action != null) {
                        action.accept(b.get(), screen);
                    }
                } catch (Exception e) {
                    JsMacrosClient.clientCore.profile.logError(e);
                }
                clickedOn(screen);
            });
            if (locked) {
                lockButton.setLocked(true);
            }
            b.set(new LockButtonWidgetHelper(lockButton, getZIndex()));
            return b.get();
        }

    }

}
