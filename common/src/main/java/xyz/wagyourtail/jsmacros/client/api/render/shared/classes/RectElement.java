package xyz.wagyourtail.jsmacros.client.api.render.shared.classes;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import com.mojang.blaze3d.systems.RenderSystem;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.RenderCommon.Alignable;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.RenderCommon.RenderElement;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.RenderCommon.RenderElementBuilder;
import xyz.wagyourtail.jsmacros.client.api.render.shared.interfaces.IDraw2D;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class RectElement implements RenderElement, Alignable<RectElement> {

    public IDraw2D<?> parent;
    public float rotation;
    public int x1;
    public int y1;
    public int x2;
    public int y2;
    public int color;
    public int zIndex;

    public RectElement(int x1, int y1, int x2, int y2, int color, float rotation, int zIndex) {
        this(x1, y1, x2, y2, color, 0xFF, rotation, zIndex);
        setColor(color);
    }

    public RectElement(int x1, int y1, int x2, int y2, int color, int alpha, float rotation, int zIndex) {
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
    public RectElement setX1(int x1) {
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
    public RectElement setY1(int y1) {
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
    public RectElement setPos1(int x1, int y1) {
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
    public RectElement setX2(int x2) {
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
    public RectElement setY2(int y2) {
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
    public RectElement setPos2(int x2, int y2) {
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
    public RectElement setPos(int x1, int y1, int x2, int y2) {
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
    public RectElement setWidth(int width) {
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
    public RectElement setHeight(int height) {
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
    public RectElement setSize(int width, int height) {
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
    public RectElement setColor(int color) {
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
    public RectElement setColor(int color, int alpha) {
        this.color = (alpha << 24) | (color & 0xFFFFFF);
        return this;
    }

    /**
     * @param alpha
     * @return
     *
     * @since 1.1.8
     */
    public RectElement setAlpha(int alpha) {
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
    public RectElement setRotation(double rotation) {
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
     * @param zIndex the new z-index for this rectangle
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public RectElement setZIndex(int zIndex) {
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
        matrices.translate(x1, y1, 0);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
        matrices.translate(-x1, -y1, 0);

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

    public RectElement setParent(IDraw2D<?> parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public int getScaledWidth() {
        return getWidth();
    }

    @Override
    public int getParentWidth() {
        return parent != null ? parent.getWidth() : mc.currentScreen.width;
    }

    @Override
    public int getScaledHeight() {
        return getHeight();
    }

    @Override
    public int getParentHeight() {
        return parent != null ? parent.getHeight() : mc.currentScreen.height;
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
    public RectElement moveTo(int x, int y) {
        return setPos(x, y, x + getScaledWidth(), y + getScaledHeight());
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static final class Builder extends RenderElementBuilder<RectElement> implements Alignable<Builder> {
        private int x1 = 0;
        private int y1 = 0;
        private int x2 = 0;
        private int y2 = 0;
        private int color = 0xFFFFFF;
        private int alpha = 0xFF;
        private float rotation = 0;
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
        public RectElement createElement() {
            return new RectElement(x1, y1, x2, y2, color, alpha, rotation, zIndex).setParent(parent);
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
