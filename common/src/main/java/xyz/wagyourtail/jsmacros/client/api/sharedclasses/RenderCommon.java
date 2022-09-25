package xyz.wagyourtail.jsmacros.client.api.sharedclasses;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import xyz.wagyourtail.jsmacros.client.api.classes.render.CustomImage;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.helpers.item.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;

/**
 * @author Wagyourtail
 * @since 1.2.3
 */
@SuppressWarnings("unused")
public class RenderCommon {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public static interface RenderElement extends Drawable {
        int getZIndex();

        default void render3D(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            render(matrices, mouseX, mouseY, delta);
        }
    }

    public abstract static class RenderElementBuilder<T extends RenderElement> {

        private final IDraw2D<?> draw2D;

        protected RenderElementBuilder(IDraw2D<?> draw2D) {
            this.draw2D = draw2D;
        }

        /**
         * Builds and adds the element to the draw2D the builder was created from.
         *
         * @return the newly created element.
         *
         * @since 1.8.4
         */
        public T build() {
            T element = createElement();
            draw2D.reAddElement(element);
            return element;
        }

        protected abstract T createElement();

    }
    
    /**
     * @author Wagyourtail
     * @since 1.0.5
     */
    public static class Item implements RenderElement {
        public ItemStack item;
        public String ovText;
        public boolean overlay;
        public double scale;
        public float rotation;
        public int x;
        public int y;
        public int zIndex;
        
        public Item(int x, int y, int zIndex, String id, boolean overlay, double scale, float rotation) {
            this.x = x;
            this.y = y;
            this.setItem(id, 1);
            this.overlay = overlay;
            this.scale = scale;
            this.rotation = rotation;
            this.zIndex = zIndex;
        }
        
        public Item(int x, int y, int zIndex, ItemStackHelper i, boolean overlay, double scale, float rotation) {
            this.x = x;
            this.y = y;
            this.item = i.getRaw();
            this.overlay = overlay;
            this.scale = scale;
            this.rotation = rotation;
            this.zIndex = zIndex;
        }
        
        /**
         * @since 1.0.5
         * @param x
         * @param y
         * @return
         */
        public Item setPos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        /**
         * @since 1.2.6
         * @param scale
         * @return
         * @throws IllegalArgumentException
         */
        public Item setScale(double scale) throws IllegalArgumentException {
            if (scale == 0) throw new IllegalArgumentException("Scale can't be 0");
            this.scale = scale;
            return this;
        }
        
        /**
         * @since 1.2.6
         * @param rotation
         * @return
         */
        public Item setRotation(double rotation) {
            this.rotation = MathHelper.wrapDegrees((float)rotation);
            return this;
        }
        
        /**
         * @since 1.2.0
         * @param overlay
         * @return
         */
        public Item setOverlay(boolean overlay) {
            this.overlay = overlay;
            return this;
        }
        
        /**
         * @since 1.2.0
         * @param ovText
         * @return
         */
        public Item setOverlayText(String ovText) {
            this.ovText = ovText;
            return this;
        }
        
        /**
         * @since 1.0.5 [citation needed]
         * @param i
         * @return
         */
        public Item setItem(ItemStackHelper i) {
            if (i != null) this.item = i.getRaw();
            else this.item = null;
            return this;
        }
        
        /**
         * @since 1.0.5 [citation needed]
         * @param id
         * @param count
         * @return
         */
        public Item setItem(String id, int count) {
            net.minecraft.item.Item it = Registry.ITEM.get(new Identifier(id));
            this.item = new ItemStack(it, count);
            return this;
        }
        
        /**
         * @since 1.0.5 [citation needed]
         * @return
         */
        public ItemStackHelper getItem() {
            return new ItemStackHelper(item);
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            matrices.translate(x, y, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            matrices.translate(-x, -y, 0);
            matrices.scale((float) scale, (float) scale, 1);
            MatrixStack ms = RenderSystem.getModelViewStack();
            ms.push();
            ms.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                i.renderGuiItemIcon(item,(int) (x / scale), (int) (y / scale));
                if (overlay) i.renderGuiItemOverlay(mc.textRenderer, item, (int) (x / scale), (int) (y / scale), ovText);
            }
            ms.pop();
            RenderSystem.applyModelViewMatrix();
            matrices.pop();
        }

