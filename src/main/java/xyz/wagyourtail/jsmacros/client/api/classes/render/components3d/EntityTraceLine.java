package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;

/**
 * @author aMelonRind
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class EntityTraceLine extends TraceLine {
    public static boolean dirty = false;

    @Nullable
    public Entity entity;
    public double yOffset = 0.5;
    public boolean shouldRemove = false;

    public EntityTraceLine(@Nullable EntityHelper<?> entity, int color, double yOffset) {
        super(0, 0, 0, color);
        setEntity(entity).setYOffset(yOffset);
    }

    public EntityTraceLine(@Nullable EntityHelper<?> entity, int color, int alpha, double yOffset) {
        super(0, 0, 0, color, alpha);
        setEntity(entity).setYOffset(yOffset);
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public EntityTraceLine setEntity(@Nullable EntityHelper<?> entity) {
        if (entity == null) return this;
        this.entity = entity.getRaw();
        shouldRemove = false;
        return this;
    }

    /**
     * @return self for chaining
     * @since 1.9.0
     */
    public EntityTraceLine setYOffset(double yOffset) {
        this.yOffset = yOffset;
        return this;
    }

    @Override
    public void render(DrawContext drawContext, BufferBuilder builder, float tickDelta) {
        if (shouldRemove || entity == null || entity.isRemoved()) {
            shouldRemove = true;
            dirty = true;
            return;
        }

        Vec3d vec = (entity.prevX == 0.0 && entity.prevY == 0.0 && entity.prevZ == 0.0)
                ? entity.getPos()
                : entity.getLerpedPos(tickDelta);
        pos.x = vec.x;
        pos.y = vec.y + yOffset;
        pos.z = vec.z;
        super.render(drawContext, builder, tickDelta);
    }

    public static class Builder {
        private final Draw3D parent;

        public Pos2D screenPos = new Pos2D(0.0, 0.0);
        @Nullable
        private EntityHelper<?> entity = null;
        private double yOffset = 0.5;
        private int color = 0xFFFFFF;
        private int alpha = 0xFF;

        public Builder(Draw3D parent) {
            this.parent = parent;
        }

        /**
         * @param entity the target entity
         * @return self for chaining
         * @since 1.9.0
         */
        public Builder entity(@Nullable EntityHelper<?> entity) {
            this.entity = entity;
            return this;
        }

        /**
         * @return the target entity
         * @since 1.9.0
         */
        @Nullable
        public EntityHelper<?> getEntity() {
            return entity;
        }

        /**
         * @param yOffset the offset of y-axis
         * @return self for chaining
         * @since 1.9.0
         */
        public Builder yOffset(double yOffset) {
            this.yOffset = yOffset;
            return this;
        }

        /**
         * @return the offset of y-axis
         * @since 1.9.0
         */
        public double getYOffset() {
            return yOffset;
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
         * @param entity the target entity
         * @return the build line
         * @since 1.9.0
         */
        public EntityTraceLine buildAndAdd(@Nullable EntityHelper<?> entity) {
            return entity(entity).buildAndAdd();
        }

        /**
         * Creates the trace line for the given values and adds it to the draw3D
         *
         * @return the build line
         * @since 1.9.0
         */
        public EntityTraceLine buildAndAdd() {
            EntityTraceLine line = build();
            parent.addTraceLine(line);
            return line;
        }

        /**
         * Builds the line from the given values
         *
         * @param entity the target entity
         * @return the build line
         * @since 1.9.0
         */
        public EntityTraceLine build(@Nullable EntityHelper<?> entity) {
            return entity(entity).build();
        }

        /**
         * Builds the line from the given values
         *
         * @return the build line
         * @since 1.9.0
         */
        public EntityTraceLine build() {
            EntityTraceLine line = new EntityTraceLine(entity, color, alpha, yOffset);
            line.screenPos = screenPos;
            return line;
        }

    }

}
