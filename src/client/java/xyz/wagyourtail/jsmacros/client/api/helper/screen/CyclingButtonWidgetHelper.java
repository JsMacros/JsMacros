package xyz.wagyourtail.jsmacros.client.api.helper.screen;

import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinCyclingButton;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CyclingButtonWidgetHelper<T> extends ClickableWidgetHelper<CyclingButtonWidgetHelper<T>, CyclingButtonWidget<T>> {

    public CyclingButtonWidgetHelper(CyclingButtonWidget<T> btn) {
        super(btn);
    }

    public CyclingButtonWidgetHelper(CyclingButtonWidget<T> btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @return the current value.
     * @since 1.8.4
     */
    public T getValue() {
        return base.getValue();
    }

    /**
     * @return the current value in their string representation.
     * @since 1.8.4
     */
    public String getStringValue() {
        return ((MixinCyclingButton<T>) this).getValueToText().apply(getValue()).getString();
    }

    /**
     * @param val the new value
     * @return {@code true} if the value has changed, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean setValue(T val) {
        T lastVal = base.getValue();
        base.setValue(val);
        return lastVal.equals(base.getValue());
    }

    /**
     * @param amount the amount to cycle by
     * @return self for chaining.
     * @since 1.8.4
     */
    public CyclingButtonWidgetHelper<T> cycle(int amount) {
        ((MixinCyclingButton) base).invokeCycle(amount);
        return this;
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public CyclingButtonWidgetHelper<T> forward() {
        return cycle(1);
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public CyclingButtonWidgetHelper<T> backward() {
        return cycle(-1);
    }

    @Override
    public String toString() {
        return String.format("CyclingButtonWidgetHelper:{\"value\": \"%s\"}", getStringValue());
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class CyclicButtonBuilder<T> extends AbstractWidgetBuilder<CyclicButtonBuilder<T>, CyclingButtonWidget<T>, CyclingButtonWidgetHelper<T>> {

        private T value = null;
        private Text optionText = Text.empty();
        @Nullable
        private MethodWrapper<CyclingButtonWidgetHelper<T>, IScreen, Object, ?> action;
        private MethodWrapper<T, ?, TextHelper, ?> valueToText;
        @Nullable
        private MethodWrapper<?, ?, Boolean, ?> alternateToggle;
        private boolean optionTextOmitted = false;
        private List<T> defaultValues = Collections.emptyList();
        private List<T> alternateValues = Collections.emptyList();

        public CyclicButtonBuilder(IScreen screen, MethodWrapper<T, ?, TextHelper, ?> valueToText) {
            super(screen);
            this.valueToText = valueToText;
        }

        /**
         * @return the initial value of the slider.
         * @since 1.8.4
         */
        public T getInitialValue() {
            return value;
        }

        /**
         * @param value the initial value of the slider
         * @return self for chaining.
         * @since 1.8.4
         */
        public CyclicButtonBuilder<T> initially(T value) {
            this.value = value;
            return this;
        }

        /**
         * The option text is a prefix of all values, separated by a colon.
         *
         * @return the option text of the button or an empty text if it is omitted.
         * @since 1.8.4
         */
        public TextHelper getOption() {
            return TextHelper.wrap(optionText);
        }

        /**
         * @param option the option text of the button
         * @return self for chaining.
         * @since 1.8.4
         */
        public CyclicButtonBuilder<T> option(String option) {
            if (option != null) {
                optionText = Text.literal(option);
            }
            return this;
        }

        /**
         * @param option the option text of the button
         * @return self for chaining.
         * @since 1.8.4
         */
        public CyclicButtonBuilder<T> option(TextHelper option) {
            if (option != null) {
                optionText = option.getRaw();
            }
            return this;
        }

        /**
         * @return the action to run when the button is pressed.
         * @since 1.8.4
         */
        @Nullable
        public MethodWrapper<CyclingButtonWidgetHelper<T>, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the action to run when the button is pressed
         * @return self for chaining.
         * @since 1.8.4
         */
        public CyclicButtonBuilder<T> action(@Nullable MethodWrapper<CyclingButtonWidgetHelper<T>, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        /**
         * @return the function to convert a value to a text.
         * @since 1.8.4
         */
        public MethodWrapper<T, ?, TextHelper, ?> getValueToText() {
            return valueToText;
        }

        /**
         * @param valueToText the function to convert a value to a text
         * @return self for chaining.
         * @since 1.8.4
         */
        public CyclicButtonBuilder<T> valueToText(MethodWrapper<T, ?, TextHelper, ?> valueToText) {
            if (valueToText != null) {
                this.valueToText = valueToText;
            }
            return this;
        }

        /**
         * The button will normally cycle through the default values, but if the alternate toggle is
         * true, it will cycle through the alternate values.
         *
         * @return the list of all default values.
         * @since 1.8.4
         */
        public List<T> getDefaultValues() {
            return defaultValues;
        }

        /**
         * The button will normally cycle through the default values, but if the alternate toggle is
         * true, it will cycle through the alternate values.
         *
         * @return the list of all alternate values.
         * @since 1.8.4
         */
        public List<T> getAlternateValues() {
            return alternateValues;
        }

        /**
         * @param values the default values of the button
         * @return self for chaining.
         * @since 1.8.4
         */
        @SafeVarargs
        public final CyclicButtonBuilder<T> values(T... values) {
            this.defaultValues = Arrays.asList(values);
            return this;
        }

        /**
         * @param values the alternate values of the button
         * @return self for chaining.
         * @since 1.8.4
         */
        @SafeVarargs
        public final CyclicButtonBuilder<T> alternatives(T... values) {
            this.alternateValues = Arrays.asList(values);
            return this;
        }

        /**
         * @param defaults     the default values of the button
         * @param alternatives the alternate values of the button
         * @return self for chaining.
         * @since 1.8.4
         */
        public CyclicButtonBuilder<T> values(T[] defaults, T[] alternatives) {
            return values(Arrays.asList(defaults), Arrays.asList(alternatives));
        }

        /**
         * @param defaults     the default values of the button
         * @param alternatives the alternate values of the button
         * @return self for chaining.
         * @since 1.8.4
         */
        public CyclicButtonBuilder<T> values(List<T> defaults, List<T> alternatives) {
            this.defaultValues = defaults;
            this.alternateValues = alternatives;
            return this;
        }

        /**
         * @return the toggle function to determine if the button should cycle through the default
         * or the alternate values.
         * @since 1.8.4
         */
        @Nullable
        public MethodWrapper<?, ?, Boolean, ?> getAlternateToggle() {
            return alternateToggle;
        }

        /**
         * @param alternateToggle the toggle function to determine if the button should cycle
         *                        through the default or the alternate values
         * @return self for chaining.
         * @since 1.8.4
         */
        public CyclicButtonBuilder<T> alternateToggle(@Nullable MethodWrapper<?, ?, Boolean, ?> alternateToggle) {
            if (alternateToggle != null) {
                this.alternateToggle = alternateToggle;
            }
            return this;
        }

        /**
         * @return {@code true} if the prefix option text should be omitted, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isOptionTextOmitted() {
            return optionTextOmitted;
        }

        /**
         * @param optionTextOmitted whether the prefix option text should be omitted or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public CyclicButtonBuilder<T> omitTextOption(boolean optionTextOmitted) {
            this.optionTextOmitted = optionTextOmitted;
            return this;
        }

        @Override
        public CyclingButtonWidgetHelper<T> createWidget() {
            AtomicReference<CyclingButtonWidgetHelper<T>> b = new AtomicReference<>(null);
            CyclingButtonWidget.Builder<T> builder = CyclingButtonWidget.builder(obj -> valueToText.apply(obj).getRaw());
            if (optionTextOmitted || StringUtils.isBlank(optionText.getString())) {
                builder.omitKeyText();
            }
            if (alternateToggle != null && !alternateValues.isEmpty()) {
                builder.values(alternateToggle::get, defaultValues, alternateValues);
            } else {
                builder.values(defaultValues);
            }
            builder.initially(value);
            CyclingButtonWidget<T> cyclingButton = builder.build(getX(), getY(), getWidth(), getHeight(), optionText, (btn, val) -> {
                try {
                    if (action != null) {
                        action.accept(b.get(), screen);
                    }
                } catch (Exception e) {
                    JsMacrosClient.clientCore.profile.logError(e);
                }
                clickedOn(screen);
            });
            b.set(new CyclingButtonWidgetHelper<>(cyclingButton, getZIndex()));
            return b.get();
        }

    }

}
