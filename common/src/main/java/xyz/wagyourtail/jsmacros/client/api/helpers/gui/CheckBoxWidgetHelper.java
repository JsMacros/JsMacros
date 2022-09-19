package xyz.wagyourtail.jsmacros.client.api.helpers.gui;

import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.wagyourgui.elements.CheckBox;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class CheckBoxWidgetHelper extends ButtonWidgetHelper<CheckBox> {

    public CheckBoxWidgetHelper(CheckBox btn) {
        super(btn);
    }

    public CheckBoxWidgetHelper(CheckBox btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isChecked() {
        return base.isChecked();
    }

    /**
     * @param checked
     * @return
     *
     * @since 1.9.0
     */
    public CheckBoxWidgetHelper setChecked(boolean checked) {
        if (base.isChecked() != checked) {
            base.onPress();
        }
        return this;
    }

    public static class CheckBoxBuilder extends AbstractWidgetBuilder<CheckBox, CheckBoxWidgetHelper> {

        private boolean checked = false;
        private MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> action;

        public CheckBoxBuilder(IScreen screen) {
            super(screen);
        }

        public boolean isChecked() {
            return checked;
        }

        public CheckBoxBuilder checked(boolean checked) {
            this.checked = checked;
            return this;
        }

        public MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> getAction() {
            return action;
        }

        public CheckBoxBuilder action(MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        @Override
        public CheckBoxWidgetHelper createWidget() {
            AtomicReference<CheckBoxWidgetHelper> b = new AtomicReference<>(null);
            CheckBox checkBox = new CheckBox(getX(), getY(), getWidth(), getHeight(), getMessage().getRaw(), checked, btn -> {
                try {
                    if (action != null) {
                        action.accept(b.get(), screen);
                    }
                } catch (Throwable e) {
                    Core.getInstance().profile.logError(e);
                }
            });
            b.set(new CheckBoxWidgetHelper(checkBox, getZIndex()));
            return b.get();
        }
    }

}
