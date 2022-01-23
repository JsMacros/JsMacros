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

import java.util.ArrayList;
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

    public Draw3D unregister() {
        FHud.renders.remove(this);
        return this;
    }

    public Draw2D addDraw2D(double x, double y, double z) {
        return this.addDraw2D(x, y, z, 0);
    }

    public Draw2D addDraw2D(double x, double y, double z, double yaw) {
        return this.addDraw2D(x, y, z, yaw, 0);
    }

    public Draw2D addDraw2D(double x, double y, double z, double yaw, double pitch) {
        return this.addDraw2D(x, y, z, yaw, pitch, 0);
    }

    public Draw2D addDraw2D(double x, double y, double z, double yaw, double pitch, double roll) {
        return this.addDraw2D(x, y, z, yaw, pitch, roll, 1);
    }

    public Draw2D addDraw2D(double x, double y, double z, double yaw, double pitch, double roll, double scale) {
        return this.addDraw2D(x, y, z, yaw, pitch, roll, scale, true);
    }

    public Draw2D addDraw2D(double x, double y, double z, double yaw, double pitch, double roll, double scale, boolean bothSide) {
        Surface surface = new Surface(
            (float) x,
            (float) y,
            (float) z,
            (float) yaw,
            (float) pitch,
            (float) roll,
            (float) scale,
            bothSide
        );
        this.surfaces.add(surface);
        return surface;
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

        //reset
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        synchronized (surfaces) {
            for (Surface s : surfaces) {
                s.render3D(matrixStack);
            }
        }

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

    }

    public static class Surface extends Draw2D {
        protected float posX;
        protected float posY;
        protected float posZ;
        protected float yaw;
        protected float pitch;
        protected float roll;
        protected float scale;
        protected boolean bothSide;

        public Surface(float posX, float posY, float posZ) {
            this(posX, posY, posZ, 0);
        }

        public Surface(float posX, float posY, float posZ, float yaw) {
            this(posX, posY, posZ, yaw, 0);
        }

        public Surface(float posX, float posY, float posZ, float yaw, float pitch) {
            this(posX, posY, posZ, yaw, pitch, 0);
        }

        public Surface(float posX, float posY, float posZ, float yaw, float pitch, float roll) {
            this(posX, posY, posZ, yaw, pitch, roll, 1);
        }

        public Surface(float posX, float posY, float posZ, float yaw, float pitch, float roll, float scale) {
            this(posX, posY, posZ, yaw, pitch, roll, scale, true);
        }

        public Surface(float posX, float posY, float posZ, float yaw, float pitch, float roll, float scale, boolean bothSide) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
            this.scale = scale;
            this.bothSide = bothSide;
        }

        public void setPos(float x, float y, float z) {
            this.posX = x;
            this.posY = y;
            this.posZ = z;
        }

        public void setRotation(float yaw, float pitch, float roll) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public void render3D(MatrixStack matrixStack) {
            if (bothSide) {
                RenderSystem.disableCull();
            }

            matrixStack.push();
            matrixStack.translate(posX, posY, posZ);
            matrixStack.multiply(Quaternion.fromEulerXyz(
                pitch,
                yaw,
                roll + (float) (Math.PI)
            ));
            matrixStack.scale(scale, scale, scale);
            render(matrixStack);
            matrixStack.pop();

            if (bothSide) {
                RenderSystem.enableCull();
            }
        }

    }

}
