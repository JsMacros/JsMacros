package xyz.wagyourtail.jsmacros.client.api.helper.screen;

import net.minecraft.client.gui.widget.CheckboxWidget;
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
public class CheckBoxWidgetHelper extends ClickableWidgetHelper<CheckBoxWidgetHelper, CheckboxWidget> {

    public CheckBoxWidgetHelper(CheckboxWidget btn) {
        super(btn);
    }

    public CheckBoxWidgetHelper(CheckboxWidget btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @return {@code true} if this button is checked, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isChecked() {
        return base.isChecked();
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public CheckBoxWidgetHelper toggle() {
        return setChecked(!base.isChecked());
    }

    /**
     * @param checked whether to check or uncheck this button
     * @return self for chaining.
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

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class CheckBoxBuilder extends AbstractWidgetBuilder<CheckBoxBuilder, CheckboxWidget, CheckBoxWidgetHelper> {

        private boolean checked = false;
        @Nullable
        private MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> action;

        public CheckBoxBuilder(IScreen screen) {
            super(screen);
        }

        /**
         * @return {@code true} if the checkbox is initially checked, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isChecked() {
            return checked;
        }

        /**
         * @param checked whether the checkbox is initially checked or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public CheckBoxBuilder checked(boolean checked) {
            this.checked = checked;
            return this;
        }

        /**
         * @return the action to run when the button is pressed.
         * @since 1.8.4
         */
        @Nullable
        public MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the action to run when the button is pressed
         * @return self for chaining.
         * @since 1.8.4
         */
        public CheckBoxBuilder action(@Nullable MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        @Override
        public CheckBoxWidgetHelper createWidget() {
            AtomicReference<CheckBoxWidgetHelper> b = new AtomicReference<>(null);
            CheckboxWidget checkBox = CheckboxWidget.builder(getMessage().getRaw(), mc.textRenderer).callback((btn, value) -> {
                try {
                    if (action != null) {
                        action.accept(b.get(), screen);
                    }
                } catch (Exception e) {
                    JsMacrosClient.clientCore.profile.logError(e);
                }
            }).pos(getX(), getY()).checked(isChecked()).build();
            checkBox.setWidth(getWidth());
            checkBox.setHeight(getHeight());
            b.set(new CheckBoxWidgetHelper(checkBox, getZIndex()));
            return b.get();
        }

    }

}
