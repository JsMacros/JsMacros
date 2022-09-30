package xyz.wagyourtail.jsmacros.client.api.helpers.gui;

import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.wagyourgui.elements.CheckBox;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CheckBoxWidgetHelper extends ButtonWidgetHelper<CheckBox> {

    public CheckBoxWidgetHelper(CheckBox btn) {
        super(btn);
    }

    public CheckBoxWidgetHelper(CheckBox btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @return {@code true} if this button is checked, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isChecked() {
        return base.isChecked();
    }

    /**
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public CheckBoxWidgetHelper toggle() {
        return setChecked(!base.isChecked());
    }

    /**
     * @param checked wheter to check or uncheck this button.
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public CheckBoxWidgetHelper setChecked(boolean checked) {
        if (base.isChecked() != checked) {
            base.onPress();
        }
        return this;
    }

    @Override
    public String toString() {
        return String.format("CheckBoxWidgetHelper:{\"message\": \"%s\", \"checked\": %b}", base.getMessage().getString(), isChecked());
    }

    public static class CheckBoxBuilder extends AbstractWidgetBuilder<CheckBox, CheckBoxWidgetHelper> {

        private boolean checked = false;
        private MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> action;

        public CheckBoxBuilder(IScreen screen) {
            super(screen);
        }

        /**
         * @return {@code true} if the checkbox is initially checked, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isChecked() {
            return checked;
        }

        /**
         * @param checked whether the checkbox is initially checked or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public CheckBoxBuilder checked(boolean checked) {
            this.checked = checked;
            return this;
        }

        /**
         * @return the action to run when the button is pressed.
         *
         * @since 1.8.4
         */
        public MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the action to run when the button is pressed
         * @return self for chaining.
         *
         * @since 1.8.4
         */
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
                } catch (Exception e) {
                    Core.getInstance().profile.logError(e);
                }
            });
            b.set(new CheckBoxWidgetHelper(checkBox, getZIndex()));
            return b.get();
        }
    }

}
