package xyz.wagyourtail.jsmacros.runscript.classes;

import java.util.ArrayList;
import java.util.function.Consumer;

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

public class Draw2D extends DrawableHelper {
    public ArrayList<text> textFields = new ArrayList<>();
    public ArrayList<rect> rectFields = new ArrayList<>();
    public ArrayList<item> itemFields = new ArrayList<>();
    public Consumer<Draw2D> onInit;
    
    public MinecraftClient mc;
    
    public Draw2D() {
        this.mc = MinecraftClient.getInstance();
    }
    
    public int getWidth() {
        return mc.getWindow().getScaledWidth();
    }
    
    public int getHeight() {
        return mc.getWindow().getScaledHeight();
    }
    
    public ArrayList<text> getTexts() {
        return textFields;
    }
    
    public ArrayList<rect> getRects() {
        return rectFields;
    }
    
    public ArrayList<item> getItems() {
        return itemFields;
    }
    
    
    public text addText(String text, int x, int y, int color, boolean shadow) {
        text t = new text(text, x, y, color, shadow);
        textFields.add(t);
        return t;
        
    }
    
    public void removeText(text t) {
        textFields.remove(t);
    }
    
    public rect addRect(int x1, int y1, int x2, int y2, int color) {
        rect r = new rect(x1, y1, x2, y2, color);
        rectFields.add(r);
        return r;
    }
    
    public void removeRect(rect r) {
        rectFields.remove(r);
    }
    
    public item addItem(int x, int y, String id) {
        item i = new item(y, y, id);
        itemFields.add(i);
        return i;
    }
    
    public item addItem(int x, int y, ItemStackHelper item) {
        item i = new item(y, y, item);
        itemFields.add(i);
        return i;
    }
    
    public void removeItem(item i) {
        itemFields.remove(i);
    }
    
    public void init() {
        textFields.clear();
        if (onInit != null) {
            onInit.accept(this);
        }
    }
    
    
    public void render(MatrixStack matrixStack) {
        RenderSystem.pushMatrix();
        for (rect r : rectFields) {
            r.render(matrixStack);
        }
        RenderSystem.popMatrix();
        for (item i : itemFields) {
            i.render(matrixStack);
        }
        RenderSystem.pushMatrix();
        for (text t : textFields) {
            t.render(matrixStack);
        }
        RenderSystem.popMatrix();
    }
    
    public static class item {
        public int x;
        public int y;
        public ItemStack item;
        
        public item(int x, int y, String id) {
            this.x = x;
            this.y = y;
            this.setItem(id, 1);
        }
        
        public item(int x, int y, ItemStackHelper i) {
            this.x = x;
            this.y = y;
            this.item = i.getRaw();
        }
        
        public void setPos(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void setItem(ItemStackHelper i) {
            this.item = i.getRaw();
        }
        
        public void setItem(String id, int count) {
            Item it = (Item)Registry.ITEM.get(new Identifier(id));
            if (it != null) this.item = new ItemStack(it, count);
        }
        
        public void render(MatrixStack matrixStack) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (item != null) {
                ItemRenderer i = mc.getItemRenderer();
                i.renderGuiItemIcon(item, x, y);
                i.renderGuiItemOverlay(mc.textRenderer, item, x, y);
            }
        }
    }
    
    public static class rect {
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public int color;
        
        public rect(int x1, int y1, int x2, int y2, int color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
        
        public void setColor(int color) {
            this.color = color;
        }
        
        public void setPos(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
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
        
        public void setPos(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public void setText(String text) {
            MinecraftClient mc = MinecraftClient.getInstance();
            this.text = text;
            this.width = mc.textRenderer.getWidth(text);
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
