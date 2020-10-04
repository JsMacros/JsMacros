package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

import java.util.List;

import xyz.wagyourtail.jsmacros.api.MethodWrappers;
import xyz.wagyourtail.jsmacros.api.helpers.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.helpers.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Item;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Rect;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IScreen extends IDraw2D<IScreen> {
    
    
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
     * @param callback
     * @return
     */
    public ButtonWidgetHelper addButton(int x, int y, int width, int height, String text, MethodWrappers.BiConsumer<ButtonWidgetHelper, IScreen> callback);
    
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
     * @param onChange
     * @return
     */
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message, MethodWrappers.BiConsumer<String, IScreen> onChange);
    
    /**
     * @since 1.0.5
     * @param inp
     * @return
     */
    public IScreen removeTextInput(TextFieldWidgetHelper inp);
    
    /**
     * @since 1.2.7
     * @param onMouseDown
     * @return
     */
    public IScreen setOnMouseDown(MethodWrappers.BiConsumer<PositionCommon.Pos2D, Integer> onMouseDown);
    
    /**
     * @since 1.2.7
     * @param onMouseDrag
     * @return
     */
    public IScreen setOnMouseDrag(MethodWrappers.BiConsumer<PositionCommon.Vec2D, Integer> onMouseDrag);
    
    /**
     * @since 1.2.7
     * @param onMouseUp
     * @return
     */
    public IScreen setOnMouseUp(MethodWrappers.BiConsumer<PositionCommon.Pos2D, Integer> onMouseUp);
    
    /**
     * @since 1.2.7
     * @param onScroll
     * @return
     */
    public IScreen setOnScroll(MethodWrappers.BiConsumer<PositionCommon.Pos2D, Double> onScroll);
    
    /**
     * @since 1.2.7
     * @param onKeyPressed
     * @return
     */
    public IScreen setOnKeyPressed(MethodWrappers.BiConsumer<Integer, Integer> onKeyPressed);
    
    /**
     * @since 1.2.7
     * @param onClose
     * @return
     */
    public IScreen setOnClose(MethodWrappers.Consumer<IScreen> onClose);
    
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
    
}
