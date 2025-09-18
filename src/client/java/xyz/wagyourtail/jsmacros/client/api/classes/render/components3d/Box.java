package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.doclet.DocletIgnore;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.gl.RenderPipelines;
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
public class Box implements RenderElement3D<Box> {
    private static final Field lineDepthTestFunction;
    private static final DepthTestFunction oldlineDepthTestFunction;
    private static final Field boxDepthTestFunction;
    private static final DepthTestFunction oldboxDepthTestFunction;

    static {
        try {
            lineDepthTestFunction = RenderPipelines.LINES.getClass().getDeclaredField("depthTestFunction");
            lineDepthTestFunction.setAccessible(true);
            oldlineDepthTestFunction = (DepthTestFunction) lineDepthTestFunction.get(RenderPipelines.LINES);
            boxDepthTestFunction = RenderPipelines.DEBUG_FILLED_BOX.getClass().getDeclaredField("depthTestFunction");
            boxDepthTestFunction.setAccessible(true);
            oldboxDepthTestFunction = (DepthTestFunction) boxDepthTestFunction.get(RenderPipelines.DEBUG_FILLED_BOX);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public Vec3D pos;
    public int color;
    public int fillColor;
    public boolean fill;
    public boolean cull;

    public Box(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill, boolean cull) {
        setPos(x1, y1, z1, x2, y2, z2);
        setColor(color);
        setFillColor(fillColor);
        this.fill = fill;
        this.cull = cull;
    }

    public Box(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill, boolean cull) {
        setPos(x1, y1, z1, x2, y2, z2);
        setColor(color, alpha);
        setFillColor(fillColor, fillAlpha);
        this.fill = fill;
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
     * set this component's pos to a block
     * @since 1.9.0
     */
    public void setPosToBlock(BlockPosHelper pos) {
        setPosToBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * set this component's pos to a block
     * @since 1.9.0
     */
    public void setPosToBlock(int x, int y, int z) {
        setPos(x, y, z, x + 1, y + 1, z + 1);
    }

    /**
     * set this component's pos to a point
     * @since 1.9.0
     */
    public void setPosToPoint(Pos3D pos, double radius) {
        setPosToPoint(pos.getX(), pos.getY(), pos.getZ(), radius);
    }

    /**
     * set this component's pos to a point
     * @since 1.9.0
     */
    public void setPosToPoint(double x, double y, double z, double radius) {
        setPos(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
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
     * @param fillColor
     * @since 1.0.6
     */
    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
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

    /**
     * @param fillColor
     * @param alpha
     * @since 1.1.8
     */
    public void setFillColor(int fillColor, int alpha) {
        this.fillColor = fillColor | (alpha << 24);
    }

    /**
     * @param alpha
     * @since 1.1.8
     */
    public void setFillAlpha(int alpha) {
        this.fillColor = (fillColor & 0xFFFFFF) | (alpha << 24);
    }

    /**
     * @param fill
     * @since 1.0.6
     */
    public void setFill(boolean fill) {
        this.fill = fill;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box box = (Box) o;
        return Objects.equals(pos, box.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }

    @Override
    public int compareToSame(Box other) {
        if (other instanceof Box) {
            return pos.compareTo(((Box) other).pos);
        }
        return 0;
    }


    @Override
    @DocletIgnore
    public void render(MatrixStack matrixStack, VertexConsumerProvider consumers, float tickDelta) {
        float x1 = (float) pos.x1;
        float y1 = (float) pos.y1;
        float z1 = (float) pos.z1;
        float x2 = (float) pos.x2;
        float y2 = (float) pos.y2;
        float z2 = (float) pos.z2;

        boolean seeThrough = !this.cull;
        VertexConsumerProvider.Immediate immediate = (VertexConsumerProvider.Immediate) consumers;
        try {
            if (seeThrough) {
                lineDepthTestFunction.set(RenderPipelines.LINES, DepthTestFunction.NO_DEPTH_TEST);
                boxDepthTestFunction.set(RenderPipelines.DEBUG_FILLED_BOX, DepthTestFunction.NO_DEPTH_TEST);
            }
            RenderLayer linesLayer = RenderLayer.getLines();
            RenderLayer fillLayer = RenderLayer.getDebugFilledBox();

            if (this.fill) {
                float fa = ((fillColor >> 24) & 0xFF) / 255.0F;
                float fr = ((fillColor >> 16) & 0xFF) / 255.0F;
                float fg = ((fillColor >> 8) & 0xFF) / 255.0F;
                float fb = (fillColor & 0xFF) / 255.0F;
                VertexRendering.drawFilledBox(matrixStack, consumers.getBuffer(fillLayer), x1, y1, z1, x2, y2, z2, fr, fg, fb, fa);
            }

            float r = ((color >> 16) & 0xFF) / 255.0F;
            float g = ((color >> 8) & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            float a = ((color >> 24) & 0xFF) / 255.0F;
            VertexRendering.drawBox(matrixStack, consumers.getBuffer(linesLayer), x1, y1, z1, x2, y2, z2, r, g, b, a);
            if (seeThrough) {
                immediate.draw();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {

            if (seeThrough) {
                try {
                    lineDepthTestFunction.set(RenderPipelines.LINES, oldlineDepthTestFunction);
                    boxDepthTestFunction.set(RenderPipelines.DEBUG_FILLED_BOX, oldboxDepthTestFunction);
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
        private int fillColor = 0xFFFFFF;
        private int alpha = 0xFF;
        private int fillAlpha = 0;
        private boolean fill = false;
        private boolean cull = false;

        public Builder(Draw3D parent) {
            this.parent = parent;
        }

        /**
         * @param pos1 the first position of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos1(Pos3D pos1) {
            this.pos1 = pos1;
            return this;
        }

        /**
         * @param pos1 the first position of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos1(BlockPosHelper pos1) {
            this.pos1 = pos1.toPos3D();
            return this;
        }

        /**
         * @param x1 the x coordinate of the first position of the box
         * @param y1 the y coordinate of the first position of the box
         * @param z1 the z coordinate of the first position of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos1(double x1, double y1, double z1) {
            this.pos1 = new Pos3D(x1, y1, z1);
            return this;
        }

        /**
         * @return the first position of the box.
         * @since 1.8.4
         */
        public Pos3D getPos1() {
            return pos1;
        }

        /**
         * @param pos2 the second position of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos2(Pos3D pos2) {
            this.pos2 = pos2;
            return this;
        }

        /**
         * @param pos2 the second position of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos2(BlockPosHelper pos2) {
            this.pos2 = pos2.toPos3D();
            return this;
        }

        /**
         * @param x2 the x coordinate of the second position of the box
         * @param y2 the y coordinate of the second position of the box
         * @param z2 the z coordinate of the second position of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos2(double x2, double y2, double z2) {
            this.pos2 = new Pos3D(x2, y2, z2);
            return this;
        }

        /**
         * @return the second position of the box.
         * @since 1.8.4
         */
        public Pos3D getPos2() {
            return pos2;
        }

        /**
         * @param x1 the x coordinate of the first position of the box
         * @param y1 the y coordinate of the first position of the box
         * @param z1 the z coordinate of the first position of the box
         * @param x2 the x coordinate of the second position of the box
         * @param y2 the y coordinate of the second position of the box
         * @param z2 the z coordinate of the second position of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.pos1 = new Pos3D(x1, y1, z1);
            this.pos2 = new Pos3D(x2, y2, z2);
            return this;
        }

        /**
         * @param pos1 the first position of the box
         * @param pos2 the second position of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos(BlockPosHelper pos1, BlockPosHelper pos2) {
            this.pos1 = pos1.toPos3D();
            this.pos2 = pos2.toPos3D();
            return this;
        }

        /**
         * @param pos1 the first position of the box
         * @param pos2 the second position of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos(Pos3D pos1, Pos3D pos2) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            return this;
        }

        /**
         * Highlights the given block position.
         *
         * @param x the x coordinate of the block
         * @param y the y coordinate of the block
         * @param z the z coordinate of the block
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder forBlock(int x, int y, int z) {
            this.pos1 = new Pos3D(x, y, z);
            this.pos2 = new Pos3D(x + 1, y + 1, z + 1);
            return this;
        }

        /**
         * Highlights the given block position.
         *
         * @param pos the block position
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder forBlock(BlockPosHelper pos) {
            this.pos1 = pos.toPos3D();
            this.pos2 = pos.offset(1, 1, 1).toPos3D();
            return this;
        }

        /**
         * @param color the color of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder color(int color) {
            this.color = color;
            return this;
        }

        /**
         * @param color the fill color of the box
         * @param alpha the alpha value for the box's fill color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder color(int color, int alpha) {
            this.color = fillColor;
            this.alpha = alpha;
            return this;
        }

        /**
         * @param r the red component of the fill color
         * @param g the green component of the fill color
         * @param b the blue component of the fill color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder color(int r, int g, int b) {
            this.color = (r << 16) | (g << 8) | b;
            return this;
        }

        /**
         * @param r the red component of the fill color
         * @param g the green component of the fill color
         * @param b the blue component of the fill color
         * @param a the alpha component of the fill color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder color(int r, int g, int b, int a) {
            this.color = (r << 16) | (g << 8) | b;
            this.alpha = a;
            return this;
        }

        /**
         * @return the color of the box.
         * @since 1.8.4
         */
        public int getColor() {
            return color;
        }

        /**
         * @param alpha the alpha value for the box's color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder alpha(int alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * @return the alpha value of the box's color.
         * @since 1.8.4
         */
        public int getAlpha() {
            return alpha;
        }

        /**
         * @param fillColor the fill color of the box
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder fillColor(int fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        /**
         * @param fillColor the fill color of the box
         * @param alpha     the alpha value for the box's fill color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder fillColor(int fillColor, int alpha) {
            this.fillColor = fillColor;
            this.fillAlpha = alpha;
            return this;
        }

        /**
         * @param r the red component of the fill color
         * @param g the green component of the fill color
         * @param b the blue component of the fill color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder fillColor(int r, int g, int b) {
            this.fillColor = (r << 16) | (g << 8) | b;
            return this;
        }

        /**
         * @param r the red component of the fill color
         * @param g the green component of the fill color
         * @param b the blue component of the fill color
         * @param a the alpha component of the fill color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder fillColor(int r, int g, int b, int a) {
            this.fillColor = (r << 16) | (g << 8) | b;
            this.fillAlpha = a;
            return this;
        }

        /**
         * @return the fill color of the box.
         * @since 1.8.4
         */
        public int getFillColor() {
            return fillColor;
        }

        /**
         * @param fillAlpha the alpha value for the box's fill color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder fillAlpha(int fillAlpha) {
            this.fillAlpha = fillAlpha;
            return this;
        }

        /**
         * @return the alpha value of the box's fill color.
         * @since 1.8.4
         */
        public int getFillAlpha() {
            return fillAlpha;
        }

        /**
         * @param fill {@code true} if the box should be filled, {@code false} otherwise
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder fill(boolean fill) {
            this.fill = fill;
            return this;
        }

        /**
         * @return {@code true} if the box should be filled, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isFilled() {
            return fill;
        }

        /**
         * @param cull whether to enable culling or not
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder cull(boolean cull) {
            this.cull = cull;
            return this;
        }

        /**
         * @return {@code true} if culling is enabled for this box, {@code false} otherwise.
         * @since 1.8.4
         */
        public boolean isCulled() {
            return cull;
        }

        /**
         * Creates the box for the given values and adds it to the draw3D.
         *
         * @return the build box.
         * @since 1.8.4
         */
        public Box buildAndAdd() {
            Box box = build();
            parent.addBox(box);
            return box;
        }

        /**
         * Builds the box from the given values.
         *
         * @return the build box.
         */
        public Box build() {
            return new Box(
                    pos1.x,
                    pos1.y,
                    pos1.z,
                    pos2.x,
                    pos2.y,
                    pos2.z,
                    color,
                    alpha,
                    fillColor,
                    fillAlpha,
                    fill,
                    cull
            );
        }

    }
}