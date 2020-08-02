package xyz.wagyourtail.jsmacros.runscript.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;
import xyz.wagyourtail.jsmacros.runscript.classes.common.RenderCommon.image;
import xyz.wagyourtail.jsmacros.runscript.classes.common.RenderCommon.item;
import xyz.wagyourtail.jsmacros.runscript.classes.common.RenderCommon.rect;
import xyz.wagyourtail.jsmacros.runscript.classes.common.RenderCommon.text;

public class Draw2D extends DrawableHelper {
    public List<text> textFields = new ArrayList<>();
    public List<rect> rectFields = new ArrayList<>();
    public List<item> itemFields = new ArrayList<>();
    public List<image> imageFields = new ArrayList<>();
    public Consumer<Draw2D> onInit;
    public Consumer<String> catchInit;
    
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
    
    public List<text> getTexts() {
        return textFields;
    }
    
    public List<rect> getRects() {
        return rectFields;
    }
    
    public List<item> getItems() {
        return itemFields;
    }
    
    public List<image> getImages() {
        return imageFields;
    }
    
    
    public text addText(String text, int x, int y, int color, boolean shadow) {
        text t = new text(text, x, y, color, shadow);
        textFields.add(t);
        return t;
        
    }
    
    public Draw2D removeText(text t) {
        textFields.remove(t);
        return this;
    }
    
    public image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        image i = new image(x, y, width, height, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight);
        imageFields.add(i);
        return i;
    }
    
    public Draw2D removeImage(image i) {
        imageFields.remove(i);
        return this;
    }
    
    public rect addRect(int x1, int y1, int x2, int y2, int color) {
        rect r = new rect(x1, y1, x2, y2, color);
        rectFields.add(r);
        return r;
    }
    
    public rect addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        rect r = new rect(x1, y1, x2, y2, color, alpha);
        rectFields.add(r);
        return r;
    }
    
    public Draw2D removeRect(rect r) {
        rectFields.remove(r);
        return this;
    }
    
    public item addItem(int x, int y, String id, boolean overlay) {
        item i = new item(y, y, id, overlay);
        itemFields.add(i);
        return i;
    }
    
    public item addItem(int x, int y, ItemStackHelper item, boolean overlay) {
        item i = new item(x, y, item, overlay);
        itemFields.add(i);
        return i;
    }
    
    public Draw2D removeItem(item i) {
        itemFields.remove(i);
        return this;
    }
    
    public void init() {
        textFields.clear();
        rectFields.clear();
        itemFields.clear();
        if (onInit != null) {
            try {
                onInit.accept(this);
            } catch(Exception e) {
                e.printStackTrace();
                try {
                    if (catchInit != null) catchInit.accept(e.toString());
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        }
    }
    
    
    public void render(MatrixStack matrixStack) {
        if (matrixStack == null) return;
        
        RenderSystem.pushMatrix();
        for (rect r : ImmutableList.copyOf(this.rectFields)) {
            r.render(matrixStack);
        }
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        for (item i : ImmutableList.copyOf(this.itemFields)) {
            i.render(matrixStack);
        }
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        for (image i : ImmutableList.copyOf(this.imageFields)) {
            i.render(matrixStack);
        }
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        for (text t : ImmutableList.copyOf(this.textFields)) {
            t.render(matrixStack);
        }
        RenderSystem.popMatrix();
    }
}
