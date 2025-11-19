package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class Item implements RenderElement, Alignable<Item> {

    private static final int DEFAULT_ITEM_SIZE = 16;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Nullable
    public IDraw2D<?> parent;
    public ItemStack item;
    public String ovText;
    public boolean overlay;
    public double scale;
    public float rotation;
    public boolean rotateCenter;
    public int x;
    public int y;
    public int zIndex;

    @DocletReplaceParams("x: int, y: int, zIndex: int, id: CanOmitNamespace<ItemId>, overlay: boolean, scale: double, rotation: float")
    public Item(int x, int y, int zIndex, String id, boolean overlay, double scale, float rotation) {
        this(x, y, zIndex, new ItemStackHelper(id, 1), overlay, scale, rotation);
    }

    public Item(int x, int y, int zIndex, ItemStackHelper i, boolean overlay, double scale, float rotation) {
        this(x, y, zIndex, i, overlay, scale, rotation, null);
    }

    public Item(int x, int y, int zIndex, ItemStackHelper itemStack, boolean overlay, double scale, float rotation, String ovText) {
        this.x = x;
        this.y = y;
        this.item = itemStack.getRaw();
        this.overlay = overlay;
        this.scale = scale;
        this.rotation = rotation;
        this.zIndex = zIndex;
        this.ovText = ovText;
    }

    /**
     * @param i
     * @return
     * @since 1.0.5 [citation needed]
     */
    public Item setItem(ItemStackHelper i) {
        if (i != null) {
            this.item = i.getRaw();
        } else {
            this.item = null;
        }
        return this;
    }

    /**
     * @param id
     * @param count
     * @return
     * @since 1.0.5 [citation needed]
     */
    @DocletReplaceParams("id: CanOmitNamespace<ItemId>, count: int")
    public Item setItem(String id, int count) {
        this.item = new ItemStack(Registries.ITEM.get(RegistryHelper.parseIdentifier(id)), count);
        return this;
    }

    /**
     * @return
     * @since 1.0.5 [citation needed]
     */
    public ItemStackHelper getItem() {
        return new ItemStackHelper(item);
    }

    /**
     * @param x the new x position of this element
     * @return self for chaining.
     * @since 1.8.4
     */
    public Item setX(int x) {
        this.x = x;
        return this;
    }

    /**
     * @return the x position of this element.
     * @since 1.8.4
     */
    public int getX() {
        return x;
    }

    /**
     * @param y the new y position of this element
     * @return self for chaining.
     * @since 1.8.4
     */
    public Item setY(int y) {
        this.y = y;
        return this;
    }

    /**
     * @return the y position of this element.
     * @since 1.8.4
     */
    public int getY() {
        return y;
    }

    /**
     * @param x
     * @param y
     * @return
     * @since 1.0.5
     */
    public Item setPos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * @param scale
     * @return
     * @throws IllegalArgumentException
     * @since 1.2.6
     */
    public Item setScale(double scale) throws IllegalArgumentException {
        if (scale == 0) {
            throw new IllegalArgumentException("Scale can't be 0");
        }
        this.scale = scale;
        return this;
    }

    /**
     * @return the scale of this item.
     * @since 1.8.4
     */
    public double getScale() {
        return scale;
    }

    /**
     * @param rotation
     * @return
     * @since 1.2.6
     */
    public Item setRotation(double rotation) {
        this.rotation = MathHelper.wrapDegrees((float) rotation);
        return this;
    }

    /**
     * @return the rotation of this item.
     * @since 1.8.4
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * @param rotateCenter whether the item should be rotated around its center
     * @return self for chaining.
     * @since 1.8.4
     */
    public Item setRotateCenter(boolean rotateCenter) {
        this.rotateCenter = rotateCenter;
        return this;
    }

    /**
     * @return {@code true} if this item should be rotated around its center, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isRotatingCenter() {
        return rotateCenter;
    }

    /**
     * @param overlay
     * @return
     * @since 1.2.0
     */
    public Item setOverlay(boolean overlay) {
        this.overlay = overlay;
        return this;
    }

    /**
     * @return {@code true}, if the overlay, i.e. the durability bar, and the overlay text or
     * item count should be shown, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean shouldShowOverlay() {
        return overlay;
    }

    /**
     * @param ovText
     * @return
     * @since 1.2.0
     */
    public Item setOverlayText(String ovText) {
        this.ovText = ovText;
        return this;
    }

    /**
     * @return the overlay text of this item.
     * @since 1.8.4
     */
    public String getOverlayText() {
        return ovText;
    }

    /**
     * @param zIndex the new z-index of this item
     * @return self for chaining.
     * @since 1.8.4
     */
    public Item setZIndex(int zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        render(drawContext, mouseX, mouseY, delta, false);
    }

    @Override
    @DocletIgnore
    public void render3D(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        render(drawContext, mouseX, mouseY, delta, true);
    }

    @DocletIgnore
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta, boolean is3dRender) {
        if (item == null) {
            return;
        }
        Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        setupMatrix(matrices, x, y, (float) scale, rotation, DEFAULT_ITEM_SIZE, DEFAULT_ITEM_SIZE, rotateCenter);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        if (is3dRender) {
            // The item has an offset of 100 and item texts of 200. This will make them render at the correct position
            // by translating them back and scaling the item down to be flat
            // Translate by -0.1 = scaleZ * 100 to get it to the render in the plane
            matrices.translate(0, 0, matrices);
            // Don't make this to small, otherwise there will be z-fighting for items like anvils
            final float scaleZ = 0.001f;
            matrices.scale(1, 1, matrices);
            drawContext.drawItem(item, x, y);
            matrices.scale(1, 1, matrices);
        } else {
            drawContext.drawItem(item, x, y);
        }
        if (overlay) {
            if (is3dRender) {
                matrices.translate(0, 0, matrices);
            }
            drawContext.drawStackOverlay(mc.textRenderer, item, x, y, ovText);
        }
        matrices.popMatrix();
    }

    public Item setParent(IDraw2D<?> parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public int getScaledWidth() {
        return (int) (scale * DEFAULT_ITEM_SIZE);
    }

    @Override
    public int getParentWidth() {
        return parent != null ? parent.getWidth() : mc.getWindow().getScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return (int) (scale * DEFAULT_ITEM_SIZE);
    }

    @Override
    public int getParentHeight() {
        return parent != null ? parent.getHeight() : mc.getWindow().getScaledHeight();
    }

    @Override
    public int getScaledLeft() {
        return x;
    }

    @Override
    public int getScaledTop() {
        return y;
    }

    @Override
    public Item moveTo(int x, int y) {
        return setPos(x, y);
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static final class Builder extends RenderElementBuilder<Item> implements Alignable<Builder> {
        private int x = 0;
        private int y = 0;
        private ItemStackHelper itemStack = new ItemStackHelper(ItemStack.EMPTY);
        private String ovText = "";
        private boolean overlay = false;
        private double scale = 1;
        private float rotation = 0;
        private boolean rotateCenter = true;
        private int zIndex = 0;

        public Builder(IDraw2D<?> draw2D) {
            super(draw2D);
        }

        /**
         * @param x the x position of the item
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder x(int x) {
            this.x = x;
            return this;
        }

        /**
         * @return the x position of the item.
         * @since 1.8.4
         */
        public int getX() {
            return x;
        }

        /**
         * @param y the y position of the item
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder y(int y) {
            this.y = y;
            return this;
        }

        /**
         * @return the y position of the item.
         * @since 1.8.4
         */
        public int getY() {
            return y;
        }

        /**
         * @param x the x position of the item
         * @param y the y position of the item
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * @param item the item to draw
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder item(ItemStackHelper item) {
            if (item != null) {
                this.itemStack = item.copy();
            }
            return this;
        }

        /**
         * @param id the id of the item to draw
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("id: CanOmitNamespace<ItemId>")
        public Builder item(String id) {
            this.itemStack = new ItemStackHelper(Registries.ITEM.get(RegistryHelper.parseIdentifier(id))
                    .getDefaultStack());
            return this;
        }

        /**
         * @param id    the id of the item to draw
         * @param count the stack size
         * @return self for chaining.
         * @since 1.8.4
         */
        @DocletReplaceParams("id: ItemId, count: int")
        public Builder item(String id, int count) {
            this.itemStack = new ItemStackHelper(id, count);
            return this;
        }

        /**
         * @return the item to be drawn.
         * @since 1.8.4
         */
        public ItemStackHelper getItem() {
            return itemStack.copy();
        }

        /**
         * This also sets the overlay to be shown.
         *
         * @param overlayText the overlay text
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder overlayText(String overlayText) {
            this.ovText = overlayText;
            this.overlay = true;
            return this;
        }

        /**
         * @return the overlay text.
         * @since 1.8.4
         */
        public String getOverlayText() {
            return ovText;
        }

        /**
         * @param visible whether the overlay should be visible or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder overlayVisible(boolean visible) {
            this.overlay = visible;
            return this;
        }

        /**
         * @return {@code true} if the overlay should be visible, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isOverlayVisible() {
            return overlay;
        }

        /**
         * @param scale the scale of the item
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder scale(double scale) {
            if (scale <= 0) {
                throw new IllegalArgumentException("Scale must be greater than 0");
            }
            this.scale = scale;
            return this;
        }

        /**
         * @return the scale of the item.
         * @since 1.8.4
         */
        public double getScale() {
            return scale;
        }

        /**
         * @param rotation the rotation (clockwise) of the item in degrees
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder rotation(double rotation) {
            this.rotation = (float) rotation;
            return this;
        }

        /**
         * @return the rotation (clockwise) of the item in degrees.
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether the item should be rotated around its center
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder rotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this item should be rotated around its center, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param zIndex the z-index of the item
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder zIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        /**
         * @return the z-index of the item.
         * @since 1.8.4
         */
        public int getZIndex() {
            return zIndex;
        }

        @Override
        protected Item createElement() {
            return new Item(x, y, zIndex, itemStack, overlay, scale, rotation, ovText).setRotateCenter(rotateCenter)
                    .setParent(parent);
        }

        @Override
        public int getScaledWidth() {
            return (int) (DEFAULT_ITEM_SIZE * scale);
        }

        @Override
        public int getParentWidth() {
            return parent.getWidth();
        }

        @Override
        public int getScaledHeight() {
            return (int) (DEFAULT_ITEM_SIZE * scale);
        }

        @Override
        public int getParentHeight() {
            return parent.getHeight();
        }

        @Override
        public int getScaledLeft() {
            return x;
        }

        @Override
        public int getScaledTop() {
            return y;
        }

        @Override
        public Builder moveTo(int x, int y) {
            return pos(x, y);
        }

    }

}
