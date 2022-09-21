package xyz.wagyourtail.jsmacros.client.api.classes.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.*;
import java.util.function.IntSupplier;

/**
 * @author Wagyourtail
 *
 * @since 1.0.5
 *
 * @see xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D
 */
@SuppressWarnings("deprecation")
public class Draw2D extends DrawableHelper implements IDraw2D<Draw2D> {
    protected final Set<RenderCommon.RenderElement> elements = new LinkedHashSet<>();
    public final IntSupplier getWidth;
    public final IntSupplier getHeight;
    public int zIndex;
    
    /**
     * @since 1.0.5
     * @deprecated please use {@link Draw2D#setOnInit(MethodWrapper)}
     */
     @Deprecated
    public MethodWrapper<Draw2D, Object, Object, ?> onInit;
    /**
     * @since 1.1.9 [citation needed]
     * @deprecated please use {@link Draw2D#setOnFailInit(MethodWrapper)}
     */
     @Deprecated
    public MethodWrapper<String, Object, Object, ?> catchInit;
    
    protected final MinecraftClient mc;
    
    public Draw2D() {
        this.mc = MinecraftClient.getInstance();
        this.getWidth = () -> mc.getWindow().getScaledWidth();
        this.getHeight = () -> mc.getWindow().getScaledHeight();
    }
    
