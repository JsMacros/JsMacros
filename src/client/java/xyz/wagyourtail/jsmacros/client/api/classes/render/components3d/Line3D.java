package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.api.math.Vec3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class Line3D implements RenderElement3D<Line3D> {
    private static final Field lineDepthTestFunction;
    private static final DepthTestFunction oldlineDepthTestFunction;

    static {
        try {
            lineDepthTestFunction = RenderPipelines.LINES.getClass().getDeclaredField("depthTestFunction");
            lineDepthTestFunction.setAccessible(true);
            oldlineDepthTestFunction = (DepthTestFunction) lineDepthTestFunction.get(RenderPipelines.LINES);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("JS-Macros 3D Rendering failed to reflect into RenderLayer for Line3D", e);
        }
    }

    public Vec3D pos;
    public int color;
    public boolean cull;

    public Line3D(double x1, double y1, double z1, double x2, double y2, double z2, int color, boolean cull) {
        setPos(x1, y1, z1, x2, y2, z2);
        setColor(color);
        this.cull = cull;
    }

    public Line3D(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, boolean cull) {
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
        pos = new Vec3D(x1, y1, z1, x2, y2, z2);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line3D line3D = (Line3D) o;
        return Objects.equals(pos, line3D.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }

    @Override
    public int compareToSame(Line3D o) {
        return pos.compareTo(o.pos);
    }

    @Override
    @DocletIgnore
    public void render(MatrixStack matrixStack, VertexConsumerProvider consumers, float tickDelta) {
        boolean seeThrough = !this.cull;
        VertexConsumer consumer = consumers.getBuffer(RenderLayer.getLines());

        try {
            if (seeThrough) {
                lineDepthTestFunction.set(RenderPipelines.LINES, DepthTestFunction.NO_DEPTH_TEST);
            }
            MatrixStack.Entry entry = matrixStack.peek();

            // Draw 3 lines in each of the normals for consistency
            consumer.vertex(entry, (float) pos.x1, (float) pos.y1, (float) pos.z1).color(color).normal(entry, 1, 0, 0);
            consumer.vertex(entry, (float) pos.x2, (float) pos.y2, (float) pos.z2).color(color).normal(entry, 1, 0, 0);
            consumer.vertex(entry, (float) pos.x1, (float) pos.y1, (float) pos.z1).color(color).normal(entry, 0, 1, 0);
            consumer.vertex(entry, (float) pos.x2, (float) pos.y2, (float) pos.z2).color(color).normal(entry, 0, 1, 0);
            consumer.vertex(entry, (float) pos.x1, (float) pos.y1, (float) pos.z1).color(color).normal(entry, 0, 0, 1);
            consumer.vertex(entry, (float) pos.x2, (float) pos.y2, (float) pos.z2).color(color).normal(entry, 0, 0, 1);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            if (seeThrough) {
                try {
                    lineDepthTestFunction.set(RenderPipelines.LINES, oldlineDepthTestFunction);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class Builder {
        private final Draw3D parent;

        private Pos3D pos1 = new Pos3D(0, 0, 0);
        private Pos3D pos2 = new Pos3D(0, 0, 0);
        private int color = 0xFFFFFF;
        private int alpha = 255;
        private boolean cull = false;

        public Builder(Draw3D parent) {
            this.parent = parent;
        }

        /**
         * @param pos1 the first position of the line
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos1(Pos3D pos1) {
            this.pos1 = pos1;
            return this;
        }

        /**
         * @param pos1 the first position of the line
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public Builder pos1(double x1, double y1, double z1) {
            this.pos1 = new Pos3D(x1, y1, z1);
            return this;
        }

        /**
         * @return the first position of the line.
         * @since 1.8.4
         */
        public Pos3D getPos1() {
            return pos1;
        }

        /**
         * @param pos2 the second position of the line
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos2(Pos3D pos2) {
            this.pos2 = pos2;
            return this;
        }

        /**
         * @param pos2 the second position of the line
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public Builder pos2(int x2, int y2, int z2) {
            this.pos2 = new Pos3D(x2, y2, z2);
            return this;
        }

        /**
         * @return the second position of the line.
         * @since 1.8.4
         */
        public Pos3D getPos2() {
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
         * @since 1.8.4
         */
        public Builder pos(int x1, int y1, int z1, int x2, int y2, int z2) {
            this.pos1 = new Pos3D(x1, y1, z1);
            this.pos2 = new Pos3D(x2, y2, z2);
            return this;
        }

        /**
         * @param pos1 the first position of the line
         * @param pos2 the second position of the line
         * @return self for chaining.
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
         * @since 1.8.4
         */
        public Builder pos(Pos3D pos1, Pos3D pos2) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            return this;
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
         * @param alpha the alpha value of the line's color
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
            this.color = (r << 16) | (g << 8) | b;
            this.alpha = a;
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
         * @param alpha the alpha value for the line's color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder alpha(int alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * @return the alpha value of the line's color.
         * @since 1.8.4
         */
        public int getAlpha() {
            return alpha;
        }

        /**
         * @param cull whether to cull the line or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder cull(boolean cull) {
            this.cull = cull;
            return this;
        }

        /**
         * @return {@code true} if the line should be culled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isCulled() {
            return cull;
        }

        /**
         * Creates the line for the given values and adds it to the draw3D.
         *
         * @return the build line.
         * @since 1.8.4
         */
        public Line3D buildAndAdd() {
            Line3D line = build();
            parent.addLine(line);
            return line;
        }

        /**
         * Builds the line from the given values.
         *
         * @return the build line.
         */
        public Line3D build() {
            return new Line3D(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, color, alpha, cull);
        }

    }

}