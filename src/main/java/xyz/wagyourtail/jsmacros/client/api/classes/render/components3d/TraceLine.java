package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos2D;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;

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

    public void render(DrawContext drawContext, BufferBuilder builder, float tickDelta) {
        MatrixStack matrixStack = drawContext.getMatrices();
        RenderSystem.disableDepthTest();

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        Matrix4f model = matrixStack.peek().getPositionMatrix();
        RenderSystem.lineWidth(2.5F);
        buf.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buf.vertex(screenPos.x, screenPos.y, -4.0).color(r, g, b, a).next();
        buf.vertex(model, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ()).color(r, g, b, a).next();
        tess.draw();

        RenderSystem.enableDepthTest();
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
