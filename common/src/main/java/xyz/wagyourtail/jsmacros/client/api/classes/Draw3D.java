package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@link Draw2D} is cool
 *
 * @author Wagyourtail
 * @since 1.0.6
 */
@SuppressWarnings("unused")
public class Draw3D {
    private final List<Box> boxes = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();

    private final List<Surface> surfaces = new ArrayList<>();

    /**
     * @return
     *
     * @since 1.0.6
     */
    public List<Box> getBoxes() {
        return ImmutableList.copyOf(boxes);
    }

    /**
     * @return
     *
     * @since 1.0.6
     */
    public List<Line> getLines() {
        return ImmutableList.copyOf(lines);
    }

    /**
     * @return
     *
     * @since 1.6.5
     */
    public List<Surface> getDraw2Ds() {
        return ImmutableList.copyOf(surfaces);
    }

    /**
     * @param box
     * @since 1.8.4
     */
    public void addBox(Box box) {
        synchronized (boxes) {
            boxes.add(box);
        }
    }

    /**
     * @param line
     * @since 1.8.4
     */
    public void addLine(Line line) {
        synchronized (lines) {
            lines.add(line);
        }
    }

    /**
     * @param surface
     * @since 1.8.4
     */
    public void addSurface(Surface surface) {
        synchronized (surfaces) {
            surfaces.add(surface);
        }
    }
    
