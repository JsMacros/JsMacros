package xyz.wagyourtail.jsmacros.client.api.classes.render;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos2D;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Vec2D;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.screen.*;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IScreen extends IDraw2D<IScreen> {

    /**
     * @return
     * @since 1.2.7
     */
    default String getScreenClassName() {
        return this.getClass().getSimpleName();
    }

    /**
     * @return
     * @since 1.0.5
     */
    String getTitleText();

    /**
     * in {@code 1.3.1} updated to work with all button widgets not just ones added by scripts.
     *
     * @return
     * @since 1.0.5
     */
    List<ClickableWidgetHelper<?, ?>> getButtonWidgets();

    /**
     * in {@code 1.3.1} updated to work with all text fields not just ones added by scripts.
     *
     * @return
     * @since 1.0.5
     */
    List<TextFieldWidgetHelper> getTextFields();

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param text
     * @param callback calls your method as a {@link Consumer}&lt;{@link ClickableWidgetHelper}&gt;
     * @return
     * @since 1.0.5
     */
    ClickableWidgetHelper<?, ?> addButton(int x, int y, int width, int height, String text, MethodWrapper<ClickableWidgetHelper<?, ?>, IScreen, Object, ?> callback);

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zIndex
     * @param text
     * @param callback calls your method as a {@link Consumer}&lt;{@link ClickableWidgetHelper}&gt;
     * @return
     * @since 1.4.0
     */
    ClickableWidgetHelper<?, ?> addButton(int x, int y, int width, int height, int zIndex, String text, MethodWrapper<ClickableWidgetHelper<?, ?>, IScreen, Object, ?> callback);

    /**
     * @param x           the x position of the checkbox
     * @param y           the y position of the checkbox
     * @param width       the width of the checkbox
     * @param height      the height of the checkbox
     * @param text        the text to display next to the checkbox
     * @param checked     whether the checkbox is checked or not
     * @param showMessage whether to show the message or not
     * @param callback    calls your method as a
     *                    {@link Consumer}&lt;{@link CheckBoxWidgetHelper}&gt;
     * @return a {@link CheckBoxWidgetHelper} for the given input.
     * @since 1.8.4
     */
    CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, String text, boolean checked, boolean showMessage, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the checkbox
     * @param y        the y position of the checkbox
     * @param width    the width of the checkbox
     * @param height   the height of the checkbox
     * @param text     the text to display next to the checkbox
     * @param checked  whether the checkbox is checked or not
     * @param callback calls your method as a {@link Consumer}&lt;{@link CheckBoxWidgetHelper}&gt;
     * @return a {@link CheckBoxWidgetHelper} for the given input.
     * @since 1.8.4
     */
    CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, String text, boolean checked, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the checkbox
     * @param y        the y position of the checkbox
     * @param width    the width of the checkbox
     * @param height   the height of the checkbox
     * @param zIndex   the z-index of the checkbox
     * @param text     the text to display next to the checkbox
     * @param checked  whether the checkbox is checked or not
     * @param callback calls your method as a {@link Consumer}&lt;{@link CheckBoxWidgetHelper}&gt;
     * @return a {@link CheckBoxWidgetHelper} for the given input.
     * @since 1.8.4
     */
    CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, int zIndex, String text, boolean checked, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x           the x position of the checkbox
     * @param y           the y position of the checkbox
     * @param width       the width of the checkbox
     * @param height      the height of the checkbox
     * @param zIndex      the z-index of the checkbox
     * @param text        the text to display next to the checkbox
     * @param checked     whether the checkbox is checked or not
     * @param showMessage whether to show the message or not
     * @param callback    calls your method as a
     *                    {@link Consumer}&lt;{@link CheckBoxWidgetHelper}&gt;
     * @return a {@link CheckBoxWidgetHelper} for the given input.
     * @since 1.8.4
     */
    CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, int zIndex, String text, boolean checked, boolean showMessage, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the slider
     * @param y        the y position of the slider
     * @param width    the width of the slider
     * @param height   the height of the slider
     * @param text     the text to be displayed inside the slider
     * @param value    the initial value of the slider
     * @param callback calls your method as a {@link Consumer}&lt;{@link SliderWidgetHelper}&gt;
     * @param steps    the number of steps the slider should have
     * @return a {@link SliderWidgetHelper} for the given input.
     * @since 1.8.4
     */
    SliderWidgetHelper addSlider(int x, int y, int width, int height, String text, double value, int steps, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the slider
     * @param y        the y position of the slider
     * @param width    the width of the slider
     * @param height   the height of the slider
     * @param zIndex   the z-index of the slider
     * @param text     the text to be displayed inside the slider
     * @param value    the initial value of the slider
     * @param callback calls your method as a {@link Consumer}&lt;{@link SliderWidgetHelper}&gt;
     * @param steps    the number of steps the slider should have
     * @return a {@link SliderWidgetHelper} for the given input.
     * @since 1.8.4
     */
    SliderWidgetHelper addSlider(int x, int y, int width, int height, int zIndex, String text, double value, int steps, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the slider
     * @param y        the y position of the slider
     * @param width    the width of the slider
     * @param height   the height of the slider
     * @param text     the text to be displayed inside the slider
     * @param value    the initial value of the slider
     * @param callback calls your method as a {@link Consumer}&lt;{@link SliderWidgetHelper}&gt;
     * @return a {@link SliderWidgetHelper} for the given input.
     * @since 1.8.4
     */
    SliderWidgetHelper addSlider(int x, int y, int width, int height, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the slider
     * @param y        the y position of the slider
     * @param width    the width of the slider
     * @param height   the height of the slider
     * @param zIndex   the z-index of the slider
     * @param text     the text to be displayed inside the slider
     * @param value    the initial value of the slider
     * @param callback calls your method as a {@link Consumer}&lt;{@link SliderWidgetHelper}&gt;
     * @return a {@link SliderWidgetHelper} for the given input.
     * @since 1.8.4
     */
    SliderWidgetHelper addSlider(int x, int y, int width, int height, int zIndex, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the lock button
     * @param y        the y position of the lock button
     * @param callback calls your method as a
     *                 {@link Consumer}&lt;{@link LockButtonWidgetHelper}&gt;
     * @return {@link LockButtonWidgetHelper} for the given input.
     * @since 1.8.4
     */
    LockButtonWidgetHelper addLockButton(int x, int y, MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the lock button
     * @param y        the y position of the lock button
     * @param zIndex   the z-index of the lock button
     * @param callback calls your method as a
     *                 {@link Consumer}&lt;{@link LockButtonWidgetHelper}&gt;
     * @return {@link LockButtonWidgetHelper} for the given input.
     * @since 1.8.4
     */
    LockButtonWidgetHelper addLockButton(int x, int y, int zIndex, MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the cylcing button
     * @param y        the y position of the cylcing button
     * @param width    the width of the cylcing button
     * @param height   the height of the cycling button
     * @param callback calls your method as a
     *                 {@link Consumer}&lt;{@link CyclingButtonWidgetHelper}&gt;
     * @param values   the values to cycle through
     * @param initial  the initial value of the cycling button
     * @return {@link CyclingButtonWidgetHelper} for the given input.
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, String[] values, String initial, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the cylcing button
     * @param y        the y position of the cylcing button
     * @param width    the width of the cylcing button
     * @param height   the height of the cycling button
     * @param zIndex   the z-index of the cycling button
     * @param callback calls your method as a
     *                 {@link Consumer}&lt;{@link CyclingButtonWidgetHelper}&gt;
     * @param values   the values to cycle through
     * @param initial  the initial value of the cycling button
     * @return {@link CyclingButtonWidgetHelper} for the given input.
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, String[] values, String initial, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback);

    /**
     * @param x            the x position of the cylcing button
     * @param y            the y position of the cylcing button
     * @param width        the width of the cylcing button
     * @param height       the height of the cycling button
     * @param zIndex       the z-index of the cycling button
     * @param callback     calls your method as a
     *                     {@link Consumer}&lt;{@link CyclingButtonWidgetHelper}&gt;
     * @param values       the values to cycle through
     * @param alternatives the alternative values to cycle through
     * @param prefix       the prefix of the values
     * @param initial      the initial value of the cycling button
     * @return {@link CyclingButtonWidgetHelper} for the given input.
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, String[] values, String[] alternatives, String initial, String prefix, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback);

    /**
     * @param x               the x position of the cylcing button
     * @param y               the y position of the cylcing button
     * @param width           the width of the cylcing button
     * @param height          the height of the cycling button
     * @param zIndex          the z-index of the cycling button
     * @param callback        calls your method as a
     *                        {@link Consumer}&lt;{@link CyclingButtonWidgetHelper}&gt;
     * @param values          the values to cycle through
     * @param alternatives    the alternative values to cycle through
     * @param prefix          the prefix of the values
     * @param initial         the initial value of the cycling button
     * @param alternateToggle the method to determine if the cycling button should use the
     *                        alternative values
     * @return {@link CyclingButtonWidgetHelper} for the given input.
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, String[] values, String[] alternatives, String initial, String prefix, MethodWrapper<?, ?, Boolean, ?> alternateToggle, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback);

    /**
     * @param btn
     * @return
     * @since 1.0.5
     */
    @Deprecated
    IScreen removeButton(ClickableWidgetHelper<?, ?> btn);

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param message
     * @param onChange calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return
     * @since 1.0.5
     */
    TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message, MethodWrapper<String, IScreen, Object, ?> onChange);

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zIndex
     * @param message
     * @param onChange calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return
     * @since 1.0.5
     */
    TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, int zIndex, String message, MethodWrapper<String, IScreen, Object, ?> onChange);

    /**
     * @param inp
     * @return
     * @since 1.0.5
     */
    @Deprecated
    IScreen removeTextInput(TextFieldWidgetHelper inp);

    /**
     * @param onMouseDown calls your method as a {@link BiConsumer}&lt;{@link Pos2D}, {@link Integer}&gt;
     * @return
     * @since 1.2.7
     */
    IScreen setOnMouseDown(MethodWrapper<Pos2D, Integer, Object, ?> onMouseDown);

    /**
     * @param onMouseDrag calls your method as a {@link BiConsumer}&lt;{@link Vec2D}, {@link Integer}&gt;
     * @return
     * @since 1.2.7
     */
    IScreen setOnMouseDrag(MethodWrapper<Vec2D, Integer, Object, ?> onMouseDrag);

    /**
     * @param onMouseUp calls your method as a {@link BiConsumer}&lt;{@link Pos2D}, {@link Integer}&gt;
     * @return
     * @since 1.2.7
     */
    IScreen setOnMouseUp(MethodWrapper<Pos2D, Integer, Object, ?> onMouseUp);

    /**
     * @param onScroll calls your method as a {@link BiConsumer}&lt;{@link Pos2D}, {@link Double}&gt;
     * @return
     * @since 1.2.7
     */
    IScreen setOnScroll(MethodWrapper<Pos2D, Pos2D, Object, ?> onScroll);

    /**
     * @param onKeyPressed calls your method as a {@link BiConsumer}&lt;{@link Integer}, {@link Integer}&gt;
     * @return
     * @since 1.2.7
     */
    IScreen setOnKeyPressed(MethodWrapper<Integer, Integer, Object, ?> onKeyPressed);

    /**
     * @param onCharTyped calls your method as a {@link BiConsumer}&lt;{@link Character}, {@link Integer}&gt;
     * @return
     * @since 1.8.4
     */
    IScreen setOnCharTyped(MethodWrapper<Character, Integer, Object, ?> onCharTyped);

    /**
     * @param onClose calls your method as a {@link Consumer}&lt;{@link IScreen}&gt;
     * @return
     * @since 1.2.7
     */
    IScreen setOnClose(MethodWrapper<IScreen, Object, Object, ?> onClose);

    /**
     * @since 1.1.9
     */
    void close();

    /**
     * calls the screen's init function re-loading it.
     *
     * @since 1.2.7
     */
    IScreen reloadScreen();

    /**
     * @return a new builder for buttons.
     * @since 1.8.4
     */
    ButtonWidgetHelper.ButtonBuilder buttonBuilder();

    /**
     * @return a new builder for checkboxes.
     * @since 1.8.4
     */
    CheckBoxWidgetHelper.CheckBoxBuilder checkBoxBuilder();

    /**
     * @param checked whether the checkbox should be checked by default
     * @return a new builder for checkboxes.
     * @since 1.8.4
     */
    CheckBoxWidgetHelper.CheckBoxBuilder checkBoxBuilder(boolean checked);

    /**
     * @return a new builder for cycling buttons.
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper.CyclicButtonBuilder<?> cyclicButtonBuilder(MethodWrapper<Object, ?, TextHelper, ?> valueToText);

    /**
     * @return a new builder for lock buttons.
     * @since 1.8.4
     */
    LockButtonWidgetHelper.LockButtonBuilder lockButtonBuilder();

    /**
     * @param locked whether the lock button should be locked by default
     * @return a new builder for lock buttons.
     * @since 1.8.4
     */
    LockButtonWidgetHelper.LockButtonBuilder lockButtonBuilder(boolean locked);

    /**
     * @return a new builder for sliders.
     * @since 1.8.4
     */
    SliderWidgetHelper.SliderBuilder sliderBuilder();

    /**
     * @return a new builder for text fields.
     * @since 1.8.4
     */
    TextFieldWidgetHelper.TextFieldBuilder textFieldBuilder();

    /**
     * @return a new builder for textured buttons.
     * @since 1.8.4
     */
    ButtonWidgetHelper.TexturedButtonBuilder texturedButtonBuilder();

    /**
     * @return {@code true} if the shift key is pressed, {@code false} otherwise.
     * @since 1.8.4
     */
    default boolean isShiftDown() {
        return Screen.hasShiftDown();
    }

    /**
     * @return {@code true} if the ctrl key is pressed, {@code false} otherwise.
     * @since 1.8.4
     */
    default boolean isCtrlDown() {
        return Screen.hasControlDown();
    }

    /**
     * @return {@code true} if the alt key is pressed, {@code false} otherwise.
     * @since 1.8.4
     */
    default boolean isAltDown() {
        return Screen.hasAltDown();
    }

    @Nullable
    MethodWrapper<IScreen, Object, Object, ?> getOnClose();

}
