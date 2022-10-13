package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandManager;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.CommandManagerForge;
import xyz.wagyourtail.jsmacros.forge.client.forgeevents.ForgeEvents;

@Mod(JsMacros.MOD_ID)
public class JsMacrosForge {

    public JsMacrosForge() {

        System.setProperty("jnr.ffi.provider", "cause.class.not.found.please");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitialize);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitializeClient);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, parent) -> {
            JsMacros.prevScreen.setParent(parent);
            return JsMacros.prevScreen;
        });

        // needs to be earlier because forge does this too late and Core.instance ends up null for first sound event
        JsMacros.onInitialize();
    }

    public void onInitialize(FMLCommonSetupEvent event) {

        // initialize loader-specific stuff
        CommandManager.instance = new CommandManagerForge();

        ForgeEvents.init();

        ClientRegistry.registerKeyBinding(JsMacros.keyBinding);
    }

    public void onInitializeClient(FMLClientSetupEvent event) {
        JsMacros.onInitializeClient();
    }
}
