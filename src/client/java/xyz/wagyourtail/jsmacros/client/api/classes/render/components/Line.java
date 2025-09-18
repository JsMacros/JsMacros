package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class Line implements RenderElement, Alignable<Line> {

    @Nullable
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
     * @since 1.8.4
     */
    public Line setX1(int x1) {
        this.x1 = x1;
        return this;
    }

    /**
     * @return the x position of the start of the line.
     * @since 1.8.4
     */
    public int getX1() {
        return x1;
    }

    /**
     * @param y1 the y position of the start of the line
     * @return self for chaining.
     * @since 1.8.4
     */
    public Line setY1(int y1) {
        this.y1 = y1;
        return this;
    }

    /**
     * @return the y position of the start of the line.
     * @since 1.8.4
     */
    public int getY1() {
        return y1;
    }

    /**
     * @param x1 the x position of the start of the line
     * @param y1 the y position of the start of the line
     * @return self for chaining.
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
     * @since 1.8.4
     */
    public Line setX2(int x2) {
        this.x2 = x2;
        return this;
    }

    /**
     * @return the x position of the end of the line.
     * @since 1.8.4
     */
    public int getX2() {
        return x2;
    }

    /**
     * @param y2 the y position of the end of the line
     * @return self for chaining.
     * @since 1.8.4
     */
    public Line setY2(int y2) {
        this.y2 = y2;
        return this;
    }

    /**
     * @return the y position of the end of the line.
     * @since 1.8.4
     */
    public int getY2() {
        return y2;
    }

    /**
     * @param x2 the x position of the end of the line
     * @param y2 the y position of the end of the line
     * @return self for chaining.
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
     * @since 1.8.4
     */
    public Line setColor(int color) {
        if (color < 0xFFFFFFFF) {
            color = color | 0xFF000000;
        }
        this.color = color;
        return this;
    }

    /**
     * @param color the color of the line
     * @param alpha the alpha of the line
     * @return self for chaining.
     * @since 1.8.4
     */
    public Line setColor(int color, int alpha) {
        this.color = (alpha << 24) | (color & 0xFFFFFFFF);
        return this;
    }

    /**
     * @return the color of the line.
     * @since 1.8.4
     */
    public int getColor() {
        return color;
    }

    /**
     * @param alpha the alpha value of the line's color
     * @return self for chaining.
     * @since 1.8.4
     */
    public Line setAlpha(int alpha) {
        this.color = (alpha << 24) | (color & 0xFFFFFFFF);
        return this;
    }

    /**
     * @return the alpha value of the line's color.
     * @since 1.8.4
     */
    public int getAlpha() {
        return (color >> 24) & 0xFF;
    }

    /**
     * @param rotation the rotation (clockwise) of the line
     * @return self for chaining.
     * @since 1.8.4
     */
    public Line setRotation(double rotation) {
        this.rotation = (float) rotation;
        return this;
    }

    /**
     * @return the rotation (clockwise) of the line.
     * @since 1.8.4
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * @param rotateCenter whether this line should be rotated around its center
     * @return self for chaining.
     * @since 1.8.4
     */
    public Line setRotateCenter(boolean rotateCenter) {
        this.rotateCenter = rotateCenter;
        return this;
    }

    /**
     * @return {@code true} if this line should be rotated around its center, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isRotatingCenter() {
        return rotateCenter;
    }

    /**
     * @param width the width of the line
     * @return self for chaining.
     * @since 1.8.4
     */
    public Line setWidth(double width) {
        this.width = (float) width;
        return this;
    }

    /**
     * @return the width of the line.
     * @since 1.8.4
     */
    public float getWidth() {
        return width;
    }

    /**
     * @param zIndex the z-index of the line
     * @return self for chaining.
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
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        setupMatrix(matrices, x1, y1, 1, rotation, getScaledWidth(), getScaledHeight(), rotateCenter);
        try {
            float halfWidth = this.width / 2.0f;
            float dx = this.x2 - this.x1;
            float dy = this.y2 - this.y1;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            float angle = (float) Math.atan2(dy, dx);

            matrices.translate(this.x1, this.y1, 0);
            matrices.multiply(new Quaternionf().rotateLocalZ(angle));

            drawContext.fill(
                    0,
                    (int) -halfWidth,
                    (int) length,
                    (int) halfWidth,
                    this.color
            );
        } finally {
            matrices.pop();
        }
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
        private int color = 0xFFFFFFFF;
        private int alpha = 0xFF;
        private int zIndex = 0;
        private float width = 1;

        public Builder(IDraw2D<?> draw2D) {
            super(draw2D);
        }

        /**
         * @param x1 the x position of the first point
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder x1(int x1) {
            this.x1 = x1;
            return this;
        }

        /**
         * @return the x position of the first point.
         * @since 1.8.4
         */
        public int getX1() {
            return x1;
        }

        /**
         * @param y1 the y position of the first point
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder y1(int y1) {
            this.y1 = y1;
            return this;
        }

        /**
         * @return the y position of the first point.
         * @since 1.8.4
         */
        public int getY1() {
            return y1;
        }

        /**
         * @param x1 the x position of the first point
         * @param y1 the y position of the first point
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public Builder x2(int x2) {
            this.x2 = x2;
            return this;
        }

        /**
         * @return the x position of the second point.
         * @since 1.8.4
         */
        public int getX2() {
            return x2;
        }

        /**
         * @param y2 the y position of the second point
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder y2(int y2) {
            this.y2 = y2;
            return this;
        }

        /**
         * @return the y position of the second point.
         * @since 1.8.4
         */
        public int getY2() {
            return y2;
        }

        /**
         * @param x2 the x position of the second point
         * @param y2 the y position of the second point
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public Builder rotation(double rotation) {
            this.rotation = (float) rotation;
            return this;
        }

        /**
         * @return the rotation (clockwise) of the line.
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether this line should be rotated around its center
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder rotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this line should be rotated around its center, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param width the width of the line
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder width(double width) {
            this.width = (float) width;
            return this;
        }

        /**
         * @return the width of the line.
         * @since 1.8.4
         */
        public float getWidth() {
            return width;
        }

        /**
         * @param color the color of the line
         * @return self for chaining.
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
            this.color = (a << 24) | (r << 16) | (g << 8) | b;
            return this;
        }

        /**
         * @return the color of the line.
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
         * @param zIndex the z-index of the line
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder zIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        /**
         * @return the z-index of the line.
         * @since 1.8.4
         */
        public int getZIndex() {
            return zIndex;
        }

        @Override
        protected Line createElement() {
            return new Line(
                    x1,
                    y1,
                    x2,
                    y2,
                    (alpha << 24) | (color & 0xFFFFFFFF),
                    rotation,
                    width,
                    zIndex
            ).setRotateCenter(rotateCenter).setParent(parent);
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
