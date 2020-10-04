package xyz.wagyourtail.jsmacros.api.sharedclasses;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.api.helpers.TextHelper;

/**
 * @author Wagyourtail
 * @since 1.2.3
 */
public class RenderCommon {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    /**
     * @author Wagyourtail
     * @since 1.0.5
     */
    public static class Item {
        public ItemStack item;
        public String ovText;
        public boolean overlay;
        public double scale;
        public float rotation;
        public int x;
        public int y;
        
        public Item(int x, int y, String id, boolean overlay, double scale, float rotation) {
            this.x = x;
            this.y = y;
            this.setItem(id, 1);
            this.overlay = overlay;
            this.scale = scale;
            this.rotation = rotation;
        }
        
        public Item(int x, int y, ItemStackHelper i, boolean overlay, double scale, float rotation) {
            this.x = x;
            this.y = y;
            this.item = i.getRaw();
            this.overlay = overlay;
            this.scale = scale;
            this.rotation = rotation;
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
        public Item setRotation(float rotation) {
            this.rotation = MathHelper.fwrapDegrees(rotation);
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
            net.minecraft.item.Item it = (net.minecraft.item.Item)Registry.ITEM.get(new Identifier(id));
            if (it != null) this.item = new ItemStack(it, count);
            return this;
        }
        
        /**
         * @since 1.0.5 [citation needed]
         * @return
         */
        public ItemStackHelper getItem() {
            return new ItemStackHelper(item);
        }
        
        public void render(MatrixStack matrixStack) {
            RenderSystem.translated(x, y, 0);
            RenderSystem.rotatef(rotation, 0, 0, 1);
            RenderSystem.translated(-x, -y, 0);
            RenderSystem.scaled(scale, scale, 1);
            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                i.renderGuiItemIcon(item, x, y);
                if (overlay) i.renderGuiItemOverlay(mc.textRenderer, item, x, y, ovText);
            }
            RenderSystem.translated(-x, -y, 0);
            RenderSystem.rotatef(-rotation, 0, 0, 1);
            RenderSystem.translated(x, y, 0);
            RenderSystem.scaled(1 / scale, 1 / scale, 1);
        }
    }
    
    /**
     * @author Wagyourtail
     * @since 1.2.3
     */
    public static class Image {
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
        
        public Image(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, float rotation) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
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
        public Image setRotation(float rotation) {
            this.rotation = MathHelper.fwrapDegrees(rotation);
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
        
        public void render(MatrixStack matrixStack) {
            RenderSystem.translated(x, y, 0);
            RenderSystem.rotatef(rotation, 0, 0, 1);
            RenderSystem.translated(-x, -y, 0);
            mc.getTextureManager().bindTexture(imageid);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrixStack, x, y, width, height, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight);
            RenderSystem.disableBlend();
            RenderSystem.translated(-x, -y, 0);
            RenderSystem.rotatef(-rotation, 0, 0, 1);
            RenderSystem.translated(x, y, 0);
        }
    }
    
    /**
     * @author Wagyourtail
     * @since 1.0.5
     */
    public static class Rect {
        public float rotation;
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int color;
        
        public Rect(int x1, int y1, int x2, int y2, int color, float rotation) {
            setPos(x1, y1, x2, y2);
            setColor(color);
            this.rotation = MathHelper.fwrapDegrees(rotation);
        }
        
        public Rect(int x1, int y1, int x2, int y2, int color, int alpha, float rotation) {
            setPos(x1, y1, x2, y2);
            setColor(color, alpha);
            this.rotation = MathHelper.fwrapDegrees(rotation);
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
        public Rect setRotation(float rotation) {
            this.rotation = MathHelper.fwrapDegrees(rotation);
            return this;
        }
        
        public void render(MatrixStack matrixStack) {
            RenderSystem.translated(x1, y1, 0);
            RenderSystem.rotatef(rotation, 0, 0, 1);
            RenderSystem.translated(-x1, -y1, 0);
            Draw2D.fill(matrixStack, x1, y1, x2, y2, color);
            RenderSystem.translated(x1, y1, 0);
            RenderSystem.rotatef(-rotation, 0, 0, 1);
            RenderSystem.translated(-x1, -y1, 0);
        }
    }
    
    /**
     * @author Wagyourtail
     * @since 1.0.5
     */
    public static class Text {
        public net.minecraft.text.Text text;
        public double scale;
        public float rotation;
        public int x;
        public int y;
        public int color;
        public int width;
        public boolean shadow;
        
        public Text(String text, int x, int y, int color, boolean shadow, double scale, float rotation) {
            this.text = new LiteralText(text);
            this.x = x;
            this.y = y;
            this.color = color;
            this.width = mc.textRenderer.getWidth(text);
            this.shadow = shadow;
            this.scale = scale;
            this.rotation = MathHelper.fwrapDegrees(rotation);
        }
        
        public Text(TextHelper text, int x, int y, int color, boolean shadow, double scale, float rotation) {
            this.text = text.getRaw();
            this.x = x;
            this.y = y;
            this.color = color;
            this.width = mc.textRenderer.getWidth(this.text);
            this.shadow = shadow;
            this.scale = scale;
            this.rotation = MathHelper.fwrapDegrees(rotation);
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
        public Text setRotation(float rotation) {
            this.rotation = MathHelper.fwrapDegrees(rotation);
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
        
        public void render(MatrixStack matrixStack) {
            RenderSystem.translated(x, y, 0);
            RenderSystem.rotatef(rotation, 0, 0, 1);
            RenderSystem.translated(-x, -y, 0);
            RenderSystem.scaled(scale, scale, 1);
            if (shadow) mc.textRenderer.drawWithShadow(matrixStack, text, (int)(x / scale), (int)(y / scale), color);
            else mc.textRenderer.draw(matrixStack, text, (int)(x / scale), (int)(y / scale), color);
            RenderSystem.scaled(1 / scale, 1 / scale, 1);
            RenderSystem.translated(x, y, 0);
            RenderSystem.rotatef(-rotation, 0, 0, 1);
            RenderSystem.translated(-x, -y, 0);
        }
    }
}
