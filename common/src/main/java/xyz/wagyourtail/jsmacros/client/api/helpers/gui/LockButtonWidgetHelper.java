package xyz.wagyourtail.jsmacros.client.api.helpers.gui;

import net.minecraft.client.gui.widget.LockButtonWidget;

import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class LockButtonWidgetHelper extends ButtonWidgetHelper<LockButtonWidget> {

    public LockButtonWidgetHelper(LockButtonWidget btn) {
        super(btn);
    }

    public LockButtonWidgetHelper(LockButtonWidget btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isLocked() {
        return base.isLocked();
    }

    /**
     * @param locked
     * @return
     *
     * @since 1.9.0
     */
    public LockButtonWidgetHelper setLocked(boolean locked) {
        base.setLocked(locked);
        return this;
    }

    public static class LockButtonBuilder extends AbstractWidgetBuilder<LockButtonWidget, LockButtonWidgetHelper> {

        private boolean locked = false;
        private MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> action;

        public LockButtonBuilder(IScreen screen) {
            super(screen);
        }

        public boolean isLocked() {
            return locked;
        }

        public LockButtonBuilder locked(boolean locked) {
            this.locked = locked;
            return this;
        }

        public MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> getAction() {
            return action;
        }

        public LockButtonBuilder action(MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> action) {
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
                } catch (Throwable e) {
                    Core.getInstance().profile.logError(e);
                }
            });
            if (locked) {
                lockButton.setLocked(true);
            }
            b.set(new LockButtonWidgetHelper(lockButton, getZIndex()));
            return b.get();
        }
    }

}
