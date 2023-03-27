package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.math.MathHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;

import static xyz.wagyourtail.jsmacros.client.access.backports.TextBackport.empty;
import static xyz.wagyourtail.jsmacros.client.access.backports.TextBackport.literal;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class Text implements RenderElement, Alignable<Text> {

    public IDraw2D<?> parent;

    public net.minecraft.text.Text text;
    public double scale;
    public float rotation;
    public boolean rotateCenter;
    public int x;
    public int y;
    public int color;
    public int width;
    public boolean shadow;
    public int zIndex;

    public Text(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, float rotation) {
        this(new TextHelper(literal(text)), x, y, color, zIndex, shadow, scale, rotation);
    }

    public Text(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, float rotation) {
        this.text = text.getRaw();
        this.x = x;
        this.y = y;
        this.color = color;
        this.width = mc.textRenderer.getStringWidth(this.text.asFormattedString());
        this.shadow = shadow;
        this.scale = scale;
        this.rotation = MathHelper.wrapDegrees(rotation);
        this.zIndex = zIndex;
    }

    /**
     * @param x the new x position for this text element
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public Text setX(int x) {
        this.x = x;
        return this;
    }

    /**
     * @return the x position of this element.
     *
     * @since 1.8.4
     */
    public int getX() {
        return x;
    }

    /**
     * @param y the new y position for this text element
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public Text setY(int y) {
        this.y = y;
        return this;
    }

    /**
     * @return the y position of this element.
     *
     * @since 1.8.4
     */
    public int getY() {
        return y;
    }

    /**
     * @param x
     * @param y
     *
     * @return
     *
     * @since 1.0.5
     */
    public Text setPos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * @param text
     *
     * @return
     *
     * @since 1.0.5
     */
    public Text setText(String text) {
        this.text = literal(text);
        this.width = mc.textRenderer.getStringWidth(this.text.asFormattedString());
        return this;
    }

    /**
     * @param text
     *
     * @return
     *
     * @since 1.2.7
     */
    public Text setText(TextHelper text) {
        this.text = text.getRaw();
        this.width = mc.textRenderer.getStringWidth(this.text.asFormattedString());
        return this;
    }

    /**
     * @return
     *
     * @since 1.2.7
     */
    public TextHelper getText() {
        return new TextHelper(text);
    }

    /**
     * @return
     *
     * @since 1.0.5
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return the height of this text.
     *
     * @since 1.8.4
     */
    public int getHeight() {
        return mc.textRenderer.fontHeight;
    }

    /**
     * @param shadow whether the text should be rendered with a shadow
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public Text setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    /**
     * @return {@code true} if this text element is rendered with a shadow, {@code false}
     *     otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasShadow() {
        return shadow;
    }

    /**
     * @param scale
     *
     * @return
     *
     * @throws IllegalArgumentException
     * @since 1.0.5
     */
    public Text setScale(double scale) throws IllegalArgumentException {
        if (scale == 0) {
            throw new IllegalArgumentException("Scale can't be 0");
        }
        this.scale = scale;
        return this;
    }

    /**
     * @return the scale of this text.
     *
     * @since 1.8.4
     */
    public double getScale() {
        return scale;
    }

    /**
     * @param rotation
     *
     * @return
     *
     * @since 1.0.5
     */
    public Text setRotation(double rotation) {
        this.rotation = MathHelper.wrapDegrees((float) rotation);
        return this;
    }

    /**
     * @return the rotation of this text.
     *
     * @since 1.8.4
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * @param rotateCenter whether this text should be rotated around its center
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public Text setRotateCenter(boolean rotateCenter) {
        this.rotateCenter = rotateCenter;
        return this;
    }

    /**
     * @return {@code true} if this text should be rotated around its center, {@code false}
     *     otherwise.
     *
     * @since 1.8.4
     */
    public boolean isRotatingCenter() {
        return rotateCenter;
    }

    /**
     * @param color the new color for this text element
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public Text setColor(int color) {
        this.color = color;
        return this;
    }

    /**
     * @return the color of this text.
     *
     * @since 1.8.4
     */
    public int getColor() {
        return this.color;
    }

    /**
     * @param zIndex the new z-index for this text element
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public Text setZIndex(int zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (shadow) {
            mc.textRenderer.drawWithShadow(text.asFormattedString(), x, y, color);
        } else {
            mc.textRenderer.draw(text.asFormattedString(), x, y, color);
        }
    }

    @Override
    public void render3D(int mouseX, int mouseY, float delta) {
        GlStateManager.pushMatrix();
        GlStateManager.scaled(scale, scale, 1);
        GlStateManager.translatef(x, y, 0);
        GlStateManager.rotatef(rotation, 0, 0, 1);
        GlStateManager.translatef(-x, -y, 0);
        Tessellator tess = Tessellator.getInstance();
        if (shadow) mc.textRenderer.drawWithShadow(text.asFormattedString(), (int)(x / scale), (int)(y / scale), color);
        else mc.textRenderer.draw(text.asFormattedString(), (int)(x / scale), (int)(y / scale), color);
        GlStateManager.popMatrix();
    }

    public Text setParent(IDraw2D<?> parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public int getScaledWidth() {
        return (int) (scale * getWidth());
    }

    @Override
    public int getParentWidth() {
        return parent != null ? parent.getWidth() : mc.window.getScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return (int) (scale * mc.textRenderer.fontHeight);
    }

    @Override
    public int getParentHeight() {
        return parent != null ? parent.getHeight() : mc.window.getScaledHeight();
    }

    @Override
    public int getScaledLeft() {
        return x;
    }

    @Override
    public int getScaledTop() {
        return y;
    }

    @Override
    public Text moveTo(int x, int y) {
        return setPos(x, y);
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class Builder extends RenderElementBuilder<Text> implements Alignable<Builder> {
        private int x = 0;
        private int y = 0;
        private net.minecraft.text.Text text = empty();
        private int color = 0xFFFFFFFF;
        private double scale = 1;
        private float rotation = 0;
        private boolean rotateCenter = true;
        private boolean shadow = false;
        private int zIndex = 0;

        public Builder(IDraw2D<?> draw2D) {
            super(draw2D);
        }

        /**
         * @param text the content of the text element
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder text(TextHelper text) {
            if (text != null) {
                this.text = text.getRaw();
            }
            return this;
        }

        /**
         * @param text the content of the text element
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder text(TextBuilder text) {
            if (text != null) {
                this.text = text.build().getRaw();
            }
            return this;
        }

        /**
         * @param text the content of the text element
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder text(String text) {
            if (text != null) {
                this.text = literal(text);
            }
            return this;
        }

        /**
         * @return the content of the text element.
         *
         * @since 1.8.4
         */
        public TextHelper getText() {
            return new TextHelper(text.copy());
        }

        /**
         * @param x the x position of the text element
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder x(int x) {
            this.x = x;
            return this;
        }

        /**
         * @return the x position of the text element.
         *
         * @since 1.8.4
         */
        public int getX() {
            return x;
        }

        /**
         * @param y the y position of the text element
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder y(int y) {
            this.y = y;
            return this;
        }

        /**
         * @return the y position of the text element.
         *
         * @since 1.8.4
         */
        public int getY() {
            return y;
        }

        /**
         * @param x the x position of the text element
         * @param y the y position of the text element
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * @return the width of the string.
         *
         * @since 1.8.4
         */
        public int getWidth() {
            return mc.textRenderer.getStringWidth(text.asFormattedString());
        }

        /**
         * @return the height of the string.
         *
         * @since 1.8.4
         */
        public int getHeight() {
            return mc.textRenderer.fontHeight;
        }

        /**
         * @param color the color of the text element
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder color(int color) {
            this.color = color;
            return this;
        }

        /**
         * @param r the red component of the color
         * @param g the green component of the color
         * @param b the blue component of the color
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder color(int r, int g, int b) {
            return color(r, g, b, 255);
        }

        /**
         * @param r the red component of the color
         * @param g the green component of the color
         * @param b the blue component of the color
         * @param a the alpha component of the color
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder color(int r, int g, int b, int a) {
            this.color = (a << 24) | (r << 16) | (g << 8) | b;
            return this;
        }

        /**
         * @return the color of the text element.
         *
         * @since 1.8.4
         */
        public int getColor() {
            return color;
        }

        /**
         * @param scale the scale of the text element
         *
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
         * @return the scale of the text element.
         *
         * @since 1.8.4
         */
        public double getScale() {
            return scale;
        }

        /**
         * @param rotation the rotation (clockwise) of the text element in degrees
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder rotation(double rotation) {
            this.rotation = (float) rotation;
            return this;
        }

        /**
         * @return the rotation (clockwise) of the text element in degrees.
         *
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether this text should be rotated around its center
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
         * @return {@code true} if this text should be rotated around its center, {@code false}
         *     otherwise.
         *
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param shadow whether the text should have a shadow or not
         *
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        /**
         * @return {@code true} if the text element has a shadow, {@code false} otherwise.
         *
         * @since 1.8.4
         */
        public boolean hasShadow() {
            return shadow;
        }

        /**
         * @param zIndex the z-index of the text element
         *
         * @return self for chaining.
         */
        public Builder zIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        /**
         * @return the z-index of the text element.
         *
         * @since 1.8.4
         */
        public int getZIndex() {
            return zIndex;
        }

        @Override
        public Text createElement() {
            return new Text(new TextHelper(text), x, y, color, zIndex, shadow, scale, rotation).setRotateCenter(
                rotateCenter).setParent(parent);
        }

        @Override
        public int getScaledWidth() {
            return (int) (scale * getWidth());
        }

        @Override
        public int getParentWidth() {
            return parent.getWidth();
        }

        @Override
        public int getScaledHeight() {
            return (int) (scale * getHeight());
        }

        @Override
        public int getParentHeight() {
            return parent.getHeight();
        }

        @Override
        public int getScaledLeft() {
            return x;
        }

        @Override
        public int getScaledTop() {
            return y;
        }

        @Override
        public Builder moveTo(int x, int y) {
            return pos(x, y);
        }

    }

}
