package xyz.wagyourtail.jsmacros.client.api.helper.screen;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.wagyourgui.elements.Slider;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class SliderWidgetHelper extends ClickableWidgetHelper<SliderWidgetHelper, Slider> {

    public SliderWidgetHelper(Slider btn) {
        super(btn);
    }

    public SliderWidgetHelper(Slider btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @return the current value of this slider.
     * @since 1.8.4
     */
    public double getValue() {
        return this.base.getValue();
    }

    /**
     * @param value the new value
     * @return self for chaining.
     * @since 1.8.4
     */
    public SliderWidgetHelper setValue(double value) {
        base.setValue(value);
        return this;
    }

    /**
     * @return the set amount of steps of this slider.
     * @since 1.8.4
     */
    public int getSteps() {
        return base.getSteps();
    }

    /**
     * @param steps the amount of steps
     * @return self for chaining.
     * @since 1.8.4
     */
    public SliderWidgetHelper setSteps(int steps) {
        base.setSteps(steps);
        return this;
    }

    @Override
    public String toString() {
        return String.format("SliderWidgetHelper:{\"message\": \"%s\", \"value\": %f, \"steps\": %d}", base.getMessage().getString(), getValue(), getSteps());
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class SliderBuilder extends AbstractWidgetBuilder<SliderBuilder, Slider, SliderWidgetHelper> {

        private int steps = 2;
        private int value = 0;
        @Nullable
        private MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> action;

        public SliderBuilder(IScreen screen) {
            super(screen);
        }

        /**
         * @return the amount of steps of this slider.
         * @since 1.8.4
         */
        public int getSteps() {
            return steps;
        }

        /**
         * @param steps the amount of steps for the slider. Must be greater or equal to 2
         * @return self for chaining.
         * @since 1.8.4
         */
        public SliderBuilder steps(int steps) {
            this.steps = MathHelper.clamp(steps, 2, Integer.MAX_VALUE);
            return this;
        }

        /**
         * @return the initial value of the slider.
         * @since 1.8.4
         */
        public int getValue() {
            return value;
        }

        /**
         * @param value the initial value of the slider. Must be between 0 and steps - 1
         * @return self for chaining.
         * @since 1.8.4
         */
        public SliderBuilder initially(int value) {
            this.value = MathHelper.clamp(value, 0, steps - 1);
            return this;
        }

        /**
         * @return the change listener of the slider.
         * @since 1.8.4
         */
        @Nullable
        public MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the change listener for the slider
         * @return self for chaining.
         */
        public SliderBuilder action(@Nullable MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        @Override
        public SliderWidgetHelper createWidget() {
            AtomicReference<SliderWidgetHelper> b = new AtomicReference<>(null);
            Slider slider = new Slider(getX(), getY(), getWidth(), getHeight(), getMessage().getRaw(), value, (btn) -> {
                try {
                    if (action != null) {
                        action.accept(b.get(), screen);
                    }
                } catch (Exception e) {
                    JsMacrosClient.clientCore.profile.logError(e);
                }
            }, steps);
            b.set(new SliderWidgetHelper(slider, getZIndex()));
            return b.get();
        }

    }

}
