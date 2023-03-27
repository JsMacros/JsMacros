package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos2D;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.components.Draw2DElement;
import xyz.wagyourtail.jsmacros.client.api.classes.render.components.RenderElement;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;

import java.util.Iterator;

/**
 * @author Wagyourtail
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public class Surface extends Draw2D implements RenderElement {
    public boolean rotateToPlayer;
    public boolean rotateCenter;
    public EntityHelper<?> boundEntity;
    public Pos3D boundOffset;
    public final Pos3D pos;
    public final Pos3D rotations;
    protected final Pos2D sizes;
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

    public Surface(Pos3D pos, Pos3D rotations, Pos2D sizes, int minSubdivisions, boolean renderBack, boolean cull) {
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
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public Surface setPos(Pos3D pos) {
        this.pos.x = pos.x;
        this.pos.y = pos.y;
        this.pos.z = pos.z;
        return this;
    }

    /**
     * @param pos the position of the surface
     *
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
     *
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
     *     entity.
     *
     * @since 1.8.4
     */
    public EntityHelper<?> getBoundEntity() {
        return boundEntity;
    }

    /**
     * @param boundOffset the offset from the entity's position to render the surface at
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public Surface setBoundOffset(Pos3D boundOffset) {
        this.boundOffset = boundOffset;
        return this;
    }

    /**
     * @param x the x offset from the entity's position to render the surface at
     * @param y the y offset from the entity's position to render the surface at
     * @param z the z offset from the entity's position to render the surface at
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public Surface setBoundOffset(double x, double y, double z) {
        this.boundOffset = new Pos3D(x, y, z);
        return this;
    }

    /**
     * @return the offset from the entity's position to render the surface at.
     *
     * @since 1.8.4
     */
    public Pos3D getBoundOffset() {
        return boundOffset;
    }

    /**
     * @param rotateToPlayer whether to rotate the surface to face the player or not
     *
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
     *     otherwise.
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

    public Pos2D getSizes() {
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
     *
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
     *     otherwise.
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
    public void render3D(int mouseX, int mouseY, float delta) {
        GlStateManager.pushMatrix();
        if (boundEntity != null && boundEntity.isAlive()) {
            Pos3D entityPos = boundEntity.getPos().add(boundOffset);
            pos.x += (entityPos.x - pos.x) * delta;
            pos.y += (entityPos.y - pos.y) * delta;
            pos.z += (entityPos.z - pos.z) * delta;
        }

        GlStateManager.translated(pos.x, pos.y, pos.z);

        if (rotateToPlayer) {
            Camera q = MinecraftClient.getInstance().gameRenderer.getCamera();
            // to euler angles
            rotations.x = -q.getPitch();
            rotations.y = 180 + q.getYaw();
            rotations.z = 0;
        }
        if (rotateCenter) {
            GlStateManager.translated(sizes.x / 2, 0, 0);
//            GlStateManager.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) rotations.y));
            GlStateManager.rotatef((float) rotations.y, 0, 1, 0);
            GlStateManager.translated(-sizes.x / 2, 0, 0);
            GlStateManager.translated(0, -sizes.y / 2, 0);
//            GlStateManager.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((float) rotations.x));
            GlStateManager.rotatef((float) rotations.x, 1, 0, 0);
            GlStateManager.translated(0, sizes.y / 2, 0);
            GlStateManager.translated(sizes.x / 2, -sizes.y / 2, 0);
//            GlStateManager.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float) rotations.z));
            GlStateManager.rotatef((float) rotations.z, 0, 0, 1);
            GlStateManager.translated(-sizes.x / 2, sizes.y / 2, 0);
        } else {
            Vector3f rot = rotations.toVector().toMojangFloatVector();
//            GlStateManager.multiply(new Quaternion(rot.getX(), rot.getY(), rot.getZ(), true));
            GlStateManager.rotatef(rot.getX(), 1, 0, 0);
            GlStateManager.rotatef(rot.getY(), 0, 1, 0);
            GlStateManager.rotatef(rot.getZ(), 0, 0, 1);
        }
        // fix it so that y-axis goes down instead of up
        GlStateManager.scaled(1, -1, 1);
        // scale so that x or y have minSubdivisions units between them
        GlStateManager.scaled((float) scale, (float) scale, (float) scale);

        synchronized (elements) {
            renderElements3D(getElementsByZIndex());
        }
        GlStateManager.popMatrix();

        if (!cull) {
            GlStateManager.enableDepthTest();
        }
        if (renderBack) {
            GlStateManager.enableCull();
        }
    }

    private void renderElements3D(Iterator<RenderElement> iter) {
        while (iter.hasNext()) {
            RenderElement element = iter.next();
            // Render each draw2D element individually so that the cull and renderBack settings are used
            if (element instanceof Draw2DElement) {
                Draw2DElement draw2DElement = (Draw2DElement) element;
                renderDraw2D3D(draw2DElement);
            } else {
                renderElement3D(element);
            }
        }
    }

    private void renderDraw2D3D(Draw2DElement draw2DElement) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(draw2DElement.x, draw2DElement.y, 0);
        GlStateManager.scaled(draw2DElement.scale, draw2DElement.scale, 1);
        if (rotateCenter) {
            GlStateManager.translated(draw2DElement.width.getAsInt() / 2d, draw2DElement.height.getAsInt() / 2d, 0);
        }
        GlStateManager.rotatef(draw2DElement.rotation, 0, 0, 1);
        if (rotateCenter) {
            GlStateManager.translated(-draw2DElement.width.getAsInt() / 2d, -draw2DElement.height.getAsInt() / 2d, 0);
        }
        // Don't translate back!
        Draw2D draw2D = draw2DElement.getDraw2D();
        synchronized (draw2D.getElements()) {
            renderElements3D(draw2D.getElementsByZIndex());
        }
        GlStateManager.popMatrix();
    }

    private void renderElement3D(RenderElement element) {
        if (renderBack) {
            GlStateManager.disableCull();
        } else {
            GlStateManager.enableCull();
        }
        if (!cull) {
            GlStateManager.disableDepthTest();
        } else {
            GlStateManager.enableDepthTest();
        }
        GlStateManager.pushMatrix();
        GlStateManager.translated(0, 0, zIndexScale * element.getZIndex());
        element.render3D(0, 0, 0);
        GlStateManager.popMatrix();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {

    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class Builder {
        private final Draw3D parent;

        private Pos3D pos = new Pos3D(0, 0, 0);
        private EntityHelper<?> boundEntity;
        private Pos3D boundOffset = Pos3D.ZERO;
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
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos(Pos3D pos) {
            this.pos = pos;
            return this;
        }

        /**
         * @param pos the position of the surface
         *
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
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos(double x, double y, double z) {
            this.pos = new Pos3D(x, y, z);
            return this;
        }

        /**
         * @return the position of the surface.
         *
         * @since 1.8.4
         */
        public Pos3D getPos() {
            return pos;
        }

        /**
         * The surface will move with the entity at the offset location.
         *
         * @param boundEntity the entity to bind the surface to
         *
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
         *     entity.
         *
         * @since 1.8.4
         */
        public EntityHelper<?> getBoundEntity() {
            return boundEntity;
        }

        /**
         * @param entityOffset the offset from the entity's position to render the surface at
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder boundOffset(Pos3D entityOffset) {
            this.boundOffset = entityOffset;
            return this;
        }

        /**
         * @param x the x offset from the entity's position to render the surface at
         * @param y the y offset from the entity's position to render the surface at
         * @param z the z offset from the entity's position to render the surface at
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder boundOffset(double x, double y, double z) {
            this.boundOffset = new Pos3D(x, y, z);
            return this;
        }

        /**
         * @return the offset from the entity's position to render the surface at.
         *
         * @since 1.8.4
         */
        public Pos3D getBoundOffset() {
            return boundOffset;
        }

        /**
         * @param xRot the x rotation of the surface
         *
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
         *
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
         *
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
         *
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
         *
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
         *     {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param rotateToPlayer whether to rotate the surface to face the player or not
         *
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
         *     {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean doesRotateToPlayer() {
            return rotateToPlayer;
        }

        /**
         * @param width the width of the surface
         *
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
         *
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
         * @param width the width of the surface
         * @param height the height of the surface
         *
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
         *
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
         *
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
         *     otherwise.
         *
         * @since 1.8.4
         */
        public boolean shouldRenderBack() {
            return renderBack;
        }

        /**
         * @param cull whether to enable culling or not
         *
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
         *
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
            Surface surface = new Surface(
                pos,
                new Pos3D(xRot, yRot, zRot),
                new Pos2D(width, height),
                minSubdivisions,
                renderBack,
                cull
            )
                .setRotateCenter(rotateCenter)
                .setRotateToPlayer(rotateToPlayer)
                .bindToEntity(boundEntity)
                .setBoundOffset(boundOffset);
            return surface;
        }

    }

}
