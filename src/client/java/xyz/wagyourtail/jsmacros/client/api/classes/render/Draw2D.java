package xyz.wagyourtail.jsmacros.client.api.classes.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.classes.render.components.*;
import xyz.wagyourtail.jsmacros.client.api.classes.render.components3d.Surface;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.classes.Registrable;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @see IDraw2D
 * @since 1.0.5
 */
@SuppressWarnings("deprecation")
public class Draw2D implements IDraw2D<Draw2D>, Registrable<Draw2D> {
    protected final Set<RenderElement> elements = new LinkedHashSet<>();
    public IntSupplier widthSupplier;
    public IntSupplier heightSupplier;
    public int zIndex;
    public boolean visible = true;

    /**
     * @since 1.0.5
     * @deprecated please use {@link Draw2D#setOnInit(MethodWrapper)}
     */
    @Deprecated
    @Nullable
    public MethodWrapper<Draw2D, Object, Object, ?> onInit;
    /**
     * @since 1.1.9 [citation needed]
     * @deprecated please use {@link Draw2D#setOnFailInit(MethodWrapper)}
     */
    @Deprecated
    @Nullable
    public MethodWrapper<String, Object, Object, ?> catchInit;

    protected final MinecraftClient mc;

    public Draw2D() {
        this.mc = MinecraftClient.getInstance();
        this.widthSupplier = () -> mc.getWindow().getScaledWidth();
        this.heightSupplier = () -> mc.getWindow().getScaledHeight();
    }

    /**
     * @see IDraw2D#getWidth()
     * @since 1.0.5
     */
    @Override
    public int getWidth() {
        return widthSupplier.getAsInt();
    }

    /**
     * @see IDraw2D#getHeight()
     * @since 1.0.5
     */
    @Override
    public int getHeight() {
        return heightSupplier.getAsInt();
    }

