package xyz.wagyourtail.jsmacros.client.api.render.draw3d;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import com.mojang.blaze3d.systems.RenderSystem;
import xyz.wagyourtail.jsmacros.client.api.classes.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.Draw2DElement;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.RenderCommon;

import java.util.Iterator;

/**
 * @author Wagyourtail
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public class Surface extends Draw2D {
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
            if (element instanceof Draw2DElement draw2DElement) {
                renderDraw2D3D(matrixStack, draw2DElement);
            } else {
                renderElement3D(matrixStack, element);
            }
        }
    }

    private void renderDraw2D3D(MatrixStack matrixStack, Draw2DElement draw2DElement) {
        matrixStack.push();
        Draw2D draw2D = draw2DElement.getDraw2D();
        matrixStack.translate(draw2DElement.x, draw2DElement.y, 0);
        matrixStack.scale(draw2DElement.scale, draw2DElement.scale, 1);
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(draw2DElement.rotation));
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

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class Builder {
        private final Draw3D parent;

        private PositionCommon.Pos3D pos = new PositionCommon.Pos3D(0, 0, 0);
        private int xRot = 0;
        private int yRot = 0;
        private int zRot = 0;
        private int width = 10;
        private int height = 10;
        private int minSubdivisions = 1;
        private double scale = -1;
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
         * @param xRot the x rotation of the surface
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder xRot(int xRot) {
            this.xRot = xRot;
            return this;
        }

        /**
         * @return the x rotation of the surface.
         *
         * @since 1.8.4
         */
        public int getXRot() {
            return xRot;
        }

        /**
         * @param yRot the y rotation of the surface
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder yRot(int yRot) {
            this.yRot = yRot;
            return this;
        }

        /**
         * @return the y rotation of the surface.
         *
         * @since 1.8.4
         */
        public int getYRot() {
            return yRot;
        }

        /**
         * @param zRot the z rotation of the surface
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder zRot(int zRot) {
            this.zRot = zRot;
            return this;
        }

        /**
         * @return the z rotation of the surface.
         *
         * @since 1.8.4
         */
        public int getZRot() {
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
        public Builder rot(int xRot, int yRot, int zRot) {
            this.xRot = xRot;
            this.yRot = yRot;
            this.zRot = zRot;
            return this;
        }

        /**
         * @param width the width of the surface
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * @return the width of the surface.
         *
         * @since 1.8.4
         */
        public int getWidth() {
            return width;
        }

        /**
         * @param height the height of the surface
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * @return the height of the surface.
         *
         * @since 1.8.4
         */
        public int getHeight() {
            return height;
        }

        /**
         * @param width  the width of the surface
         * @param height the height of the surface
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder size(int width, int height) {
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
         * @param scale the scale of the surface
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder scale(double scale) {
            if (scale <= 0) {
                throw new IllegalArgumentException("Scale must be greater than 0");
            }
            this.scale = scale;
            return this;
        }

        /**
         * @return the scale of the surface.
         *
         * @since 1.8.4
         */
        public double getScale() {
            return scale;
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
            Surface surface = new Surface(pos, new PositionCommon.Pos3D(xRot, yRot, zRot), new PositionCommon.Pos2D(width, height), minSubdivisions, renderBack, cull);
            if (scale != -1) {
                surface.setMinSubdivisions((int) (Math.min(width, height) / scale));
            }
            return surface;
        }

    }
}
