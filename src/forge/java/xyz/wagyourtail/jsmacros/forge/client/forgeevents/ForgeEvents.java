package xyz.wagyourtail.jsmacros.forge.client.forgeevents;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import xyz.wagyourtail.jsmacros.client.access.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.render.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.render.ScriptScreen;
import xyz.wagyourtail.jsmacros.client.api.library.FHud;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.CommandBuilderForge;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ForgeEvents {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static final Constructor<DrawContext> DRAW_CONTEXT_CONSTRUCTOR;

    static {
        try {
            DRAW_CONTEXT_CONSTRUCTOR = DrawContext.class.getDeclaredConstructor(MinecraftClient.class, MatrixStack.class, VertexConsumerProvider.Immediate.class);
            DRAW_CONTEXT_CONSTRUCTOR.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        NeoForge.EVENT_BUS.addListener(ForgeEvents::renderWorldListener);
        NeoForge.EVENT_BUS.addListener(ForgeEvents::onTick);
        NeoForge.EVENT_BUS.addListener(ForgeEvents::onRegisterCommands);

        NeoForge.EVENT_BUS.addListener(ForgeEvents::onScreenDraw);

        NeoForge.EVENT_BUS.addListener(ForgeEvents::onScreenKeyPressed);
        NeoForge.EVENT_BUS.addListener(ForgeEvents::onScreenCharTyped);

        NeoForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseClicked);
        NeoForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseReleased);
        NeoForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseScroll);
        NeoForge.EVENT_BUS.addListener(ForgeEvents::onScreenMouseDragged);
    }

    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_keyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    public static void onScreenCharTyped(ScreenEvent.CharacterTyped.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_charTyped(event.getCodePoint(), event.getModifiers());
    }

    public static void onScreenDraw(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof ScriptScreen)) {
            ((IScreenInternal) event.getScreen()).jsmacros_render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
        }
    }

    public static void onScreenMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_mouseClicked(event.getMouseX(), event.getMouseY(), event.getButton());
    }

    public static void onScreenMouseReleased(ScreenEvent.MouseButtonPressed.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_mouseReleased(event.getMouseX(), event.getMouseY(), event.getButton());
    }

    public static void onScreenMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_mouseScrolled(event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());
    }

    public static void onScreenMouseDragged(ScreenEvent.MouseDragged.Pre event) {
        ((IScreenInternal) event.getScreen()).jsmacros_mouseDragged(event.getMouseX(), event.getMouseY(), event.getMouseButton(), event.getDragX(), event.getDragY());
    }

    public static void renderHudListener(DrawContext drawContext, RenderTickCounter partialTicks) {
        for (IDraw2D<Draw2D> h : ImmutableSet.copyOf(FHud.overlays).stream().sorted(Comparator.comparingInt(IDraw2D::getZIndex)).collect(Collectors.toList())) {
            try {
                h.render(drawContext);
            } catch (Throwable ignored) {
            }
        }
    }

    public static void onRegisterGuiOverlays(RegisterGuiLayersEvent ev) {
        ev.registerBelow(VanillaGuiLayers.DEBUG_OVERLAY, Identifier.of("jsmacros:hud"), ForgeEvents::renderHudListener);
    }

    public static void renderWorldListener(RenderLevelStageEvent e) {
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }
        client.getProfiler().swap("jsmacros_draw3d");
        for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {
            try {
                DrawContext drawContext = DRAW_CONTEXT_CONSTRUCTOR.newInstance(client, e.getPoseStack(), client.getBufferBuilders().getEntityVertexConsumers());
                d.render(drawContext, e.getPartialTick().getLastDuration());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        client.getProfiler().pop();
    }

    public static void onTick(ClientTickEvent.Post event) {
        TickBasedEvents.onTick(MinecraftClient.getInstance());
    }

    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        CommandBuilderForge.onRegisterEvent(event);
    }

}
