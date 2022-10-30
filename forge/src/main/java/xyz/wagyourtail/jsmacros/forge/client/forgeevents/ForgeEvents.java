package xyz.wagyourtail.jsmacros.forge.client.forgeevents;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FKeyBind;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.wagyourgui.BaseScreen;

public class ForgeEvents {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ForgeEvents());
    }

//    public static void renderHudListener(ForgeIngameGui gui, MatrixStack mStack, float partialTicks, int width, int height) {
//        for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays)) {
//            try {
//                h.render(mStack);
//            } catch (Throwable ignored) {}
//        }
//    }

    @SubscribeEvent
    public void renderWorldListener(RenderWorldLastEvent e) {
        client.profiler.swap("jsmacros_draw3d");
        for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {
            try {
                d.render();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TickBasedEvents.onTick(MinecraftClient.getInstance());
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent keyEvent) {
        if (Keyboard.getEventKeyState() ^ FKeyBind.KeyTracker.getPressedKeys().contains(Keyboard.getEventKey()))
            if (EventKey.parse(Keyboard.getEventKey(), 0, Keyboard.getEventKeyState() ? 1 : 0, BaseScreen.createModifiers())) {
                keyEvent.setCanceled(true);
            }
    }

    @SubscribeEvent
    public void onMouse(InputEvent.MouseInputEvent mouseEvent) {
        if (Mouse.getEventButtonState() ^ FKeyBind.KeyTracker.getPressedKeys().contains(Mouse.getEventButton() - 100))
            if (EventKey.parse(Mouse.getEventButton() - 100, 0, Mouse.getEventButtonState() ? 1 : 0, BaseScreen.createModifiers())) {
                mouseEvent.setCanceled(true);
            }
    }
}
