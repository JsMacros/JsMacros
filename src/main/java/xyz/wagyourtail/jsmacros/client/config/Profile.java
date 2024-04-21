package xyz.wagyourtail.jsmacros.client.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.*;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.*;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.*;
import xyz.wagyourtail.jsmacros.client.api.library.impl.*;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.impl.EventCustom;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros;

import java.util.Arrays;

import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.literal;
import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.translatable;

public class Profile extends BaseProfile {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public Profile(Core<Profile, ?> runner, Logger logger) {
        super(runner, logger);
    }

    @Override
    protected boolean loadProfile(String profileName) {
        boolean val = super.loadProfile(profileName);
        final MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen instanceof MacroScreen) {
            mc.execute(() -> ((MacroScreen) mc.currentScreen).reload());
        }
        return val;
    }

    public static Class<? extends Throwable>[] ignoredErrors = new Class[]{
            InterruptedException.class,
            BaseScriptContext.ScriptAssertionError.class,
    };

    @Override
    public void logError(Throwable ex) {
        ex.printStackTrace();
        Throwable finalEx = ex;
        if (Arrays.stream(ignoredErrors).anyMatch(e -> e.isAssignableFrom(finalEx.getClass()))) {
            return;
        }
        if (ex instanceof RuntimeException) {
            if (ex.getCause() != null) {
                Throwable cause = ex.getCause();
                if (Arrays.stream(ignoredErrors).anyMatch(e -> e.isAssignableFrom(cause.getClass()))) {
                    return;
                }
                // un-wrap exceptions
                if (ex.getMessage().equals(ex.getCause().toString())) {
                    ex = ex.getCause();
                }
            }
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud != null) {
            BaseWrappedException<?> e;
            try {
                e = runner.wrapException(ex);
            } catch (Throwable t) {
                t.printStackTrace();
                mc.execute(() -> ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(translatable("jsmacros.errorerror").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED))));
                return;
            }
            Text text = compileError(e);
            mc.execute(() -> {
                try {
                    ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text);
                } catch (Throwable t) {
                    ((IChatHud) mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(translatable("jsmacros.errorerror").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public boolean checkJoinedThreadStack() {
        return mc.isOnThread() || joinedThreadStack.contains(Thread.currentThread());
    }

    private Text compileError(BaseWrappedException<?> ex) {
        if (ex == null) {
            return null;
        }
        BaseWrappedException<?> head = ex;
        MutableText text = literal("");
        do {
            String message = head.message;
            MutableText line = literal(message).setStyle(Style.EMPTY.withColor(Formatting.RED));
            if (head.location != null) {
                Style locationStyle = Style.EMPTY.withColor(Formatting.GOLD);
                if (head.location instanceof BaseWrappedException.GuestLocation) {
                    BaseWrappedException.GuestLocation loc = (BaseWrappedException.GuestLocation) head.location;
                    if (loc.file != null) {
                        locationStyle = locationStyle.withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, translatable("jsmacros.clicktoview"))
                        ).withClickEvent(new CustomClickEvent(() -> {
                            if (loc.startIndex > -1) {
                                EditorScreen.openAndScrollToIndex(loc.file, loc.startIndex, loc.endIndex);
                            } else if (loc.line > -1) {
                                EditorScreen.openAndScrollToLine(loc.file, loc.line, loc.column, -1);
                            } else {
                                EditorScreen.openAndScrollToIndex(loc.file, 0, 0);
                            }
                        }));
                    }
                }
                line.append(literal(" (" + head.location + ")").setStyle(locationStyle));
            }
            if ((head = head.next) != null) {
                line.append("\n");
            }
            text.append(line);
        } while (head != null);
        return text;
    }

    @Override
    public void initRegistries() {
        super.initRegistries();

        runner.eventRegistry.addEvent(EventAirChange.class);
        runner.eventRegistry.addEvent(EventArmorChange.class);
        runner.eventRegistry.addEvent(EventAttackBlock.class);
        runner.eventRegistry.addEvent(EventAttackEntity.class);
        runner.eventRegistry.addEvent(EventBlockUpdate.class);
        runner.eventRegistry.addEvent(EventBossbar.class);
        runner.eventRegistry.addEvent(EventChunkLoad.class);
        runner.eventRegistry.addEvent(EventChunkUnload.class);
        runner.eventRegistry.addEvent(EventContainerUpdate.class);
        runner.eventRegistry.addEvent(EventClickSlot.class);
        runner.eventRegistry.addEvent(EventDamage.class);
        runner.eventRegistry.addEvent(EventHeal.class);
        runner.eventRegistry.addEvent(EventDeath.class);
        runner.eventRegistry.addEvent(EventDimensionChange.class);
        runner.eventRegistry.addEvent(EventDisconnect.class);
        runner.eventRegistry.addEvent(EventDropSlot.class);
        runner.eventRegistry.addEvent(EventEntityDamaged.class);
        runner.eventRegistry.addEvent(EventEntityHealed.class);
        runner.eventRegistry.addEvent(EventEntityLoad.class);
        runner.eventRegistry.addEvent(EventEntityUnload.class);
        runner.eventRegistry.addEvent(EventEXPChange.class);
        runner.eventRegistry.addEvent(EventFallFlying.class);
        runner.eventRegistry.addEvent(EventHealthChange.class);
        runner.eventRegistry.addEvent(EventHeldItemChange.class);
        runner.eventRegistry.addEvent(EventHungerChange.class);
        runner.eventRegistry.addEvent(EventInteractBlock.class);
        runner.eventRegistry.addEvent(EventInteractEntity.class);
        runner.eventRegistry.addEvent(EventItemDamage.class);
        runner.eventRegistry.addEvent(EventItemPickup.class);
        runner.eventRegistry.addEvent(EventRecvPacket.class);
        runner.eventRegistry.addEvent(EventSendPacket.class);
        runner.eventRegistry.addEvent(EventJoinServer.class);
        runner.eventRegistry.addEvent(EventKey.class);
        runner.eventRegistry.addEvent(EventLaunchGame.class);
        runner.eventRegistry.addEvent(EventMouseScroll.class);
        runner.eventRegistry.addEvent(EventNameChange.class);
        runner.eventRegistry.addEvent(EventOpenContainer.class);
        runner.eventRegistry.addEvent(EventOpenScreen.class);
        runner.eventRegistry.addEvent(EventRecvPacket.class);
        runner.eventRegistry.addEvent(EventSendPacket.class);
        runner.eventRegistry.addEvent(EventPlayerJoin.class);
        runner.eventRegistry.addEvent(EventPlayerLeave.class);
        runner.eventRegistry.addEvent(EventQuitGame.class);
        runner.eventRegistry.addEvent(EventRecvMessage.class);
        runner.eventRegistry.addEvent(EventRiding.class);
        runner.eventRegistry.addEvent(EventResourcePackLoaded.class);
        runner.eventRegistry.addEvent(EventSendMessage.class);
        runner.eventRegistry.addEvent(EventSignEdit.class);
        runner.eventRegistry.addEvent(EventSlotUpdate.class);
        runner.eventRegistry.addEvent(EventSound.class);
        runner.eventRegistry.addEvent(EventStatusEffectUpdate.class);
        runner.eventRegistry.addEvent(EventTick.class);
        runner.eventRegistry.addEvent(EventTitle.class);

        runner.libraryRegistry.addLibrary(FChat.class);
        runner.libraryRegistry.addLibrary(FHud.class);
        runner.libraryRegistry.addLibrary(FClient.class);
        runner.libraryRegistry.addLibrary(FKeyBind.class);
        runner.libraryRegistry.addLibrary(FPlayer.class);
        runner.libraryRegistry.addLibrary(FPositionCommon.class);
        runner.libraryRegistry.addLibrary(FJavaUtils.class);
        runner.libraryRegistry.addLibrary(FUtils.class);
        runner.libraryRegistry.addLibrary(FWorld.class);
    }

}