        @Override
        public void render3D(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            //TODO: cull and renderBack still not working
            matrices.push();
            matrices.translate(x, y, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            matrices.translate(-x, -y, 0);
            matrices.scale((float) scale, (float) scale, 1);

            MatrixStack ms = RenderSystem.getModelViewStack();
            ms.push();
            ms.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
            RenderSystem.applyModelViewMatrix();

            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                ms.push();
                ms.scale(1, 1, 0);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.disableDepthTest();
                i.renderGuiItemIcon(item,(int) (x / scale), (int) (y / scale));
                ms.pop();
                RenderSystem.applyModelViewMatrix();
                i.zOffset = -199.9f;
                if (overlay) i.renderGuiItemOverlay(mc.textRenderer, item, (int) (x / scale), (int) (y / scale), ovText);
                i.zOffset = 0;
            }
            ms.pop();
            RenderSystem.applyModelViewMatrix();
            matrices.pop();
        }
    
        @Override
        public int getZIndex() {
            return zIndex;
        }


        public static final class Builder extends RenderElementBuilder<Item>{
            private ItemStackHelper itemStack = new ItemStackHelper(ItemStack.EMPTY);
            private String ovText = "";
            private boolean overlay = false;
            private double scale = 1;
            private float rotation = 0;
            private int x = 0;
            private int y = 0;
            private int zIndex = 0;

            public Builder(IDraw2D<?> draw2D) {
                super(draw2D);
            }

            /**
             * @return the item to draw.
             *
             * @since 1.8.4
             */
            public ItemStackHelper getItem() {
                return itemStack;
            }

            /**
             * @param item the item to draw
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder item(ItemStackHelper item) {
                if (item != null) {
                    this.itemStack = item;
                }
                return this;
            }

            /**
             * @param item the item id to draw
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder item(String item) {
                this.itemStack = new ItemStackHelper(Registry.ITEM.get(new Identifier(item)).getDefaultStack());
                return this;
            }

            /**
             * @param item  the item to draw
             * @param count the stack size
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder item(String item, int count) {
                ItemStack itemStack = Registry.ITEM.get(new Identifier(item)).getDefaultStack();
                itemStack.setCount(count);
                this.itemStack = new ItemStackHelper(itemStack);
                return this;
            }

            /**
             * @return the overlay text.
             *
             * @since 1.8.4
             */
            public String getOverlayText() {
                return ovText;
            }

            /**
             * @param overlayText the overlay text
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder overlayText(String overlayText) {
                this.ovText = overlayText;
                return this;
            }

            /**
             * @return {@code true} if the overlay should be visible, {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean isOverlayVisible() {
                return overlay;
            }

            /**
             * @param overlay whether the overlay should be visible or not
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder overlay(boolean overlay) {
                this.overlay = overlay;
                return this;
            }

            /**
             * @return the scale of the item.
             *
             * @since 1.8.4
             */
            public double getScale() {
                return scale;
            }

            /**
             * @param scale the scale of the item
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder scale(double scale) {
                if (scale <= 0) {
                    throw new IllegalArgumentException("Scale must be positive");
                }
                this.scale = scale;
                return this;
            }

            /**
             * @return the rotation of the item in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotation the rotation of the item in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(float rotation) {
                this.rotation = rotation;
                return this;
            }

            /**
             * @return the x position of the item.
             *
             * @since 1.8.4
             */
            public int getX() {
                return x;
            }

            /**
             * @param x the x position of the item
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x(int x) {
                this.x = x;
                return this;
            }

            /**
             * @return the y position of the item.
             *
             * @since 1.8.4
             */
            public int getY() {
                return y;
            }

            /**
             * @param y the y position of the item
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y(int y) {
                this.y = y;
                return this;
            }

            /**
             * @param x the x position of the item
             * @param y the y position of the item
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
             * @return the z-index of the item.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            /**
             * @param zIndex the z-index of the item
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            @Override
            protected Item createElement() {
                Item item = new Item(x, y, zIndex, itemStack, overlay, scale, rotation);
                item.setOverlayText(ovText);
                return item;
            }
        }
    }
    
    /**
     * @author Wagyourtail
     * @since 1.2.3
     */
    public static class Image implements RenderElement {
        private Identifier imageid;
        public float rotation;
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
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.zIndex = zIndex;
            setColor(color);
            this.imageX = imageX;
            this.imageY = imageY;
            this.regionWidth = regionWidth;
            this.regionHeight = regionHeight;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            imageid = new Identifier(id);
            this.rotation = rotation;
        }

