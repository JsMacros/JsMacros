package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;

import java.util.function.IntSupplier;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class Draw2DElement implements RenderElement, Alignable<Draw2DElement> {

    public final Draw2D draw2D;
    @Nullable
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
     * @since 1.8.4
     */
    public Draw2D getDraw2D() {
        return draw2D;
    }

    /**
     * @param x the x position
     * @return self for chaining.
     * @since 1.8.4
     */
    public Draw2DElement setX(int x) {
        this.x = x;
        return this;
    }

    /**
     * @return the x position of this draw2D.
     * @since 1.8.4
     */
    public int getX() {
        return x;
    }

    /**
     * @param y the y position
     * @return self for chaining.
     * @since 1.8.4
     */
    public Draw2DElement setY(int y) {
        this.y = y;
        return this;
    }

    /**
     * @return the y position of this draw2D.
     * @since 1.8.4
     */
    public int getY() {
        return y;
    }

    /**
     * @param x the x position
     * @param y the y position
     * @return self for chaining.
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
     * @since 1.8.4
     */
    public int getWidth() {
        return width.getAsInt();
    }

    /**
     * @param height the height
     * @return self for chaining.
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
     * @since 1.8.4
     */
    public int getHeight() {
        return height.getAsInt();
    }

    /**
     * @param width  the width
     * @param height the height
     * @return self for chaining.
     * @since 1.8.4
     */
    public Draw2DElement setSize(int width, int height) {
        return setWidth(width).setHeight(height);
    }

    /**
     * @param scale the scale
     * @return self for chaining.
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
     * @since 1.8.4
     */
    public float getScale() {
        return scale;
    }

    /**
     * @param rotation the rotation
     * @return self for chaining.
     * @since 1.8.4
     */
    public Draw2DElement setRotation(double rotation) {
        this.rotation = (float) rotation;
        return this;
    }

    /**
     * @return the rotation of this draw2D.
     * @since 1.8.4
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * @param rotateCenter whether this draw2D should be rotated around its center
     * @return self for chaining.
     * @since 1.8.4
     */
    public Draw2DElement setRotateCenter(boolean rotateCenter) {
        this.rotateCenter = rotateCenter;
        return this;
    }

    /**
     * @return {@code true} if this draw2D should be rotated around its center, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isRotatingCenter() {
        return rotateCenter;
    }

    /**
     * @param zIndex the z-index of this draw2D
     * @return self for chaining.
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
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);
        if (rotateCenter) {
            matrices.translate(width.getAsInt() / 2f, height.getAsInt() / 2f);
        }
        matrices.rotate((float) Math.toRadians(rotation));
        if (rotateCenter) {
            matrices.translate(-width.getAsInt() / 2f, -height.getAsInt() / 2f);
        }
        //don't translate back
        draw2D.render(drawContext);
        matrices.popMatrix();
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
         * @since 1.8.4
         */
        public Builder x(int x) {
            this.x = x;
            return this;
        }

        /**
         * @return the x position of the draw2D.
         * @since 1.8.4
         */
        public int getX() {
            return x;
        }

        /**
         * @param y the y position of the draw2D
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder y(int y) {
            this.y = y;
            return this;
        }

        /**
         * @return the y position of the draw2D.
         * @since 1.8.4
         */
        public int getY() {
            return y;
        }

        /**
         * @param x the x position of the draw2D
         * @param y the y position of the draw2D
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public int getWidth() {
            return width.getAsInt();
        }

        /**
         * @param height the height of the draw2D
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public int getHeight() {
            return height.getAsInt();
        }

        /**
         * @param width  the width of the draw2D
         * @param height the height of the draw2D
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder size(int width, int height) {
            return width(width).height(height);
        }

        /**
         * @param scale the scale of the draw2D
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder scale(double scale) {
            this.scale = (float) scale;
            return this;
        }

        /**
         * @return the scale of the draw2D.
         * @since 1.8.4
         */
        public float getScale() {
            return scale;
        }

        /**
         * @param rotation the rotation (clockwise) of the draw2D in degrees
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder rotation(double rotation) {
            this.rotation = (float) rotation;
            return this;
        }

        /**
         * @return the rotation (clockwise) of the draw2D in degrees.
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether this draw2D should be rotated around its center
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder rotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this draw2D should be rotated around its center,
         * {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @return the z-index of the draw2D.
         * @since 1.8.4
         */
        public Builder zIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        /**
         * @return the z-index of the draw2D.
         * @since 1.8.4
         */
        public int getZIndex() {
            return zIndex;
        }

        @Override
        protected Draw2DElement createElement() {
            return new Draw2DElement(draw2D, x, y, width, height, zIndex, scale, rotation).setRotateCenter(rotateCenter)
                    .setParent(parent);
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
