package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.classes.CustomImage;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;

/**
 * @author Wagyourtail
 * @since 1.2.3
 */
@SuppressWarnings("unused")
public class Image implements RenderElement, Alignable<Image> {

    private static MinecraftClient mc = MinecraftClient.getInstance();

    private Identifier imageid;
    @Nullable
    public IDraw2D<?> parent;
    public float rotation;
    public boolean rotateCenter;
    public int x;
    public int y;
    public int width;
    public int height;
    public int imageX;
    public int imageY;
    public int regionWidth;
    public int regionHeight;
    public int textureWidth;
    public int textureHeight;
    public int color;
    public int zIndex;

    public Image(int x, int y, int width, int height, int zIndex, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float rotation) {
        this(
                x,
                y,
                width,
                height,
                zIndex,
                0xFF,
                color,
                id,
                imageX,
                imageY,
                regionWidth,
                regionHeight,
                textureWidth,
                textureHeight,
                rotation
        );
        setColor(color);
    }

    public Image(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float rotation) {
        setPos(x, y, width, height);
        setColor(color, alpha);
        setImage(id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight);
        this.rotation = rotation;
    }

    /**
     * @param id
     * @param imageX
     * @param imageY
     * @param regionWidth
     * @param regionHeight
     * @param textureWidth
     * @param textureHeight
     * @return self for chaining.
     * @since 1.2.3
     */
    public Image setImage(String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        imageid = RegistryHelper.parseIdentifier(id);
        this.imageX = imageX;
        this.imageY = imageY;
        this.regionWidth = regionWidth;
        this.regionHeight = regionHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        return this;
    }

    /**
     * @return
     * @since 1.2.3
     */
    public String getImage() {
        return imageid.toString();
    }

    /**
     * @param x the new x position of this image
     * @return self for chaining.
     * @since 1.8.4
     */
    public Image setX(int x) {
        this.x = x;
        return this;
    }

    /**
     * @return the x position of this image.
     * @since 1.8.4
     */
    public int getX() {
        return x;
    }

    /**
     * @param y the new y position of this image
     * @return self for chaining.
     * @since 1.8.4
     */
    public Image setY(int y) {
        this.y = y;
        return this;
    }

    /**
     * @return the y position of this image.
     * @since 1.8.4
     */
    public int getY() {
        return y;
    }

    /**
     * @param x the new x position of this image
     * @param y the new y position of this image
     * @return self for chaining.
     * @since 1.8.4
     */
    public Image setPos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @since 1.2.3
     */
    public Image setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * @param width the new width of this image
     * @return self for chaining.
     * @since 1.8.4
     */
    public Image setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * @return the width of this image.
     * @since 1.8.4
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param height the new height of this image
     * @return self for chaining.
     * @since 1.8.4
     */
    public Image setHeight(int height) {
        this.height = height;
        return this;
    }

    /**
     * @return the height of this image.
     * @since 1.8.4
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param width  the new width of this image
     * @param height the new height of this image
     * @return self for chaining.
     * @since 1.8.4
     */
    public Image setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * @param color
     * @return
     * @since 1.6.5
     */
    public Image setColor(int color) {
        if (color <= 0xFFFFFFFF) {
            color = color | 0xFF000000;
        }
        this.color = color;
        return this;
    }

    /**
     * @param color
     * @param alpha
     * @return
     * @since 1.6.5
     */
    public Image setColor(int color, int alpha) {
        this.color = (alpha << 24) | (color & 0xFFFFFFFF);
        return this;
    }

    /**
     * @return the color of this image.
     * @since 1.8.4
     */
    public int getColor() {
        return color;
    }

    /**
     * @return the alpha value of this image.
     * @since 1.8.4
     */
    public int getAlpha() {
        return (color >> 24) & 0xFF;
    }

    /**
     * @param rotation
     * @return
     * @since 1.2.6
     */
    public Image setRotation(double rotation) {
        this.rotation = MathHelper.wrapDegrees((float) rotation);
        return this;
    }

    /**
     * @return the rotation of this image.
     * @since 1.8.4
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * @param rotateCenter whether the image should be rotated around its center
     * @return self for chaining.
     * @since 1.8.4
     */
    public Image setRotateCenter(boolean rotateCenter) {
        this.rotateCenter = rotateCenter;
        return this;
    }