    /**
     * @since 1.0.5
     * @see IDraw2D#getWidth()
     */
    @Override
    public int getWidth() {
        return getWidth.getAsInt();
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#getHeight()
     */
    @Override
    public int getHeight() {
        return getHeight.getAsInt();
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#getTexts()
     */
    @Override
    public List<RenderCommon.Text> getTexts() {
        List<RenderCommon.Text> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RenderCommon.Text) list.add((RenderCommon.Text) e);
            }
        }
        return list;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#getRects()
     */
    @Override
    public List<RenderCommon.Rect> getRects() {
        List<RenderCommon.Rect> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RenderCommon.Rect) list.add((RenderCommon.Rect) e);
            }
        }
        return list;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#getItems()
     */
    @Override
    public List<RenderCommon.Item> getItems() {
        List<RenderCommon.Item> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RenderCommon.Item) list.add((RenderCommon.Item) e);
            }
        }
        return list;
    }

    /**
     * @since 1.2.3
     * @see IDraw2D#getImages()
     */
    @Override
    public List<RenderCommon.Image> getImages() {
        List<RenderCommon.Image> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RenderCommon.Image) list.add((RenderCommon.Image) e);
            }
        }
        return list;
    }

    @Override
    public List<RenderCommon.Draw2DElement> getDraw2Ds() {
        List<RenderCommon.Draw2DElement> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RenderCommon.Draw2DElement) list.add((RenderCommon.Draw2DElement) e);
            }
        }
        return list;
    }
    
    @Override
    public List<RenderCommon.RenderElement> getElements() {
        return ImmutableList.copyOf(elements);
    }
    
    @Override
    public Draw2D removeElement(RenderCommon.RenderElement e) {
        synchronized (elements) {
            elements.remove(e);
        }
        return this;
    }

    @Override
    public<T extends RenderCommon.RenderElement> T reAddElement(T e) {
        if (e instanceof RenderCommon.Draw2DElement draw2DElement) {
            Draw2D draw2D = draw2DElement.getDraw2D();
            if (draw2DElement.getDraw2D() == null || draw2DElement.getDraw2D() == this || hasCyclicDependencies(draw2D)) {
                return null;
            }
        }
        synchronized (elements) {
            elements.add(e);
        }
        return e;
    }

    @Override
    public RenderCommon.Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height) {
        return addDraw2D(draw2D, x, y, width, height, 0);
    }

    @Override
    public RenderCommon.Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height, int zIndex) {
        RenderCommon.Draw2DElement d = new RenderCommon.Draw2DElement(draw2D, x, y, width, height, zIndex, 1, 0);
        return reAddElement(d);
    }

    /**
     * @param draw2d
     * @return {@code true} if adding the child to the parent would result in a cyclic dependency.
     *
     * @since 1.8.4
     */
    private boolean hasCyclicDependencies(Draw2D draw2d) {
        Deque<Draw2D> queue = new ArrayDeque<>();
        queue.addFirst(draw2d);
        //Basic BFS algorithm to check whether this instance is a descendant of the specified draw2d
        while (!queue.isEmpty()) {
            Draw2D draw2D = queue.removeFirst();
            if (this == draw2D) {
                return true;
            }
            queue.addAll(draw2D.getDraw2Ds().stream().map(RenderCommon.Draw2DElement::getDraw2D).toList());
        }
        return false;
    }

    @Override
    public Draw2D removeDraw2D(RenderCommon.Draw2DElement draw2D) {
        synchronized (elements) {
            elements.remove(draw2D);
        }
        return this;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#addText(String, int, int, int, boolean)
     */
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
        
    }
    
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }
    
    /**
     * @since 1.2.6
     * @see IDraw2D#addText(String, int, int, int, boolean, double, double)
     */
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }
    
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        return reAddElement(new RenderCommon.Text(text, x, y, color, zIndex, shadow, scale, (float) rotation));
    }
    
    
    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }
    
    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }
    
    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }
    
    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        return reAddElement(new RenderCommon.Text(text, x, y, color, zIndex, shadow, scale, (float) rotation));
    }
    
    /**
     * @since 1.0.5
     * @see IDraw2D#removeText(xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Text)
     */
    @Override
    public Draw2D removeText(RenderCommon.Text t) {
        synchronized (elements) {
            elements.remove(t);
        }
        return this;
    }

    /**
     * @since 1.2.3
     * @see IDraw2D#addImage(int, int, int, int, String, int, int, int, int, int, int)
     */
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }
    
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, zIndex, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }
    
    /**
     * @since 1.2.6
     * @see IDraw2D#addImage(int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, 0, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }

    /**
     * @since 1.4.0
     * @see IDraw2D#addImage(int, int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, zIndex, 0xFFFFFFFF, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }

    /**
     * @since 1.6.5
     * @see IDraw2D#addImage(int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return reAddElement(new RenderCommon.Image(x, y, width, height, zIndex, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation));
    }
    /**
     * @since 1.6.5
     * @see IDraw2D#addImage(int, int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return reAddElement(new RenderCommon.Image(x, y, width, height, zIndex, alpha, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation));
    }

    /**
     * @since 1.2.3
     * @see IDraw2D#removeImage(xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Image)
     */
    @Override
    public Draw2D removeImage(RenderCommon.Image i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#addRect(int, int, int, int, int)
     */
    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color) {
        return reAddElement(new RenderCommon.Rect(x1, y1, x2, y2, color, 0F, 0));
    }

    /**
     * @since 1.1.8
     * @see IDraw2D#addRect(int, int, int, int, int, int)
     */
    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        return addRect(x1, y1, x2, y2, color, alpha, 0, 0);
    }

    /**
     * @since 1.2.6
     * @see IDraw2D#addRect(int, int, int, int, int, int, double)
     */
    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation) {
        return addRect(x1, y1, x2, y2, color, alpha, 0, 0);
    }
    
    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation, int zIndex) {
        return reAddElement(new RenderCommon.Rect(x1, y1, x2, y2, color, alpha, (float) rotation, zIndex));
    }
    
    /**
     * @since 1.0.5
     * @see IDraw2D#removeRect(xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Rect)
     */
    @Override
    public Draw2D removeRect(RenderCommon.Rect r) {
        synchronized (elements) {
            elements.remove(r);
        }
        return this;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#addItem(int, int, String)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, String id) {
        return addItem(x, y, id, true);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, String id) {
        return null;
    }
    
    /**
     * @since 1.2.0
     * @see IDraw2D#addItem(int, int, String, boolean)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, String id, boolean overlay) {
        return addItem(x, y, 0, id, overlay, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, String id, boolean overlay) {
        return addItem(x, y, zIndex, id, overlay, 1, 0);
    }
    
    /**
     * @since 1.2.0
     * @see IDraw2D#addItem(int, int, String, boolean, double, double)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, String id, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, id, overlay, scale, rotation);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, String id, boolean overlay, double scale, double rotation) {
        return reAddElement(new RenderCommon.Item(x, y, zIndex, id, overlay, scale, (float) rotation));
    }
    
    /**
     * @since 1.0.5
     * @see IDraw2D#addItem(int, int, ItemStackHelper)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper Item) {
        return addItem(x, y, Item, true);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, ItemStackHelper item) {
        return null;
    }
    
    /**
     * @since 1.2.0
     * @see IDraw2D#addItem(int, int, ItemStackHelper, boolean)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper Item, boolean overlay) {
        return addItem(x, y, Item, overlay, 1, 0);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, zIndex, item, overlay, 1, 0);
    }
    
    /**
     * @since 1.2.6
     * @see IDraw2D#addItem(int, int, ItemStackHelper, boolean, double, double)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, item, overlay, scale, rotation);
    }
    
    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        return reAddElement(new RenderCommon.Item(x, y, zIndex, item, overlay, scale, (float) rotation));
    }
    
    /**
     * @since 1.0.5
     * @see IDraw2D#removeItem(xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon.Item)
     */
    @Override
    public Draw2D removeItem(RenderCommon.Item i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }

    @Override
    public RenderCommon.Item.Builder getItemBuilder() {
        return new RenderCommon.Item.Builder(this);
    }

    @Override
    public RenderCommon.Image.Builder getImageBuilder() {
        return new RenderCommon.Image.Builder(this);
    }

    @Override
    public RenderCommon.Rect.Builder getRectBuilder() {
        return new RenderCommon.Rect.Builder(this);
    }

    @Override
    public RenderCommon.Text.Builder getTextBuilder() {
        return new RenderCommon.Text.Builder(this);
    }

    @Override
    public RenderCommon.Draw2DElement.Builder getDraw2DBuilder(Draw2D element) {
        return new RenderCommon.Draw2DElement.Builder(this, element);
    }


    public void init() {
        synchronized (elements) {
            elements.clear();
        }
        if (onInit != null) {
            try {
                onInit.accept(this);
            } catch(Throwable e) {
                e.printStackTrace();
                try {
                    if (catchInit != null) catchInit.accept(e.toString());
                    else throw e;
                } catch (Throwable f) {
                    Core.getInstance().profile.logError(f);
                }
            }
        }
        getDraw2Ds().forEach(e -> e.getDraw2D().init());
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if (matrixStack == null) return;

        synchronized (elements) {
            Iterator<RenderCommon.RenderElement> iter = getElementsByZIndex();
            while (iter.hasNext()) {
                iter.next().render(matrixStack, 0, 0, 0);
            }
        }
    }

    @NotNull
    protected Iterator<RenderCommon.RenderElement> getElementsByZIndex() {
        return elements.stream().sorted(Comparator.comparingInt(RenderCommon.RenderElement::getZIndex)).iterator();
    }
    
    /**
     *
     * init function, called when window is resized or screen/draw2d is registered.
     * clears all previous elements when called.
     *
     * @since 1.2.7
     * @see IDraw2D#setOnInit(MethodWrapper)
     * @param onInit calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link Draw2D}&gt;
     */
    @Override
    public Draw2D setOnInit(MethodWrapper<Draw2D, Object, Object, ?> onInit) {
        this.onInit = onInit;
        return this;
    }

    
    /**
     *
     * @since 1.2.7
     * @see IDraw2D#setOnFailInit(MethodWrapper)
     * @param catchInit calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link java.lang.String String}&gt;
     */
    @Override
    public Draw2D setOnFailInit(MethodWrapper<String, Object, Object, ?> catchInit) {
        this.catchInit = catchInit;
        return this;
    }

    /**
     * register so the overlay actually renders
     * @since 1.6.5
     * @return self for chaining
     */
    public Draw2D register() {
        this.init();
        FHud.overlays.add(this);
        return this;
    }

    /**
     * unregister so the overlay stops rendering
     * @since 1.6.5
     * @return self for chaining
     */
    public Draw2D unregister() {
        FHud.overlays.remove(this);
        return this;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }
    
}
