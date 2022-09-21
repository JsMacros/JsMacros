package xyz.wagyourtail.jsmacros.client.api.helpers.gui;

import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import xyz.wagyourtail.jsmacros.client.access.ICyclingButtonWidget;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CyclingButtonWidgetHelper<T> extends ButtonWidgetHelper<CyclingButtonWidget<T>> {

    public CyclingButtonWidgetHelper(CyclingButtonWidget<T> btn) {
        super(btn);
    }

    public CyclingButtonWidgetHelper(CyclingButtonWidget<T> btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @return the current value.
     *
     * @since 1.8.4
     */
    public T getValue() {
        return base.getValue();
    }

    /**
     * @param val the new value
     * @return {@code true} if the value has changed, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean setValue(T val) {
        T lastText = base.getValue();
        base.setValue(val);
        return lastText.equals(base.getValue());
    }

    /**
     * @param amount the amount to cycle by
     * @return this instance for chaining.
     *
     * @since 1.8.4
     */
    public CyclingButtonWidgetHelper<T> cycle(int amount) {
        ((ICyclingButtonWidget) base).jsmacros_cycle(amount);
        return this;
    }

    /**
     * @return this instance for chaining.
     *
     * @since 1.8.4
     */
    public CyclingButtonWidgetHelper<T> forward() {
        return cycle(1);
    }

    /**
     * @return this instance for chaining.
     *
     * @since 1.8.4
     */
    public CyclingButtonWidgetHelper<T> backward() {
        return cycle(-1);
    }

    public static class CyclicButtonBuilder<T> extends AbstractWidgetBuilder<CyclingButtonWidget<T>, CyclingButtonWidgetHelper<T>> {

        private T value = null;
        private Text optionText = Text.empty();
        private MethodWrapper<CyclingButtonWidgetHelper<T>, IScreen, Object, ?> action;
        private MethodWrapper<T, ?, TextHelper, ?> valueToText;
        private MethodWrapper<?, ?, Boolean, ?> alternateToggle;
        private boolean optionTextOmitted = false;
        private List<T> defaultValues = List.of();
        private List<T> alternateValues = List.of();

        public CyclicButtonBuilder(IScreen screen, MethodWrapper<T, ?, TextHelper, ?> valueToText) {
            super(screen);
            this.valueToText = valueToText;
        }

        public T getInitialValue() {
            return value;
        }

        public CyclicButtonBuilder<T> initially(T value) {
            this.value = value;
            return this;
        }

        public TextHelper getMessage() {
            return new TextHelper(optionText);
        }

        public CyclicButtonBuilder<T> option(String message) {
            optionText = Text.literal(message);
            return this;
        }

        public CyclicButtonBuilder<T> option(TextHelper message) {
            optionText = message.getRaw();
            return this;
        }

        public MethodWrapper<CyclingButtonWidgetHelper<T>, IScreen, Object, ?> getAction() {
            return action;
        }

        public CyclicButtonBuilder<T> action(MethodWrapper<CyclingButtonWidgetHelper<T>, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        public MethodWrapper<T, ?, TextHelper, ?> getValueToText() {
            return valueToText;
        }

        public CyclicButtonBuilder<T> valueToText(MethodWrapper<T, ?, TextHelper, ?> valueToText) {
            this.valueToText = valueToText;
            return this;
        }

        public List<T> getDefaultValues() {
            return defaultValues;
        }

        public List<T> getAlternateValues() {
            return alternateValues;
        }

        @SafeVarargs
        public final CyclicButtonBuilder<T> values(T... values) {
            this.defaultValues = List.of(values);
            return this;
        }

        public CyclicButtonBuilder<T> values(T[] defaults, T[] alternatives) {
            return values(List.of(defaults), List.of(alternatives));
        }

        public CyclicButtonBuilder<T> values(List<T> defaults, List<T> alternatives) {
            this.defaultValues = defaults;
            this.alternateValues = alternatives;
            return this;
        }

        public MethodWrapper<?, ?, Boolean, ?> getAlternateToggle() {
            return alternateToggle;
        }

        public void alternateToggle(MethodWrapper<?, ?, Boolean, ?> alternateToggle) {
            this.alternateToggle = alternateToggle;
        }

        public boolean isOptionTextOmitted() {
            return optionTextOmitted;
        }

        public CyclicButtonBuilder<T> omitTextOption(boolean optionTextOmitted) {
            this.optionTextOmitted = optionTextOmitted;
            return this;
        }

        @Override
        public CyclingButtonWidgetHelper<T> createWidget() {
            AtomicReference<CyclingButtonWidgetHelper<T>> b = new AtomicReference<>(null);
            CyclingButtonWidget.Builder<T> builder = CyclingButtonWidget.builder(obj -> valueToText.apply(obj).getRaw());
            if (optionTextOmitted || optionText.getString().isBlank()) {
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
                    Core.getInstance().profile.logError(e);
                }
            });
            b.set(new CyclingButtonWidgetHelper<T>(cyclingButton, getZIndex()));
            return b.get();
        }
    }

}
