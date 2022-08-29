package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
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
                s.render3D(matrixStack);
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

            Matrix4f matrix  = matrixStack.peek().getModel();

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
            Matrix4f model = matrixStack.peek().getModel();
            RenderSystem.lineWidth(2.5F);
            buf.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            buf.vertex(model, (float) pos.x1, (float) pos.y1, (float) pos.z1).color(r, g, b, a).next();
            buf.vertex(model, (float) pos.x2, (float) pos.y2, (float) pos.z2).color(r, g, b, a).next();
            tess.draw();

            if (cull) {
                RenderSystem.enableDepthTest();
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

        public void render3D(MatrixStack matrixStack) {
            matrixStack.push();

            matrixStack.translate(pos.x, pos.y, pos.z);

            matrixStack.multiply(Quaternion.method_35823(rotations.toVector().toMojangFloatVector()));

            // fix it so that y axis goes down instead of up
            matrixStack.scale(1, -1, 1);

            // scale so that x or y have minSubdivisions units between them
            matrixStack.scale((float) scale, (float) scale, (float) scale);

            render(matrixStack);

            matrixStack.pop();

            if (!cull) {
                RenderSystem.enableDepthTest();
            }
            if (renderBack) {
                RenderSystem.enableCull();
            }
        }

        @Override
        public void render(MatrixStack matrixStack) {
            if (matrixStack == null) return;

            synchronized (elements) {
                Iterator<RenderCommon.RenderElement> iter = elements.stream().sorted(Comparator.comparingInt(RenderCommon.RenderElement::getZIndex)).iterator();
                float current = 0;
                while (iter.hasNext()) {
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
                    RenderCommon.RenderElement next = iter.next();
                    matrixStack.push();
                    matrixStack.translate(0, 0, zIndexScale * next.getZIndex());
                    next.render3D(matrixStack, 0, 0, 0);
                    matrixStack.pop();
                }
            }
        }
    }

}
