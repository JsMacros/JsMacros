package xyz.wagyourtail.jsmacros.client.api.render.draw3d;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import xyz.wagyourtail.jsmacros.client.api.classes.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.render.Draw3D;

/**
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class Line {
    public PositionCommon.Vec3D pos;
    public int color;
    public boolean cull;

    public Line(double x1, double y1, double z1, double x2, double y2, double z2, int color, boolean cull) {
        setPos(x1, y1, z1, x2, y2, z2);
        setColor(color);
        this.cull = cull;
    }

    public Line(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, boolean cull) {
        setPos(x1, y1, z1, x2, y2, z2);
        setColor(color, alpha);
        this.cull = cull;
    }

    /**
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @since 1.0.6
     */
    public void setPos(double x1, double y1, double z1, double x2, double y2, double z2) {
        pos = new PositionCommon.Vec3D(x1, y1, z1, x2, y2, z2);
    }

    /**
     * @param color
     * @since 1.0.6
     */
    public void setColor(int color) {
        if (color <= 0xFFFFFF) {
            color = color | 0xFF000000;
        }
        this.color = color;
    }

    /**
     * @param color
     * @param alpha
     * @since 1.1.8
     */
    public void setColor(int color, int alpha) {
        this.color = (alpha << 24) | (color & 0xFFFFFF);
    }

    /**
     * @param alpha
     * @since 1.1.8
     */
    public void setAlpha(int alpha) {
        this.color = (alpha << 24) | (color & 0xFFFFFF);
    }

    public void render(MatrixStack matrixStack) {
        final boolean cull = !this.cull;
        if (cull) {
            RenderSystem.disableDepthTest();
        }

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        Matrix4f model = matrixStack.peek().getPositionMatrix();
        RenderSystem.lineWidth(2.5F);
        buf.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buf.vertex(model, (float) pos.x1, (float) pos.y1, (float) pos.z1).color(r, g, b, a).next();
        buf.vertex(model, (float) pos.x2, (float) pos.y2, (float) pos.z2).color(r, g, b, a).next();
        tess.draw();

        if (cull) {
            RenderSystem.enableDepthTest();
        }
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class Builder {
        private final Draw3D parent;

        private PositionCommon.Pos3D pos1 = new PositionCommon.Pos3D(0, 0, 0);
        private PositionCommon.Pos3D pos2 = new PositionCommon.Pos3D(0, 0, 0);
        private int color = 0xFFFFFF;
        private int alpha = 255;
        private boolean cull = false;

        public Builder(Draw3D parent) {
            this.parent = parent;
        }

        /**
         * @param pos1 the first position of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos1(PositionCommon.Pos3D pos1) {
            this.pos1 = pos1;
            return this;
        }

        /**
         * @param pos1 the first position of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos1(BlockPosHelper pos1) {
            this.pos1 = pos1.toPos3D();
            return this;
        }

        /**
         * @param x1 the x coordinate of the first position of the line
         * @param y1 the y coordinate of the first position of the line
         * @param z1 the z coordinate of the first position of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos1(double x1, double y1, double z1) {
            this.pos1 = new PositionCommon.Pos3D(x1, y1, z1);
            return this;
        }

        /**
         * @return the first position of the line.
         *
         * @since 1.8.4
         */
        public PositionCommon.Pos3D getPos1() {
            return pos1;
        }

        /**
         * @param pos2 the second position of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos2(PositionCommon.Pos3D pos2) {
            this.pos2 = pos2;
            return this;
        }

        /**
         * @param pos2 the second position of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos2(BlockPosHelper pos2) {
            this.pos2 = pos2.toPos3D();
            return this;
        }

        /**
         * @param x2 the x coordinate of the second position of the line
         * @param y2 the y coordinate of the second position of the line
         * @param z2 the z coordinate of the second position of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos2(int x2, int y2, int z2) {
            this.pos2 = new PositionCommon.Pos3D(x2, y2, z2);
            return this;
        }

        /**
         * @return the second position of the line.
         *
         * @since 1.8.4
         */
        public PositionCommon.Pos3D getPos2() {
            return pos2;
        }

        /**
         * @param x1 the x coordinate of the first position of the line
         * @param y1 the y coordinate of the first position of the line
         * @param z1 the z coordinate of the first position of the line
         * @param x2 the x coordinate of the second position of the line
         * @param y2 the x coordinate of the second position of the line
         * @param z2 the z coordinate of the second position of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos(int x1, int y1, int z1, int x2, int y2, int z2) {
            this.pos1 = new PositionCommon.Pos3D(x1, y1, z1);
            this.pos2 = new PositionCommon.Pos3D(x2, y2, z2);
            return this;
        }

        /**
         * @param pos1 the first position of the line
         * @param pos2 the second position of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos(BlockPosHelper pos1, BlockPosHelper pos2) {
            this.pos1 = pos1.toPos3D();
            this.pos2 = pos2.toPos3D();
            return this;
        }

        /**
         * @param pos1 the first position of the line
         * @param pos2 the second position of the line
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos(PositionCommon.Pos3D pos1, PositionCommon.Pos3D pos2) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            return this;
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
         * @param alpha the alpha value of the line's color
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
            this.color = (r << 16) | (g << 8) | b;
            this.alpha = a;
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
         * @param alpha the alpha value for the line's color
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder alpha(int alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * @return the alpha value of the line's color.
         *
         * @since 1.8.4
         */
        public int getAlpha() {
            return alpha;
        }

        /**
         * @param cull whether to cull the line or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder cull(boolean cull) {
            this.cull = cull;
            return this;
        }

        /**
         * @return {@code true} if the line should be culled, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isCulled() {
            return cull;
        }

        /**
         * Creates the line for the given values and adds it to the draw3D.
         *
         * @return the build line.
         *
         * @since 1.8.4
         */
        public Line buildAndAdd() {
            Line line = build();
            parent.addLine(line);
            return line;
        }

        /**
         * Builds the line from the given values.
         *
         * @return the build line.
         */
        public Line build() {
            return new Line(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, color, alpha, cull);
        }
    }
}
