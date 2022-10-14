package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandManager;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.CommandManagerForge;
import xyz.wagyourtail.jsmacros.forge.client.forgeevents.ForgeEvents;
import xyz.wagyourtail.wagyourgui.BaseScreen;

@Mod(modid = JsMacros.MOD_ID, version = "@VERSION@", guiFactory = "xyz.wagyourtail.jsmacros.forge.client.JsMacrosModConfigFactory")
public class JsMacrosForge {

    public JsMacrosForge() {
        // needs to be earlier because forge does this too late and Core.instance ends up null for first sound event
        JsMacros.onInitialize();
    }

    @Mod.EventHandler
    public void onInitialize(FMLInitializationEvent event) {

        // initialize loader-specific stuff
        CommandManager.instance = new CommandManagerForge();

        ForgeEvents.init();

        ClientRegistry.registerKeyBinding(JsMacros.keyBinding);
    }

    @Mod.EventHandler
    public void onInitializeClient(FMLPostInitializationEvent event) {
        JsMacros.onInitializeClient();
    }
}