    /**
     * @return {@code true} if this image should be rotated around its center, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isRotatingCenter() {
        return rotateCenter;
    }

    /**
     * @param zIndex the new z-index of this image
     * @return self for chaining.
     * @since 1.8.4
     */
    public Image setZIndex(int zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        setupMatrix(matrices, x, y, 1, rotation, getWidth(), getHeight(), rotateCenter);
        try {
            float u = this.imageX / (float) this.textureWidth;
            float v = this.imageY / (float) this.textureHeight;

            drawContext.drawTexture(
                RenderLayer::getGuiTextured,
                this.imageid,
                this.x,
                this.y,
                u,
                v,
                this.width,
                this.height,
                this.textureWidth,
                this.textureHeight,
                this.color
            );

        } finally {
            matrices.pop();
        }
    }

    public Image setParent(IDraw2D<?> parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public int getScaledWidth() {
        return width;
    }

    @Override
    public int getParentWidth() {
        return parent != null ? parent.getWidth() : mc.getWindow().getScaledWidth();
    }

    @Override
    public int getScaledHeight() {
        return height;
    }

    @Override
    public int getParentHeight() {
        return parent != null ? parent.getHeight() : mc.getWindow().getScaledHeight();
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
    public Image moveTo(int x, int y) {
        return setPos(x, y, width, height);
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static final class Builder extends RenderElementBuilder<Image> implements Alignable<Builder> {
        private String identifier;
        private int x = 0;
        private int y = 0;
        private int width = 0;
        private int height = 0;
        private int imageX = 0;
        private int imageY = 0;
        private int regionWidth = 0;
        private int regionHeight = 0;
        private int textureWidth = 256;
        private int textureHeight = 256;
        private int color = 0xFFFFFFFF;
        private int alpha = 0xFF;
        private float rotation = 0;
        private boolean rotateCenter = true;
        private int zIndex = 0;

        public Builder(IDraw2D<?> draw2D) {
            super(draw2D);
        }

        /**
         * Will automatically set all attributes to the default values of the custom image.
         * Values set before the call of this method will be overwritten.
         *
         * @param customImage the custom image to use
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder fromCustomImage(CustomImage customImage) {
            this.width = customImage.getWidth();
            this.height = customImage.getHeight();
            this.imageX = 0;
            this.imageY = 0;
            this.regionWidth = customImage.getWidth();
            this.regionHeight = customImage.getHeight();
            this.textureWidth = customImage.getWidth();
            this.textureHeight = customImage.getHeight();
            this.identifier = customImage.getIdentifier();
            return this;
        }

        /**
         * @param identifier the identifier of the image to use
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        /**
         * @return the identifier of the used image or {@code null} if no image is used.
         * @since 1.8.4
         */
        public String getIdentifier() {
            return identifier;
        }

        /**
         * @param x the x position of the image
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder x(int x) {
            this.x = x;
            return this;
        }

        /**
         * @return the x position of the image.
         * @since 1.8.4
         */
        public int getX() {
            return x;
        }

        /**
         * @param y the y position of the image
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder y(int y) {
            this.y = y;
            return this;
        }

        /**
         * @return the y position of the image.
         * @since 1.8.4
         */
        public int getY() {
            return y;
        }

        /**
         * @param x the x position of the image
         * @param y the y position of the image
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * @param width the width of the image
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder width(int width) {
            this.width = width;
            return this;
        }

        /**
         * @return the width of the image.
         * @since 1.8.4
         */
        public int getWidth() {
            return width;
        }

        /**
         * @param height the height of the image
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * @return the height of the image.
         * @since 1.8.4
         */
        public int getHeight() {
            return height;
        }

        /**
         * @param width  the width of the image
         * @param height the height of the image
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * @param imageX the x position in the image texture to start drawing from
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder imageX(int imageX) {
            this.imageX = imageX;
            return this;
        }

        /**
         * @return the x position in the image texture to start drawing from.
         * @since 1.8.4
         */
        public int getImageX() {
            return imageX;
        }

        /**
         * @param imageY the y position in the image texture to start drawing from
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder imageY(int imageY) {
            this.imageY = imageY;
            return this;
        }

        /**
         * @return the y position in the image texture to start drawing from.
         * @since 1.8.4
         */
        public int getImageY() {
            return imageY;
        }

        /**
         * @param imageX the x position in the image texture to start drawing from
         * @param imageY the y position in the image texture to start drawing from
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder imagePos(int imageX, int imageY) {
            this.imageX = imageX;
            this.imageY = imageY;
            return this;
        }

        /**
         * @param regionWidth the width of the region to draw
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder regionWidth(int regionWidth) {
            this.regionWidth = regionWidth;
            return this;
        }

        /**
         * @return the width of the region to draw.
         * @since 1.8.4
         */
        public int getRegionWidth() {
            return regionWidth;
        }

        /**
         * @param regionHeight the height of the region to draw
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder regionHeight(int regionHeight) {
            this.regionHeight = regionHeight;
            return this;
        }

        /**
         * @return the height of the region to draw.
         * @since 1.8.4
         */
        public int getRegionHeight() {
            return regionHeight;
        }

        /**
         * @param regionWidth  the width of the region to draw
         * @param regionHeight the height of the region to draw
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder regionSize(int regionWidth, int regionHeight) {
            this.regionWidth = regionWidth;
            this.regionHeight = regionHeight;
            return this;
        }

        /**
         * @param x      the x position in the image texture to start drawing from
         * @param y      the y position in the image texture to start drawing from
         * @param width  the width of the region to draw
         * @param height the height of the region to draw
         * @return
         * @since 1.8.4
         */
        public Builder regions(int x, int y, int width, int height) {
            this.imageX = x;
            this.imageY = y;
            this.regionWidth = width;
            this.regionHeight = height;
            return this;
        }

        /**
         * @param x             the x position in the image texture to start drawing from
         * @param y             the y position in the image texture to start drawing from
         * @param width         the width of the region to draw
         * @param height        the height of the region to draw
         * @param textureWidth  the width of the used texture
         * @param textureHeight the height of the used texture
         * @return
         * @since 1.8.4
         */
        public Builder regions(int x, int y, int width, int height, int textureWidth, int textureHeight) {
            this.imageX = x;
            this.imageY = y;
            this.regionWidth = width;
            this.regionHeight = height;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        /**
         * @param textureWidth the width of the used texture
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder textureWidth(int textureWidth) {
            this.textureWidth = textureWidth;
            return this;
        }

        /**
         * @return the width of the used texture.
         * @since 1.8.4
         */
        public int getTextureWidth() {
            return textureWidth;
        }

        /**
         * @param textureHeight the height of the used texture
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder textureHeight(int textureHeight) {
            this.textureHeight = textureHeight;
            return this;
        }

        /**
         * @return the height of the used texture.
         * @since 1.8.4
         */
        public int getTextureHeight() {
            return textureHeight;
        }

        /**
         * @param textureWidth  the width of the used texture
         * @param textureHeight the height of the used texture
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder textureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        /**
         * @param color the color of the image
         * @return self for chaining.
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
         * @return self for chaining.
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
         * @param a the alpha component of the color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder color(int r, int g, int b, int a) {
            this.color = (r << 16) | (g << 8) | b;
            this.alpha = a;
            return this;
        }

        /**
         * @param color the color of the image
         * @param alpha the alpha value of the color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder color(int color, int alpha) {
            this.color = color;
            this.alpha = alpha;
            return this;
        }

        /**
         * @return the color of the image.
         * @since 1.8.4
         */
        public int getColor() {
            return color;
        }

        /**
         * @param alpha the alpha value of the color
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder alpha(int alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * @return the alpha value of the color.
         * @since 1.8.4
         */
        public int getAlpha() {
            return alpha;
        }

        /**
         * @param rotation the rotation (clockwise) of the image in degrees
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder rotation(double rotation) {
            this.rotation = (float) rotation;
            return this;
        }

        /**
         * @return the rotation (clockwise) of the image in degrees.
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotateCenter whether the image should be rotated around its center
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder rotateCenter(boolean rotateCenter) {
            this.rotateCenter = rotateCenter;
            return this;
        }

        /**
         * @return {@code true} if this image should be rotated around its center, {@code false}
         * otherwise.
         * @since 1.8.4
         */
        public boolean isRotatingCenter() {
            return rotateCenter;
        }

        /**
         * @param zIndex the z-index of the image
         * @return self for chaining.
         * @since 1.8.4
         */
        public Builder zIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        /**
         * @return the z-index of the image.
         * @since 1.8.4
         */
        public int getZIndex() {
            return zIndex;
        }

        @Override
        public Image createElement() {
            return new Image(
                    x,
                    y,
                    width,
                    height,
                    zIndex,
                    alpha,
                    color,
                    identifier,
                    imageX,
                    imageY,
                    regionWidth,
                    regionHeight,
                    textureWidth,
                    textureHeight,
                    rotation
            ).setRotateCenter(rotateCenter).setParent(parent);
        }

        @Override
        public int getScaledWidth() {
            return width;
        }

        @Override
        public int getParentWidth() {
            return parent.getWidth();
        }

        @Override
        public int getScaledHeight() {
            return height;
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
