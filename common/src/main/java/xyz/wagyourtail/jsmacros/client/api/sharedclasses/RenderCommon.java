package xyz.wagyourtail.jsmacros.client.api.sharedclasses;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;

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
         * @throws Exception
         */
        public Item setScale(double scale) throws Exception {
            if (scale == 0) throw new Exception("Scale can't be 0");
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
            matrices.scale((float) scale, (float) scale, 1);
            matrices.translate(x, y, 0);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation));
            matrices.translate(-x, -y, 0);
            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                i.renderGuiItemIcon(item,(int) (x / scale), (int) (y / scale));
                if (overlay) i.renderGuiItemOverlay(mc.textRenderer, item, (int) (x / scale), (int) (y / scale), ovText);
            }
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
            MatrixStack ms = RenderSystem.getModelViewStack();
            ms.push();
            ms.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                i.zOffset = -100f;
                i.renderGuiItemIcon(item,(int) (x / scale), (int) (y / scale));
                i.zOffset = -200f;
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
            this.text = new LiteralText(text);
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
         * @throws Exception
         */
        public Text setScale(double scale) throws Exception {
            if (scale == 0) throw new Exception("Scale can't be 0");
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
            this.text = new LiteralText(text);
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
    
    }
}
