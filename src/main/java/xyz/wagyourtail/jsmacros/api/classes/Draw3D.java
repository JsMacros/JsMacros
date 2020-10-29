package xyz.wagyourtail.jsmacros.api.classes;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.wagyourtail.jsmacros.api.sharedclasses.PositionCommon;

/**
 * @author Wagyourtail
 *
 * @since 1.0.6
 *
 */
public class Draw3D {
    private final List<Box> boxes = new ArrayList<>();
    private final List<Line> lines = new ArrayList<>();
    
    /**
     * @since 1.0.6
     * 
     * @return
     */
    public List<Box> getBoxes() {
        return ImmutableList.copyOf(boxes);
    }
    
    /**
     * @sine 1.0.6
     * 
     * @return
     */
    public List<Line> getLines() {
        return ImmutableList.copyOf(lines);
    }
    
    /**
     * @since 1.0.6
     * 
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param fillColor
     * @param fill
     * @return The {@link Box} you added.
     */
    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill) {
        Box b = new Box(x1, y1, z1, x2, y2, z2, color, fillColor, fill);
        synchronized (boxes) {
            boxes.add(b);
        }
        return b;
    }
    
    /**
     * @since 1.1.8
     * 
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param alpha
     * @param fillColor
     * @param fillAlpha
     * @param fill
     * @return the {@link Box} you added.
     */
    public Box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill) {
        Box b = new Box(x1, y1, z1, x2, y2, z2, color, alpha, fillColor, fillAlpha, fill);
        synchronized (boxes) {
            boxes.add(b);
        }
        return b;
    }
    
    /**
     * @since 1.0.6
     * 
     * @param b
     * @return
     */
    public Draw3D removeBox(Box b) {
        synchronized (boxes) {
            boxes.remove(b);
        }
        return this;
    }
    
    
    /**
     * @since 1.0.6
     * 
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @return the {@link Line} you added.
     */
    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        Line l = new Line(x1, y1, z1, x2, y2, z2, color);
        synchronized (lines) {
            lines.add(l);
        }
        return l;
    }
    
    /**
     * @since 1.1.8
     * 
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param color
     * @param alpha
     * @return the {@link Line} you added.
     */
    public Line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha) {
        Line l = new Line(x1, y1, z1, x2, y2, z2, color, alpha);
        synchronized (lines) {
            lines.add(l);
        }
        return l;
    }
    
    /**
     * @since 1.0.6
     * 
     * @param l
     * @return
     */
    public Draw3D removeLine(Line l) {
        synchronized (lines) {
            lines.remove(l);
        }
        return this;
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
        synchronized (boxes) {
            for (Box b : boxes) {
                b.render();
            }
        }
        
        synchronized (lines) {
            for (Line l : lines) {
                l.render();
            }
        }
        
        RenderSystem.popMatrix();
        
        //reset
        RenderSystem.matrixMode(5888);
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
    
    public static class Box {
        public PositionCommon.Vec3D pos;
        public int color;
        public int fillColor;
        public boolean fill;
        
        public Box(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color);
            setFillColor(fillColor);
            this.fill = fill;
        }
        
        public Box(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color, alpha);
            setFillColor(fillColor, fillAlpha);
            this.fill = fill;
        }
        
        /**
         * @since 1.0.6
         * 
         * @param x1
         * @param y1
         * @param z1
         * @param x2
         * @param y2
         * @param z2
         */
        public void setPos(double x1, double y1, double z1, double x2, double y2, double z2) {
            pos = new PositionCommon.Vec3D(x1, y1, z1, x2, y2, z2);
        }
        
        
        
        /**
         * @since 1.0.6
         * 
         * @param color
         */
        public void setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
        }
        
        /**
         * @since 1.0.6
         * 
         * @param fillColor
         */
        public void setFillColor(int fillColor) {
            this.fillColor = fillColor;
        }
        
        /**
         * @since 1.1.8
         * 
         * @param color
         * @param alpha
         */
        public void setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
        }
        
        /**
         * @since 1.1.8
         * 
         * @param alpha
         */
        public void setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
        }
        
        /**
         * @since 1.1.8
         * 
         * @param fillColor
         * @param alpha
         */
        public void setFillColor(int fillColor, int alpha) {
            this.fillColor = fillColor | (alpha << 24);
        }
        
        /**
         * @since 1.1.8
         * 
         * @param alpha
         */
        public void setFillAlpha(int alpha) {
            this.fillColor = (fillColor & 0xFFFFFF) | (alpha << 24);
        }
        
        /**
         * @since 1.0.6
         * 
         * @param fill
         */
        public void setFill(boolean fill) {
            this.fill = fill;
        }
        
        public void render() {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            
            if (this.fill) {
                float fa = ((fillColor >> 24) & 0xFF)/255F;
                float fr = ((fillColor >> 16) & 0xFF)/255F;
                float fg = ((fillColor >> 8) & 0xFF)/255F;
                float fb = (fillColor & 0xFF)/255F;
                
                buf.begin(GL11.GL_TRIANGLE_STRIP,  VertexFormats.POSITION_COLOR);
                
                //why'd they change it from build box to draw box... it's not calling tess.draw()
                WorldRenderer.drawBox(buf, pos.x1, pos.y1, pos.z1, pos.x2, pos.y2, pos.z2, fr, fg, fb, fa);
                
                tess.draw();
            }
            
            buf.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
            
            buf.vertex(pos.x1, pos.y1, pos.z1).color(r, g, b, a).next();
            buf.vertex(pos.x1, pos.y1, pos.z2).color(r, g, b, a).next();
            buf.vertex(pos.x2, pos.y1, pos.z2).color(r, g, b, a).next();
            buf.vertex(pos.x2, pos.y1, pos.z1).color(r, g, b, a).next();
            buf.vertex(pos.x1, pos.y1, pos.z1).color(r, g, b, a).next();
            buf.vertex(pos.x1, pos.y2, pos.z1).color(r, g, b, a).next();
            buf.vertex(pos.x2, pos.y2, pos.z1).color(r, g, b, a).next();
            buf.vertex(pos.x2, pos.y2, pos.z2).color(r, g, b, a).next();
            buf.vertex(pos.x1, pos.y2, pos.z2).color(r, g, b, a).next();
            buf.vertex(pos.x1, pos.y2, pos.z1).color(r, g, b, a).next();
            buf.vertex(pos.x1, pos.y1, pos.z2).color(r, g, b, 0).next();
            buf.vertex(pos.x1, pos.y2, pos.z2).color(r, g, b, a).next();
            buf.vertex(pos.x2, pos.y1, pos.z2).color(r, g, b, 0).next();
            buf.vertex(pos.x2, pos.y2, pos.z2).color(r, g, b, a).next();
            buf.vertex(pos.x2, pos.y1, pos.z1).color(r, g, b, 0).next();
            buf.vertex(pos.x2, pos.y2, pos.z1).color(r, g, b, a).next();
            
            tess.draw();
        }
    }
    
    public static class Line {
        public PositionCommon.Vec3D pos;
        public int color;
        public Line(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color);
        }
        
        public Line(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color, alpha);
        }
                
        /**
         * @since 1.0.6
         * 
         * @param x1
         * @param y1
         * @param z1
         * @param x2
         * @param y2
         * @param z2
         */
        public void setPos(double x1, double y1, double z1, double x2, double y2, double z2) {
            pos = new PositionCommon.Vec3D(x1, y1, z1, x2, y2, z2);
        }
        
        /**
         * @sine 1.0.6
         * 
         * @param color
         */
        public void setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
        }
        
        /**
         * @since 1.1.8
         * 
         * @param color
         * @param alpha
         */
        public void setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
        }
        
        /**
         * @since 1.1.8
         * 
         * @param alpha
         */
        public void setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
        }
        
        public void render() {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            buf.begin(GL11.GL_LINE_STRIP,  VertexFormats.POSITION_COLOR);
            buf.vertex(pos.x1, pos.y1, pos.z1).color(r, g, b, a).next();
            buf.vertex(pos.x1, pos.y1, pos.z1).color(r, g, b, a).next();
            buf.vertex(pos.x2, pos.y2, pos.z2).color(r, g, b, a).next();
            tess.draw();
        }
    }
}