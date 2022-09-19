package xyz.wagyourtail.jsmacros.client.api.helpers.gui;

import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.wagyourgui.elements.Slider;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class SliderWidgetHelper extends ButtonWidgetHelper<Slider> {

    public SliderWidgetHelper(Slider btn) {
        super(btn);
    }

    public SliderWidgetHelper(Slider btn, int zIndex) {
        super(btn, zIndex);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public double getValue() {
        return this.base.getValue();
    }

    /**
     * @param value
     * @return
     *
     * @since 1.9.0
     */
    public SliderWidgetHelper setValue(double value) {
        base.setValue(value);
        return this;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getSteps() {
        return base.getSteps();
    }

    /**
     * @param steps
     * @return
     *
     * @since 1.9.0
     */
    public SliderWidgetHelper setSteps(int steps) {
        base.setSteps(steps);
        return this;
    }

    public static class SliderBuilder extends AbstractWidgetBuilder<Slider, SliderWidgetHelper> {

        private int steps = 2;
        private int value = 0;
        private MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> action;

        public SliderBuilder(IScreen screen) {
            super(screen);
        }

        public int getSteps() {
            return steps;
        }

        public SliderBuilder steps(int steps) {
            this.steps = steps;
            return this;
        }

        public int getValue() {
            return value;
        }

        public SliderBuilder initially(int value) {
            this.value = value;
            return this;
        }

        public MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> getAction() {
            return action;
        }

        public SliderBuilder action(MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> action) {
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
                    Core.getInstance().profile.logError(e);
                }
            }, steps);
            b.set(new SliderWidgetHelper(slider, getZIndex()));
            return b.get();
        }
    }

}
