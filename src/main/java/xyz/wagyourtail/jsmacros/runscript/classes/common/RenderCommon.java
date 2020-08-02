package xyz.wagyourtail.jsmacros.runscript.classes.common;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;
import xyz.wagyourtail.jsmacros.runscript.classes.Draw2D;

public class RenderCommon {
    public static class item {
        public int x;
        public int y;
        public ItemStack item;
        public boolean overlay;
        public String ovText;
        
        public item(int x, int y, String id, boolean overlay) {
            this.x = x;
            this.y = y;
            this.setItem(id, 1);
            this.overlay = overlay;
        }
        
        public item(int x, int y, ItemStackHelper i, boolean overlay) {
            this.x = x;
            this.y = y;
            this.item = i.getRaw();
            this.overlay = overlay;
        }
        
        public item setPos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        public item setOverlay(boolean overlay) {
            this.overlay = overlay;
            return this;
        }
        
        public item setOverlayText(String ovText) {
            this.ovText = ovText;
            return this;
        }
        
        public item setItem(ItemStackHelper i) {
            if (i != null) this.item = i.getRaw();
            else this.item = null;
            return this;
        }
        
        public item setItem(String id, int count) {
            Item it = (Item)Registry.ITEM.get(new Identifier(id));
            if (it != null) this.item = new ItemStack(it, count);
            return this;
        }
        
        public ItemStackHelper getItem() {
            return new ItemStackHelper(item);
        }
        
        public void render(MatrixStack matrixStack) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                i.renderGuiItemIcon(item, x, y);
                if (overlay) i.renderGuiItemOverlay(mc.textRenderer, item, x, y, ovText);
            }
        }
    }
    
    public static class image {
        private Identifier imageid;
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
        public image(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
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
        }
        
        public void setPos(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public void setImage(String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
            imageid = new Identifier(id);
            this.imageX = imageX;
            this.imageY = imageY;
            this.regionWidth = regionWidth;
            this.regionHeight = regionHeight;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }
        
        public String getImage() {
            return imageid.toString();
        }
        
        public void render(MatrixStack matrixStack) {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.getTextureManager().bindTexture(imageid);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrixStack, x, y, width, height, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight);
            RenderSystem.disableBlend();
        }
    }
    
    public static class rect {
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int color;
        
        public rect(int x1, int y1, int x2, int y2, int color) {
            setPos(x1, y1, x2, y2);
            setColor(color);
        }
        
        public rect(int x1, int y1, int x2, int y2, int color, int alpha) {
            setPos(x1, y1, x2, y2);
            setColor(color, alpha);
        }
        
        public rect setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
            return this;
        }
        
        public rect setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
            return this;
        }
        
        public rect setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
            return this;
        }
        
        public rect setPos(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            return this;
        }
        
        public void render(MatrixStack matrixStack) {
            Draw2D.fill(matrixStack, x1, y1, x2, y2, color);
        }
    }
    
    public static class text {
        public String text;
        public int x;
        public int y;
        public int color;
        public int width;
        public boolean shadow;
        
        public text(String text, int x, int y, int color, boolean shadow) {
            MinecraftClient mc = MinecraftClient.getInstance();
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
            this.width = mc.textRenderer.getWidth(text);
            this.shadow = shadow;
        }
        
        public text setPos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        public text setText(String text) {
            MinecraftClient mc = MinecraftClient.getInstance();
            this.text = text;
            this.width = mc.textRenderer.getWidth(text);
            return this;
        }
        
        public int getWidth() {
            return this.width;
        }
        
        public void render(MatrixStack matrixStack) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (shadow) mc.textRenderer.drawWithShadow(matrixStack, text, x, y, color);
            else mc.textRenderer.draw(matrixStack, text, x, y, color);
        }
    }
}
