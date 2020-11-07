package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

import xyz.wagyourtail.jsmacros.api.helpers.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.helpers.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Item;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Rect;
import xyz.wagyourtail.jsmacros.extensionbase.MethodWrapper;

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
    public default String getScreenClassName() {
        return this.getClass().getSimpleName();
    }
    
    /**
     * @since 1.0.5
     * @return
     */
    public String getTitleText();
    
    /**
     * @since 1.0.5
     * @return
     */
    public List<ButtonWidgetHelper> getButtonWidgets();
    
    /**
     * @since 1.0.5
     * @return
     */
    public List<TextFieldWidgetHelper> getTextFields();
    
    /**
     * @since 1.0.5
     * @param x
     * @param y
     * @param width
     * @param height
     * @param text
     * @param callback calls your method as a {@link Consumer}&lt;{@link ButtonWidgetHelper}&gt;
     * @return
     */
    public ButtonWidgetHelper addButton(int x, int y, int width, int height, String text, MethodWrapper<ButtonWidgetHelper, IScreen, Object> callback);
    
    /**
     * @since 1.0.5
     * @param btn
     * @return
     */
    public IScreen removeButton(ButtonWidgetHelper btn);
    
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
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message, MethodWrapper<String, IScreen, Object> onChange);
    
    /**
     * @since 1.0.5
     * @param inp
     * @return
     */
    public IScreen removeTextInput(TextFieldWidgetHelper inp);
    
    /**
     * @since 1.2.7
     * @param onMouseDown calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Pos2D}, {@link Integer}&gt;
     * @return
     */
    public IScreen setOnMouseDown(MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseDown);
    
    /**
     * @since 1.2.7
     * @param onMouseDrag calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Vec2D}, {@link Integer}&gt;
     * @return
     */
    public IScreen setOnMouseDrag(MethodWrapper<PositionCommon.Vec2D, Integer, Object> onMouseDrag);
    
    /**
     * @since 1.2.7
     * @param onMouseUp calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Pos2D}, {@link Integer}&gt;
     * @return
     */
    public IScreen setOnMouseUp(MethodWrapper<PositionCommon.Pos2D, Integer, Object> onMouseUp);
    
    /**
     * @since 1.2.7
     * @param onScroll calls your method as a {@link BiConsumer}&lt;{@link PositionCommon.Pos2D}, {@link Double}&gt;
     * @return
     */
    public IScreen setOnScroll(MethodWrapper<PositionCommon.Pos2D, Double, Object> onScroll);
    
    /**
     * @since 1.2.7
     * @param onKeyPressed calls your method as a {@link BiConsumer}&lt;{@link Integer}, {@link Integer}&gt;
     * @return
     */
    public IScreen setOnKeyPressed(MethodWrapper<Integer, Integer, Object> onKeyPressed);
    
    /**
     * @since 1.2.7
     * @param onClose calls your method as a {@link Consumer}&lt;{@link IScreen}&gt;
     * @return
     */
    public IScreen setOnClose(MethodWrapper<IScreen, Object, Object> onClose);
    
    /**
     * @since 1.1.9
     */
    public void close();
    
    /**
     * @since 1.2.0
     */
    public Rect addRect(int x1, int y1, int x2, int y2, int color);
    
    /**
     * @since 1.2.0
     */
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha);
    
    /**
     * @since 1.2.0
     */
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, float rotation);
    
    /**
    * @since 1.2.0
     */
    public IScreen removeRect(Rect r);
    
    /**
     * @since 1.2.0
     */
    public Item addItem(int x, int y, String id);
    
    /**
     * @since 1.2.0
     */
    public Item addItem(int x, int y, String id, boolean overlay);
    
    /**
     * @since 1.2.0
     */
    public Item addItem(int x, int y, String id, boolean overlay, double scale, float rotation);
    
    /**
     * @since 1.2.0
     */
    public Item addItem(int x, int y, ItemStackHelper item);
    
    /**
     * @since 1.2.0
     */
    public Item addItem(int x, int y, ItemStackHelper item, boolean overlay);
    
    /**
     * @since 1.2.0
     */
    public Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, float rotation);
    
    /**
     * @since 1.2.0
     */
    public IScreen removeItem(Item i);
    
    /**
     * calls the screen's init function re-loading it.
     * @since 1.2.7
     */
    public IScreen reloadScreen();
    
}
