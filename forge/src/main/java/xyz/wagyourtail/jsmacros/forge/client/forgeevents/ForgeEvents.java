package xyz.wagyourtail.jsmacros.forge.client.forgeevents;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.ScriptScreen;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.CommandBuilderForge;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ForgeEvents {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::renderWorldListener);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onTick);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onRegisterCommands);

        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenDraw);
        
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenKeyPressed);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenCharTyped);
        
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseClicked);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseReleased);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseScroll);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseDragged);
    }

    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    public static void onScreenCharTyped(ScreenEvent.CharacterTyped.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_charTyped(event.getCodePoint(), event.getModifiers());
    }
    
    public static void onScreenDraw(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof ScriptScreen)) {
            ((IScreenInternal) event.getScreen()).jsmacros_render(event.getPoseStack(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
        }
    }
    
    public static void onScreenMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton());
    }

    public static void onScreenMouseReleased(ScreenEvent.MouseButtonPressed.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton());
    }
    
    public static void onScreenMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta());
    }
    
    public static void onScreenMouseDragged(ScreenEvent.MouseDragged.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY());
    }

    public static void renderHudListener(ForgeGui gui, MatrixStack mStack, float partialTicks, int width, int height) {
        for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays).stream().sorted(Comparator.comparingInt(IDraw2D::getZIndex)).collect(Collectors.toList())) {
            try {
                h.render(mStack);
            } catch (Throwable ignored) {}
        }
    }

    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent ev) {
        ev.registerBelow(VanillaGuiOverlay.DEBUG_TEXT.id(), "jsmacros_hud",  ForgeEvents::renderHudListener);
    }

    public static void renderWorldListener(RenderLevelLastEvent e) {
        client.getProfiler().swap("jsmacros_draw3d");
        for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {
            try {
                d.render(e.getPoseStack(), e.getPartialTick());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        client.getProfiler().pop();
    }


    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TickBasedEvents.onTick(MinecraftClient.getInstance());
        }
    }

    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        CommandBuilderForge.onRegisterEvent(event);
    }
}