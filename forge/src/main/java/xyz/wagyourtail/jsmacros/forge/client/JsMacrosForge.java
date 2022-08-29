package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
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
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((mc, parent) -> {
            JsMacros.prevScreen.setParent(parent);
            return JsMacros.prevScreen;
        }));
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
