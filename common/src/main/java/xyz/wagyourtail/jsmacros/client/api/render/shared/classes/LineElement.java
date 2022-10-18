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
import xyz.wagyourtail.jsmacros.client.api.render.shared.interfaces.IDraw2D;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class LineElement implements RenderCommon.RenderElement {

    public int x1;
    public int y1;
    public int x2;
    public int y2;
    public int color;
    public float rotation;
    public float width;
    public int zIndex;

    public LineElement(int x1, int y1, int x2, int y2, int color, float rotation, float width, int zIndex) {
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
    public LineElement setX1(int x1) {
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
    public LineElement setY1(int y1) {
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
    public LineElement setPos1(int x1, int y1) {
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
    public LineElement setX2(int x2) {
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
    public LineElement setY2(int y2) {
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
    public LineElement setPos2(int x2, int y2) {
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
    public LineElement setPos(int x1, int y1, int x2, int y2) {
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
    public LineElement setColor(int color) {
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
    public LineElement setColor(int color, int alpha) {
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
    public LineElement setAlpha(int alpha) {
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
    public LineElement setRotation(double rotation) {
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
     * @param width the width of the line
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public LineElement setWidth(double width) {
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
    public LineElement setZIndex(int zIndex) {
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

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class Builder extends RenderCommon.RenderElementBuilder<LineElement> {

        private int x1 = 0;
        private int y1 = 0;
        private int x2 = 0;
        private int y2 = 0;
        private float rotation = 0;
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
        protected LineElement createElement() {
            return new LineElement(x1, y1, x2, y2, (alpha << 24) | (color & 0xFFFFFF), rotation, width, zIndex);
        }
    }
}
