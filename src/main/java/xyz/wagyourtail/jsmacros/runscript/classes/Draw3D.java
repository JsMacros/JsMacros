package xyz.wagyourtail.jsmacros.runscript.classes;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Draw3D {
    public ArrayList<box> boxes = new ArrayList<>();
    public ArrayList<line> lines = new ArrayList<>();
    
    public box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill) {
        box b = new box(x1, y1, z1, x2, y2, z2, color, fillColor, fill);
        boxes.add(b);
        return b;
    }
    
    public void removeBox(box b) {
        boxes.remove(b);
    }
    
    public ArrayList<box> listBoxes() {
        return boxes;
    }
    
    public line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        line l = new line(x1, y1, z1, x2, y2, z2, color);
        lines.add(l);
        return l;
    }
    
    public void removeLine(line l) {
        lines.remove(l);
    }
    
    public ArrayList<line> listLines() {
        return lines;
    }
    
    public void render() {
        MinecraftClient mc  = MinecraftClient.getInstance();
        //setup
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.5F);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.matrixMode(5889);
        
        RenderSystem.pushMatrix();
        
        // offsetRender
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();
        RenderSystem.rotatef(MathHelper.wrapDegrees(camera.getPitch()), 1, 0, 0);
        RenderSystem.rotatef(MathHelper.wrapDegrees(camera.getYaw() + 180.0F), 0, 1, 0);
        RenderSystem.translated(-camPos.x, -camPos.y, -camPos.z);
        
        //render
        for (box b : boxes) {
            b.render();
        }
        
        for (line l : lines) {
            l.render();
        }
        
        RenderSystem.popMatrix();
        
        //reset
        RenderSystem.matrixMode(5888);
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    
    public static class box {
        public double x1;
        public double y1;
        public double z1;
        public double x2;
        public double y2;
        public double z2;
        public int color;
        public int fillColor;
        public boolean fill;
        public box(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.color = color;
            this.fillColor = fillColor;
            this.fill = fill;
        }
        
        public void render() {
            float a = ((color >> 24) & 0xFF)/255F;
            float r = ((color >> 16) & 0xFF)/255F;
            float g = ((color >> 8) & 0xFF)/255F;
            float b = (color & 0xFF)/255F;

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            buf.begin(GL11.GL_TRIANGLE_STRIP,  VertexFormats.POSITION_COLOR);
            WorldRenderer.drawBox(buf, x1, y1, z1, x2, y2, z2, r, g, b, a);
            tess.draw();
            
            
        }
    }
    
    public static class line {
        public double x1;
        public double y1;
        public double z1;
        public double x2;
        public double y2;
        public double z2;
        public int color;
        public line(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.color = color;
        }
        
        public void render() {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            buf.begin(GL11.GL_LINE_STRIP,  VertexFormats.POSITION_COLOR);
            buf.vertex(x1, y1, z1).color(r, g, b, a).next();
            buf.vertex(x1, y1, z1).color(r, g, b, a).next();
            buf.vertex(x2, y2, z2).color(r, g, b, a).next();
            tess.draw();
        }
    }
}
