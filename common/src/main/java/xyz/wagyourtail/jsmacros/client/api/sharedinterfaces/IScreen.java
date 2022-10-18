package xyz.wagyourtail.jsmacros.client.api.sharedinterfaces;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;

import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ClickableWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.CheckBoxWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.CyclingButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.LockButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.SliderWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
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
     * @since 1.2.7
     * @return
     */
    default String getScreenClassName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * @since 1.0.5
     * @return
     */
    String getTitleText();
    
    /**
     * in {@code 1.3.1} updated to work with all button widgets not just ones added by scripts.
     * @since 1.0.5
     * @return
     */
    List<ClickableWidgetHelper<?, ?>> getButtonWidgets();
    
    /**
     * in {@code 1.3.1} updated to work with all text fields not just ones added by scripts.
     * @since 1.0.5
     * @return
     */
    List<TextFieldWidgetHelper> getTextFields();
    
    /**
     * @since 1.0.5
     * @param x
     * @param y
     * @param width
     * @param height
     * @param text
     * @param callback calls your method as a {@link Consumer}&lt;{@link ClickableWidgetHelper}&gt;
     * @return
     */
    ClickableWidgetHelper<?, ?> addButton(int x, int y, int width, int height, String text, MethodWrapper<ClickableWidgetHelper<?, ?>, IScreen, Object, ?> callback);
    
    /**
     * @since 1.4.0
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zIndex
     * @param text
     * @param callback calls your method as a {@link Consumer}&lt;{@link ClickableWidgetHelper}&gt;
     * @return
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
     *
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
     *
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
     *
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
     *
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
     *
     * @since 1.8.4
     */
    SliderWidgetHelper addSlider(int x, int y, int width, int height, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback, int steps);

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
     *
     * @since 1.8.4
     */
    SliderWidgetHelper addSlider(int x, int y, int width, int height, int zIndex, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback, int steps);

    /**
     * @param x        the x position of the slider
     * @param y        the y position of the slider
     * @param width    the width of the slider
     * @param height   the height of the slider
     * @param text     the text to be displayed inside the slider
     * @param value    the initial value of the slider
     * @param callback calls your method as a {@link Consumer}&lt;{@link SliderWidgetHelper}&gt;
     * @return a {@link SliderWidgetHelper} for the given input.
     *
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
     *
     * @since 1.8.4
     */
    SliderWidgetHelper addSlider(int x, int y, int width, int height, int zIndex, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback);

    /**
     * @param x             the x position of the textured button
     * @param y             the y position of the textured button
     * @param width         the width of the textured button
     * @param height        the height of the textured button
     * @param textureStartX the x position in the texture to start drawing from
     * @param textureStartY the y position in the texture to start drawing from
     * @param hoverOffset   the offset to add to the textureStartY when the button is hovered
     * @param texture       the identifier of the texture to use
     * @param textureWidth  the width of the texture
     * @param textureHeight the height of the texture
     * @param callback      calls your method as a
     *                      {@link Consumer}&lt;{@link ButtonWidgetHelper}&gt;
     * @return a {@link ButtonWidgetHelper} for the given input.
     *
     * @since 1.8.4
     */
    ButtonWidgetHelper<TexturedButtonWidget> addTexturedButton(int x, int y, int width, int height, int textureStartX, int textureStartY, int hoverOffset, String texture, int textureWidth, int textureHeight, MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> callback);

    /**
     * @param x             the x position of the textured button
     * @param y             the y position of the textured button
     * @param width         the width of the textured button
     * @param height        the height of the textured button
     * @param zIndex        the z-index of the textured button
     * @param textureStartX the x position in the texture to start drawing from
     * @param textureStartY the y position in the texture to start drawing from
     * @param hoverOffset   the offset to add to the textureStartY when the button is hovered
     * @param texture       the identifier of the texture to use
     * @param textureWidth  the width of the texture
     * @param textureHeight the height of the texture
     * @param callback      calls your method as a
     *                      {@link Consumer}&lt;{@link ButtonWidgetHelper}&gt;
     * @return a {@link ButtonWidgetHelper} for the given input.
     *
     * @since 1.8.4
     */
    ButtonWidgetHelper<TexturedButtonWidget> addTexturedButton(int x, int y, int width, int height, int zIndex, int textureStartX, int textureStartY, int hoverOffset, String texture, int textureWidth, int textureHeight, MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> callback);

    /**
     * @param x             the x position of the textured button
     * @param y             the y position of the textured button
     * @param width         the width of the textured button
     * @param height        the height of the textured button
     * @param textureStartX the x position in the texture to start drawing from
     * @param textureStartY the y position in the texture to start drawing from
     * @param texture       the identifier of the texture to use
     * @param callback      calls your method as a
     *                      {@link Consumer}&lt;{@link ButtonWidgetHelper}&gt;
     * @return a {@link ButtonWidgetHelper} for the given input.
     *
     * @since 1.8.4
     */
    ButtonWidgetHelper<TexturedButtonWidget> addTexturedButton(int x, int y, int width, int height, int textureStartX, int textureStartY, String texture, MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> callback);

    /**
     * @param x             the x position of the textured button
     * @param y             the y position of the textured button
     * @param width         the width of the textured button
     * @param height        the height of the textured button
     * @param zIndex        the z-index of the textured button
     * @param textureStartX the x position in the texture to start drawing from
     * @param textureStartY the y position in the texture to start drawing from
     * @param texture       the identifier of the texture to use
     * @param callback      calls your method as a
     *                      {@link Consumer}&lt;{@link ButtonWidgetHelper}&gt;
     * @return a {@link ButtonWidgetHelper} for the given input.
     *
     * @since 1.8.4
     */
    ButtonWidgetHelper<TexturedButtonWidget> addTexturedButton(int x, int y, int width, int height, int zIndex, int textureStartX, int textureStartY, String texture, MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> callback);

    /**
     * @param x        the x position of the lock button
     * @param y        the y position of the lock button
     * @param callback calls your method as a
     *                 {@link Consumer}&lt;{@link LockButtonWidgetHelper}&gt;
     * @return {@link LockButtonWidgetHelper} for the given input.
     *
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
     *
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
     *
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback, String[] values, String initial);

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
     *
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback, String[] values, String initial);

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
     *
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback, String[] values, String[] alternatives, String initial, String prefix);

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
     *
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback, String[] values, String[] alternatives, String initial, String prefix, MethodWrapper<?, ?, Boolean, ?> alternateToggle);

    /**
     * @since 1.0.5
     * @param btn
     * @return
     */
     @Deprecated
    IScreen removeButton(ClickableWidgetHelper<?, ?> btn);
    
    /**
     * @since 1.0.5
     * @param x
     * @param y
     * @param width
     * @param height
     * @param message
     * @param onChange calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return
     */
    TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message, MethodWrapper<String, IScreen, Object, ?> onChange);
    
    /**
     * @since 1.0.5
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zIndex
     * @param message
     * @param onChange calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return
     */
    TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, int zIndex, String message, MethodWrapper<String, IScreen, Object, ?> onChange);
    
    /**
     * @since 1.0.5
     * @param inp
     * @return
     */
     @Deprecated
    IScreen removeTextInput(TextFieldWidgetHelper inp);
    
    /**
     * @since 1.2.7
     * @param onMouseDown calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Pos2D}, {@link Integer}&gt;
     * @return
     */
    IScreen setOnMouseDown(MethodWrapper<PositionCommon.Pos2D, Integer, Object, ?> onMouseDown);
    
    /**
     * @since 1.2.7
     * @param onMouseDrag calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Vec2D}, {@link Integer}&gt;
     * @return
     */
    IScreen setOnMouseDrag(MethodWrapper<PositionCommon.Vec2D, Integer, Object, ?> onMouseDrag);
    
    /**
     * @since 1.2.7
     * @param onMouseUp calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Pos2D}, {@link Integer}&gt;
     * @return
     */
    IScreen setOnMouseUp(MethodWrapper<PositionCommon.Pos2D, Integer, Object, ?> onMouseUp);
    
    /**
     * @since 1.2.7
     * @param onScroll calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Pos2D}, {@link Double}&gt;
     * @return
     */
    IScreen setOnScroll(MethodWrapper<PositionCommon.Pos2D, Double, Object, ?> onScroll);
    
    /**
     * @since 1.2.7
     * @param onKeyPressed calls your method as a {@link BiConsumer}&lt;{@link Integer}, {@link Integer}&gt;
     * @return
     */
    IScreen setOnKeyPressed(MethodWrapper<Integer, Integer, Object, ?> onKeyPressed);

    /**
     * @since 1.8.4
     * @param onCharTyped calls your method as a {@link BiConsumer}&lt;{@link Character}, {@link Integer}&gt;
     * @return
     */
    IScreen setOnCharTyped(MethodWrapper<Character, Integer, Object, ?> onCharTyped);
    
    /**
     * @since 1.2.7
     * @param onClose calls your method as a {@link Consumer}&lt;{@link IScreen}&gt;
     * @return
     */
    IScreen setOnClose(MethodWrapper<IScreen, Object, Object, ?> onClose);
    
    /**
     * @since 1.1.9
     */
    void close();
    
    /**
     * calls the screen's init function re-loading it.
     * @since 1.2.7
     */
    IScreen reloadScreen();

    /**
     * @return a new builder for buttons.
     *
     * @since 1.8.4
     */
    ButtonWidgetHelper.ButtonBuilder buttonBuilder();

    /**
     * @return a new builder for checkboxes.
     *
     * @since 1.8.4
     */
    CheckBoxWidgetHelper.CheckBoxBuilder checkBoxBuilder();

    /**
     * @return a new builder for cycling buttons.
     *
     * @since 1.8.4
     */
    CyclingButtonWidgetHelper.CyclicButtonBuilder<?> cyclicButtonBuilder(MethodWrapper<Object, ?, TextHelper, ?> valueToText);

    /**
     * @return a new builder for lock buttons.
     *
     * @since 1.8.4
     */
    LockButtonWidgetHelper.LockButtonBuilder lockButtonBuilder();

    /**
     * @return a new builder for sliders.
     *
     * @since 1.8.4
     */
    SliderWidgetHelper.SliderBuilder sliderBuilder();

    /**
     * @return a new builder for text fields.
     *
     * @since 1.8.4
     */
    TextFieldWidgetHelper.TextFieldBuilder textFieldBuilder();

    /**
     * @return a new builder for textured buttons.
     *
     * @since 1.8.4
     */
    ButtonWidgetHelper.TexturedButtonBuilder texturedButtonBuilder();

    /**
     * @return {@code true} if the shift key is pressed, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    default boolean isShiftDown() {
        return Screen.hasShiftDown();
    }

    /**
     * @return {@code true} if the ctrl key is pressed, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    default boolean isCtrlDown() {
        return Screen.hasControlDown();
    }

    /**
     * @return {@code true} if the alt key is pressed, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    default boolean isAltDown() {
        return Screen.hasAltDown();
    }
    
    MethodWrapper<IScreen, Object, Object, ?> getOnClose();
}