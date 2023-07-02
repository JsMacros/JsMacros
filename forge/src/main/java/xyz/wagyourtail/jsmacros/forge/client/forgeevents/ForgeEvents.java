package xyz.wagyourtail.jsmacros.forge.client.forgeevents;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.CommandBuilderForge;

public class ForgeEvents {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::renderWorldListener);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onTick);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::onRegisterGuiOverlays);
    }

    public static void renderHudListener(ForgeGui gui, MatrixStack mStack, float partialTicks, int width, int height) {
        for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays)) {
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
                d.render(e.getPoseStack());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
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
