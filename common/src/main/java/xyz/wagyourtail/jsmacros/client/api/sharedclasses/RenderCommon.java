package xyz.wagyourtail.jsmacros.client.api.sharedclasses;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import com.mojang.blaze3d.systems.RenderSystem;
import xyz.wagyourtail.jsmacros.client.api.classes.CustomImage;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;

import java.util.Locale;
import java.util.function.IntSupplier;

/**
 * @author Wagyourtail
 * @since 1.2.3
 */
@SuppressWarnings("unused")
public class RenderCommon {

    /**
     * @author Wagyourtail
     */
    public interface RenderElement extends Drawable {

        MinecraftClient mc = MinecraftClient.getInstance();

        int getZIndex();

        default void render3D(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            render(matrices, mouseX, mouseY, delta);
        }

        default void setupMatrix(MatrixStack matrices, double x, double y, float scale, float rotation) {
            setupMatrix(matrices, x, y, scale, rotation, 0, 0, false);
        }

        default void setupMatrix(MatrixStack matrices, double x, double y, float scale, float rotation, double width, double height, boolean rotateAroundCenter) {
            matrices.translate(x, y, 0);
            matrices.scale(scale, scale, 1);
            if (rotateAroundCenter) {
                matrices.translate(width / 2, height / 2, 0);
            }
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            if (rotateAroundCenter) {
                matrices.translate(-width / 2, -height / 2, 0);
            }
            matrices.translate(-x, -y, 0);
        }

    }

    /**
     * @param <T> the type of the render element for this builder
     * @author Etheradon
     * @since 1.8.4
     */
    public abstract static class RenderElementBuilder<T extends RenderElement> {

        protected final IDraw2D<?> parent;

        protected RenderElementBuilder(IDraw2D<?> parent) {
            this.parent = parent;
        }

        /**
         * @return the newly created element.
         *
         * @since 1.8.4
         */
        public T build() {
            return createElement();
        }

        /**
         * Builds and adds the element to the draw2D the builder was created from.
         *
         * @return the newly created element.
         *
         * @since 1.8.4
         */
        public T buildAndAdd() {
            T element = createElement();
            parent.reAddElement(element);
            return element;
        }

        protected abstract T createElement();

    }

    /**
     * @param <B> the builder class
     * @since 1.8.4
     */
    public interface Alignable<B extends Alignable<B>> {

        /**
         * @param other     the element to align to
         * @param alignment the alignment to use
         * @return self for chaining.
         *
         * @see #alignHorizontally(Alignable, String, int)
         * @since 1.8.4
         */
        default B alignHorizontally(Alignable<?> other, String alignment) {
            return alignHorizontally(other, alignment, 0);
        }

        /**
         * The alignment must be of the format
         * {@code [left|center|right|x%]On[left|center|right|x%]}. The input is case-insensitive.
         * The first alignment is for the element this method is called on and the second is for the
         * other element. As an example, {@code LeftOnCenter} would align the left side of this
         * element to the center of the other element.
         *
         * @param other     the element to align to
         * @param alignment the alignment to use
         * @param offset    the offset to use
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B alignHorizontally(Alignable<?> other, String alignment, int offset) {
            String[] alignments = alignment.toLowerCase(Locale.ROOT).split("on");
            String thisAlignment = alignments[0];
            String toAlignment = alignments[1];
            int alignToX = switch (toAlignment) {
                case "left" -> other.getScaledLeft();
                case "center" -> other.getScaledLeft() + other.getScaledWidth() / 2;
                case "right" -> other.getScaledRight();
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        yield other.getScaledLeft() + (other.getScaledWidth() * percent / 100);
                    }
                    throw new IllegalArgumentException("Invalid alignment: " + alignment);
                }
            };
            switch (thisAlignment) {
                case "left" -> moveToX(alignToX + offset);
                case "center" -> moveToX(alignToX - getScaledWidth() / 2 + offset);
                case "right" -> moveToX(alignToX - getScaledWidth() + offset);
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        moveToX(alignToX - (getScaledWidth() * percent / 100) + offset);
                    }
                    throw new IllegalArgumentException("Invalid alignment: " + alignment);
                }
            }
            return (B) this;
        }

        /**
         * @param alignment the alignment to use
         * @return self for chaining.
         *
         * @see #alignHorizontally(String, int)
         * @since 1.8.4
         */
        default B alignHorizontally(String alignment) {
            return alignHorizontally(alignment, 0);
        }

        /**
         * Possible alignments are {@code left}, {@code center}, {@code right} or {@code y%} where y
         * is a number between 0 and 100.
         *
         * @param alignment the alignment to use
         * @param offset    the offset to use
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B alignHorizontally(String alignment, int offset) {
            int parentWidth = getParentWidth();
            int width = getScaledWidth();

            switch (alignment.toLowerCase(Locale.ROOT)) {
                case "left" -> moveToX(offset);
                case "center" -> moveToX((parentWidth - width) / 2 + offset);
                case "right" -> moveToX(parentWidth - width + offset);
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        moveToX((parentWidth - width) * percent / 100 + offset);
                    }
                }
            }
            return (B) this;
        }

        /**
         * @param other     the element to align to
         * @param alignment the alignment to use
         * @return self for chaining.
         *
         * @see #alignVertically(Alignable, String, int)
         * @since 1.8.4
         */
        default B alignVertically(Alignable<?> other, String alignment) {
            return alignVertically(other, alignment, 0);
        }

        /**
         * The alignment must be of the format
         * {@code [top|center|bottom|y%]On[top|center|bottom|y%]}. The input is case-insensitive.
         * The first alignment is for the element this method is called on and the second is for the
         * other element. As an example, {@code BottomOnTop} would align the bottom side of this
         * element to the top of the other element. Thus, the element would be placed above the
         * other one.
         *
         * @param other     the element to align to
         * @param alignment the alignment to use
         * @param offset    the offset to use
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B alignVertically(Alignable<?> other, String alignment, int offset) {
            String[] alignments = alignment.toLowerCase(Locale.ROOT).split("on");
            String thisAlignment = alignments[0];
            String toAlignment = alignments[1];
            int alignToY = switch (toAlignment) {
                case "top" -> other.getScaledTop();
                case "center" -> other.getScaledTop() + other.getScaledHeight() / 2;
                case "bottom" -> other.getScaledBottom();
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        yield other.getScaledTop() + (other.getScaledHeight() * percent / 100);
                    }
                    throw new IllegalArgumentException("Invalid alignment: " + alignment);
                }
            };
            switch (thisAlignment) {
                case "top" -> moveToY(alignToY + offset);
                case "center" -> moveToY(alignToY - getScaledHeight() / 2 + offset);
                case "bottom" -> moveToY(alignToY - getScaledHeight() + offset);
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        moveToY(alignToY - (getScaledHeight() * percent / 100) + offset);
                    }
                    throw new IllegalArgumentException("Invalid alignment: " + alignment);
                }
            }
            return (B) this;
        }

        /**
         * @param alignment the alignment to use
         * @return self for chaining.
         *
         * @see #alignVertically(String, int)
         * @since 1.8.4
         */
        default B alignVertically(String alignment) {
            return alignVertically(alignment, 0);
        }

        /**
         * Possible alignments are {@code top}, {@code center}, {@code bottom} or {@code x%} where x
         * is a number between 0 and 100.
         *
         * @param alignment the alignment to use
         * @param offset    the offset to use
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B alignVertically(String alignment, int offset) {
            int parentHeight = getParentHeight();
            int height = getScaledHeight();

            switch (alignment.toLowerCase(Locale.ROOT)) {
                case "top" -> moveToY(offset);
                case "center" -> moveToY((parentHeight - height) / 2 + offset);
                case "bottom" -> moveToY(parentHeight - height + offset);
                default -> {
                    int percent = parsePercentage(alignment);
                    if (percent != -1) {
                        moveToY((parentHeight - height) * percent / 100 + offset);
                    }
                }
            }
            return (B) this;
        }

        /**
         * @param horizontal the horizontal alignment to use
         * @param vertical   the vertical alignment to use
         * @return self for chaining.
         *
         * @see #align(String, int, String, int)
         * @since 1.8.4
         */
        default B align(String horizontal, String vertical) {
            return align(horizontal, 0, vertical, 0);
        }

        /**
         * @param horizontal       the horizontal alignment to use
         * @param horizontalOffset the horizontal offset to use
         * @param vertical         the vertical alignment to use
         * @param verticalOffset   the vertical offset to use
         * @return self for chaining.
         *
         * @see #alignHorizontally(String, int)
         * @see #alignVertically(String, int)
         * @since 1.8.4
         */
        default B align(String horizontal, int horizontalOffset, String vertical, int verticalOffset) {
            return alignHorizontally(horizontal, horizontalOffset).alignVertically(vertical, verticalOffset);
        }