    /**
     * @see IDraw2D#getTexts()
     * @since 1.0.5
     */
    @Override
    public List<Text> getTexts() {
        List<Text> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Text) {
                    list.add((Text) e);
                }
            }
        }
        return list;
    }

    /**
     * @see IDraw2D#getRects()
     * @since 1.0.5
     */
    @Override
    public List<Rect> getRects() {
        List<Rect> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Rect) {
                    list.add((Rect) e);
                }
            }
        }
        return list;
    }

    @Override
    public List<Line> getLines() {
        List<Line> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Line) {
                    list.add((Line) e);
                }
            }
        }
        return list;
    }

    /**
     * @see IDraw2D#getItems()
     * @since 1.0.5
     */
    @Override
    public List<Item> getItems() {
        List<Item> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Item) {
                    list.add((Item) e);
                }
            }
        }
        return list;
    }

    /**
     * @see IDraw2D#getImages()
     * @since 1.2.3
     */
    @Override
    public List<Image> getImages() {
        List<Image> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Image) {
                    list.add((Image) e);
                }
            }
        }
        return list;
    }

    @Override
    public List<Draw2DElement> getDraw2Ds() {
        List<Draw2DElement> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Draw2DElement) {
                    list.add((Draw2DElement) e);
                }
            }
        }
        return list;
    }

    @Override
    public List<RenderElement> getElements() {
        return ImmutableList.copyOf(elements);
    }

    @Override
    public Draw2D removeElement(RenderElement e) {
        synchronized (elements) {
            elements.remove(e);
        }
        return this;
    }

    @Override
    public <T extends RenderElement> T reAddElement(T e) {
        if (e instanceof Draw2DElement) {
            Draw2DElement draw2DElement = (Draw2DElement) e;
            Draw2D draw2D = draw2DElement.getDraw2D();
            if (draw2DElement.getDraw2D() == null || draw2DElement.getDraw2D() == this || hasCyclicDependencies(draw2D)) {
                return null;
            }
            if (this instanceof Surface) {
                draw2DElement.getDraw2D().init();
            }
        }
        synchronized (elements) {
            elements.add(e);
        }
        return e;
    }

    /**
     * @param visible whether to render this element.
     * @return self for chaining.
     * @since 1.8.4
     */
    public Draw2D setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /**
     * @return {@code true} if this draw2d is visible, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isVisible() {
        return visible;
    }

    @Override
    public Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height) {
        return addDraw2D(draw2D, x, y, width, height, 0);
    }

    @Override
    public Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height, int zIndex) {
        Draw2DElement d = draw2DBuilder(draw2D).pos(x, y).size(width, height).zIndex(zIndex).build();
        return reAddElement(d);
    }

    /**
     * @param draw2d the draw2d to check for cyclic dependencies
     * @return {@code true} if adding the child to the parent would result in a cyclic dependency.
     * @since 1.8.4
     */
    private boolean hasCyclicDependencies(Draw2D draw2d) {
        Deque<Draw2D> queue = new ArrayDeque<>();
        queue.addFirst(draw2d);
        // Basic BFS algorithm to check whether this instance is a descendant of the specified draw2d
        while (!queue.isEmpty()) {
            Draw2D draw2D = queue.removeFirst();
            if (this == draw2D) {
                return true;
            }
            queue.addAll(draw2D.getDraw2Ds().stream().map(Draw2DElement::getDraw2D).collect(Collectors.toList()));
        }
        return false;
    }

    @Override
    public Draw2D removeDraw2D(Draw2DElement draw2D) {
        synchronized (elements) {
            elements.remove(draw2D);
        }
        return this;
    }

    /**
     * @see IDraw2D#addText(String, int, int, int, boolean)
     * @since 1.0.5
     */
    @Override
    public Text addText(String text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);

    }

    @Override
    public Text addText(String text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }

    /**
     * @see IDraw2D#addText(String, int, int, int, boolean, double, double)
     * @since 1.2.6
     */
    @Override
    public Text addText(String text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }

    @Override
    public Text addText(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        return reAddElement(new Text(text, x, y, color, zIndex, shadow, scale, (float) rotation).setParent(this));
    }

    @Override
    public Text addText(TextHelper text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }

    @Override
    public Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }

    @Override
    public Text addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }

    @Override
    public Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        return reAddElement(new Text(text, x, y, color, zIndex, shadow, scale, (float) rotation).setParent(this));
    }

    /**
     * @see IDraw2D#removeText(Text)
     * @since 1.0.5
     */
    @Override
    public Draw2D removeText(Text t) {
        synchronized (elements) {
            elements.remove(t);
        }
        return this;
    }

    /**
     * @see IDraw2D#addImage(int, int, int, int, String, int, int, int, int, int, int)
     * @since 1.2.3
     */
    @Override
    public Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }

    @Override
    public Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, zIndex, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }

    /**
     * @see IDraw2D#addImage(int, int, int, int, String, int, int, int, int, int, int, double)
     * @since 1.2.6
     */
    @Override
    public Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, 0, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }

    /**
     * @see IDraw2D#addImage(int, int, int, int, int, String, int, int, int, int, int, int, double)
     * @since 1.4.0
     */
    @Override
    public Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, zIndex, 0xFFFFFFFF, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }

    /**
     * @see IDraw2D#addImage(int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     * @since 1.6.5
     */
    @Override
    public Image addImage(int x, int y, int width, int height, int zIndex, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return reAddElement(new Image(x, y, width, height, zIndex, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation).setParent(this));
    }

    /**
     * @see IDraw2D#addImage(int, int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     * @since 1.6.5
     */
    @Override
    public Image addImage(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return reAddElement(new Image(x, y, width, height, zIndex, alpha, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation).setParent(this));
    }

    /**
     * @see IDraw2D#removeImage(Image)
     * @since 1.2.3
     */
    @Override
    public Draw2D removeImage(Image i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }

    /**
     * @see IDraw2D#addRect(int, int, int, int, int)
     * @since 1.0.5
     */
    @Override
    public Rect addRect(int x1, int y1, int x2, int y2, int color) {
        return reAddElement(new Rect(x1, y1, x2, y2, color, 0F, 0).setParent(this));
    }

    /**
     * @see IDraw2D#addRect(int, int, int, int, int, int)
     * @since 1.1.8
     */
    @Override
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        return addRect(x1, y1, x2, y2, color, alpha, 0, 0);
    }

    /**
     * @see IDraw2D#addRect(int, int, int, int, int, int, double)
     * @since 1.2.6
     */
    @Override
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation) {
        return addRect(x1, y1, x2, y2, color, alpha, 0, 0);
    }

    @Override
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation, int zIndex) {
        return reAddElement(new Rect(x1, y1, x2, y2, color, alpha, (float) rotation, zIndex).setParent(this));
    }

    /**
     * @see IDraw2D#removeRect(Rect)
     * @since 1.0.5
     */
    @Override
    public Draw2D removeRect(Rect r) {
        synchronized (elements) {
            elements.remove(r);
        }
        return this;
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color) {
        return addLine(x1, y1, x2, y2, color, 0);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, int zIndex) {
        return addLine(x1, y1, x2, y2, color, zIndex, 1);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, double width) {
        return addLine(x1, y1, x2, y2, color, 0, width);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width) {
        return addLine(x1, y1, x2, y2, color, zIndex, width, 0);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, double width, double rotation) {
        return addLine(x1, y1, x2, y2, color, 0, width, rotation);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width, double rotation) {
        Line r = new Line(x1, y1, x2, y2, color, (float) rotation, (float) width, zIndex).setParent(this);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public Draw2D removeLine(Line l) {
        synchronized (elements) {
            elements.remove(l);
        }
        return this;
    }

    /**
     * @see IDraw2D#addItem(int, int, String)
     * @since 1.0.5
     */
    @Override
    @DocletReplaceParams("x: int, y: int, id: CanOmitNamespace<ItemId>")
    public Item addItem(int x, int y, String id) {
        return addItem(x, y, id, true);
    }

    @Override
    @DocletReplaceParams("x: int, y: int, zIndex: int, id: CanOmitNamespace<ItemId>")
    public Item addItem(int x, int y, int zIndex, String id) {
        return null;
    }

    /**
     * @see IDraw2D#addItem(int, int, String, boolean)
     * @since 1.2.0
     */
    @Override
    @DocletReplaceParams("x: int, y: int, id: CanOmitNamespace<ItemId>, overlay: boolean")
    public Item addItem(int x, int y, String id, boolean overlay) {
        return addItem(x, y, 0, id, overlay, 1, 0);
    }

    @Override
    @DocletReplaceParams("x: int, y: int, zIndex: int, id: CanOmitNamespace<ItemId>, overlay: boolean")
    public Item addItem(int x, int y, int zIndex, String id, boolean overlay) {
        return addItem(x, y, zIndex, id, overlay, 1, 0);
    }

    /**
     * @see IDraw2D#addItem(int, int, String, boolean, double, double)
     * @since 1.2.0
     */
    @Override
    @DocletReplaceParams("x: int, y: int, id: CanOmitNamespace<ItemId>, overlay: boolean, scale: double, rotation: double")
    public Item addItem(int x, int y, String id, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, id, overlay, scale, rotation);
    }

    @Override
    @DocletReplaceParams("x: int, y: int, zIndex: int, id: CanOmitNamespace<ItemId>, overlay: boolean, scale: double, rotation: double")
    public Item addItem(int x, int y, int zIndex, String id, boolean overlay, double scale, double rotation) {
        return reAddElement(new Item(x, y, zIndex, id, overlay, scale, (float) rotation).setParent(this));
    }

    /**
     * @see IDraw2D#addItem(int, int, ItemStackHelper)
     * @since 1.0.5
     */
    @Override
    public Item addItem(int x, int y, ItemStackHelper Item) {
        return addItem(x, y, Item, true);
    }

    @Override
    public Item addItem(int x, int y, int zIndex, ItemStackHelper item) {
        return null;
    }

    /**
     * @see IDraw2D#addItem(int, int, ItemStackHelper, boolean)
     * @since 1.2.0
     */
    @Override
    public Item addItem(int x, int y, ItemStackHelper Item, boolean overlay) {
        return addItem(x, y, Item, overlay, 1, 0);
    }

    @Override
    public Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, zIndex, item, overlay, 1, 0);
    }

    /**
     * @see IDraw2D#addItem(int, int, ItemStackHelper, boolean, double, double)
     * @since 1.2.6
     */
    @Override
    public Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, item, overlay, scale, rotation);
    }

    @Override
    public Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        return reAddElement(new Item(x, y, zIndex, item, overlay, scale, (float) rotation).setParent(this));
    }

    /**
     * @see IDraw2D#removeItem(Item)
     * @since 1.0.5
     */
    @Override
    public Draw2D removeItem(Item i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }

    public void init() {
        synchronized (elements) {
            elements.clear();
        }
        if (onInit != null) {
            try {
                onInit.accept(this);
                getDraw2Ds().forEach(e -> e.getDraw2D().init());
            } catch (Throwable e) {
                e.printStackTrace();
                try {
                    if (catchInit != null) {
                        catchInit.accept(e.toString());
                    } else {
                        throw e;
                    }
                } catch (Throwable f) {
                    JsMacrosClient.clientCore.profile.logError(f);
                }
            }
        }
    }

    @Override
    @DocletIgnore
    public void render(DrawContext drawContext) {
        if (drawContext == null || !visible) {
            return;
        }

        synchronized (elements) {
            Iterator<RenderElement> iter = getElementsByZIndex();
            while (iter.hasNext()) {
                iter.next().render(drawContext, 0, 0, 0);
            }
        }
    }

    public Iterator<RenderElement> getElementsByZIndex() {
        return elements.stream().sorted(Comparator.comparingInt(RenderElement::getZIndex)).iterator();
    }

    /**
     * init function, called when window is resized or screen/draw2d is registered.
     * clears all previous elements when called.
     *
     * @param onInit calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link Draw2D}&gt;
     * @see IDraw2D#setOnInit(MethodWrapper)
     * @since 1.2.7
     */
    @Override
    public Draw2D setOnInit(@Nullable MethodWrapper<Draw2D, Object, Object, ?> onInit) {
        this.onInit = onInit;
        return this;
    }

    /**
     * @param catchInit calls your method as a {@link java.util.function.Consumer Consumer}&lt;{@link String String}&gt;
     * @see IDraw2D#setOnFailInit(MethodWrapper)
     * @since 1.2.7
     */
    @Override
    public Draw2D setOnFailInit(@Nullable MethodWrapper<String, Object, Object, ?> catchInit) {
        this.catchInit = catchInit;
        return this;
    }

    /**
     * register so the overlay actually renders
     *
     * @return self for chaining
     * @since 1.6.5
     */
    @Override
    public Draw2D register() {
        this.init();
        FHud.overlays.add(this);
        return this;
    }

    /**
     * unregister so the overlay stops rendering
     *
     * @return self for chaining
     * @since 1.6.5
     */
    @Override
    public Draw2D unregister() {
        FHud.overlays.remove(this);
        return this;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public int getZIndex() {
        return zIndex;
    }

}
