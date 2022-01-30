package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.VideoMode;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.ScriptScreen;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * Functions for displaying stuff in 2 to 3 dimensions
 * 
 * An instance of this class is passed to scripts as the {@code Hud} variable.
 * 
 * @since 1.0.5
 * 
 * @author Wagyourtail
 */
 @Library("Window")
 @SuppressWarnings("unused")
public class FWindow extends BaseLibrary {
    
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * @return get game window width
     */
    public int getWindowWidth() {
        return mc.getWindow().getWidth();
    }

    /**
     * @return get game window height
     */
    public int getWindowHeight() {
        return mc.getWindow().getHeight();
    }

    /**
     * @return set game window size
     */
    public void setWindowSize(int width, int height) {
        mc.getWindow().setWindowedSize(width, height);
    }

    /**
     * @return hud hidden
     */
    public void hudHidden(boolean hiddenFlag) {
        mc.options.hudHidden = hiddenFlag;
    }
}