        /**
         * @param other      the element to align to
         * @param horizontal the horizontal alignment to use
         * @param vertical   the vertical alignment to use
         * @return self for chaining.
         *
         * @see #align(Alignable, String, int, String, int)
         * @since 1.8.4
         */
        default B align(Alignable<?> other, String horizontal, String vertical) {
            return align(other, horizontal, 0, vertical, 0);
        }

        /**
         * @param other            the element to align to
         * @param horizontal       the horizontal alignment to use
         * @param horizontalOffset the horizontal offset to use
         * @param vertical         the vertical alignment to use
         * @param verticalOffset   the vertical offset to use
         * @return self for chaining.
         *
         * @see #alignHorizontally(Alignable, String, int)
         * @see #alignVertically(Alignable, String, int)
         * @since 1.8.4
         */
        default B align(Alignable<?> other, String horizontal, int horizontalOffset, String vertical, int verticalOffset) {
            return alignHorizontally(other, horizontal, horizontalOffset).alignVertically(other, vertical, verticalOffset);
        }

        /**
         * @param x the new x position
         * @param y the new y position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        B moveTo(int x, int y);

        /**
         * @param x the new x position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B moveToX(int x) {
            return moveTo(x, getScaledTop());
        }

        /**
         * @param y the new y position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        default B moveToY(int y) {
            return moveTo(getScaledLeft(), y);
        }

        /**
         * @return the scaled width of the element.
         *
         * @since 1.8.4
         */
        int getScaledWidth();

        /**
         * @return the width of the parent element.
         *
         * @since 1.8.4
         */
        int getParentWidth();

        /**
         * @return the scaled height of the element.
         *
         * @since 1.8.4
         */
        int getScaledHeight();

        /**
         * @return the height of the parent element.
         *
         * @since 1.8.4
         */
        int getParentHeight();

        /**
         * @return the position of the scaled element's left side.
         *
         * @since 1.8.4
         */
        int getScaledLeft();

        /**
         * @return the position of the scaled element's top side.
         *
         * @since 1.8.4
         */
        int getScaledTop();

        /**
         * @return the position of the scaled element's right side.
         *
         * @since 1.8.4
         */
        default int getScaledRight() {
            return getScaledLeft() + getScaledWidth();
        }

        /**
         * @return the position of the scaled element's bottom side.
         *
         * @since 1.8.4
         */
        default int getScaledBottom() {
            return getScaledTop() + getScaledHeight();
        }

        /**
         * Parse the string containing a percentage of the form {@code x%} and return its value.
         *
         * @param string the string to parse
         * @return the percentage or {@code -1} if the string is not a valid percentage.
         *
         * @since 1.8.4
         */
        private static int parsePercentage(String string) {
            if (string.endsWith("%")) {
                int percent = Integer.parseInt(string.substring(0, string.length() - 1));
                if (percent >= 0 && percent <= 100) {
                    return percent;
                }
            }
            return -1;
        }

    }

    /**
     * @author Wagyourtail
     * @since 1.0.5
     */
    @SuppressWarnings("unused")
    public static class Item implements RenderElement, Alignable<Item> {

        private static final int DEFAULT_ITEM_SIZE = 16;
        private static final MinecraftClient mc = MinecraftClient.getInstance();

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
         *
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
         *
         * @since 1.0.5 [citation needed]
         */
        public Item setItem(String id, int count) {
            this.item = new ItemStack(Registry.ITEM.get(RegistryHelper.parseIdentifier(id)), count);
            return this;
        }

        /**
         * @return
         *
         * @since 1.0.5 [citation needed]
         */
        public ItemStackHelper getItem() {
            return new ItemStackHelper(item);
        }

        /**
         * @param x the new x position of this element
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Item setX(int x) {
            this.x = x;
            return this;
        }

        /**
         * @return the x position of this element.
         *
         * @since 1.8.4
         */
        public int getX() {
            return x;
        }

        /**
         * @param y the new y position of this element
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Item setY(int y) {
            this.y = y;
            return this;
        }

        /**
         * @return the y position of this element.
         *
         * @since 1.8.4
         */
        public int getY() {
            return y;
        }

        /**
         * @param x
         * @param y
         * @return
         *
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
         *
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
         *
         * @since 1.8.4
         */
        public double getScale() {
            return scale;
        }

        /**
         * @param rotation
         * @return
         *
         * @since 1.2.6
         */
        public Item setRotation(double rotation) {
            this.rotation = MathHelper.wrapDegrees((float) rotation);
            return this;
        }

        /**
         * @return the rotation of this item.
         *
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether the item should be rotated around its center
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Item setRotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this item should be rotated around its center, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param overlay
         * @return
         *
         * @since 1.2.0
         */
        public Item setOverlay(boolean overlay) {
            this.overlay = overlay;
            return this;
        }

        /**
         * @return {@code true}, if the overlay, i.e. the durability bar, and the overlay text or
         *         item count should be shown, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean shouldShowOverlay() {
            return overlay;
        }

        /**
         * @param ovText
         * @return
         *
         * @since 1.2.0
         */
        public Item setOverlayText(String ovText) {
            this.ovText = ovText;
            return this;
        }

        /**
         * @return the overlay text of this item.
         *
         * @since 1.8.4
         */
        public String getOverlayText() {
            return ovText;
        }

        /**
         * @param zIndex the new z-index of this item
         * @return self for chaining.
         *
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
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            setupMatrix(matrices, x, y, (float) scale, rotation, DEFAULT_ITEM_SIZE, DEFAULT_ITEM_SIZE, rotateCenter);
            MatrixStack ms = RenderSystem.getModelViewStack();
            ms.push();
            ms.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                i.renderGuiItemIcon(item, x, y);
                if (overlay) {
                    i.renderGuiItemOverlay(mc.textRenderer, item, x, y, ovText);
                }
            }
            ms.pop();
            RenderSystem.applyModelViewMatrix();
            matrices.pop();
        }

        @Override
        public void render3D(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            //TODO: cull and renderBack still not working, draw this to a FrameBuffer and render that instead.
            matrices.push();
            setupMatrix(matrices, x, y, (float) scale, rotation, DEFAULT_ITEM_SIZE, DEFAULT_ITEM_SIZE, rotateCenter);

            MatrixStack ms = RenderSystem.getModelViewStack();
            ms.push();
            ms.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
            RenderSystem.applyModelViewMatrix();

            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                ms.push();
                //make the item really flat, but not too flat to avoid z-fighting
                ms.scale(1, 1, 0.005f);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.disableDepthTest();
                i.renderGuiItemIcon(item, x, y);
                ms.pop();
                RenderSystem.applyModelViewMatrix();
                i.zOffset = -199.9f;
                if (overlay) {
                    i.renderGuiItemOverlay(mc.textRenderer, item, x, y, ovText);
                }
                i.zOffset = 0;
            }
            ms.pop();
            RenderSystem.applyModelViewMatrix();
            matrices.pop();
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
             *
             * @since 1.8.4
             */
            public Builder x(int x) {
                this.x = x;
                return this;
            }

            /**
             * @return the x position of the item.
             *
             * @since 1.8.4
             */
            public int getX() {
                return x;
            }

            /**
             * @param y the y position of the item
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y(int y) {
                this.y = y;
                return this;
            }

            /**
             * @return the y position of the item.
             *
             * @since 1.8.4
             */
            public int getY() {
                return y;
            }

            /**
             * @param x the x position of the item
             * @param y the y position of the item
             * @return self for chaining.
             *
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
             *
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
             *
             * @since 1.8.4
             */
            public Builder item(String id) {
                this.itemStack = new ItemStackHelper(Registry.ITEM.get(RegistryHelper.parseIdentifier(id)).getDefaultStack());
                return this;
            }

            /**
             * @param id    the id of the item to draw
             * @param count the stack size
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder item(String id, int count) {
                this.itemStack = new ItemStackHelper(id, count);
                return this;
            }

            /**
             * @return the item to be drawn.
             *
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
             *
             * @since 1.8.4
             */
            public Builder overlayText(String overlayText) {
                this.ovText = overlayText;
                this.overlay = true;
                return this;
            }

            /**
             * @return the overlay text.
             *
             * @since 1.8.4
             */
            public String getOverlayText() {
                return ovText;
            }

            /**
             * @param visible whether the overlay should be visible or not
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder overlayVisible(boolean visible) {
                this.overlay = visible;
                return this;
            }

            /**
             * @return {@code true} if the overlay should be visible, {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean isOverlayVisible() {
                return overlay;
            }

            /**
             * @param scale the scale of the item
             * @return self for chaining.
             *
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
             *
             * @since 1.8.4
             */
            public double getScale() {
                return scale;
            }

            /**
             * @param rotation the rotation (clockwise) of the item in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(double rotation) {
                this.rotation = (float) rotation;
                return this;
            }

            /**
             * @return the rotation (clockwise) of the item in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotateCenter whether the item should be rotated around its center
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotateCenter(boolean rotateCenter) {
                this.rotateCenter = rotateCenter;
                return this;
            }

            /**
             * @return {@code true} if this item should be rotated around its center, {@code false}
             *         otherwise.
             *
             * @since 1.8.4
             */
            public boolean isRotatingCenter() {
                return rotateCenter;
            }