        public Image(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float rotation) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.zIndex = zIndex;
            setColor(alpha, color);
            this.imageX = imageX;
            this.imageY = imageY;
            this.regionWidth = regionWidth;
            this.regionHeight = regionHeight;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            imageid = new Identifier(id);
            this.rotation = rotation;
        }

        /**
         * @since 1.6.5
         * @param color
         * @return
         */
        public Image setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
            return this;
        }

        /**
         * @since 1.6.5
         * @param color
         * @param alpha
         * @return
         */
        public Image setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
            return this;
        }
        
        /**
         * @since 1.2.3
         * @param x
         * @param y
         * @param width
         * @param height
         */
        public void setPos(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        /**
         * @since 1.2.6
         * @param rotation
         * @return
         */
        public Image setRotation(double rotation) {
            this.rotation = MathHelper.wrapDegrees((float) rotation);
            return this;
        }
        
        /**
         * @since 1.2.3
         * @param id
         * @param imageX
         * @param imageY
         * @param regionWidth
         * @param regionHeight
         * @param textureWidth
         * @param textureHeight
         */
        public void setImage(String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
            imageid = new Identifier(id);
            this.imageX = imageX;
            this.imageY = imageY;
            this.regionWidth = regionWidth;
            this.regionHeight = regionHeight;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }
        
        /**
         * @since 1.2.3
         * @return
         */
        public String getImage() {
            return imageid.toString();
        }
    
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            matrices.translate(x, y, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            matrices.translate(-x, -y, 0);
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, imageid);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            
            buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP,  VertexFormats.POSITION_TEXTURE_COLOR);
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            float x1 = x;
            float y1 = y;
            float x2 = x + width;
            float y2 = y + height;

            float u1 = imageX / (float) textureWidth;
            float v1 = imageY / (float) textureHeight;
            float u2 = (imageX + regionWidth) / (float) textureWidth;
            float v2 = (imageY + regionHeight) / (float) textureHeight;

            //draw a rectangle using triangle strips
            buf.vertex(matrix, x1, y2, 0).texture(u1, v2).color(color).next(); // Top-left
            buf.vertex(matrix, x2, y2, 0).texture(u2, v2).color(color).next(); // Top-right
            buf.vertex(matrix, x1, y1, 0).texture(u1, v1).color(color).next(); // Bottom-left
            buf.vertex(matrix, x2, y1, 0).texture(u2, v1).color(color).next(); // Bottom-right
            tess.draw();

            matrices.pop();
            RenderSystem.disableBlend();
