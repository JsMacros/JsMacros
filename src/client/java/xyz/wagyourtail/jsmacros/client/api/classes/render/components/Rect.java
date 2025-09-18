package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class Rect implements RenderElement, Alignable<Rect> {

    @Nullable
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
     * @since 1.8.4
     */
    public Rect setX1(int x1) {
        this.x1 = x1;
        return this;
    }

    /**
     * @return the first x position of this rectangle.
     * @since 1.8.4
     */
    public int getX1() {
        return x1;
    }

    /**
     * @param y1 the first y position of this rectangle
     * @return self for chaining.
     * @since 1.8.4
     */
    public Rect setY1(int y1) {
        this.y1 = y1;
        return this;
    }

    /**
     * @return the first y position of this rectangle.
     * @since 1.8.4
     */
    public int getY1() {
        return y1;
    }

    /**
     * @param x1 the first x position of this rectangle
     * @param y1 the first y position of this rectangle
     * @return self for chaining.
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
     * @since 1.8.4
     */
    public Rect setX2(int x2) {
        this.x2 = x2;
        return this;
    }

    /**
     * @return the second x position of this rectangle.
     * @since 1.8.4
     */
    public int getX2() {
        return x2;
    }

    /**
     * @param y2 the second y position of this rectangle
     * @return self for chaining.
     * @since 1.8.4
     */
    public Rect setY2(int y2) {
        this.y2 = y2;
        return this;
    }

    /**
     * @return the second y position of the rectangle.
     * @since 1.8.4
     */
    public int getY2() {
        return y2;
    }

    /**
     * @param x2 the second x position of this rectangle
     * @param y2 the second y position of this rectangle
     * @return self for chaining.
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
     * @since 1.8.4
     */
    public int getWidth() {
        return Math.abs(x2 - x1);
    }

    /**
     * @param height the new height of this rectangle
     * @return self for chaining.
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
     * @since 1.8.4
     */
    public int getHeight() {
        return Math.abs(y2 - y1);
    }

    /**
     * @param width  the new width of this rectangle
     * @param height the new height of this rectangle
     * @return self for chaining.
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
     * @since 1.0.5
     */
    public Rect setColor(int color) {
        if (color <= 0xFFFFFFFF) {
            color = color | 0xFF000000;
        }
        this.color = color;
        return this;
    }

    /**
     * @param color
     * @param alpha
     * @return
     * @since 1.1.8
     */
    public Rect setColor(int color, int alpha) {
        this.color = (alpha << 24) | (color & 0xFFFFFFFF);
        return this;
    }

    /**
     * @param alpha
     * @return
     * @since 1.1.8
     */
    public Rect setAlpha(int alpha) {
        this.color = (color & 0xFFFFFFFF) | (alpha << 24);
        return this;
    }

    /**
     * @return the color value of this rectangle.
     * @since 1.8.4
     */
    public int getColor() {
        return color;
    }

    /**
     * @return the alpha value of this rectangle.
     * @since 1.8.4
     */
    public int getAlpha() {
        return (color >> 24) & 0xFF;
    }

    /**
     * @param rotation
     * @return
     * @since 1.2.6
     */
    public Rect setRotation(double rotation) {
        this.rotation = MathHelper.wrapDegrees((float) rotation);
        return this;
    }

    /**
     * @return the rotation of this rectangle.
     * @since 1.8.4
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * @param rotateCenter whether this rectangle should be rotated around its center
     * @return self for chaining.
     * @since 1.8.4
     */
    public Rect setRotateCenter(boolean rotateCenter) {
        this.rotateCenter = rotateCenter;
        return this;
    }

    /**
     * @return {@code true} if this rectangle should be rotated around its center, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isRotatingCenter() {
        return rotateCenter;
    }

    /**
     * @param zIndex the new z-index for this rectangle
     * @return self for chaining.
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
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        setupMatrix(matrices, x1, y1, 1, rotation, getWidth(), getHeight(), rotateCenter);
        drawContext.fill(x1, y1, x2, y2, this.color);
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
        private int color = 0xFFFFFFFF;
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
         * @since 1.8.4
         */
        public Builder x1(int x1) {
            this.x1 = x1;
            return this;
        }

        /**
         * @return the first x position of the rectangle.
         * @since 1.8.4
         */
        public int getX1() {
            return x1;
        }

        /**
         * @param y1 the first y position of the rectangle
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder y1(int y1) {
            this.y1 = y1;
            return this;
        }

        /**
         * @return the first y position of the rectangle.
         * @since 1.8.4
         */
        public int getY1() {
            return y1;
        }

        /**
         * @param x1 the first x position of the rectangle
         * @param y1 the first y position of the rectangle
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public Builder x2(int x2) {
            this.x2 = x2;
            return this;
        }

        /**
         * @return the second x position of the rectangle.
         * @since 1.8.4
         */
        public int getX2() {
            return x2;
        }

        /**
         * @param y2 the second y position of the rectangle
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder y2(int y2) {
            this.y2 = y2;
            return this;
        }

        /**
         * @return the second y position of the rectangle.
         * @since 1.8.4
         */
        public int getY2() {
            return y2;
        }

        /**
         * @param x2 the second x position of the rectangle
         * @param y2 the second y position of the rectangle
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public Builder width(int width) {
            this.x2 = this.x1 + width;
            return this;
        }

        /**
         * @return the width of the rectangle.
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
         * @since 1.8.4
         */
        public Builder height(int height) {
            this.y2 = this.y1 + height;
            return this;
        }

        /**
         * @return the height of the rectangle.
         * @since 1.8.4
         */
        public int getHeight() {
            return Math.abs(this.y2 - this.y1);
        }

        /**
         * @param width  the width of the rectangle
         * @param height the height of the rectangle
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder size(int width, int height) {
            return width(width).height(height);
        }

        /**
         * @param color the color of the rectangle
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public Builder color(int color, int alpha) {
            this.color = color;
            this.alpha = alpha;
            return this;
        }

        /**
         * @return the color of the rectangle.
         * @since 1.8.4
         */
        public int getColor() {
            return color;
        }

        /**
         * @param alpha the alpha value of the color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder alpha(int alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * @return the alpha value of the color.
         * @since 1.8.4
         */
        public int getAlpha() {
            return alpha;
        }

        /**
         * @param rotation the rotation (clockwise) of the rectangle in degrees
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder rotation(double rotation) {
            this.rotation = (float) rotation;
            return this;
        }

        /**
         * @return the rotation (clockwise) of the rectangle in degrees.
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether this rectangle should be rotated around its center
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder rotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this rectangle should be rotated around its center,
         * {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param zIndex the z-index of the rectangle
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder zIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        /**
         * @return the z-index of the rectangle.
         * @since 1.8.4
         */
        public int getZIndex() {
            return zIndex;
        }

        @Override
        public Rect createElement() {
            return new Rect(x1, y1, x2, y2, color, alpha, rotation, zIndex).setRotateCenter(rotateCenter).setParent(
                    parent);
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