    /**
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param fillColor
     * @param fill
     *
     * @return The {@link Box} you added.
     *
     * @since 1.0.6
     */
    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill) {
        return addBox(x1, y1, z1, x2, y2, z2, color, fillColor, fill, false);
    }

    /**
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param fillColor
     * @param fill
     * @param cull
     *
     * @return
     *
     * @since 1.3.1
     */
    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill, boolean cull) {
        Box b = new Box(x1, y1, z1, x2, y2, z2, color, fillColor, fill, cull);
        synchronized (boxes) {
            boxes.add(b);
        }
        return b;
    }

    /**
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param alpha
     * @param fillColor
     * @param fillAlpha
     * @param fill
     *
     * @return the {@link Box} you added.
     *
     * @since 1.1.8
     */
    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill) {
        return addBox(x1, y1, z1, x2, y2, z2, color, alpha, fillColor, fillAlpha, fill, false);
    }
    
    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill, boolean cull) {
        Box b = new Box(x1, y1, z1, x2, y2, z2, color, alpha, fillColor, fillAlpha, fill, cull);
        synchronized (boxes) {
            boxes.add(b);
        }
        return b;
    }

    /**
     * @param b
     *
     * @return
     *
     * @since 1.0.6
     */
    public Draw3D removeBox(Box b) {
        synchronized (boxes) {
            boxes.remove(b);
        }
        return this;
    }


    /**
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     *
     * @return the {@link Line} you added.
     *
     * @since 1.0.6
     */
    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        return addLine(x1, y1, z1, x2, y2, z2, color, false);
    }


    /**
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param cull
     *
     * @return
     *
     * @since 1.3.1
     */
    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, boolean cull) {
        Line l = new Line(x1, y1, z1, x2, y2, z2, color, cull);
        synchronized (lines) {
            lines.add(l);
        }
        return l;
    }

    /**
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param alpha
     *
     * @return the {@link Line} you added.
     *
     * @since 1.1.8
     */

    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha) {
        return addLine(x1, y1, z1, x2, y2, z2, color, alpha, false);
    }

    /**
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param alpha
     * @param cull
     *
     * @return
     *
     * @since 1.3.1
     */
    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, boolean cull) {
        Line l = new Line(x1, y1, z1, x2, y2, z2, color, alpha, cull);
        synchronized (lines) {
            lines.add(l);
        }
        return l;
    }

    /**
     * @param l
     *
     * @return
     *
     * @since 1.0.6
     */
    public Draw3D removeLine(Line l) {
        synchronized (lines) {
            lines.remove(l);
        }
        return this;
    }

    /**
     * Draws a cube({@link Box}) with a specific radius({@code side length = 2*radius})
     *
     * @param point the center point
     * @param radius 1/2 of the side length of the cube
     * @param color point color
     *
     * @return the {@link Box} generated, and visualized
     *
     * @see Box
     * @since 1.4.0
     */
    public Box addPoint(PositionCommon.Pos3D point, double radius, int color) {
        return addPoint(point.getX(), point.getY(), point.getZ(), radius, color);
    }

    /**
     * Draws a cube({@link Box}) with a specific radius({@code side length = 2*radius})
     *
     * @param x x value of the center point
     * @param y y value of the center point
     * @param z z value of the center point
     * @param radius 1/2 of the side length of the cube
     * @param color point color
     *
     * @return the {@link Box} generated, and visualized
     *
     * @see Box
     * @since 1.4.0
     */
    public Box addPoint(double x, double y, double z, double radius, int color) {
        return addBox(
            x - radius,
            y - radius,
            z - radius,
            x + radius,
            y + radius,
            z + radius,
            color,
            color,
            true,
            false
        );
    }

    /**
     * Draws a cube({@link Box}) with a specific radius({@code side length = 2*radius})
     *
     * @param x x value of the center point
     * @param y y value of the center point
     * @param z z value of the center point
     * @param radius 1/2 of the side length of the cube
     * @param color point color
     * @param alpha alpha of the point
     * @param cull whether to cull the point or not
     *
     * @return the {@link Box} generated, and visualized
     *
     * @see Box
     * @since 1.4.0
     */
    public Box addPoint(double x, double y, double z, double radius, int color, int alpha, boolean cull) {
        return addBox(
            x - radius,
            y - radius,
            z - radius,
            x + radius,
            y + radius,
            z + radius,
            color,
            color,
            alpha,
            alpha,
            true,
            cull
        );
    }

    /**
     * @param x top left
     * @param y
     * @param z
     *
     * @since 1.6.5
     * @return
     */
    public Surface addDraw2D(double x, double y, double z) {
        return addDraw2D(x, y, z, 0, 0, 0, 1, 1, 200, false, false);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param width
     * @param height
     *
     * @since 1.6.5
     * @return
     */
    public Surface addDraw2D(double x, double y, double z, double width, double height) {
        return addDraw2D(x, y, z, 0, 0, 0, width, height, 200, false, false);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param xRot
     * @param yRot
     * @param zRot
     *
     * @since 1.6.5
     * @return
     */
    public Surface addDraw2D(double x, double y, double z, double xRot, double yRot, double zRot) {
        return addDraw2D(x, y, z, xRot, yRot, zRot, 1, 1, 200, false, false);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param xRot
     * @param yRot
     * @param zRot
     * @param width
     * @param height
     *
     * @since 1.6.5
     * @return
     */
    public Surface addDraw2D(double x, double y, double z, double xRot, double yRot, double zRot, double width, double height) {
        return addDraw2D(x, y, z, xRot, yRot, zRot, width, height, 200, false, false);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param xRot
     * @param yRot
     * @param zRot
     * @param width
     * @param height
     * @param minSubdivisions
     *
     * @since 1.6.5
     * @return
     */
    public Surface addDraw2D(double x, double y, double z, double xRot, double yRot, double zRot, double width, double height, int minSubdivisions) {
        return addDraw2D(x, y, z, xRot, yRot, zRot, width, height, minSubdivisions, false, false);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param xRot
     * @param yRot
     * @param zRot
     * @param width
     * @param height
     * @param minSubdivisions
     * @param renderBack
     *
     * @since 1.6.5
     * @return
     */
    public Surface addDraw2D(double x, double y, double z, double xRot, double yRot, double zRot, double width, double height, int minSubdivisions, boolean renderBack) {
        return addDraw2D(x, y, z, xRot, yRot, zRot, width, height, minSubdivisions, renderBack, false);
    }

    /**
     * @param x top left
     * @param y
     * @param z
     * @param xRot
     * @param yRot
     * @param zRot
     * @param width
     * @param height
     * @param minSubdivisions
     * @param renderBack
     *
     * @since 1.6.5
     * @return
     */
    public Surface addDraw2D(double x, double y, double z, double xRot, double yRot, double zRot, double width, double height, int minSubdivisions, boolean renderBack, boolean cull) {
        Surface surface = new Surface(
            new PositionCommon.Pos3D(x, y, z),
            new PositionCommon.Pos3D(xRot, yRot, zRot),
            new PositionCommon.Pos2D(width, height),
            minSubdivisions,
            renderBack,
            cull
        );
        synchronized (surfaces) {
            this.surfaces.add(surface);
        }
        return surface;
    }

    /**
     * @since 1.6.5
     */
     public void removeDraw2D(Surface surface) {
        synchronized (this.surfaces) {
            this.surfaces.remove(surface);
        }
    }

    /**
     * @return a new {@link Box.Builder} instance.
     *
     * @since 1.8.4
     */
    public Box.Builder boxBuilder() {
        return new Box.Builder(this);
    }

    /**
     * @param pos the block position of the box
     * @return a new {@link Box.Builder} instance.
     *
     * @since 1.8.4
     */
    public Box.Builder boxBuilder(BlockPosHelper pos) {
        return new Box.Builder(this).forBlock(pos);
    }

    /**
     * @param x the x coordinate of the box
     * @param y the y coordinate of the box
     * @param z the z coordinate of the box
     * @return a new {@link Box.Builder} instance.
     *
     * @since 1.8.4
     */
    public Box.Builder boxBuilder(int x, int y, int z) {
        return new Box.Builder(this).forBlock(x, y, z);
    }

    /**
     * @return a new {@link Line.Builder} instance.
     *
     * @since 1.8.4
     */
    public Line.Builder lineBuilder() {
        return new Line.Builder(this);
    }

    /**
     * @return a new {@link Surface.Builder} instance.
     *
     * @since 1.8.4
     */
    public Surface.Builder surfaceBuilder() {
        return new Surface.Builder(this);
    }
    
    /**
     * register so it actually shows up
     *
     * @return self for chaining
     *
     * @since 1.6.5
     */
    public Draw3D register() {
        FHud.renders.add(this);
        return this;
    }

    /**
     * @return self for chaining
     *
     * @since 1.6.5
     */
    public Draw3D unregister() {
        FHud.renders.remove(this);
        return this;
    }

    public void render(MatrixStack matrixStack, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();

        matrixStack.push();
        //setup
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);


        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        matrixStack.translate(-camPos.x, -camPos.y, -camPos.z);

        //render
        synchronized (boxes) {
            for (Box b : boxes) {
                b.render(matrixStack);
            }
        }

        synchronized (lines) {
            for (Line l : lines) {
                l.render(matrixStack);
            }
        }

        synchronized (surfaces) {
            for (Surface s : surfaces) {
                s.render3D(matrixStack, 0, 0, tickDelta);
            }
        }

        //reset
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableTexture();

        matrixStack.pop();

    }

    /**
     * @author Wagyourtail
     */
    @SuppressWarnings("unused")
    public static class Box {
        public PositionCommon.Vec3D pos;
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
    
        public void render(MatrixStack matrixStack) {
            final boolean cull = !this.cull;
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
    
            float x1 = (float) pos.x1;
            float y1 = (float) pos.y1;
            float z1 = (float) pos.z1;
            float x2 = (float) pos.x2;
            float y2 = (float) pos.y2;
            float z2 = (float) pos.z2;
    
            if (cull) {
                RenderSystem.disableDepthTest();
            }
    
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
    
            Matrix4f matrix = matrixStack.peek().getPositionMatrix();
    
            if (this.fill) {
                float fa = ((fillColor >> 24) & 0xFF) / 255F;
                float fr = ((fillColor >> 16) & 0xFF) / 255F;
                float fg = ((fillColor >> 8) & 0xFF) / 255F;
                float fb = (fillColor & 0xFF) / 255F;
    
                //1.15+ culls insides
                RenderSystem.disableCull();
    
                buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
    
                //draw a cube using triangle strips
                buf.vertex(matrix, x1, y2, z2).color(fr, fg, fb, fa).next(); // Front-top-left
                buf.vertex(matrix, x2, y2, z2).color(fr, fg, fb, fa).next(); // Front-top-right
                buf.vertex(matrix, x1, y1, z2).color(fr, fg, fb, fa).next(); // Front-bottom-left
                buf.vertex(matrix, x2, y1, z2).color(fr, fg, fb, fa).next(); // Front-bottom-right
                buf.vertex(matrix, x2, y1, z1).color(fr, fg, fb, fa).next(); // Front-bottom-left
                buf.vertex(matrix, x2, y2, z2).color(fr, fg, fb, fa).next(); // Front-top-right
                buf.vertex(matrix, x2, y2, z1).color(fr, fg, fb, fa).next(); // Back-top-right
                buf.vertex(matrix, x1, y2, z2).color(fr, fg, fb, fa).next(); // Front-top-left
                buf.vertex(matrix, x1, y2, z1).color(fr, fg, fb, fa).next(); // Back-top-left
                buf.vertex(matrix, x1, y1, z2).color(fr, fg, fb, fa).next(); // Front-bottom-left
                buf.vertex(matrix, x1, y1, z1).color(fr, fg, fb, fa).next(); // Back-bottom-left
                buf.vertex(matrix, x2, y1, z1).color(fr, fg, fb, fa).next(); // Back-bottom-right
                buf.vertex(matrix, x1, y2, z1).color(fr, fg, fb, fa).next(); // Back-top-left
                buf.vertex(matrix, x2, y2, z1).color(fr, fg, fb, fa).next(); // Back-top-right
    
                tess.draw();
    
                RenderSystem.enableCull();
            }
    
            RenderSystem.lineWidth(2.5F);
            buf.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
    
            buf.vertex(matrix, x1, y1, z1).color(r, g, b, a).next();
            buf.vertex(matrix, x1, y1, z2).color(r, g, b, a).next();
    
            buf.vertex(matrix, x2, y1, z2).color(r, g, b, a).next();
    
            buf.vertex(matrix, x2, y1, z1).color(r, g, b, a).next();
    
            buf.vertex(matrix, x1, y1, z1).color(r, g, b, a).next();
    
            buf.vertex(matrix, x1, y2, z1).color(r, g, b, a).next();
    
            buf.vertex(matrix, x1, y2, z2).color(r, g, b, a).next();
    
            buf.vertex(matrix, x2, y2, z2).color(r, g, b, a).next();
    
            buf.vertex(matrix, x2, y2, z1).color(r, g, b, a).next();
    
            buf.vertex(matrix, x1, y2, z1).color(r, g, b, a).next();
    
            buf.vertex(matrix, x1, y2, z1).color(r, g, b, 0).next();
            buf.vertex(matrix, x2, y1, z1).color(r, g, b, 0).next();
    
            buf.vertex(matrix, x2, y1, z1).color(r, g, b, a).next();
            buf.vertex(matrix, x2, y2, z1).color(r, g, b, a).next();
    
            buf.vertex(matrix, x2, y2, z1).color(r, g, b, 0).next();
            buf.vertex(matrix, x1, y1, z2).color(r, g, b, 0).next();
    
            buf.vertex(matrix, x1, y1, z2).color(r, g, b, a).next();
            buf.vertex(matrix, x1, y2, z2).color(r, g, b, a).next();
    
            buf.vertex(matrix, x1, y2, z2).color(r, g, b, 0).next();
            buf.vertex(matrix, x2, y1, z2).color(r, g, b, 0).next();
    
            buf.vertex(matrix, x2, y1, z2).color(r, g, b, a).next();
            buf.vertex(matrix, x2, y2, z2).color(r, g, b, a).next();
    
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
             *
             * @since 1.8.4
             */
            public Builder pos1(PositionCommon.Pos3D pos1) {
                this.pos1 = pos1;
                return this;
            }
    
            /**
             * @param pos1 the first position of the box
             * @return self for chaining.
             *
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
             *
             * @since 1.8.4
             */
            public Builder pos1(double x1, double y1, double z1) {
                this.pos1 = new PositionCommon.Pos3D(x1, y1, z1);
                return this;
            }
    
            /**
             * @return the first position of the box.
             *
             * @since 1.8.4
             */
            public PositionCommon.Pos3D getPos1() {
                return pos1;
            }
    
            /**
             * @param pos2 the second position of the box
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos2(PositionCommon.Pos3D pos2) {
                this.pos2 = pos2;
                return this;
            }
    
            /**
             * @param pos2 the second position of the box
             * @return self for chaining.
             *
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
             *
             * @since 1.8.4
             */
            public Builder pos2(double x2, double y2, double z2) {
                this.pos2 = new PositionCommon.Pos3D(x2, y2, z2);
                return this;
            }
    
            /**
             * @return the second position of the box.
             *
             * @since 1.8.4
             */
            public PositionCommon.Pos3D getPos2() {
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
             *
             * @since 1.8.4
             */
            public Builder pos(double x1, double y1, double z1, double x2, double y2, double z2) {
                this.pos1 = new PositionCommon.Pos3D(x1, y1, z1);
                this.pos2 = new PositionCommon.Pos3D(x2, y2, z2);
                return this;
            }
    
            /**
             * @param pos1 the first position of the box
             * @param pos2 the second position of the box
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
             * @param pos1 the first position of the box
             * @param pos2 the second position of the box
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
             * Highlights the given block position.
             *
             * @param x the x coordinate of the block
             * @param y the y coordinate of the block
             * @param z the z coordinate of the block
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder forBlock(int x, int y, int z) {
                this.pos1 = new PositionCommon.Pos3D(x, y, z);
                this.pos2 = new PositionCommon.Pos3D(x + 1, y + 1, z + 1);
                return this;
            }
    
            /**
             * Highlights the given block position.
             *
             * @param pos the block position
             * @return self for chaining.
             *
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
             *
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
             *
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
             *
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
             *
             * @since 1.8.4
             */
            public Builder color(int r, int g, int b, int a) {
                this.color = (r << 16) | (g << 8) | b;
                this.alpha = a;
                return this;
            }
    
            /**
             * @return the color of the box.
             *
             * @since 1.8.4
             */
            public int getColor() {
                return color;
            }
    
            /**
             * @param alpha the alpha value for the box's color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder alpha(int alpha) {
                this.alpha = alpha;
                return this;
            }
    
            /**
             * @return the alpha value of the box's color.
             *
             * @since 1.8.4
             */
            public int getAlpha() {
                return alpha;
            }
    
            /**
             * @param fillColor the fill color of the box
             * @return self for chaining.
             *
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
             *
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
             *
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
             *
             * @since 1.8.4
             */
            public Builder fillColor(int r, int g, int b, int a) {
                this.fillColor = (r << 16) | (g << 8) | b;
                this.fillAlpha = a;
                return this;
            }
    
            /**
             * @return the fill color of the box.
             *
             * @since 1.8.4
             */
            public int getFillColor() {
                return fillColor;
            }
    
            /**
             * @param fillAlpha the alpha value for the box's fill color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder fillAlpha(int fillAlpha) {
                this.fillAlpha = fillAlpha;
                return this;
            }
    
            /**
             * @return the alpha value of the box's fill color.
             *
             * @since 1.8.4
             */
            public int getFillAlpha() {
                return fillAlpha;
            }
    
            /**
             * @param fill {@code true} if the box should be filled, {@code false} otherwise
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder fill(boolean fill) {
                this.fill = fill;
                return this;
            }
    
            /**
             * @return {@code true} if the box should be filled, {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean isFilled() {
                return fill;
            }
    
            /**
             * @param cull whether to enable culling or not
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder cull(boolean cull) {
                this.cull = cull;
                return this;
            }
    
            /**
             * @return {@code true} if culling is enabled for this box, {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean isCulled() {
                return cull;
            }
    
            /**
             * Creates the box for the given values and adds it to the draw3D.
             *
             * @return the build box.
             *
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
                return new Box(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, color, alpha, fillColor, fillAlpha, fill, cull);
            }
        }
    }

    /**
     * @author Wagyourtail
     */
    @SuppressWarnings("unused")
    public static class Line {
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

    /**
     * @author Wagyourtail
     * @since 1.6.5
     */
    @SuppressWarnings("unused")
    public static class Surface extends Draw2D implements RenderCommon.RenderElement {
        public boolean rotateToPlayer;
        public boolean rotateCenter;
        public EntityHelper<?> boundEntity;
        public PositionCommon.Pos3D boundOffset;
        public final PositionCommon.Pos3D pos;
        public final PositionCommon.Pos3D rotations;
        protected final PositionCommon.Pos2D sizes;
        protected int minSubdivisions;
    
        protected double scale;
        /**
         * scale that zIndex is multiplied by to get the actual offset (in blocks) for rendering
         * default: {@code 1/1000} if there is still z-fighting, increase this value
         *
         * @since 1.6.5
         */
        public double zIndexScale = 0.001;
        public boolean renderBack;
        public boolean cull;
    
        public Surface(PositionCommon.Pos3D pos, PositionCommon.Pos3D rotations, PositionCommon.Pos2D sizes, int minSubdivisions, boolean renderBack, boolean cull) {
            this.pos = pos;
            this.rotations = rotations;
            this.sizes = sizes;
            this.minSubdivisions = minSubdivisions;
            this.renderBack = renderBack;
            this.cull = cull;
            init();
        }

        /**
         * @param pos the position of the surface
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Surface setPos(PositionCommon.Pos3D pos) {
            this.pos.x = pos.x;
            this.pos.y = pos.y;
            this.pos.z = pos.z;
            return this;
        }

        /**
         * @param pos the position of the surface
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Surface setPos(BlockPosHelper pos) {
            this.pos.x = pos.getX();
            this.pos.y = pos.getY();
            this.pos.z = pos.getZ();
            return this;
        }
        
        public Surface setPos(double x, double y, double z) {
            this.pos.x = x;
            this.pos.y = y;
            this.pos.z = z;
            return this;
        }

        /**
         * The surface will move with the entity at the offset location.
         *
         * @param boundEntity the entity to bind the surface to
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Surface bindToEntity(EntityHelper<?> boundEntity) {
            this.boundEntity = boundEntity;
            return this;
        }

        /**
         * @return the entity the surface is bound to, or {@code null} if it is not bound to an
         *         entity.
         *
         * @since 1.8.4
         */
        public EntityHelper<?> getBoundEntity() {
            return boundEntity;
        }

        /**
         * @param boundOffset the offset from the entity's position to render the surface at
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Surface setBoundOffset(PositionCommon.Pos3D boundOffset) {
            this.boundOffset = boundOffset;
            return this;
        }

        /**
         * @param x the x offset from the entity's position to render the surface at
         * @param y the y offset from the entity's position to render the surface at
         * @param z the z offset from the entity's position to render the surface at
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Surface setBoundOffset(double x, double y, double z) {
            this.boundOffset = new PositionCommon.Pos3D(x, y, z);
            return this;
        }

        /**
         * @return the offset from the entity's position to render the surface at.
         *
         * @since 1.8.4
         */
        public PositionCommon.Pos3D getBoundOffset() {
            return boundOffset;
        }

        /**
         * @param rotateToPlayer whether to rotate the surface to face the player or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Surface setRotateToPlayer(boolean rotateToPlayer) {
            this.rotateToPlayer = rotateToPlayer;
            return this;
        }

        /**
         * @return {@code true} if the surface should be rotated to face the player, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean doesRotateToPlayer() {
            return rotateToPlayer;
        }
        
        public void setRotations(double x, double y, double z) {
            this.rotations.x = x;
            this.rotations.y = y;
            this.rotations.z = z;
        }
    
        public void setSizes(double x, double y) {
            this.sizes.x = x;
            this.sizes.y = y;
            init();
        }
    
        public PositionCommon.Pos2D getSizes() {
            return sizes.add(0, 0);
        }
    
        public void setMinSubdivisions(int minSubdivisions) {
            this.minSubdivisions = minSubdivisions;
            init();
        }
    
        public int getMinSubdivisions() {
            return minSubdivisions;
        }
    
        @Override
        public int getHeight() {
            return (int) (sizes.y / scale);
        }
    
        @Override
        public int getWidth() {
            return (int) (sizes.x / scale);
        }

        /**
         * @param rotateCenter whether to rotate the surface around its center or not
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Surface setRotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this surface is rotated around it's center, {@code false}
         *         otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }
        
        @Override
        public void init() {
            scale = Math.min(sizes.x, sizes.y) / minSubdivisions;
            super.init();
        }

        @Override
        public int getZIndex() {
            return 0;
        }

        @Override
        public void render3D(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
            matrixStack.push();
            if (boundEntity != null && boundEntity.isAlive()) {
                PositionCommon.Pos3D entityPos = boundEntity.getPos().add(boundOffset);
                pos.x += (entityPos.x - pos.x) * delta;
                pos.y += (entityPos.y - pos.y) * delta;
                pos.z += (entityPos.z - pos.z) * delta;
            }

            matrixStack.translate(pos.x, pos.y, pos.z);

            if (rotateToPlayer) {
                Vector3f rot = MinecraftClient.getInstance().gameRenderer.getCamera().getRotation().getEulerAnglesXYZ(new Vector3f());
                rotations.x = -rot.x();
                rotations.y = 180 + rot.y();
                rotations.z = 0;
            }
            if (rotateCenter) {
                matrixStack.translate(sizes.x / 2, 0, 0);
                matrixStack.multiply(new Quaternionf().rotateLocalY((float) rotations.y));
                matrixStack.translate(-sizes.x / 2, 0, 0);
                matrixStack.translate(0, -sizes.y / 2, 0);
                matrixStack.multiply(new Quaternionf().rotateLocalX((float) rotations.x));
                matrixStack.translate(0, sizes.y / 2, 0);
                matrixStack.translate(sizes.x / 2, -sizes.y / 2, 0);
                matrixStack.multiply(new Quaternionf().rotateLocalZ((float) rotations.z));
                matrixStack.translate(-sizes.x / 2, sizes.y / 2, 0);
            } else {
                Quaternionf q = new Quaternionf();
                q.rotateLocalY((float) rotations.y);
                q.rotateLocalX((float) rotations.x);
                q.rotateLocalZ((float) rotations.z);
                matrixStack.multiply(q);
            }
            // fix it so that y-axis goes down instead of up
            matrixStack.scale(1, -1, 1);
            // scale so that x or y have minSubdivisions units between them
            matrixStack.scale((float) scale, (float) scale, (float) scale);

            synchronized (elements) {
                renderElements3D(matrixStack, getElementsByZIndex());
            }
            matrixStack.pop();
    
            if (!cull) {
                RenderSystem.enableDepthTest();
            }
            if (renderBack) {
                RenderSystem.enableCull();
            }
        }
    
        private void renderElements3D(MatrixStack matrixStack, Iterator<RenderCommon.RenderElement> iter) {
            while (iter.hasNext()) {
                RenderCommon.RenderElement element = iter.next();
                // Render each draw2D element individually so that the cull and renderBack settings are used
                if (element instanceof RenderCommon.Draw2DElement draw2DElement) {
                    renderDraw2D3D(matrixStack, draw2DElement);
                } else {
                    renderElement3D(matrixStack, element);
                }
            }
        }
    
        private void renderDraw2D3D(MatrixStack matrixStack, RenderCommon.Draw2DElement draw2DElement) {
            matrixStack.push();
            matrixStack.translate(draw2DElement.x, draw2DElement.y, 0);
            matrixStack.scale(draw2DElement.scale, draw2DElement.scale, 1);
            if (rotateCenter) {
                matrixStack.translate(draw2DElement.width.getAsInt() / 2d, draw2DElement.height.getAsInt() / 2d, 0);
            }
            matrixStack.multiply(new Quaternionf().rotateLocalZ(draw2DElement.rotation));
            if (rotateCenter) {
                matrixStack.translate(-draw2DElement.width.getAsInt() / 2d, -draw2DElement.height.getAsInt() / 2d, 0);
            }
            // Don't translate back!
            Draw2D draw2D = draw2DElement.getDraw2D();
            synchronized (draw2D.getElements()) {
                renderElements3D(matrixStack, draw2D.getElementsByZIndex());
            }
            matrixStack.pop();
        }
    
        private void renderElement3D(MatrixStack matrixStack, RenderCommon.RenderElement element) {
            if (renderBack) {
                RenderSystem.disableCull();
            } else {
                RenderSystem.enableCull();
            }
            if (!cull) {
                RenderSystem.disableDepthTest();
            } else {
                RenderSystem.enableDepthTest();
            }
            matrixStack.push();
            matrixStack.translate(0, 0, zIndexScale * element.getZIndex());
            element.render3D(matrixStack, 0, 0, 0);
            matrixStack.pop();
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        }

        /**
         * @author Etheradon
         * @since 1.8.4
         */
        public static class Builder {
            private final Draw3D parent;
    
            private PositionCommon.Pos3D pos = new PositionCommon.Pos3D(0, 0, 0);
            private EntityHelper<?> boundEntity;
            private PositionCommon.Pos3D boundOffset = PositionCommon.Pos3D.ZERO;
            private double xRot = 0;
            private double yRot = 0;
            private double zRot = 0;
            private boolean rotateCenter = true;
            private boolean rotateToPlayer = false;
            private double width = 10;
            private double height = 10;
            private int minSubdivisions = 1;
            private double zIndexScale = 0.001;
            private boolean renderBack = true;
            private boolean cull = false;
    
            public Builder(Draw3D parent) {
                this.parent = parent;
            }
    
            /**
             * @param pos the position of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos(PositionCommon.Pos3D pos) {
                this.pos = pos;
                return this;
            }
    
            /**
             * @param pos the position of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos(BlockPosHelper pos) {
                this.pos = pos.toPos3D();
                return this;
            }
    
            /**
             * @param x the x position of the surface
             * @param y the y position of the surface
             * @param z the z position of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos(double x, double y, double z) {
                this.pos = new PositionCommon.Pos3D(x, y, z);
                return this;
            }
    
            /**
             * @return the position of the surface.
             *
             * @since 1.8.4
             */
            public PositionCommon.Pos3D getPos() {
                return pos;
            }

            /**
             * The surface will move with the entity at the offset location.
             *
             * @param boundEntity the entity to bind the surface to
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder bindToEntity(EntityHelper<?> boundEntity) {
                this.boundEntity = boundEntity;
                return this;
            }

            /**
             * @return the entity the surface is bound to, or {@code null} if it is not bound to an
             *         entity.
             *
             * @since 1.8.4
             */
            public EntityHelper<?> getBoundEntity() {
                return boundEntity;
            }

            /**
             * @param entityOffset the offset from the entity's position to render the surface at
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder boundOffset(PositionCommon.Pos3D entityOffset) {
                this.boundOffset = entityOffset;
                return this;
            }

            /**
             * @param x the x offset from the entity's position to render the surface at
             * @param y the y offset from the entity's position to render the surface at
             * @param z the z offset from the entity's position to render the surface at
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder boundOffset(double x, double y, double z) {
                this.boundOffset = new PositionCommon.Pos3D(x, y, z);
                return this;
            }

            /**
             * @return the offset from the entity's position to render the surface at.
             *
             * @since 1.8.4
             */
            public PositionCommon.Pos3D getBoundOffset() {
                return boundOffset;
            }
            
            /**
             * @param xRot the x rotation of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder xRotation(double xRot) {
                this.xRot = xRot;
                return this;
            }
    
            /**
             * @return the x rotation of the surface.
             *
             * @since 1.8.4
             */
            public double getXRotation() {
                return xRot;
            }
    
            /**
             * @param yRot the y rotation of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder yRotation(double yRot) {
                this.yRot = yRot;
                return this;
            }
    
            /**
             * @return the y rotation of the surface.
             *
             * @since 1.8.4
             */
            public double getYRotation() {
                return yRot;
            }
    
            /**
             * @param zRot the z rotation of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder zRotation(double zRot) {
                this.zRot = zRot;
                return this;
            }
    
            /**
             * @return the z rotation of the surface.
             *
             * @since 1.8.4
             */
            public double getZRotation() {
                return zRot;
            }
    
            /**
             * @param xRot the x rotation of the surface
             * @param yRot the y rotation of the surface
             * @param zRot the z rotation of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(double xRot, double yRot, double zRot) {
                this.xRot = xRot;
                this.yRot = yRot;
                this.zRot = zRot;
                return this;
            }

            /**
             * @param rotateCenter whether to rotate around the center of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotateCenter(boolean rotateCenter) {
                this.rotateCenter = rotateCenter;
                return this;
            }

            /**
             * @return {@code true} if this surface should be rotated around its center,
             *         {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean isRotatingCenter() {
                return rotateCenter;
            }

            /**
             * @param rotateToPlayer whether to rotate the surface to face the player or not
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotateToPlayer(boolean rotateToPlayer) {
                this.rotateToPlayer = rotateToPlayer;
                return this;
            }

            /**
             * @return {@code true} if the surface should be rotated to face the player,
             *         {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean doesRotateToPlayer() {
                return rotateToPlayer;
            }
            
            /**
             * @param width the width of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder width(double width) {
                this.width = width;
                return this;
            }
    
            /**
             * @return the width of the surface.
             *
             * @since 1.8.4
             */
            public double getWidth() {
                return width;
            }
    
            /**
             * @param height the height of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder height(double height) {
                this.height = height;
                return this;
            }
    
            /**
             * @return the height of the surface.
             *
             * @since 1.8.4
             */
            public double getHeight() {
                return height;
            }
    
            /**
             * @param width  the width of the surface
             * @param height the height of the surface
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder size(double width, double height) {
                this.width = width;
                this.height = height;
                return this;
            }
    
            /**
             * @param minSubdivisions the minimum number of subdivisions
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder minSubdivisions(int minSubdivisions) {
                this.minSubdivisions = minSubdivisions;
                return this;
            }
    
            /**
             * @return the minimum number of subdivisions.
             *
             * @since 1.8.4
             */
            public int getMinSubdivisions() {
                return minSubdivisions;
            }
    
            /**
             * @param renderBack whether the back of the surface should be rendered or not
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder renderBack(boolean renderBack) {
                this.renderBack = renderBack;
                return this;
            }
    
            /**
             * @return {@code true} if the back of the surface should be rendered, {@code false}
             *         otherwise.
             *
             * @since 1.8.4
             */
            public boolean shouldRenderBack() {
                return renderBack;
            }
    
            /**
             * @param cull whether to enable culling or not
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder cull(boolean cull) {
                this.cull = cull;
                return this;
            }
    
            /**
             * @return {@code true} if culling is enabled for this box, {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean isCulled() {
                return cull;
            }
    
            /**
             * @param zIndexScale the scale of the z-index
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder zIndex(double zIndexScale) {
                this.zIndexScale = zIndexScale;
                return this;
            }
    
            /**
             * @return the scale of the z-index.
             *
             * @since 1.8.4
             */
            public double getZIndexScale() {
                return zIndexScale;
            }
    
            /**
             * Creates the surface for the given values and adds it to the draw3D.
             *
             * @return the build surface.
             *
             * @since 1.8.4
             */
            public Surface buildAndAdd() {
                Surface surface = build();
                parent.addSurface(surface);
                return surface;
            }
    
            /**
             * Builds the surface from the given values.
             *
             * @return the build surface.
             */
            public Surface build() {
                Surface surface = new Surface(pos, new PositionCommon.Pos3D(xRot, yRot, zRot), new PositionCommon.Pos2D(width, height), minSubdivisions, renderBack, cull)
                        .setRotateCenter(rotateCenter)
                        .setRotateToPlayer(rotateToPlayer)
                        .bindToEntity(boundEntity)
                        .setBoundOffset(boundOffset);
                return surface;
            }
    
        }
    }
}