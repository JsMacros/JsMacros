package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Image;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Item;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Rect;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Text;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IDraw2D<T> {
    
    /**
     * @since 1.2.7
     * @return screen width
     */
    public int getWidth();
    
    /**
     * @since 1.2.7
     * @return screen height
     */
    public int getHeight();
    
    /**
     * @since 1.2.7
     * @return text elements
     */
     @Deprecated
    public List<Text> getTexts();
    
    /**
     * @since 1.2.7
     * @return rect elements
     */
     @Deprecated
    public List<Rect> getRects();
    
    /**
     * @since 1.2.7
     * @return item elements
     */
     @Deprecated
    public List<Item> getItems();
    
    /**
     * @since 1.2.7
     * @return image elements
     */
     @Deprecated
    public List<Image> getImages();
    
    /**
    * @since 1.2.9
     * @return a read only copy of the list of all elements added by scripts.
     */
    public List<Drawable> getElements();
    
    /**
    * removes any element regardless of type.
    * @since 1.2.9
     * @return
     */
    public T removeElement(Drawable e);
    
    /**
    * re-add an element you removed with {@link #removeElement(Drawable)}
    * @since 1.2.9
     * @return
     */
    public Drawable reAddElement(Drawable e);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x
     * @param y
     * @param color
     * @param shadow
     * @return
     */
    public Text addText(String text, int x, int y, int color, boolean shadow);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x
     * @param y
     * @param color
     * @param shadow
     * @param scale
     * @param rotation
     * @return
     */
    public Text addText(String text, int x, int y, int color, boolean shadow, double scale, float rotation);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x
     * @param y
     * @param color
     * @param shadow
     * @return
     */
    public Text addText(TextHelper text, int x, int y, int color, boolean shadow);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x
     * @param y
     * @param color
     * @param shadow
     * @param scale
     * @param rotation
     * @return
     */
    public Text addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, float rotation);
    
    
    /**
     * @since 1.2.7
     * @param t
     * @return
     */
     @Deprecated
    public T removeText(Text t);
    
    /**
     * @since 1.2.7
     * @param x
     * @param y
     * @param width
     * @param height
     * @param id
     * @param imageX
     * @param imageY
     * @param regionWidth
     * @param regionHeight
     * @param textureWidth
     * @param textureHeight
     * @return
     */
    public Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight);
    
    /**
     * @since 1.2.7
     * @param x
     * @param y
     * @param width
     * @param height
     * @param id
     * @param imageX
     * @param imageY
     * @param regionWidth
     * @param regionHeight
     * @param textureWidth
     * @param textureHeight
     * @param rotation
     * @return
     */
    public Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float rotation);
    
    /**
     * @since 1.2.7
     * @param i
     * @return
     */
     @Deprecated
    public T removeImage(Image i);
    
    /**
     * @since 1.2.7
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color
     * @return
     */
    public Rect addRect(int x1, int y1, int x2, int y2, int color);
    
    /**
     * @since 1.2.7
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color
     * @param alpha
     * @return
     */
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha);
    
    /**
     * @since 1.2.7
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color
     * @param alpha
     * @param rotation
     * @return
     */
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, float rotation);
    
    /**
     * @since 1.2.7
     * @param r
     * @return
     */
     @Deprecated
    public T removeRect(Rect r);
    
    /**
     * @since 1.2.7
     * @param x
     * @param y
     * @param id
     * @return
     */
    public Item addItem(int x, int y, String id);
    
    /**
     * @since 1.2.7
     * @param x
     * @param y
     * @param id
     * @param overlay
     * @return
     */
    public Item addItem(int x, int y, String id, boolean overlay);
    
    /**
     * @since 1.2.7
     * @param x
     * @param y
     * @param id
     * @param overlay
     * @param scale
     * @param rotation
     * @return
     */
    public Item addItem(int x, int y, String id, boolean overlay, double scale, float rotation);
    
    /**
     * @since 1.2.7
     * @param x
     * @param y
     * @param item
     * @return
     */
    public Item addItem(int x, int y, ItemStackHelper item);
    
    /**
     * @since 1.2.7
     * @param x
     * @param y
     * @param item
     * @param overlay
     * @return
     */
    public Item addItem(int x, int y, ItemStackHelper item, boolean overlay);
    
    /**
     * @since 1.2.7
     * @param x
     * @param y
     * @param item
     * @param overlay
     * @param scale
     * @param rotation
     * @return
     */
    public Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, float rotation);
    
    /**
     * @since 1.2.7
     * @param i
     * @return
     */
     @Deprecated
    public T removeItem(Item i);

    /**
     * @since 1.2.7
     * @param onInit calls your method as a {@link Consumer}&lt;{@link T}&gt;
     * @return
     */
    public T setOnInit(MethodWrapper<T, Object, Object> onInit);
    
    /**
     * @since 1.2.7
     * @param catchInit calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return
     */
    public T setOnFailInit(MethodWrapper<String, Object, Object> catchInit);

    
    public void init();
    public void render(MatrixStack matrixStack);
}
