package xyz.wagyourtail.jsmacros.api.classes;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Text;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IDraw2D;
import xyz.wagyourtail.jsmacros.extensionbase.MethodWrapper;

/**
 * @author Wagyourtail
 *
 * @since 1.0.5
 *
 * @see xyz.wagyourtail.jsmacros.api.sharedinterfaces.IDraw2D
 */
public class Draw2D extends DrawableHelper implements IDraw2D<Draw2D> {
    private List<RenderCommon.Text> textFields = new ArrayList<>();
    private List<RenderCommon.Rect> rectFields = new ArrayList<>();
    private List<RenderCommon.Item> itemFields = new ArrayList<>();
    private List<RenderCommon.Image> imageFields = new ArrayList<>();
    /**
     * @since 1.0.5
     * @deprecated please use {@link Draw2D#setOnInit(Consumer)}
     */
    public MethodWrapper<Draw2D, Object> onInit;
    /**
     * @since 1.1.9 [citation needed]
     * @deprecated please use {@link Draw2D#setOnFailInit(Consumer)}
     */
    public MethodWrapper<String, Object> catchInit;
    
    public MinecraftClient mc;
    
    public Draw2D() {
        this.mc = MinecraftClient.getInstance();
    }
    
    /**
     * @since 1.0.5
     * @see IDraw2D#getWidth()
     */
    @Override
    public int getWidth() {
        return mc.getWindow().getScaledWidth();
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#getHeight()
     */
    @Override
    public int getHeight() {
        return mc.getWindow().getScaledHeight();
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#getTexts()
     */
    @Override
    public List<RenderCommon.Text> getTexts() {
        return ImmutableList.copyOf(textFields);
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#getRects()
     */
    @Override
    public List<RenderCommon.Rect> getRects() {
        return ImmutableList.copyOf(rectFields);
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#getItems()
     */
    @Override
    public List<RenderCommon.Item> getItems() {
        return ImmutableList.copyOf(itemFields);
    }

    /**
     * @since 1.2.3
     * @see IDraw2D#getImages()
     */
    @Override
    public List<RenderCommon.Image> getImages() {
        return ImmutableList.copyOf(imageFields);
    }
    

    /**
     * @since 1.0.5
     * @see IDraw2D#addText(String, int, int, int, boolean)
     */
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, shadow, 1, 0);
        
    }

    /**
     * @since 1.2.6
     * @see IDraw2D#addText(String, int, int, int, boolean, double, float)
     */
    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, boolean shadow, double scale, float rotation) {
        RenderCommon.Text t = new RenderCommon.Text(text, x, y, color, shadow, scale, rotation);
        textFields.add(t);
        return t;
        
    }
    

    @Override
    public Text addText(TextHelper text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, shadow, 1, 0);
    }

    @Override
    public Text addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, float rotation) {
        RenderCommon.Text t = new RenderCommon.Text(text, x, y, color, shadow, scale, rotation);
        textFields.add(t);
        return t;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#removeText(xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Text)
     */
    @Override
    public Draw2D removeText(RenderCommon.Text t) {
        textFields.remove(t);
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

    /**
     * @since 1.2.6
     * @see IDraw2D#addImage(int, int, int, int, String, int, int, int, int, int, int, float)
     */
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float rotation) {
        RenderCommon.Image i = new RenderCommon.Image(x, y, width, height, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
        imageFields.add(i);
        return i;
    }

    /**
     * @since 1.2.3
     * @see IDraw2D#removeImage(xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Image)
     */
    @Override
    public Draw2D removeImage(RenderCommon.Image i) {
        imageFields.remove(i);
        return this;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#addRect(int, int, int, int, int)
     */
    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color) {
        RenderCommon.Rect r = new RenderCommon.Rect(x1, y1, x2, y2, color, 0F);
        rectFields.add(r);
        return r;
    }

    /**
     * @since 1.1.8
     * @see IDraw2D#addRect(int, int, int, int, int, int)
     */
    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        return addRect(x1, y1, x2, y2, color, alpha, 0);
    }

    /**
     * @since 1.2.6
     * @see IDraw2D#addRect(int, int, int, int, int, int, float)
     */
    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, float rotation) {
        RenderCommon.Rect r = new RenderCommon.Rect(x1, y1, x2, y2, color, alpha, rotation);
        rectFields.add(r);
        return r;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#removeRect(xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Rect)
     */
    @Override
    public Draw2D removeRect(RenderCommon.Rect r) {
        rectFields.remove(r);
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
    
    /**
     * @since 1.2.0
     * @see IDraw2D#addItem(int, int, String, boolean)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, String id, boolean overlay) {
        return addItem(x, y, id, overlay, 1, 0);
    }

    /**
     * @since 1.2.0
     * @see IDraw2D#addItem(int, int, String, boolean, double, float)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, String id, boolean overlay, double scale, float rotation) {
        RenderCommon.Item i = new RenderCommon.Item(x, y, id, overlay, scale, rotation);
        itemFields.add(i);
        return i;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#addItem(int, int, ItemStackHelper)
     */
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper Item) {
        return addItem(x, y, Item, true);
    }
    
    /**
     * @since 1.2.0
     * @see IDraw2D#addItem(int, int, ItemStackHelper, boolean)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper Item, boolean overlay) {
        return addItem(x, y, Item, overlay, 1, 0);
    }

    /**
     * @since 1.2.6
     * @see IDraw2D#addItem(int, int, ItemStackHelper, boolean, double, float)
     */
    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper Item, boolean overlay, double scale, float rotation) {
        RenderCommon.Item i = new RenderCommon.Item(x, y, Item, overlay, scale, rotation);
        itemFields.add(i);
        return i;
    }

    /**
     * @since 1.0.5
     * @see IDraw2D#removeItem(xyz.wagyourtail.jsmacros.api.sharedclasses.RenderCommon.Item)
     */
    @Override
    public Draw2D removeItem(RenderCommon.Item i) {
        itemFields.remove(i);
        return this;
    }

    @Override
    public void init() {
        textFields.clear();
        rectFields.clear();
        itemFields.clear();
        if (onInit != null) {
            try {
                onInit.accept(this);
            } catch(Exception e) {
                e.printStackTrace();
                try {
                    if (catchInit != null) catchInit.accept(e.toString());
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack) {
        if (matrixStack == null) return;
        
        RenderSystem.pushMatrix();
        for (RenderCommon.Rect r : ImmutableList.copyOf(this.rectFields)) {
            r.render(matrixStack);
        }
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        for (RenderCommon.Item i : ImmutableList.copyOf(this.itemFields)) {
            i.render(matrixStack);
        }
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        for (RenderCommon.Image i : ImmutableList.copyOf(this.imageFields)) {
            i.render(matrixStack);
        }
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        for (RenderCommon.Text t : ImmutableList.copyOf(this.textFields)) {
            t.render(matrixStack);
        }
        RenderSystem.popMatrix();
    }

    /**
     * @since 1.2.7
     * @see IDraw2D#setOnInit(xyz.wagyourtail.jsmacros.extensionbase.MethodWrappers.Consumer)
     */
    @Override
    public Draw2D setOnInit(MethodWrapper<Draw2D, Object> onInit) {
        this.onInit = onInit;
        return this;
    }

    
    /**
     * @since 1.2.7
     * @see IDraw2D#setOnFailInit(xyz.wagyourtail.jsmacros.extensionbase.MethodWrappers.Consumer)
     */
    @Override
    public Draw2D setOnFailInit(MethodWrapper<String, Object> catchInit) {
        this.catchInit = catchInit;
        return this;
    }
}
