package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.runscript.classes.OverlayHud;
import xyz.wagyourtail.jsmacros.runscript.classes.Screen;

public class hudFunctions {
    public static ArrayList<OverlayHud> overlays = new ArrayList<>();
    
    
    public Screen createScreen(String title, boolean dirtBG) {
        return new Screen(title, dirtBG);
    }
    
    public void openScreen(Screen s) {
        jsMacros.getMinecraft().openScreen(s);
    }
    
    public OverlayHud createOverlay() {
        return new OverlayHud();
    }
    
    public void registerOverlay(OverlayHud overlay) {
        overlay.init();
        if (!overlays.contains(overlay)) overlays.add(overlay);
    }
    
    public void unregisterOverlay(OverlayHud overlay) {
        overlays.remove(overlay);
    }
    
    public ArrayList<OverlayHud> listOverlays() {
        return overlays;
    }
    
    public void clearOverlays() {
        overlays.clear();
    }
}
