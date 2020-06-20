package xyz.wagyourtail.jsmacros.runscript.functions;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.runscript.classes.OverlayHud;
import xyz.wagyourtail.jsmacros.runscript.classes.Screen;

public class hudFunctions {
    
    public Screen createScreen(String title, boolean dirtBG) {
        return new Screen(title, dirtBG);
    }
    
    public void openScreen(Screen s) {
        jsMacros.getMinecraft().openScreen(s);
    }
    
    public OverlayHud createOverlay() {
        return new OverlayHud();
    }
}
