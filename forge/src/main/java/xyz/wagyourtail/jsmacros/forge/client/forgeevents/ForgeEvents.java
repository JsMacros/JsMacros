package xyz.wagyourtail.jsmacros.forge.client.forgeevents;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.MinecraftForge;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;

public class ForgeEvents {
    private static final MinecraftClient client = MinecraftClient.getInstance();


    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::renderHudListener);
        MinecraftForge.EVENT_BUS.addListener(ForgeEvents::renderWorldListener);
    }

    public static void renderHudListener(RenderGameOverlayEvent.Pre e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.DEBUG) {
            for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays)) {
                try {
                    h.render(e.getMatrixStack());
                } catch (Throwable ignored) {}
            }
        }
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
}
