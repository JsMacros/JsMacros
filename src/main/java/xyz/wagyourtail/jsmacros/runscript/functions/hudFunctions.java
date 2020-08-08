package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;

import xyz.wagyourtail.jsmacros.runscript.classes.Draw3D;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.runscript.classes.Draw2D;
import xyz.wagyourtail.jsmacros.runscript.classes.Screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class hudFunctions extends Functions {
    public static final List<Draw2D> overlays = new ArrayList<>();
    public static final List<Draw3D> renders = new ArrayList<>();
    public static final Queue<Runnable> renderTaskQueue = Queues.newConcurrentLinkedQueue();
    
    public hudFunctions(String libName) {
        super(libName);
    }
    
    public hudFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    public Screen createScreen(String title, boolean dirtBG) {
        return new Screen(title, dirtBG);
    }
    
    public boolean openScreen(Screen s) {
        return renderTaskQueue.add(() -> {
            MinecraftClient.getInstance().openScreen(s);            
        });
    }
    
    public String getOpenScreen() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return jsMacros.getScreenName(mc.currentScreen);
    }
    
    public boolean isContainer() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.currentScreen instanceof HandledScreen;
    }
    
    public Draw2D createDraw2D() {
        return new Draw2D();
    }
    
    public void registerDraw2D(Draw2D overlay) {
        overlay.init();
        if (!overlays.contains(overlay)) overlays.add(overlay);
    }
    
    public void unregisterDraw2D(Draw2D overlay) {
        overlays.remove(overlay);
    }
    
    public List<Draw2D> listDraw2Ds() {
        return ImmutableList.copyOf(overlays);
    }
    
    public void clearDraw2Ds() {
        overlays.clear();
    }
    
    public Draw3D createDraw3D() {
        return new Draw3D();
    }
    
    public void registerDraw3D(Draw3D draw) {
        if (!renders.contains(draw)) renders.add(draw);
    }
    
    public void unregisterDraw3D(Draw3D draw) {
        renders.remove(draw);
    }
    
    public List<Draw3D> listDraw3Ds() {
        return ImmutableList.copyOf(renders);
    }
    
    public void clearDraw3Ds() {
        renders.clear();
    }
    
    public double getMouseX() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.mouse.getX() * (double)mc.getWindow().getScaledWidth() / (double)mc.getWindow().getWidth();
    }
    
    public double getMouseY() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.mouse.getY() * (double)mc.getWindow().getScaledHeight() / (double)mc.getWindow().getHeight();
    }
}
