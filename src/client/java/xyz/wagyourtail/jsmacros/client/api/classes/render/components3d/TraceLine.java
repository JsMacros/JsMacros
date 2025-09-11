package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import xyz.wagyourtail.jsmacros.api.math.Pos2D;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;

import java.lang.reflect.Field;
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
    private static final Field lineDepthTestFunction;
    private static final DepthTestFunction oldlineDepthTestFunction;

    static {
        try {
            lineDepthTestFunction = RenderPipelines.LINES.getClass().getDeclaredField("depthTestFunction");
            lineDepthTestFunction.setAccessible(true);
            oldlineDepthTestFunction = (DepthTestFunction) lineDepthTestFunction.get(RenderPipelines.LINES);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("JS-Macros 3D Rendering failed to reflect into RenderLayer for TraceLine", e);
        }
    }
    public Pos2D screenPos = new Pos2D(0.0, 0.0);
    public Pos3D pos;
    public int color;

    public TraceLine(double x, double y, double z, int color) {
        setPos(x, y, z).setColor(color);
    }

    public TraceLine(double x, double y, double z, int color, int alpha) {
        setPos(x, y, z).setColor(color, alpha);
    }

    public TraceLine(Pos3D pos, int color) {
        setPos(pos).setColor(color);
    }

    public TraceLine(Pos3D pos, int color, int alpha) {
        setPos(pos).setColor(color, alpha);
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setPos(double x, double y, double z) {
        pos = new Pos3D(x, y, z);
        return this;
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setPos(Pos3D pos) {
        this.pos = pos;
        return this;
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setColor(int color) {
        if (color <= 0xFFFFFF) {
            color = color | 0xFF000000;
        }
        this.color = color;
        return this;
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setColor(int color, int alpha) {
        this.color = (alpha << 24) | (color & 0xFFFFFF);
        return this;
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public TraceLine setAlpha(int alpha) {
        return setColor(color, alpha);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TraceLine traceLine = (TraceLine) o;
        return Objects.equals(pos, traceLine.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }

    @Override
    public int compareToSame(TraceLine other) {
        return pos.compareTo(other.pos);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider consumers, float tickDelta) {
        VertexConsumerProvider.Immediate immediate = (VertexConsumerProvider.Immediate) consumers;

        try {
            lineDepthTestFunction.set(RenderPipelines.LINES, DepthTestFunction.NO_DEPTH_TEST);

            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            Vec3d cameraPos = camera.getPos();

            float pitch = (float)Math.toRadians(camera.getPitch());
            float yaw = (float)Math.toRadians(camera.getYaw());

            float x = -((float) (Math.sin(yaw) * Math.cos(pitch)));
            float y = -((float) Math.sin(pitch));
            float z = (float) (Math.cos(yaw) * Math.cos(pitch));
            Vec3d lookVec = new Vec3d(x, y, z);

            Vec3d start = cameraPos.add(lookVec.multiply(0.05));

            VertexConsumer buffer = immediate.getBuffer(RenderLayer.getLines());
            MatrixStack.Entry matrices = matrixStack.peek();
            Matrix4f positionMatrix = matrices.getPositionMatrix();

            float a = ((color >> 24) & 0xFF) / 255.0F;
            float r = ((color >> 16) & 0xFF) / 255.0F;
            float g = ((color >> 8) & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;

            buffer.vertex(positionMatrix, (float) start.x, (float) start.y, (float) start.z).color(r, g, b, a).normal(matrices, 0, 1, 0);
            buffer.vertex(positionMatrix, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ()).color(r, g, b, a).normal(matrices, 0, 1, 0);
            buffer.vertex(positionMatrix, (float) start.x, (float) start.y, (float) start.z).color(r, g, b, a).normal(matrices, 1, 0, 0);
            buffer.vertex(positionMatrix, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ()).color(r, g, b, a).normal(matrices, 1, 0, 0);
            immediate.draw();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                // Restore the original state AFTER drawing.
                lineDepthTestFunction.set(RenderPipelines.LINES, oldlineDepthTestFunction);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Builder {
        private final Draw3D parent;

        public Pos2D screenPos = new Pos2D(0.0, 0.0);
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
            TraceLine line = new TraceLine(pos, color, alpha);
            line.screenPos = screenPos;
            return line;
        }

    }

}