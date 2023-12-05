package xyz.wagyourtail.jsmacros.forge.client.forgeevents;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.ScriptScreen;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ForgeEvents {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init() {
        OverlayRegistry.registerOverlayBelow(ForgeIngameGui.HUD_TEXT_ELEMENT, "jsmacros_hud", ForgeEvents::renderHudListener);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::renderWorldListener);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onTick);

        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenDraw);

        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenKeyPressed);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenCharTyped);

        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseClicked);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseReleased);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseScroll);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseDragged);
    }

    public static void onScreenKeyPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        ((IScreenInternal) event.getGui()).jsmacros_keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    public static void onScreenCharTyped(GuiScreenEvent.KeyboardCharTypedEvent.Pre event) {
        ((IScreenInternal) event.getGui()).jsmacros_charTyped(event.getCodePoint(), event.getModifiers());
    }

    public static void onScreenDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.getGui() instanceof ScriptScreen)) {
            ((IScreenInternal) event.getGui()).jsmacros_render(event.getMatrixStack(), event.getMouseX(), event.getMouseY(), event.getRenderPartialTicks());
        }
    }

    public static void onScreenMouseClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {
        ((IScreenInternal) event.getGui()).jsmacros_mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton());
    }

    public static void onScreenMouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        ((IScreenInternal) event.getGui()).jsmacros_mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton());
    }

    public static void onScreenMouseScroll(GuiScreenEvent.MouseScrollEvent.Pre event) {
        ((IScreenInternal) event.getGui()).jsmacros_mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDelta());
    }

    public static void onScreenMouseDragged(GuiScreenEvent.MouseDragEvent.Pre event) {
        ((IScreenInternal) event.getGui()).jsmacros_mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY());
    }

    public static void renderHudListener(ForgeIngameGui gui, MatrixStack mStack, float partialTicks, int width, int height) {
        for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays).stream().sorted(Comparator.comparingInt(IDraw2D::getZIndex)).collect(Collectors.toList())) {
            try {
                h.render(mStack);
            } catch (Throwable ignored) {}
        }
    }

    @SuppressWarnings("removal")
    public static void renderWorldListener(RenderWorldLastEvent e) {
        client.getProfiler().swap("jsmacros_draw3d");
        for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {
            try {
                d.render(e.getMatrixStack(), e.getPartialTicks());
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

}