            /**
             * @param zIndex the z-index of the item
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            /**
             * @return the z-index of the item.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            @Override
            protected Item createElement() {
                return new Item(x, y, zIndex, itemStack, overlay, scale, rotation, ovText).setRotateCenter(rotateCenter).setParent(parent);
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

    /**
     * @author Wagyourtail
     * @since 1.2.3
     */
    @SuppressWarnings("unused")
    public static class Image implements RenderElement, Alignable<Image> {

        private static MinecraftClient mc = MinecraftClient.getInstance();

        private Identifier imageid;
        public IDraw2D<?> parent;
        public float rotation;
        public boolean rotateCenter;
        public int x;
        public int y;
        public int width;
        public int height;
        public int imageX;
        public int imageY;
        public int regionWidth;
        public int regionHeight;
        public int textureWidth;
        public int textureHeight;
        public int color;
        public int zIndex;

        public Image(int x, int y, int width, int height, int zIndex, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float rotation) {
            this(x, y, width, height, zIndex, 0xFF, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
            setColor(color);
        }

        public Image(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float rotation) {
            setPos(x, y, width, height);
            setColor(color, alpha);
            setImage(id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight);
            this.rotation = rotation;
        }

        /**
         * @param id
         * @param imageX
         * @param imageY
         * @param regionWidth
         * @param regionHeight
         * @param textureWidth
         * @param textureHeight
         * @return self for chaining.
         *
         * @since 1.2.3
         */
        public Image setImage(String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
            imageid = RegistryHelper.parseIdentifier(id);
            this.imageX = imageX;
            this.imageY = imageY;
            this.regionWidth = regionWidth;
            this.regionHeight = regionHeight;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        /**
         * @return
         *
         * @since 1.2.3
         */
        public String getImage() {
            return imageid.toString();
        }

        /**
         * @param x the new x position of this image
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Image setX(int x) {
            this.x = x;
            return this;
        }

        /**
         * @return the x position of this image.
         *
         * @since 1.8.4
         */
        public int getX() {
            return x;
        }

        /**
         * @param y the new y position of this image
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Image setY(int y) {
            this.y = y;
            return this;
        }

        /**
         * @return the y position of this image.
         *
         * @since 1.8.4
         */
        public int getY() {
            return y;
        }

        /**
         * @param x the new x position of this image
         * @param y the new y position of this image
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Image setPos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * @param x
         * @param y
         * @param width
         * @param height
         * @since 1.2.3
         */
        public Image setPos(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * @param width the new width of this image
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Image setWidth(int width) {
            this.width = width;
            return this;
        }

        /**
         * @return the width of this image.
         *
         * @since 1.8.4
         */
        public int getWidth() {
            return width;
        }

        /**
         * @param height the new height of this image
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Image setHeight(int height) {
            this.height = height;
            return this;
        }

        /**
         * @return the height of this image.
         *
         * @since 1.8.4
         */
        public int getHeight() {
            return height;
        }

        /**
         * @param width  the new width of this image
         * @param height the new height of this image
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Image setSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * @param color
         * @return
         *
         * @since 1.6.5
         */
        public Image setColor(int color) {
            if (color <= 0xFFFFFF) {
                color = color | 0xFF000000;
            }
            this.color = color;
            return this;
        }

        /**
         * @param color
         * @param alpha
         * @return
         *
         * @since 1.6.5
         */
        public Image setColor(int color, int alpha) {
            this.color = (alpha << 24) | (color & 0xFFFFFF);
            return this;
        }

        /**
         * @return the color of this image.
         *
         * @since 1.8.4
         */
        public int getColor() {
            return color;
        }

        /**
         * @return the alpha value of this image.
         *
         * @since 1.8.4
         */
        public int getAlpha() {
            return (color >> 24) & 0xFF;
        }

        /**
         * @param rotation
         * @return
         *
         * @since 1.2.6
         */
        public Image setRotation(double rotation) {
            this.rotation = MathHelper.wrapDegrees((float) rotation);
            return this;
        }

        /**
         * @return the rotation of this image.
         *
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether the image should be rotated around its center
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Image setRotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this image should be rotated around its center, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param zIndex the new z-index of this image
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Image setZIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        @Override
        public int getZIndex() {
            return zIndex;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            setupMatrix(matrices, x, y, 1, rotation, getWidth(), getHeight(), rotateCenter);
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, imageid);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();

            buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_TEXTURE_COLOR);
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            float x1 = x;
            float y1 = y;
            float x2 = x + width;
            float y2 = y + height;

            float u1 = imageX / (float) textureWidth;
            float v1 = imageY / (float) textureHeight;
            float u2 = (imageX + regionWidth) / (float) textureWidth;
            float v2 = (imageY + regionHeight) / (float) textureHeight;

            //draw a rectangle using triangle strips
            buf.vertex(matrix, x1, y2, 0).texture(u1, v2).color(color).next(); // Top-left
            buf.vertex(matrix, x2, y2, 0).texture(u2, v2).color(color).next(); // Top-right
            buf.vertex(matrix, x1, y1, 0).texture(u1, v1).color(color).next(); // Bottom-left
            buf.vertex(matrix, x2, y1, 0).texture(u2, v1).color(color).next(); // Bottom-right
            tess.draw();

            matrices.pop();
            RenderSystem.disableBlend();
        }

        public Image setParent(IDraw2D<?> parent) {
            this.parent = parent;
            return this;
        }

        @Override
        public int getScaledWidth() {
            return width;
        }

        @Override
        public int getParentWidth() {
            return parent != null ? parent.getWidth() : mc.getWindow().getScaledWidth();
        }

        @Override
        public int getScaledHeight() {
            return height;
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
        public Image moveTo(int x, int y) {
            return setPos(x, y, width, height);
        }

        /**
         * @author Etheradon
         * @since 1.8.4
         */
        public static final class Builder extends RenderElementBuilder<Image> implements Alignable<Builder> {
            private String identifier;
            private int x = 0;
            private int y = 0;
            private int width = 0;
            private int height = 0;
            private int imageX = 0;
            private int imageY = 0;
            private int regionWidth = 0;
            private int regionHeight = 0;
            private int textureWidth = 256;
            private int textureHeight = 256;
            private int color = 0xFFFFFF;
            private int alpha = 0xFF;
            private float rotation = 0;
            private boolean rotateCenter = true;
            private int zIndex = 0;

            public Builder(IDraw2D<?> draw2D) {
                super(draw2D);
            }

            /**
             * Will automatically set all attributes to the default values of the custom image.
             * Values set before the call of this method will be overwritten.
             *
             * @param customImage the custom image to use
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder fromCustomImage(CustomImage customImage) {
                this.width = customImage.getWidth();
                this.height = customImage.getHeight();
                this.imageX = 0;
                this.imageY = 0;
                this.regionWidth = customImage.getWidth();
                this.regionHeight = customImage.getHeight();
                this.textureWidth = customImage.getWidth();
                this.textureHeight = customImage.getHeight();
                this.identifier = customImage.getIdentifier();
                return this;
            }

            /**
             * @param identifier the identifier of the image to use
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder identifier(String identifier) {
                this.identifier = identifier;
                return this;
            }

            /**
             * @return the identifier of the used image or {@code null} if no image is used.
             *
             * @since 1.8.4
             */
            public String getIdentifier() {
                return identifier;
            }

            /**
             * @param x the x position of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x(int x) {
                this.x = x;
                return this;
            }

            /**
             * @return the x position of the image.
             *
             * @since 1.8.4
             */
            public int getX() {
                return x;
            }

            /**
             * @param y the y position of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y(int y) {
                this.y = y;
                return this;
            }

            /**
             * @return the y position of the image.
             *
             * @since 1.8.4
             */
            public int getY() {
                return y;
            }

            /**
             * @param x the x position of the image
             * @param y the y position of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos(int x, int y) {
                this.x = x;
                this.y = y;
                return this;
            }

            /**
             * @param width the width of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder width(int width) {
                this.width = width;
                return this;
            }

            /**
             * @return the width of the image.
             *
             * @since 1.8.4
             */
            public int getWidth() {
                return width;
            }

            /**
             * @param height the height of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder height(int height) {
                this.height = height;
                return this;
            }

            /**
             * @return the height of the image.
             *
             * @since 1.8.4
             */
            public int getHeight() {
                return height;
            }

            /**
             * @param width  the width of the image
             * @param height the height of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder size(int width, int height) {
                this.width = width;
                this.height = height;
                return this;
            }

            /**
             * @param imageX the x position in the image texture to start drawing from
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder imageX(int imageX) {
                this.imageX = imageX;
                return this;
            }

            /**
             * @return the x position in the image texture to start drawing from.
             *
             * @since 1.8.4
             */
            public int getImageX() {
                return imageX;
            }

            /**
             * @param imageY the y position in the image texture to start drawing from
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder imageY(int imageY) {
                this.imageY = imageY;
                return this;
            }

            /**
             * @return the y position in the image texture to start drawing from.
             *
             * @since 1.8.4
             */
            public int getImageY() {
                return imageY;
            }

            /**
             * @param imageX the x position in the image texture to start drawing from
             * @param imageY the y position in the image texture to start drawing from
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder imagePos(int imageX, int imageY) {
                this.imageX = imageX;
                this.imageY = imageY;
                return this;
            }

            /**
             * @param regionWidth the width of the region to draw
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder regionWidth(int regionWidth) {
                this.regionWidth = regionWidth;
                return this;
            }

            /**
             * @return the width of the region to draw.
             *
             * @since 1.8.4
             */
            public int getRegionWidth() {
                return regionWidth;
            }

            /**
             * @param regionHeight the height of the region to draw
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder regionHeight(int regionHeight) {
                this.regionHeight = regionHeight;
                return this;
            }

            /**
             * @return the height of the region to draw.
             *
             * @since 1.8.4
             */
            public int getRegionHeight() {
                return regionHeight;
            }

            /**
             * @param regionWidth  the width of the region to draw
             * @param regionHeight the height of the region to draw
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder regionSize(int regionWidth, int regionHeight) {
                this.regionWidth = regionWidth;
                this.regionHeight = regionHeight;
                return this;
            }

            /**
             * @param x      the x position in the image texture to start drawing from
             * @param y      the y position in the image texture to start drawing from
             * @param width  the width of the region to draw
             * @param height the height of the region to draw
             * @return
             *
             * @since 1.8.4
             */
            public Builder regions(int x, int y, int width, int height) {
                this.imageX = x;
                this.imageY = y;
                this.regionWidth = width;
                this.regionHeight = height;
                return this;
            }

            /**
             * @param x             the x position in the image texture to start drawing from
             * @param y             the y position in the image texture to start drawing from
             * @param width         the width of the region to draw
             * @param height        the height of the region to draw
             * @param textureWidth  the width of the used texture
             * @param textureHeight the height of the used texture
             * @return
             *
             * @since 1.8.4
             */
            public Builder regions(int x, int y, int width, int height, int textureWidth, int textureHeight) {
                this.imageX = x;
                this.imageY = y;
                this.regionWidth = width;
                this.regionHeight = height;
                this.textureWidth = textureWidth;
                this.textureHeight = textureHeight;
                return this;
            }

            /**
             * @param textureWidth the width of the used texture
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder textureWidth(int textureWidth) {
                this.textureWidth = textureWidth;
                return this;
            }

            /**
             * @return the width of the used texture.
             *
             * @since 1.8.4
             */
            public int getTextureWidth() {
                return textureWidth;
            }

            /**
             * @param textureHeight the height of the used texture
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder textureHeight(int textureHeight) {
                this.textureHeight = textureHeight;
                return this;
            }

            /**
             * @return the height of the used texture.
             *
             * @since 1.8.4
             */
            public int getTextureHeight() {
                return textureHeight;
            }

            /**
             * @param textureWidth  the width of the used texture
             * @param textureHeight the height of the used texture
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder textureSize(int textureWidth, int textureHeight) {
                this.textureWidth = textureWidth;
                this.textureHeight = textureHeight;
                return this;
            }

            /**
             * @param color the color of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color) {
                this.color = color;
                return this;
            }

            /**
             * @param r the red component of the color
             * @param g the green component of the color
             * @param b the blue component of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int r, int g, int b) {
                this.color = (r << 16) | (g << 8) | b;
                return this;
            }

            /**
             * @param r the red component of the color
             * @param g the green component of the color
             * @param b the blue component of the color
             * @param a the alpha component of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int r, int g, int b, int a) {
                this.color = (r << 16) | (g << 8) | b;
                this.alpha = a;
                return this;
            }

            /**
             * @param color the color of the image
             * @param alpha the alpha value of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color, int alpha) {
                this.color = color;
                this.alpha = alpha;
                return this;
            }

            /**
             * @return the color of the image.
             *
             * @since 1.8.4
             */
            public int getColor() {
                return color;
            }

            /**
             * @param alpha the alpha value of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder alpha(int alpha) {
                this.alpha = alpha;
                return this;
            }

            /**
             * @return the alpha value of the color.
             *
             * @since 1.8.4
             */
            public int getAlpha() {
                return alpha;
            }

            /**
             * @param rotation the rotation (clockwise) of the image in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(double rotation) {
                this.rotation = (float) rotation;
                return this;
            }

            /**
             * @return the rotation (clockwise) of the image in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotateCenter whether the image should be rotated around its center
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotateCenter(boolean rotateCenter) {
                this.rotateCenter = rotateCenter;
                return this;
            }

            /**
             * @return {@code true} if this image should be rotated around its center, {@code false}
             *         otherwise.
             *
             * @since 1.8.4
             */
            public boolean isRotatingCenter() {
                return rotateCenter;
            }

            /**
             * @param zIndex the z-index of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            /**
             * @return the z-index of the image.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            @Override
            public Image createElement() {
                return new Image(x, y, width, height, zIndex, alpha, color, identifier, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation).setRotateCenter(rotateCenter).setParent(parent);
            }

            @Override
            public int getScaledWidth() {
                return width;
            }

            @Override
            public int getParentWidth() {
                return parent.getWidth();
            }

            @Override
            public int getScaledHeight() {
                return height;
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

    /**
     * @author Wagyourtail
     * @since 1.0.5
     */
    @SuppressWarnings("unused")
    public static class Rect implements RenderElement, Alignable<Rect> {

        public IDraw2D<?> parent;
        public float rotation;
        public boolean rotateCenter;
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int color;
        public int zIndex;

        public Rect(int x1, int y1, int x2, int y2, int color, float rotation, int zIndex) {
            this(x1, y1, x2, y2, color, 0xFF, rotation, zIndex);
            setColor(color);
        }

        public Rect(int x1, int y1, int x2, int y2, int color, int alpha, float rotation, int zIndex) {
            setPos(x1, y1, x2, y2);
            setColor(color, alpha);
            this.rotation = MathHelper.wrapDegrees(rotation);
            this.zIndex = zIndex;
        }

        /**
         * @param x1 the first x position of this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setX1(int x1) {
            this.x1 = x1;
            return this;
        }

        /**
         * @return the first x position of this rectangle.
         *
         * @since 1.8.4
         */
        public int getX1() {
            return x1;
        }

        /**
         * @param y1 the first y position of this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setY1(int y1) {
            this.y1 = y1;
            return this;
        }

        /**
         * @return the first y position of this rectangle.
         *
         * @since 1.8.4
         */
        public int getY1() {
            return y1;
        }

        /**
         * @param x1 the first x position of this rectangle
         * @param y1 the first y position of this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setPos1(int x1, int y1) {
            this.x1 = x1;
            this.y1 = y1;
            return this;
        }

        /**
         * @param x2 the second x position of this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setX2(int x2) {
            this.x2 = x2;
            return this;
        }

        /**
         * @return the second x position of this rectangle.
         *
         * @since 1.8.4
         */
        public int getX2() {
            return x2;
        }

        /**
         * @param y2 the second y position of this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setY2(int y2) {
            this.y2 = y2;
            return this;
        }

        /**
         * @return the second y position of the rectangle.
         *
         * @since 1.8.4
         */
        public int getY2() {
            return y2;
        }

        /**
         * @param x2 the second x position of this rectangle
         * @param y2 the second y position of this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setPos2(int x2, int y2) {
            this.x2 = x2;
            this.y2 = y2;
            return this;
        }

        /**
         * @param x1
         * @param y1
         * @param x2
         * @param y2
         * @return
         *
         * @since 1.1.8
         */
        public Rect setPos(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            return this;
        }

        /**
         * @param width the new width of this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setWidth(int width) {
            if (x1 <= x2) {
                x2 = x1 + width;
            } else {
                x1 = x2 + width;
            }
            return this;
        }

        /**
         * @return the width of this rectangle.
         *
         * @since 1.8.4
         */
        public int getWidth() {
            return Math.abs(x2 - x1);
        }

        /**
         * @param height the new height of this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setHeight(int height) {
            if (y1 <= y2) {
                y2 = y1 + height;
            } else {
                y1 = y2 + height;
            }
            return this;
        }

        /**
         * @return the height of this rectangle.
         *
         * @since 1.8.4
         */
        public int getHeight() {
            return Math.abs(y2 - y1);
        }

        /**
         * @param width  the new width of this rectangle
         * @param height the new height of this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setSize(int width, int height) {
            setWidth(width);
            setHeight(height);
            return this;
        }

        /**
         * @param color
         * @return
         *
         * @since 1.0.5
         */
        public Rect setColor(int color) {
            if (color <= 0xFFFFFF) {
                color = color | 0xFF000000;
            }
            this.color = color;
            return this;
        }

        /**
         * @param color
         * @param alpha
         * @return
         *
         * @since 1.1.8
         */
        public Rect setColor(int color, int alpha) {
            this.color = (alpha << 24) | (color & 0xFFFFFF);
            return this;
        }

        /**
         * @param alpha
         * @return
         *
         * @since 1.1.8
         */
        public Rect setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
            return this;
        }

        /**
         * @return the color value of this rectangle.
         *
         * @since 1.8.4
         */
        public int getColor() {
            return color;
        }

        /**
         * @return the alpha value of this rectangle.
         *
         * @since 1.8.4
         */
        public int getAlpha() {
            return (color >> 24) & 0xFF;
        }

        /**
         * @param rotation
         * @return
         *
         * @since 1.2.6
         */
        public Rect setRotation(double rotation) {
            this.rotation = MathHelper.wrapDegrees((float) rotation);
            return this;
        }

        /**
         * @return the rotation of this rectangle.
         *
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether this rectangle should be rotated around its center
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setRotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this rectangle should be rotated around its center, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param zIndex the new z-index for this rectangle
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Rect setZIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        @Override
        public int getZIndex() {
            return zIndex;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            setupMatrix(matrices, x1, y1, 1, rotation, getWidth(), getHeight(), rotateCenter);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();

            float fa = ((color >> 24) & 0xFF) / 255F;
            float fr = ((color >> 16) & 0xFF) / 255F;
            float fg = ((color >> 8) & 0xFF) / 255F;
            float fb = (color & 0xFF) / 255F;

            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            //draw a rectangle using triangle strips
            buf.vertex(matrix, x1, y2, 0).color(fr, fg, fb, fa).next(); // Top-left
            buf.vertex(matrix, x2, y2, 0).color(fr, fg, fb, fa).next(); // Top-right
            buf.vertex(matrix, x1, y1, 0).color(fr, fg, fb, fa).next(); // Bottom-left
            buf.vertex(matrix, x2, y1, 0).color(fr, fg, fb, fa).next(); // Bottom-right
            tess.draw();

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

            matrices.pop();
        }

        public Rect setParent(IDraw2D<?> parent) {
            this.parent = parent;
            return this;
        }

        @Override
        public int getScaledWidth() {
            return getWidth();
        }

        @Override
        public int getParentWidth() {
            return parent != null ? parent.getWidth() : mc.getWindow().getScaledWidth();
        }

        @Override
        public int getScaledHeight() {
            return getHeight();
        }

        @Override
        public int getParentHeight() {
            return parent != null ? parent.getHeight() : mc.getWindow().getScaledHeight();
        }

        @Override
        public int getScaledLeft() {
            return Math.min(x1, x2);
        }

        @Override
        public int getScaledTop() {
            return Math.min(y1, y2);
        }

        @Override
        public Rect moveTo(int x, int y) {
            return setPos(x, y, x + getScaledWidth(), y + getScaledHeight());
        }

        /**
         * @author Etheradon
         * @since 1.8.4
         */
        public static final class Builder extends RenderElementBuilder<Rect> implements Alignable<Builder> {
            private int x1 = 0;
            private int y1 = 0;
            private int x2 = 0;
            private int y2 = 0;
            private int color = 0xFFFFFF;
            private int alpha = 0xFF;
            private float rotation = 0;
            private boolean rotateCenter = true;
            private int zIndex = 0;

            public Builder(IDraw2D<?> draw2D) {
                super(draw2D);
            }

            /**
             * @param x1 the first x position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x1(int x1) {
                this.x1 = x1;
                return this;
            }

            /**
             * @return the first x position of the rectangle.
             *
             * @since 1.8.4
             */
            public int getX1() {
                return x1;
            }

            /**
             * @param y1 the first y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y1(int y1) {
                this.y1 = y1;
                return this;
            }

            /**
             * @return the first y position of the rectangle.
             *
             * @since 1.8.4
             */
            public int getY1() {
                return y1;
            }

            /**
             * @param x1 the first x position of the rectangle
             * @param y1 the first y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos1(int x1, int y1) {
                this.x1 = x1;
                this.y1 = y1;
                return this;
            }

            /**
             * @param x2 the second x position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x2(int x2) {
                this.x2 = x2;
                return this;
            }

            /**
             * @return the second x position of the rectangle.
             *
             * @since 1.8.4
             */
            public int getX2() {
                return x2;
            }

            /**
             * @param y2 the second y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y2(int y2) {
                this.y2 = y2;
                return this;
            }

            /**
             * @return the second y position of the rectangle.
             *
             * @since 1.8.4
             */
            public int getY2() {
                return y2;
            }

            /**
             * @param x2 the second x position of the rectangle
             * @param y2 the second y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos2(int x2, int y2) {
                this.x2 = x2;
                this.y2 = y2;
                return this;
            }

            /**
             * @param x1 the first x position of the rectangle
             * @param y1 the first y position of the rectangle
             * @param x2 the second x position of the rectangle
             * @param y2 the second y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos(int x1, int y1, int x2, int y2) {
                this.x1 = x1;
                this.y1 = y1;
                this.x2 = x2;
                this.y2 = y2;
                return this;
            }

            /**
             * The width will just set the x2 position to {@code x1 + width}.
             *
             * @param width the width of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder width(int width) {
                this.x2 = this.x1 + width;
                return this;
            }

            /**
             * @return the width of the rectangle.
             *
             * @since 1.8.4
             */
            public int getWidth() {
                return Math.abs(this.x2 - this.x1);
            }

            /**
             * The width will just set the y2 position to {@code y1 + height}.
             *
             * @param height the height of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder height(int height) {
                this.y2 = this.y1 + height;
                return this;
            }

            /**
             * @return the height of the rectangle.
             *
             * @since 1.8.4
             */
            public int getHeight() {
                return Math.abs(this.y2 - this.y1);
            }

            /**
             * @param width  the width of the rectangle
             * @param height the height of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder size(int width, int height) {
                return width(width).height(height);
            }

            /**
             * @param color the color of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color) {
                this.color = color;
                return this;
            }

            /**
             * @param r the red component of the color
             * @param g the green component of the color
             * @param b the blue component of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int r, int g, int b) {
                this.color = (r << 16) | (g << 8) | b;
                return this;
            }

            /**
             * @param r the red component of the color
             * @param g the green component of the color
             * @param b the blue component of the color
             * @param a the alpha value of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int r, int g, int b, int a) {
                this.color = (r << 16) | (g << 8) | b;
                this.alpha = a;
                return this;
            }

            /**
             * @param color the color of the rectangle
             * @param alpha the alpha value of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color, int alpha) {
                this.color = color;
                this.alpha = alpha;
                return this;
            }

            /**
             * @return the color of the rectangle.
             *
             * @since 1.8.4
             */
            public int getColor() {
                return color;
            }

            /**
             * @param alpha the alpha value of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder alpha(int alpha) {
                this.alpha = alpha;
                return this;
            }

            /**
             * @return the alpha value of the color.
             *
             * @since 1.8.4
             */
            public int getAlpha() {
                return alpha;
            }

            /**
             * @param rotation the rotation (clockwise) of the rectangle in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(double rotation) {
                this.rotation = (float) rotation;
                return this;
            }

            /**
             * @return the rotation (clockwise) of the rectangle in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotateCenter whether this rectangle should be rotated around its center
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotateCenter(boolean rotateCenter) {
                this.rotateCenter = rotateCenter;
                return this;
            }

            /**
             * @return {@code true} if this rectangle should be rotated around its center,
             *         {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean isRotatingCenter() {
                return rotateCenter;
            }

            /**
             * @param zIndex the z-index of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            /**
             * @return the z-index of the rectangle.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            @Override
            public Rect createElement() {
                return new Rect(x1, y1, x2, y2, color, alpha, rotation, zIndex).setRotateCenter(rotateCenter).setParent(parent);
            }

            @Override
            public int getScaledWidth() {
                return getWidth();
            }

            @Override
            public int getParentWidth() {
                return parent.getWidth();
            }

            @Override
            public int getScaledHeight() {
                return getHeight();
            }

            @Override
            public int getParentHeight() {
                return parent.getHeight();
            }

            @Override
            public int getScaledLeft() {
                return Math.min(x1, x2);
            }

            @Override
            public int getScaledTop() {
                return Math.min(y1, y2);
            }

            @Override
            public Builder moveTo(int x, int y) {
                return pos(x, y, x + getWidth(), y + getHeight());
            }
        }
    }

    /**
     * @author Wagyourtail
     * @since 1.0.5
     */
    @SuppressWarnings("unused")
    public static class Text implements RenderElement, Alignable<Text> {

        public IDraw2D<?> parent;

        public net.minecraft.text.Text text;
        public double scale;
        public float rotation;
        public boolean rotateCenter;
        public int x;
        public int y;
        public int color;
        public int width;
        public boolean shadow;
        public int zIndex;

        public Text(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, float rotation) {
            this(new TextHelper(net.minecraft.text.Text.literal(text)), x, y, color, zIndex, shadow, scale, rotation);
        }

        public Text(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, float rotation) {
            this.text = text.getRaw();
            this.x = x;
            this.y = y;
            this.color = color;
            this.width = mc.textRenderer.getWidth(this.text);
            this.shadow = shadow;
            this.scale = scale;
            this.rotation = MathHelper.wrapDegrees(rotation);
            this.zIndex = zIndex;
        }

        /**
         * @param x the new x position for this text element
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Text setX(int x) {
            this.x = x;
            return this;
        }

        /**
         * @return the x position of this element.
         *
         * @since 1.8.4
         */
        public int getX() {
            return x;
        }

        /**
         * @param y the new y position for this text element
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Text setY(int y) {
            this.y = y;
            return this;
        }

        /**
         * @return the y position of this element.
         *
         * @since 1.8.4
         */
        public int getY() {
            return y;
        }

        /**
         * @param x
         * @param y
         * @return
         *
         * @since 1.0.5
         */
        public Text setPos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * @param text
         * @return
         *
         * @since 1.0.5
         */
        public Text setText(String text) {
            this.text = net.minecraft.text.Text.literal(text);
            this.width = mc.textRenderer.getWidth(text);
            return this;
        }

        /**
         * @param text
         * @return
         *
         * @since 1.2.7
         */
        public Text setText(TextHelper text) {
            this.text = text.getRaw();
            this.width = mc.textRenderer.getWidth(this.text);
            return this;
        }

        /**
         * @return
         *
         * @since 1.2.7
         */
        public TextHelper getText() {
            return new TextHelper(text);
        }

        /**
         * @return
         *
         * @since 1.0.5
         */
        public int getWidth() {
            return this.width;
        }

        /**
         * @return the height of this text.
         *
         * @since 1.8.4
         */
        public int getHeight() {
            return mc.textRenderer.fontHeight;
        }

        /**
         * @param shadow whether the text should be rendered with a shadow
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Text setShadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        /**
         * @return {@code true} if this text element is rendered with a shadow, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean hasShadow() {
            return shadow;
        }

        /**
         * @param scale
         * @return
         *
         * @throws IllegalArgumentException
         * @since 1.0.5
         */
        public Text setScale(double scale) throws IllegalArgumentException {
            if (scale == 0) {
                throw new IllegalArgumentException("Scale can't be 0");
            }
            this.scale = scale;
            return this;
        }

        /**
         * @return the scale of this text.
         *
         * @since 1.8.4
         */
        public double getScale() {
            return scale;
        }

        /**
         * @param rotation
         * @return
         *
         * @since 1.0.5
         */
        public Text setRotation(double rotation) {
            this.rotation = MathHelper.wrapDegrees((float) rotation);
            return this;
        }

        /**
         * @return the rotation of this text.
         *
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether this text should be rotated around its center
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Text setRotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this text should be rotated around its center, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param color the new color for this text element
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Text setColor(int color) {
            this.color = color;
            return this;
        }

        /**
         * @return the color of this text.
         *
         * @since 1.8.4
         */
        public int getColor() {
            return this.color;
        }

        /**
         * @param zIndex the new z-index for this text element
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Text setZIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        @Override
        public int getZIndex() {
            return zIndex;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            setupMatrix(matrices, x, y, (float) scale, rotation, getWidth(), getHeight(), rotateCenter);
            if (shadow) {
                mc.textRenderer.drawWithShadow(matrices, text, x, y, color);
            } else {
                mc.textRenderer.draw(matrices, text, x, y, color);
            }
            matrices.pop();
        }

        @Override
        public void render3D(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            setupMatrix(matrices, x, y, (float) scale, rotation, getWidth(), getHeight(), rotateCenter);
            Tessellator tess = Tessellator.getInstance();
            VertexConsumerProvider.Immediate buffer = VertexConsumerProvider.immediate(tess.getBuffer());
            mc.textRenderer.draw(text, (float) x, (float) y, color, shadow, matrices.peek().getPositionMatrix(), buffer, true, 0, 0xF000F0);
            buffer.draw();
            matrices.pop();
        }

        public Text setParent(IDraw2D<?> parent) {
            this.parent = parent;
            return this;
        }

        @Override
        public int getScaledWidth() {
            return (int) (scale * getWidth());
        }

        @Override
        public int getParentWidth() {
            return parent != null ? parent.getWidth() : mc.getWindow().getScaledWidth();
        }

        @Override
        public int getScaledHeight() {
            return (int) (scale * mc.textRenderer.fontHeight);
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
        public Text moveTo(int x, int y) {
            return setPos(x, y);
        }

        /**
         * @author Etheradon
         * @since 1.8.4
         */
        public static class Builder extends RenderElementBuilder<Text> implements Alignable<Builder> {
            private int x = 0;
            private int y = 0;
            private net.minecraft.text.Text text = net.minecraft.text.Text.empty();
            private int color = 0xFFFFFFFF;
            private double scale = 1;
            private float rotation = 0;
            private boolean rotateCenter = true;
            private boolean shadow = false;
            private int zIndex = 0;

            public Builder(IDraw2D<?> draw2D) {
                super(draw2D);
            }

            /**
             * @param text the content of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder text(TextHelper text) {
                if (text != null) {
                    this.text = text.getRaw();
                }
                return this;
            }

            /**
             * @param text the content of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder text(TextBuilder text) {
                if (text != null) {
                    this.text = text.build().getRaw();
                }
                return this;
            }

            /**
             * @param text the content of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder text(String text) {
                if (text != null) {
                    this.text = net.minecraft.text.Text.literal(text);
                }
                return this;
            }

            /**
             * @return the content of the text element.
             *
             * @since 1.8.4
             */
            public TextHelper getText() {
                return new TextHelper(text.copy());
            }

            /**
             * @param x the x position of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x(int x) {
                this.x = x;
                return this;
            }

            /**
             * @return the x position of the text element.
             *
             * @since 1.8.4
             */
            public int getX() {
                return x;
            }

            /**
             * @param y the y position of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y(int y) {
                this.y = y;
                return this;
            }

            /**
             * @return the y position of the text element.
             *
             * @since 1.8.4
             */
            public int getY() {
                return y;
            }

            /**
             * @param x the x position of the text element
             * @param y the y position of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos(int x, int y) {
                this.x = x;
                this.y = y;
                return this;
            }

            /**
             * @return the width of the string.
             *
             * @since 1.8.4
             */
            public int getWidth() {
                return mc.textRenderer.getWidth(text);
            }

            /**
             * @return the height of the string.
             *
             * @since 1.8.4
             */
            public int getHeight() {
                return mc.textRenderer.fontHeight;
            }

            /**
             * @param color the color of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color) {
                this.color = color;
                return this;
            }

            /**
             * @param r the red component of the color
             * @param g the green component of the color
             * @param b the blue component of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int r, int g, int b) {
                return color(r, g, b, 255);
            }

            /**
             * @param r the red component of the color
             * @param g the green component of the color
             * @param b the blue component of the color
             * @param a the alpha component of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int r, int g, int b, int a) {
                this.color = (a << 24) | (r << 16) | (g << 8) | b;
                return this;
            }

            /**
             * @return the color of the text element.
             *
             * @since 1.8.4
             */
            public int getColor() {
                return color;
            }

            /**
             * @param scale the scale of the text element
             * @return self for chaining.
             *
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
             * @return the scale of the text element.
             *
             * @since 1.8.4
             */
            public double getScale() {
                return scale;
            }

            /**
             * @param rotation the rotation (clockwise) of the text element in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(double rotation) {
                this.rotation = (float) rotation;
                return this;
            }

            /**
             * @return the rotation (clockwise) of the text element in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotateCenter whether this text should be rotated around its center
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotateCenter(boolean rotateCenter) {
                this.rotateCenter = rotateCenter;
                return this;
            }

            /**
             * @return {@code true} if this text should be rotated around its center, {@code false}
             *         otherwise.
             *
             * @since 1.8.4
             */
            public boolean isRotatingCenter() {
                return rotateCenter;
            }

            /**
             * @param shadow whether the text should have a shadow or not
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder shadow(boolean shadow) {
                this.shadow = shadow;
                return this;
            }

            /**
             * @return {@code true} if the text element has a shadow, {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean hasShadow() {
                return shadow;
            }

            /**
             * @param zIndex the z-index of the text element
             * @return self for chaining.
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            /**
             * @return the z-index of the text element.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            @Override
            public Text createElement() {
                return new Text(new TextHelper(text), x, y, color, zIndex, shadow, scale, rotation).setRotateCenter(rotateCenter).setParent(parent);
            }

            @Override
            public int getScaledWidth() {
                return (int) (scale * getWidth());
            }

            @Override
            public int getParentWidth() {
                return parent.getWidth();
            }

            @Override
            public int getScaledHeight() {
                return (int) (scale * getHeight());
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

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    @SuppressWarnings("unused")
    public static class Line implements RenderElement, Alignable<Line> {

        public IDraw2D<?> parent;
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int color;
        public float rotation;
        public boolean rotateCenter;
        public float width;
        public int zIndex;

        public Line(int x1, int y1, int x2, int y2, int color, float rotation, float width, int zIndex) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            setColor(color);
            this.rotation = MathHelper.wrapDegrees(rotation);
            this.width = width;
            this.zIndex = zIndex;
        }

        /**
         * @param x1 the x position of the start of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setX1(int x1) {
            this.x1 = x1;
            return this;
        }

        /**
         * @return the x position of the start of the line.
         *
         * @since 1.8.4
         */
        public int getX1() {
            return x1;
        }

        /**
         * @param y1 the y position of the start of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setY1(int y1) {
            this.y1 = y1;
            return this;
        }

        /**
         * @return the y position of the start of the line.
         *
         * @since 1.8.4
         */
        public int getY1() {
            return y1;
        }

        /**
         * @param x1 the x position of the start of the line
         * @param y1 the y position of the start of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setPos1(int x1, int y1) {
            this.x1 = x1;
            this.y1 = y1;
            return this;
        }

        /**
         * @param x2 the x position of the end of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setX2(int x2) {
            this.x2 = x2;
            return this;
        }

        /**
         * @return the x position of the end of the line.
         *
         * @since 1.8.4
         */
        public int getX2() {
            return x2;
        }

        /**
         * @param y2 the y position of the end of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setY2(int y2) {
            this.y2 = y2;
            return this;
        }

        /**
         * @return the y position of the end of the line.
         *
         * @since 1.8.4
         */
        public int getY2() {
            return y2;
        }

        /**
         * @param x2 the x position of the end of the line
         * @param y2 the y position of the end of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setPos2(int x2, int y2) {
            this.x2 = x2;
            this.y2 = y2;
            return this;
        }

        /**
         * @param x1 the x position of the start of the line
         * @param y1 the y position of the start of the line
         * @param x2 the x position of the end of the line
         * @param y2 the y position of the end of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setPos(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            return this;
        }

        /**
         * @param color the color of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setColor(int color) {
            if (color < 0xFFFFFF) {
                color = color | 0xFF000000;
            }
            this.color = color;
            return this;
        }

        /**
         * @param color the color of the line
         * @param alpha the alpha of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setColor(int color, int alpha) {
            this.color = (alpha << 24) | (color & 0xFFFFFF);
            return this;
        }

        /**
         * @return the color of the line.
         *
         * @since 1.8.4
         */
        public int getColor() {
            return color;
        }

        /**
         * @param alpha the alpha value of the line's color
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setAlpha(int alpha) {
            this.color = (alpha << 24) | (color & 0xFFFFFF);
            return this;
        }

        /**
         * @return the alpha value of the line's color.
         *
         * @since 1.8.4
         */
        public int getAlpha() {
            return (color >> 24) & 0xFF;
        }

        /**
         * @param rotation the rotation (clockwise) of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setRotation(double rotation) {
            this.rotation = (float) rotation;
            return this;
        }

        /**
         * @return the rotation (clockwise) of the line.
         *
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether this line should be rotated around its center
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setRotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this line should be rotated around its center, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param width the width of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setWidth(double width) {
            this.width = (float) width;
            return this;
        }

        /**
         * @return the width of the line.
         *
         * @since 1.8.4
         */
        public float getWidth() {
            return width;
        }

        /**
         * @param zIndex the z-index of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Line setZIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        @Override
        public int getZIndex() {
            return zIndex;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            setupMatrix(matrices, x1, y1, 1, rotation, getScaledWidth(), getScaledHeight(), rotateCenter);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();

            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            //draw a line with the given width using triangle strips

            float halfWidth = width / 2;
            float dx = x2 - x1;
            float dy = y2 - y1;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
            float px = -dy * halfWidth;
            float py = dx * halfWidth;

            buf.vertex(matrix, x1 + px, y1 + py, 0).color(color).next();
            buf.vertex(matrix, x2 + px, y2 + py, 0).color(color).next();
            buf.vertex(matrix, x1 - px, y1 - py, 0).color(color).next();
            buf.vertex(matrix, x2 - px, y2 - py, 0).color(color).next();
            tess.draw();

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

            matrices.pop();
        }

        public Line setParent(IDraw2D<?> parent) {
            this.parent = parent;
            return this;
        }

        @Override
        public Line moveTo(int x, int y) {
            return setPos(x, y, x + getScaledWidth(), y + getScaledHeight());
        }

        @Override
        public int getScaledWidth() {
            return Math.abs(x2 - x1);
        }

        @Override
        public int getParentWidth() {
            return parent != null ? parent.getWidth() : mc.getWindow().getScaledWidth();
        }

        @Override
        public int getScaledHeight() {
            return Math.abs(y2 - y1);
        }

        @Override
        public int getParentHeight() {
            return parent != null ? parent.getHeight() : mc.getWindow().getScaledHeight();
        }

        @Override
        public int getScaledLeft() {
            return Math.min(x1, x2);
        }

        @Override
        public int getScaledTop() {
            return Math.min(y1, y2);
        }

        /**
         * @author Etheradon
         * @since 1.8.4
         */
        public static class Builder extends RenderElementBuilder<Line> implements Alignable<Builder> {

            private int x1 = 0;
            private int y1 = 0;
            private int x2 = 0;
            private int y2 = 0;
            private float rotation = 0;
            private boolean rotateCenter = true;
            private int color = 0xFFFFFF;
            private int alpha = 0xFF;
            private int zIndex = 0;
            private float width = 1;

            public Builder(IDraw2D<?> draw2D) {
                super(draw2D);
            }

            /**
             * @param x1 the x position of the first point
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x1(int x1) {
                this.x1 = x1;
                return this;
            }

            /**
             * @return the x position of the first point.
             *
             * @since 1.8.4
             */
            public int getX1() {
                return x1;
            }

            /**
             * @param y1 the y position of the first point
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y1(int y1) {
                this.y1 = y1;
                return this;
            }

            /**
             * @return the y position of the first point.
             *
             * @since 1.8.4
             */
            public int getY1() {
                return y1;
            }

            /**
             * @param x1 the x position of the first point
             * @param y1 the y position of the first point
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos1(int x1, int y1) {
                this.x1 = x1;
                this.y1 = y1;
                return this;
            }

            /**
             * @param x2 the x position of the second point
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x2(int x2) {
                this.x2 = x2;
                return this;
            }

            /**
             * @return the x position of the second point.
             *
             * @since 1.8.4
             */
            public int getX2() {
                return x2;
            }

            /**
             * @param y2 the y position of the second point
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y2(int y2) {
                this.y2 = y2;
                return this;
            }

            /**
             * @return the y position of the second point.
             *
             * @since 1.8.4
             */
            public int getY2() {
                return y2;
            }

            /**
             * @param x2 the x position of the second point
             * @param y2 the y position of the second point
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos2(int x2, int y2) {
                this.x2 = x2;
                this.y2 = y2;
                return this;
            }

            /**
             * @param x1 the x position of the first point
             * @param y1 the y position of the first point
             * @param x2 the x position of the second point
             * @param y2 the y position of the second point
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos(int x1, int y1, int x2, int y2) {
                this.x1 = x1;
                this.y1 = y1;
                this.x2 = x2;
                this.y2 = y2;
                return this;
            }

            /**
             * @param rotation the rotation (clockwise) of the line
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(double rotation) {
                this.rotation = (float) rotation;
                return this;
            }

            /**
             * @return the rotation (clockwise) of the line.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotateCenter whether this line should be rotated around its center
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotateCenter(boolean rotateCenter) {
                this.rotateCenter = rotateCenter;
                return this;
            }

            /**
             * @return {@code true} if this line should be rotated around its center, {@code false}
             *         otherwise.
             *
             * @since 1.8.4
             */
            public boolean isRotatingCenter() {
                return rotateCenter;
            }

            /**
             * @param width the width of the line
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder width(double width) {
                this.width = (float) width;
                return this;
            }

            /**
             * @return the width of the line.
             *
             * @since 1.8.4
             */
            public float getWidth() {
                return width;
            }

            /**
             * @param color the color of the line
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color) {
                this.color = color;
                return this;
            }

            /**
             * @param color the color of the line
             * @param alpha the alpha component of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color, int alpha) {
                this.color = color;
                this.alpha = alpha;
                return this;
            }

            /**
             * @param r the red component of the color
             * @param g the green component of the color
             * @param b the blue component of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int r, int g, int b) {
                this.color = (r << 16) | (g << 8) | b;
                return this;
            }

            /**
             * @param r the red component of the color
             * @param g the green component of the color
             * @param b the blue component of the color
             * @param a the alpha value of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int r, int g, int b, int a) {
                this.color = (a << 24) | (r << 16) | (g << 8) | b;
                return this;
            }

            /**
             * @return the color of the line.
             *
             * @since 1.8.4
             */
            public int getColor() {
                return color;
            }

            /**
             * @param alpha the alpha value of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder alpha(int alpha) {
                this.alpha = alpha;
                return this;
            }

            /**
             * @return the alpha value of the color.
             *
             * @since 1.8.4
             */
            public int getAlpha() {
                return alpha;
            }

            /**
             * @param zIndex the z-index of the line
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            /**
             * @return the z-index of the line.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            @Override
            protected Line createElement() {
                return new Line(x1, y1, x2, y2, (alpha << 24) | (color & 0xFFFFFF), rotation, width, zIndex).setRotateCenter(rotateCenter).setParent(parent);
            }

            @Override
            public Builder moveTo(int x, int y) {
                return pos(x, y, x + getScaledWidth(), y + getScaledHeight());
            }

            @Override
            public int getScaledWidth() {
                return Math.abs(x2 - x1);
            }

            @Override
            public int getParentWidth() {
                return parent.getWidth();
            }

            @Override
            public int getScaledHeight() {
                return Math.abs(y2 - y1);
            }

            @Override
            public int getParentHeight() {
                return parent.getHeight();
            }

            @Override
            public int getScaledLeft() {
                return Math.min(x1, x2);
            }

            @Override
            public int getScaledTop() {
                return Math.min(y1, y2);
            }
        }
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    @SuppressWarnings("unused")
    public static class Draw2DElement implements RenderElement, Alignable<Draw2DElement> {

        public final Draw2D draw2D;
        public IDraw2D<?> parent;
        public int x;
        public int y;
        public IntSupplier width;
        public IntSupplier height;
        public float scale;
        public float rotation;
        public boolean rotateCenter;
        public int zIndex;

        public Draw2DElement(Draw2D draw2D, int x, int y, IntSupplier width, IntSupplier height, int zIndex, float scale, float rotation) {
            this.draw2D = draw2D;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.zIndex = zIndex;
            this.scale = scale;
            this.rotation = rotation;
        }

        /**
         * @return the internal draw2D this draw2D element is wrapping.
         *
         * @since 1.8.4
         */
        public Draw2D getDraw2D() {
            return draw2D;
        }

        /**
         * @param x the x position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setX(int x) {
            this.x = x;
            return this;
        }

        /**
         * @return the x position of this draw2D.
         *
         * @since 1.8.4
         */
        public int getX() {
            return x;
        }

        /**
         * @param y the y position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setY(int y) {
            this.y = y;
            return this;
        }

        /**
         * @return the y position of this draw2D.
         *
         * @since 1.8.4
         */
        public int getY() {
            return y;
        }

        /**
         * @param x the x position
         * @param y the y position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setPos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * @param width the width
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setWidth(int width) {
            if (width < 0) {
                throw new IllegalArgumentException("Width must not be negative");
            }
            this.width = () -> width;
            return this;
        }

        /**
         * @return the width of this draw2D.
         *
         * @since 1.8.4
         */
        public int getWidth() {
            return width.getAsInt();
        }

        /**
         * @param height the height
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setHeight(int height) {
            if (height < 0) {
                throw new IllegalArgumentException("Height  must not be negative");
            }
            this.height = () -> height;
            return this;
        }

        /**
         * @return the height of this draw2D.
         *
         * @since 1.8.4
         */
        public int getHeight() {
            return height.getAsInt();
        }

        /**
         * @param width  the width
         * @param height the height
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setSize(int width, int height) {
            return setWidth(width).setHeight(height);
        }

        /**
         * @param scale the scale
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setScale(double scale) {
            if (scale <= 0) {
                throw new IllegalArgumentException("Scale must be greater than 0");
            }
            this.scale = (float) scale;
            return this;
        }

        /**
         * @return the scale of this draw2D.
         *
         * @since 1.8.4
         */
        public float getScale() {
            return scale;
        }

        /**
         * @param rotation the rotation
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setRotation(double rotation) {
            this.rotation = (float) rotation;
            return this;
        }

        /**
         * @return the rotation of this draw2D.
         *
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether this draw2D should be rotated around its center
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setRotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this draw2D should be rotated around its center, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param zIndex the z-index of this draw2D
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setZIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        @Override
        public int getZIndex() {
            return zIndex;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            matrices.translate(x, y, 0);
            matrices.scale(scale, scale, 1);
            if (rotateCenter) {
                matrices.translate(width.getAsInt() / 2d, height.getAsInt() / 2d, 0);
            }
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            if (rotateCenter) {
                matrices.translate(-width.getAsInt() / 2d, -height.getAsInt() / 2d, 0);
            }
            //don't translate back
            draw2D.render(matrices);
            matrices.pop();
        }

        public Draw2DElement setParent(IDraw2D<?> parent) {
            this.parent = parent;
            return this;
        }

        @Override
        public int getScaledWidth() {
            return (int) (width.getAsInt() * scale);
        }

        @Override
        public int getParentWidth() {
            return parent != null ? parent.getWidth() : mc.getWindow().getScaledWidth();
        }

        @Override
        public int getScaledHeight() {
            return (int) (height.getAsInt() * scale);
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
        public Draw2DElement moveTo(int x, int y) {
            return setPos(x, y);
        }

        /**
         * @author Etheradon
         * @since 1.8.4
         */
        public static class Builder extends RenderElementBuilder<Draw2DElement> implements Alignable<Builder> {
            private final Draw2D draw2D;
            private int x = 0;
            private int y = 0;
            private IntSupplier width;
            private IntSupplier height;
            private float scale = 1;
            private float rotation = 0;
            private boolean rotateCenter = true;
            private int zIndex = 0;

            public Builder(IDraw2D<?> parent, Draw2D draw2D) {
                super(parent);
                this.draw2D = draw2D;
                this.width = parent::getWidth;
                this.height = parent::getHeight;
                this.draw2D.widthSupplier = this.width;
                this.draw2D.heightSupplier = this.height;
            }

            /**
             * @param x the x position of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x(int x) {
                this.x = x;
                return this;
            }

            /**
             * @return the x position of the draw2D.
             *
             * @since 1.8.4
             */
            public int getX() {
                return x;
            }

            /**
             * @param y the y position of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y(int y) {
                this.y = y;
                return this;
            }

            /**
             * @return the y position of the draw2D.
             *
             * @since 1.8.4
             */
            public int getY() {
                return y;
            }

            /**
             * @param x the x position of the draw2D
             * @param y the y position of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos(int x, int y) {
                this.x = x;
                this.y = y;
                return this;
            }

            /**
             * @param width the width of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder width(int width) {
                if (width < 0) {
                    throw new IllegalArgumentException("Width  must not be negative");
                }
                this.width = () -> width;
                return this;
            }

            /**
             * @return the width of the draw2D.
             *
             * @since 1.8.4
             */
            public int getWidth() {
                return width.getAsInt();
            }

            /**
             * @param height the height of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder height(int height) {
                if (height < 0) {
                    throw new IllegalArgumentException("Height  must not be negative");
                }
                this.height = () -> height;
                return this;
            }

            /**
             * @return the height of the draw2D.
             *
             * @since 1.8.4
             */
            public int getHeight() {
                return height.getAsInt();
            }

            /**
             * @param width  the width of the draw2D
             * @param height the height of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder size(int width, int height) {
                return width(width).height(height);
            }

            /**
             * @param scale the scale of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder scale(double scale) {
                this.scale = (float) scale;
                return this;
            }

            /**
             * @return the scale of the draw2D.
             *
             * @since 1.8.4
             */
            public float getScale() {
                return scale;
            }

            /**
             * @param rotation the rotation (clockwise) of the draw2D in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(double rotation) {
                this.rotation = (float) rotation;
                return this;
            }

            /**
             * @return the rotation (clockwise) of the draw2D in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotateCenter whether this draw2D should be rotated around its center
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotateCenter(boolean rotateCenter) {
                this.rotateCenter = rotateCenter;
                return this;
            }

            /**
             * @return {@code true} if this draw2D should be rotated around its center,
             *         {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean isRotatingCenter() {
                return rotateCenter;
            }

            /**
             * @return the z-index of the draw2D.
             *
             * @since 1.8.4
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            /**
             * @return the z-index of the draw2D.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            @Override
            protected Draw2DElement createElement() {
                return new Draw2DElement(draw2D, x, y, width, height, zIndex, scale, rotation).setRotateCenter(rotateCenter).setParent(parent);
            }

            @Override
            public int getScaledWidth() {
                return (int) (width.getAsInt() * scale);
            }

            @Override
            public int getParentWidth() {
                return parent.getWidth();
            }

            @Override
            public int getScaledHeight() {
                return (int) (height.getAsInt() * scale);
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
}