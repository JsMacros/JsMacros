package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;

import java.util.Objects;

/**
 * @author aMelonRind
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class TraceLine implements RenderElement3D<TraceLine> {
    /**
     * this is not meant to be exposed because it works in a poor way<br>
     * it needs fov and aspect ratio info to render normally when not on center<br>
     * but for customize availability I just put it here as a field
     */

    private final Line3D render;

    public TraceLine(double x, double y, double z, int color) {
        render = new Line3D(0, 0, 0, x, y, z, color, false);
    }

    public TraceLine(double x, double y, double z, int color, int alpha) {
        render = new Line3D(0,0,0, x, y, z, color, alpha, false);
    }

    public TraceLine(Pos3D pos, int color) {
        render = new Line3D(0, 0, 0, pos.getX(), pos.getY(), pos.getZ(), color, false);
    }

    public TraceLine(Pos3D pos, int color, int alpha) {
        render = new Line3D(0, 0, 0, pos.getX(), pos.getY(), pos.getZ(), color, alpha, false);
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setPos(double x, double y, double z) {
        render.setPos(0, 0, 0, x, y, z);
        return this;
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setPos(Pos3D pos) {
        render.setPos(0, 0, 0, pos.x, pos.y, pos.z);
        return this;
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setColor(int color) {
        render.setColor(color);
        return this;
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setColor(int color, int alpha) {
        render.setColor(color, alpha);
        return this;
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setAlpha(int alpha) {
        return setColor(render.color, alpha);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TraceLine traceLine = (TraceLine) o;
        return Objects.equals(render.pos.getEnd(), traceLine.render.pos.getEnd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(render.pos.getEnd());
    }

    @Override
    public int compareToSame(TraceLine other) {
        return render.pos.getEnd().compareTo(other.render.pos.getEnd());
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider consumers, float tickDelta) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d p1 = camera.getPos().add(Vec3d.fromPolar(camera.getPitch(), camera.getYaw()));

        render.setPos(p1.x, p1.y, p1.z, render.pos.x2, render.pos.y2, render.pos.z2);
        render.render(matrixStack, consumers, tickDelta);
    }

    public static class Builder {
        private final Draw3D parent;

        private Pos3D pos = new Pos3D(0.0, 0.0, 0.0);
        private int color = 0xFFFFFF;
        private int alpha = 0xFF;

        public Builder(Draw3D parent) {
            this.parent = parent;
        }

        /**
         * @param pos the position of the target
         * @return self for chaining
         * @since 1.9.0
         */
        public Builder pos(Pos3D pos) {
            this.pos = pos;
            return this;
        }

        /**
         * @param pos the position of the target
         * @return self for chaining
         * @since 1.9.0
         */
        public Builder pos(BlockPosHelper pos) {
            this.pos = pos.toPos3D();
            return this;
        }

        /**
         * @param x the x coordinate of the target
         * @param y the y coordinate of the target
         * @param z the z coordinate of the target
         * @return self for chaining
         * @since 1.9.0
         */
        public Builder pos(int x, int y, int z) {
            this.pos = new Pos3D(x, y, z);
            return this;
        }

        /**
         * @return the position of the target
         * @since 1.9.0
         */
        public Pos3D getPos() {
            return pos;
        }

        /**
         * @param color the color of the line
         * @return self for chaining
         * @since 1.9.0
         */
        public Builder color(int color) {
            this.color = color;
            return this;
        }

        /**
         * @param color the color of the line
         * @param alpha the alpha value of the line's color
         * @return self for chaining
         * @since 1.9.0
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
         * @return self for chaining
         * @since 1.9.0
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
         * @return self for chaining
         * @since 1.9.0
         */
        public Builder color(int r, int g, int b, int a) {
            this.color = (r << 16) | (g << 8) | b;
            this.alpha = a;
            return this;
        }

        /**
         * @return the color of the line
         * @since 1.9.0
         */
        public int getColor() {
            return color;
        }

        /**
         * @param alpha the alpha value for the line's color
         * @return self for chaining
         * @since 1.9.0
         */
        public Builder alpha(int alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * @return the alpha value of the line's color
         * @since 1.9.0
         */
        public int getAlpha() {
            return alpha;
        }

        /**
         * Creates the trace line for the given values and adds it to the draw3D
         *
         * @return the build line
         * @since 1.9.0
         */
        public TraceLine buildAndAdd() {
            TraceLine line = build();
            parent.addTraceLine(line);
            return line;
        }

        /**
         * Builds the line from the given values
         *
         * @return the build line
         * @since 1.9.0
         */
        public TraceLine build() {
            return new TraceLine(pos, color, alpha);
        }

    }

}