//            RenderSystem.translated(-x, -y, 0);
//            RenderSystem.rotatef(-rotation, 0, 0, 1);
//            RenderSystem.translated(x, y, 0);
        }
    
        @Override
        public int getZIndex() {
            return zIndex;
        }

        public static final class Builder extends RenderElementBuilder<Image> {
            private String identifier;
            private float rotation = 0;
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
            private int alpha = 0xFF;
            private int color = 0xFFFFFFFF;
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
             *
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
             * @return the identifier of the used image or {@code null} if no image is used.
             *
             * @since 1.8.4
             */
            public String getIdentifier() {
                return identifier;
            }

            /**
             * @param identifier the identifier of the image to use
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder identifier(String identifier) {
                this.identifier = identifier;
                return this;
            }

            /**
             * @return the rotation of the image in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotation the rotation of the image in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(float rotation) {
                this.rotation = rotation;
                return this;
            }

            /**
             * @return the x position of the image.
             *
             * @since 1.8.4
             */
            public int getX() {
                return x;
            }

            /**
             * @param x the x position of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x(int x) {
                this.x = x;
                return this;
            }

            /**
             * @return the y position of the image.
             *
             * @since 1.8.4
             */
            public int getY() {
                return y;
            }

            /**
             * @param y the y position of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y(int y) {
                this.y = y;
                return this;
            }

            /**
             * @param x the x position of the image
             * @param y the y position of the image
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
             * @return the width of the image.
             *
             * @since 1.8.4
             */
            public int getWidth() {
                return width;
            }

            /**
             * @param width the width of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder width(int width) {
                this.width = width;
                return this;
            }

            /**
             * @return the height of the image.
             *
             * @since 1.8.4
             */
            public int getHeight() {
                return height;
            }

            /**
             * @param height the height of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder height(int height) {
                this.height = height;
                return this;
            }

            /**
             * @param width  the width of the image
             * @param height the height of the image
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
             * @return the x position in the image texture to start drawing from.
             *
             * @since 1.8.4
             */
            public int getImageX() {
                return imageX;
            }

            /**
             * @param imageX the x position in the image texture to start drawing from
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder imageX(int imageX) {
                this.imageX = imageX;
                return this;
            }

            /**
             * @return the y position in the image texture to start drawing from.
             *
             * @since 1.8.4
             */
            public int getImageY() {
                return imageY;
            }

            /**
             * @param imageY the y position in the image texture to start drawing from
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder imageY(int imageY) {
                this.imageY = imageY;
                return this;
            }

            /**
             * @param imageX the x position in the image texture to start drawing from
             * @param imageY the y position in the image texture to start drawing from
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder imagePos(int imageX, int imageY) {
                this.imageX = imageX;
                this.imageY = imageY;
                return this;
            }

            /**
             * @return the width of the region to draw.
             *
             * @since 1.8.4
             */
            public int getRegionWidth() {
                return regionWidth;
            }

            /**
             * @param regionWidth the width of the region to draw
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder regionWidth(int regionWidth) {
                this.regionWidth = regionWidth;
                return this;
            }

            /**
             * @return the height of the region to draw.
             *
             * @since 1.8.4
             */
            public int getRegionHeight() {
                return regionHeight;
            }

            /**
             * @param regionHeight the height of the region to draw
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder regionHeight(int regionHeight) {
                this.regionHeight = regionHeight;
                return this;
            }

            /**
             * @param regionWidth  the width of the region to draw
             * @param regionHeight the height of the region to draw
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder regionSize(int regionWidth, int regionHeight) {
                this.regionWidth = regionWidth;
                this.regionHeight = regionHeight;
                return this;
            }

            /**
             * @return the width of the used texture.
             *
             * @since 1.8.4
             */
            public int getTextureWidth() {
                return textureWidth;
            }

            /**
             * @param textureWidth the width of the used texture
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder textureWidth(int textureWidth) {
                this.textureWidth = textureWidth;
                return this;
            }

            /**
             * @return the height of the used texture.
             *
             * @since 1.8.4
             */
            public int getTextureHeight() {
                return textureHeight;
            }

            /**
             * @param textureHeight the height of the used texture
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder textureHeight(int textureHeight) {
                this.textureHeight = textureHeight;
                return this;
            }

            /**
             * @param textureWidth  the width of the used texture
             * @param textureHeight the height of the used texture
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder textureSize(int textureWidth, int textureHeight) {
                this.textureWidth = textureWidth;
                this.textureHeight = textureHeight;
                return this;
            }

            /**
             * @return the color of the image.
             *
             * @since 1.8.4
             */
            public int getColor() {
                return color;
            }

            /**
             * @param color the color of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color) {
                this.color = color;
                return this;
            }

            /**
             * @return the alpha value of the color.
             *
             * @since 1.8.4
             */
            public int getAlpha() {
                return alpha;
            }

            /**
             * @param alpha the alpha value of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder alpha(int alpha) {
                this.alpha = alpha;
                return this;
            }

            /**
             * @param color the color of the image
             * @param alpha the alpha value of the color
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
             * @return the z-index of the image.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            /**
             * @param zIndex the z-index of the image
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            @Override
            public Image createElement() {
                return new Image(x, y, width, height, zIndex, alpha, color, identifier, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
            }
        }
    }
    
    /**
     * @author Wagyourtail
     * @since 1.0.5
     */
    public static class Rect implements RenderElement {
        public float rotation;
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int color;
        public int zIndex;
        
        public Rect(int x1, int y1, int x2, int y2, int color, float rotation, int zIndex) {
            setPos(x1, y1, x2, y2);
            setColor(color);
            this.rotation = MathHelper.wrapDegrees(rotation);
            this.zIndex = zIndex;
        }
        
        public Rect(int x1, int y1, int x2, int y2, int color, int alpha, float rotation, int zIndex) {
            setPos(x1, y1, x2, y2);
            setColor(color, alpha);
            this.rotation = MathHelper.wrapDegrees(rotation);
            this.zIndex = zIndex;
        }
        
        /**
         * @since 1.0.5
         * @param color
         * @return
         */
        public Rect setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
            return this;
        }
        
        /**
         * @since 1.1.8
         * @param color
         * @param alpha
         * @return
         */
        public Rect setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
            return this;
        }
        
        /**
         * @since 1.1.8
         * @param alpha
         * @return
         */
        public Rect setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
            return this;
        }
        
        /**
         * @since 1.1.8
         * @param x1
         * @param y1
         * @param x2
         * @param y2
         * @return
         */
        public Rect setPos(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            return this;
        }
        
        /**
         * @since 1.2.6
         * @param rotation
         * @return
         */
        public Rect setRotation(double rotation) {
            this.rotation = MathHelper.wrapDegrees((float) rotation);
            return this;
        }
    
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            matrices.translate(x1, y1, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            matrices.translate(-x1, -y1, 0);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();

            float fa = ((color >> 24) & 0xFF)/255F;
            float fr = ((color >> 16) & 0xFF)/255F;
            float fg = ((color >> 8) & 0xFF)/255F;
            float fb = (color & 0xFF)/255F;

            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            buf.begin(VertexFormat.DrawMode.TRIANGLE_STRIP,  VertexFormats.POSITION_COLOR);
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            //draw a rectangle using triangle strips
            buf.vertex(matrix, x1, y2, 0).color(fr, fg, fb, fa).next(); // Top-left
            buf.vertex(matrix, x2, y2, 0).color(fr, fg, fb, fa).next(); // Top-right
            buf.vertex(matrix, x1, y1, 0).color(fr, fg, fb, fa).next(); // Bottom-left
            buf.vertex(matrix, x2, y1, 0).color(fr, fg, fb, fa).next(); // Bottom-right
            tess.draw();

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

            matrices.pop();
        }
    
        @Override
        public int getZIndex() {
            return zIndex;
        }

        public static final class Builder extends RenderElementBuilder<Rect> {
            private float rotation = 0;
            private int x1 = 0;
            private int y1 = 0;
            private int x2 = 0;
            private int y2 = 0;
            private int color = 0xFFFFFFFF;
            private int alpha = 0xFF;
            private int zIndex = 0;

            public Builder(IDraw2D<?> draw2D) {
                super(draw2D);
            }

            /**
             * @return the rotation of the rectangle in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotation the rotation of the rectangle in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(float rotation) {
                this.rotation = rotation;
                return this;
            }

            /**
             * @return the first x position of the rectangle.
             *
             * @since 1.8.4
             */
            public int getX1() {
                return x1;
            }

            /**
             * @param x1 the first x position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x1(int x1) {
                this.x1 = x1;
                return this;
            }

            /**
             * @return the first y position of the rectangle.
             *
             * @since 1.8.4
             */
            public int getY1() {
                return y1;
            }

            /**
             * @param y1 the first y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y1(int y1) {
                this.y1 = y1;
                return this;
            }

            /**
             * @param x1 the first x position of the rectangle
             * @param y1 the first y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos1(int x1, int y1) {
                this.x1 = x1;
                this.y1 = y1;
                return this;
            }

            /**
             * @return the second x position of the rectangle.
             *
             * @since 1.8.4
             */
            public int getX2() {
                return x2;
            }

            /**
             * @param x2 the second x position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x2(int x2) {
                this.x2 = x2;
                return this;
            }

            /**
             * @return the second y position of the rectangle.
             *
             * @since 1.8.4
             */
            public int getY2() {
                return y2;
            }

            /**
             * @param y2 the second y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y2(int y2) {
                this.y2 = y2;
                return this;
            }

            /**
             * @param x2 the second x position of the rectangle
             * @param y2 the second y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos2(int x2, int y2) {
                this.x2 = x2;
                this.y2 = y2;
                return this;
            }

            /**
             * @param x1 the first x position of the rectangle
             * @param y1 the first y position of the rectangle
             * @param x2 the second x position of the rectangle
             * @param y2 the second y position of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder pos(int x1, int y1, int x2, int y2) {
                this.x1 = x1;
                this.y1 = y1;
                this.x2 = x2;
                this.y2 = y2;
                return this;
            }

            /**
             * The width will just set the x2 position to {@code x1 + width}.
             *
             * @param width the width of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder width(int width) {
                this.x2 = this.x1 + width;
                return this;
            }

            /**
             * The width will just set the y2 position to {@code y1 + height}.
             *
             * @param height the height of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder height(int height) {
                this.y2 = this.y1 + height;
                return this;
            }

            /**
             * @return the color of the rectangle.
             *
             * @since 1.8.4
             */
            public int getColor() {
                return color;
            }

            /**
             * @param color the color of the rectangle
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color) {
                this.color = color;
                return this;
            }

            /**
             * @return the alpha value of the color.
             *
             * @since 1.8.4
             */
            public int getAlpha() {
                return alpha;
            }

            /**
             * @param alpha the alpha value of the color
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder alpha(int alpha) {
                this.alpha = alpha;
                return this;
            }

            /**
             * @param color the color of the rectangle
             * @param alpha the alpha value of the color
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
             * @return the z-index of the rectangle.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            /**
             * @param zIndex the z-index of the rectangle.
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            @Override
            public Rect createElement() {
                return new Rect(x1, y1, x2, y2, color, alpha, rotation, zIndex);
            }
        }
    }
    
    /**
     * @author Wagyourtail
     * @since 1.0.5
     */
    public static class Text implements RenderElement {
        public net.minecraft.text.Text text;
        public double scale;
        public float rotation;
        public int x;
        public int y;
        public int color;
        public int width;
        public boolean shadow;
        public int zIndex;
        
        public Text(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, float rotation) {
            this.text = net.minecraft.text.Text.literal(text);
            this.x = x;
            this.y = y;
            this.color = color;
            this.width = mc.textRenderer.getWidth(text);
            this.shadow = shadow;
            this.scale = scale;
            this.rotation = MathHelper.wrapDegrees(rotation);
            this.zIndex = zIndex;
        }
        
        public Text(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, float rotation) {
            this.text = text.getRaw();
            this.x = x;
            this.y = y;
            this.color = color;
            this.width = mc.textRenderer.getWidth(this.text);
            this.shadow = shadow;
            this.scale = scale;
            this.rotation = MathHelper.wrapDegrees(rotation);
            this.zIndex = zIndex;
        }
        
        /**
         * @since 1.0.5
         * @param scale
         * @return
         * @throws IllegalArgumentException
         */
        public Text setScale(double scale) throws IllegalArgumentException {
            if (scale == 0) throw new IllegalArgumentException("Scale can't be 0");
            this.scale = scale;
            return this;
        }
        
        /**
         * @since 1.0.5
         * @param rotation
         * @return
         */
        public Text setRotation(double rotation) {
            this.rotation = MathHelper.wrapDegrees((float) rotation);
            return this;
        }
        
        /**
         * @since 1.0.5
         * @param x
         * @param y
         * @return
         */
        public Text setPos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        /**
         * @since 1.0.5
         * @param text
         * @return
         */
        public Text setText(String text) {
            this.text = net.minecraft.text.Text.literal(text);
            this.width = mc.textRenderer.getWidth(text);
            return this;
        }
        
        /**
         * @since 1.2.7
         * @param text
         * @return
         */
        public Text setText(TextHelper text) {
            this.text = text.getRaw();
            this.width = mc.textRenderer.getWidth(this.text);
            return this;
        }
        
        /**
         * @since 1.2.7
         * @return
         */
        public TextHelper getText() {
            return new TextHelper(text);
        }
        
        /**
         * @since 1.0.5
         * @return
         */
        public int getWidth() {
            return this.width;
        }
    
        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            matrices.scale((float) scale, (float) scale, 1);
            matrices.translate(x, y, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            matrices.translate(-x, -y, 0);
            matrices.scale((float) scale, (float) scale, 1);
            if (shadow) mc.textRenderer.drawWithShadow(matrices, text, (int)(x / scale), (int)(y / scale), color);
            else mc.textRenderer.draw(matrices, text, (int)(x / scale), (int)(y / scale), color);
            matrices.pop();
//            RenderSystem.translated(x, y, 0);
//            RenderSystem.rotatef(-rotation, 0, 0, 1);
//            RenderSystem.translated(-x, -y, 0);
//            RenderSystem.scaled(1 / scale, 1 / scale, 1);
        }

        @Override
        public void render3D(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            matrices.scale((float) scale, (float) scale, 1);
            matrices.translate(x, y, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            matrices.translate(-x, -y, 0);
            matrices.scale((float) scale, (float) scale, 1);
            Tessellator tess = Tessellator.getInstance();
            VertexConsumerProvider.Immediate buffer = VertexConsumerProvider.immediate(tess.getBuffer());
            mc.textRenderer.draw(text, (float)(x / scale), (float)(y / scale), color, shadow, matrices.peek().getPositionMatrix(), buffer, true, 0, 0xF000F0);
            buffer.draw();
            matrices.pop();
        }

        @Override
        public int getZIndex() {
            return zIndex;
        }

        public static class Builder extends RenderElementBuilder<Text> {
            private net.minecraft.text.Text text = net.minecraft.text.Text.empty();
            private double scale = 1;
            private float rotation = 0;
            private int x = 0;
            private int y = 0;
            private int color = 0xFFFFFFFF;
            private boolean shadow = false;
            private int zIndex = 0;

            public Builder(IDraw2D<?> draw2D) {
                super(draw2D);
            }

            /**
             * @return the content of the text element.
             *
             * @since 1.8.4
             */
            public TextHelper getText() {
                return new TextHelper(text);
            }

            /**
             * @param text the content of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder text(TextHelper text) {
                this.text = text.getRaw();
                return this;
            }

            /**
             * @param text the content of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder text(String text) {
                this.text = net.minecraft.text.Text.literal(text);
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
             * @param scale the scale of the text element
             * @return self for chaining.
             *
             * @throws IllegalArgumentException if the scale is 0.
             * @since 1.8.4
             */
            public Builder scale(double scale) {
                if (scale <= 0) {
                    throw new IllegalArgumentException("Scale must be positive");
                }
                return this;
            }

            /**
             * @return the rotation of the text element in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotation the rotation of the text element in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(float rotation) {
                this.rotation = rotation;
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
             * @param x the x position of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x(int x) {
                this.x = x;
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
             * @param y the y position of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y(int y) {
                this.y = y;
                return this;
            }

            /**
             * @param x the x position of the text element
             * @param y the y position of the text element
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
             * @return the color of the text element.
             *
             * @since 1.8.4
             */
            public int getColor() {
                return color;
            }

            /**
             * @param color the color of the text element
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder color(int color) {
                this.color = color;
                return this;
            }

            /**
             * @return {@code true} if the text element has a shadow, {@code false} otherwise.
             *
             * @since 1.8.4
             */
            public boolean isShadow() {
                return shadow;
            }

            /**
             * @param shadow whether the text should have a shadow or not
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder shadow(boolean shadow) {
                this.shadow = shadow;
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

            /**
             * @param zIndex the z-index of the text element
             * @return self for chaining.
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            @Override
            public Text createElement() {
                return new Text(new TextHelper(text), x, y, color, zIndex, shadow, scale, rotation);
            }
        }
    }

    public static class Draw2DElement implements RenderElement {

        public float scale;
        public float rotation;
        public int x;
        public int y;
        public int width;
        public int height;
        public final Draw2D draw2D;
        public int zIndex;

        public Draw2DElement(Draw2D draw2D, int x, int y, int width, int height, int zIndex, float scale, float rotation) {
            this.draw2D = draw2D;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.zIndex = zIndex;
            this.scale = scale;
            this.rotation = rotation;
        }

        /**
         * @param zIndex the z-index of this draw2D
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setZIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        @Override
        public int getZIndex() {
            return zIndex;
        }

        /**
         * @return the scale of this draw2D.
         *
         * @since 1.8.4
         */
        public float getScale() {
            return scale;
        }

        /**
         * @param scale the scale
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setScale(float scale) {
            this.scale = scale;
            return this;
        }

        /**
         * @return the rotation of this draw2D.
         *
         * @since 1.8.4
         */
        public float getRotation() {
            return rotation;
        }

        /**
         * @param rotation the rotation
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setRotation(float rotation) {
            this.rotation = rotation;
            return this;
        }

        /**
         * @return the x position of this draw2D.
         *
         * @since 1.8.4
         */
        public int getX() {
            return x;
        }

        /**
         * @param x the x position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setX(int x) {
            this.x = x;
            return this;
        }

        /**
         * @return the y position of this draw2D.
         *
         * @since 1.8.4
         */
        public int getY() {
            return y;
        }

        /**
         * @param y the y position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setY(int y) {
            this.y = y;
            return this;
        }

        /**
         * @param x the x position
         * @param y the y position
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setPos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        /**
         * @return the width of this draw2D.
         *
         * @since 1.8.4
         */
        public int getWidth() {
            return width;
        }

        /**
         * @param width the width
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setWidth(int width) {
            this.width = width;
            return this;
        }

        /**
         * @return the height of this draw2D.
         *
         * @since 1.8.4
         */
        public int getHeight() {
            return height;
        }

        /**
         * @param height the height
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setHeight(int height) {
            this.height = height;
            return this;
        }

        /**
         * @param width  the width
         * @param height the height
         * @return self for chaining.
         *
         * @since 1.8.4
         */
        public Draw2DElement setSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * @return the internal draw2D this draw2D element is wrapping.
         *
         * @since 1.8.4
         */
        public Draw2D getDraw2D() {
            return draw2D;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            matrices.translate(x, y, 0);
            matrices.scale(scale, scale, 1);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));

            draw2D.render(matrices);

            matrices.pop();
        }

        public static class Builder extends RenderElementBuilder<Draw2DElement> {
            private final Draw2D draw2D;
            private int x = 0;
            private int y = 0;
            private int width = 0;
            private int height = 0;
            private int zIndex = 0;
            private float scale = 1;
            private float rotation = 0;

            public Builder(IDraw2D<?> parent, Draw2D draw2D) {
                super(parent);
                this.draw2D = draw2D;
            }

            /**
             * @return the x position of the draw2D.
             *
             * @since 1.8.4
             */
            public int getX() {
                return x;
            }

            /**
             * @param x the x position of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder x(int x) {
                this.x = x;
                return this;
            }

            /**
             * @return the y position of the draw2D.
             *
             * @since 1.8.4
             */
            public int getY() {
                return y;
            }

            /**
             * @param y the y position of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder y(int y) {
                this.y = y;
                return this;
            }

            /**
             * @param x the x position of the draw2D
             * @param y the y position of the draw2D
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
             * @return the width of the draw2D.
             *
             * @since 1.8.4
             */
            public int getWidth() {
                return width;
            }

            /**
             * @param width the width of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder width(int width) {
                this.width = width;
                return this;
            }

            /**
             * @return the height of the draw2D.
             *
             * @since 1.8.4
             */
            public int getHeight() {
                return height;
            }

            /**
             * @param height the height of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder height(int height) {
                this.height = height;
                return this;
            }

            /**
             * @param width  the width of the draw2D
             * @param height the height of the draw2D
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
             * @return the z-index of the draw2D.
             *
             * @since 1.8.4
             */
            public Builder zIndex(int zIndex) {
                this.zIndex = zIndex;
                return this;
            }

            /**
             * @return the z-index of the draw2D.
             *
             * @since 1.8.4
             */
            public int getZIndex() {
                return zIndex;
            }

            /**
             * @return the scale of the draw2D.
             *
             * @since 1.8.4
             */
            public float getScale() {
                return scale;
            }

            /**
             * @param scale the scale of the draw2D
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder scale(float scale) {
                this.scale = scale;
                return this;
            }

            /**
             * @return the rotation of the draw2D in degrees.
             *
             * @since 1.8.4
             */
            public float getRotation() {
                return rotation;
            }

            /**
             * @param rotation the rotation of the draw2D in degrees
             * @return self for chaining.
             *
             * @since 1.8.4
             */
            public Builder rotation(float rotation) {
                this.rotation = rotation;
                return this;
            }

            @Override
            protected Draw2DElement createElement() {
                return new Draw2DElement(draw2D, x, y, width, height, zIndex, scale, rotation);
            }
        }
    }

}
