package xyz.wagyourtail.jsmacros.client.api.classes.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;

import java.util.ArrayList;
import java.util.Comparator;
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
     * @see Draw3D.Box
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
     * @see Draw3D.Box
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
     * @see Draw3D.Box
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
     * @return
     *
     * @since 1.8.4
     */
    public Box.Builder getBoxBuilder() {
        return new Box.Builder(this);
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public Line.Builder getLineBuilder() {
        return new Line.Builder(this);
    }

    /**
     * @return
     *
     * @since 1.8.4
     */
    public Surface.Builder getSurfaceBuilder() {
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

    public void render(MatrixStack matrixStack) {
        MinecraftClient mc = MinecraftClient.getInstance();

        matrixStack.push();
        //setup
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);


        Vec3d camPos = mc.gameRenderer.getCamera().getPos();

        // offsetRender
        //        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.wrapDegrees(camera.getPitch())));
        //        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180F));
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
                s.render3D(matrixStack, 0, 0, 0);
            }
        }

        //reset
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableTexture();

        matrixStack.pop();

    }

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
         *
         * @since 1.0.6
         */
        public void setPos(double x1, double y1, double z1, double x2, double y2, double z2) {
            pos = new PositionCommon.Vec3D(x1, y1, z1, x2, y2, z2);
        }


        /**
         * @param color
         *
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
         *
         * @since 1.0.6
         */
        public void setFillColor(int fillColor) {
            this.fillColor = fillColor;
        }

        /**
         * @param color
         * @param alpha
         *
         * @since 1.1.8
         */
        public void setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
        }

        /**
         * @param alpha
         *
         * @since 1.1.8
         */
        public void setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
        }

        /**
         * @param fillColor
         * @param alpha
         *
         * @since 1.1.8
         */
        public void setFillColor(int fillColor, int alpha) {
            this.fillColor = fillColor | (alpha << 24);
        }

        /**
         * @param alpha
         *
         * @since 1.1.8
         */
        public void setFillAlpha(int alpha) {
            this.fillColor = (fillColor & 0xFFFFFF) | (alpha << 24);
        }

        /**
         * @param fill
         *
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

        public static class Builder {

            private final Draw3D parent;

            private PositionCommon.Pos3D pos1 = new PositionCommon.Pos3D(0, 0, 0);
            private PositionCommon.Pos3D pos2 = new PositionCommon.Pos3D(0, 0, 0);
            private int color = 0xFFFFFF;
            private int fillColor = 0xFFFFFF;
            private int alpha = 255;
            private int fillAlpha = 0;
            private boolean fill = false;
            private boolean cull = false;

            public Builder(Draw3D parent) {
                this.parent = parent;
            }

            public PositionCommon.Pos3D getPos1() {
                return pos1;
            }

            public Builder pos1(PositionCommon.Pos3D pos1) {
                this.pos1 = pos1;
                return this;
            }

            public Builder pos1(BlockPosHelper pos1) {
                this.pos1 = pos1.toPos3D();
                return this;
            }

            public Builder pos1(double x1, double y1, double z1) {
                this.pos1 = new PositionCommon.Pos3D(x1, y1, z1);
                return this;
            }

            public PositionCommon.Pos3D getPos2() {
                return pos2;
            }

            public Builder pos2(PositionCommon.Pos3D pos2) {
                this.pos2 = pos2;
                return this;
            }

            public Builder pos2(BlockPosHelper pos2) {
                this.pos2 = pos2.toPos3D();
                return this;
            }

            public Builder pos2(int x2, int y2, int z2) {
                this.pos2 = new PositionCommon.Pos3D(x2, y2, z2);
                return this;
            }

            public Builder pos(int x1, int y1, int z1, int x2, int y2, int z2) {
                this.pos1 = new PositionCommon.Pos3D(x1, y1, z1);
                this.pos2 = new PositionCommon.Pos3D(x2, y2, z2);
                return this;
            }

            public Builder pos(BlockPosHelper pos1, BlockPosHelper pos2) {
                this.pos1 = pos1.toPos3D();
                this.pos2 = pos2.toPos3D();
                return this;
            }

            public Builder pos(PositionCommon.Pos3D pos1, PositionCommon.Pos3D pos2) {
                this.pos1 = pos1;
                this.pos2 = pos2;
                return this;
            }

            public Builder forBlock(int x, int y, int z) {
                this.pos1 = new PositionCommon.Pos3D(x, y, z);
                this.pos2 = new PositionCommon.Pos3D(x + 1, y + 1, z + 1);
                return this;
            }

            public Builder forBlock(BlockPosHelper pos) {
                this.pos1 = pos.toPos3D();
                this.pos2 = pos.offset(1, 1, 1).toPos3D();
                return this;
            }

            public Builder forBlock(PositionCommon.Pos3D pos1) {
                this.pos1 = pos1;
                this.pos2 = pos1.add(1, 1, 1);
                return this;
            }

            public int getColor() {
                return color;
            }

            public Builder color(int color) {
                this.color = color;
                return this;
            }

            public int getFillColor() {
                return fillColor;
            }

            public Builder fillColor(int fillColor) {
                this.fillColor = fillColor;
                return this;
            }

            public int getAlpha() {
                return alpha;
            }

            public Builder alpha(int alpha) {
                this.alpha = alpha;
                return this;
            }

            public int getFillAlpha() {
                return fillAlpha;
            }

            public Builder fillAlpha(int fillAlpha) {
                this.fillAlpha = fillAlpha;
                return this;
            }

            public boolean isFill() {
                return fill;
            }

            public Builder fill(boolean fill) {
                this.fill = fill;
                return this;
            }

            public boolean isCull() {
                return cull;
            }

            public Builder cull(boolean cull) {
                this.cull = cull;
                return this;
            }

            public Box build() {
                Box box = new Box(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, color, alpha, fillColor, fillAlpha, fill, cull);
                parent.addBox(box);
                return box;
            }
        }
        
    }

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
         *
         * @since 1.0.6
         */
        public void setPos(double x1, double y1, double z1, double x2, double y2, double z2) {
            pos = new PositionCommon.Vec3D(x1, y1, z1, x2, y2, z2);
        }

        /**
         * @param color
         *
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
         *
         * @since 1.1.8
         */
        public void setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
        }

        /**
         * @param alpha
         *
         * @since 1.1.8
         */
        public void setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
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

            public PositionCommon.Pos3D getPos1() {
                return pos1;
            }

            public Builder pos1(PositionCommon.Pos3D pos1) {
                this.pos1 = pos1;
                return this;
            }

            public Builder pos1(BlockPosHelper pos1) {
                this.pos1 = pos1.toPos3D();
                return this;
            }

            public Builder pos1(double x1, double y1, double z1) {
                this.pos1 = new PositionCommon.Pos3D(x1, y1, z1);
                return this;
            }

            public PositionCommon.Pos3D getPos2() {
                return pos2;
            }

            public Builder pos2(PositionCommon.Pos3D pos2) {
                this.pos2 = pos2;
                return this;
            }

            public Builder pos2(BlockPosHelper pos2) {
                this.pos2 = pos2.toPos3D();
                return this;
            }

            public Builder pos2(int x2, int y2, int z2) {
                this.pos2 = new PositionCommon.Pos3D(x2, y2, z2);
                return this;
            }

            public Builder pos(int x1, int y1, int z1, int x2, int y2, int z2) {
                this.pos1 = new PositionCommon.Pos3D(x1, y1, z1);
                this.pos2 = new PositionCommon.Pos3D(x2, y2, z2);
                return this;
            }

            public Builder pos(BlockPosHelper pos1, BlockPosHelper pos2) {
                this.pos1 = pos1.toPos3D();
                this.pos2 = pos2.toPos3D();
                return this;
            }

            public Builder pos(PositionCommon.Pos3D pos1, PositionCommon.Pos3D pos2) {
                this.pos1 = pos1;
                this.pos2 = pos2;
                return this;
            }

            public int getColor() {
                return color;
            }

            public Builder color(int color) {
                this.color = color;
                return this;
            }

            public int getAlpha() {
                return alpha;
            }

            public Builder alpha(int alpha) {
                this.alpha = alpha;
                return this;
            }

            public boolean isCull() {
                return cull;
            }

            public Builder cull(boolean cull) {
                this.cull = cull;
                return this;
            }

            public Line build() {
                Line line = new Line(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z, color, alpha, cull);
                parent.addLine(line);
                return line;
            }
        }
        
    }

    /**
     * @since 1.6.5
     */
    public static class Surface extends Draw2D {
        public final PositionCommon.Pos3D pos;
        public final PositionCommon.Pos3D rotations;
        protected final PositionCommon.Pos2D sizes;
        protected int minSubdivisions;

        protected double scale;
        /**
         * scale that zIndex is multiplied by to get the actual offset (in blocks) for rendering
         * default: {@code 1/1000}
         * if there is still z-fighting, increase this value
         * @since 1.6.5
         */
        public double zIndexScale = .001;
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

        public void setPos(double x, double y, double z) {
            this.pos.x = x;
            this.pos.y = y;
            this.pos.z = z;
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

        @Override
        public void init() {
            scale = Math.min(sizes.x, sizes.y) / minSubdivisions;
            super.init();
        }

        @Override
        public void render3D(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
            matrixStack.push();

            matrixStack.translate(pos.x, pos.y, pos.z);

            matrixStack.multiply(Quaternion.fromEulerXyzDegrees(rotations.toVector().toMojangFloatVector()));

            // fix it so that y axis goes down instead of up
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
                //render each draw2D element individually so that the cull and renderBack settings are used
                if (element instanceof RenderCommon.Draw2DElement draw2DElement) {
                    renderDraw2D3D(matrixStack, draw2DElement);
                } else {
                    renderElement3D(matrixStack, element);
                }
            }
        }

        private void renderDraw2D3D(MatrixStack matrixStack, RenderCommon.Draw2DElement draw2DElement) {
            matrixStack.push();
            Draw2D draw2D = draw2DElement.getDraw2D();
            matrixStack.translate(draw2DElement.x, draw2DElement.y, 0);
            matrixStack.scale(draw2DElement.scale, draw2DElement.scale, 1);
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(draw2DElement.rotation));
            synchronized (draw2D.elements) {
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

        public static class Builder {
            private final Draw3D parent;

            private PositionCommon.Pos3D pos = new PositionCommon.Pos3D(0, 0, 0);
            private int xRot = 0;
            private int yRot = 0;
            private int zRot = 0;
            private int width = 1000;
            private int height = 1000;
            private int minSubdivisions = 1;
            private double scale = 1;
            private double zIndexScale = 0.001;
            private boolean renderBack = true;
            private boolean cull = false;

            public Builder(Draw3D parent) {
                this.parent = parent;
            }

            public PositionCommon.Pos3D getPos() {
                return pos;
            }

            public Builder pos(PositionCommon.Pos3D pos) {
                this.pos = pos;
                return this;
            }

            public Builder pos(BlockPosHelper pos) {
                this.pos = pos.toPos3D();
                return this;
            }

            public Builder pos(double x, double y, double z) {
                this.pos = new PositionCommon.Pos3D(x, y, z);
                return this;
            }

            public int getXRot() {
                return xRot;
            }

            public Builder xRot(int xRot) {
                this.xRot = xRot;
                return this;
            }

            public int getYRot() {
                return yRot;
            }

            public Builder yRot(int yRot) {
                this.yRot = yRot;
                return this;
            }

            public int getZRot() {
                return zRot;
            }

            public Builder zRot(int zRot) {
                this.zRot = zRot;
                return this;
            }

            public Builder setRot(int xRot, int yRot, int zRot) {
                this.xRot = xRot;
                this.yRot = yRot;
                this.zRot = zRot;
                return this;
            }

            public int getWidth() {
                return width;
            }

            public Builder width(int width) {
                this.width = width;
                return this;
            }

            public int getHeight() {
                return height;
            }

            public Builder height(int height) {
                this.height = height;
                return this;
            }

            public Builder setSize(int width, int height) {
                this.width = width;
                this.height = height;
                return this;
            }

            public int getMinSubdivisions() {
                return minSubdivisions;
            }

            public Builder minSubdivisions(int minSubdivisions) {
                this.minSubdivisions = minSubdivisions;
                return this;
            }

            public double getScale() {
                return scale;
            }

            public Builder scale(double scale) {
                this.scale = scale;
                return this;
            }

            public double getZIndexScale() {
                return zIndexScale;
            }

            public Builder zIndex(double zIndexScale) {
                this.zIndexScale = zIndexScale;
                return this;
            }

            public boolean isRenderBack() {
                return renderBack;
            }

            public Builder renderBack(boolean renderBack) {
                this.renderBack = renderBack;
                return this;
            }

            public boolean isCull() {
                return cull;
            }

            public Builder cull(boolean cull) {
                this.cull = cull;
                return this;
            }

            public Surface build() {
                Surface surface = new Surface(pos, new PositionCommon.Pos3D(xRot, yRot, zRot), new PositionCommon.Pos2D(width, height), minSubdivisions, renderBack, cull);
                parent.addSurface(surface);
                return surface;
            }
        }
        
    }

}
