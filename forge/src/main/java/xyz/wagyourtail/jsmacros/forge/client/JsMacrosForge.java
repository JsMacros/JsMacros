package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraft.client.MinecraftClient;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.CommandBuilderForge;
import xyz.wagyourtail.jsmacros.forge.client.forgeevents.ForgeEvents;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

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
        CommandBuilder.createNewBuilder = CommandBuilderForge::new;

        ForgeEvents.init();

        ClientRegistry.registerKeyBinding(JsMacros.keyBinding);

        // load fabric-style plugins
        Thread.currentThread().setContextClassLoader(new ShimClassLoader());
        FakeFabricLoader.instance.loadEntries();
    }

    public void onInitializeClient(FMLClientSetupEvent event) {
        JsMacros.onInitializeClient();

        // load fabric-style plugins
        Thread.currentThread().setContextClassLoader(new ShimClassLoader());
        FakeFabricLoader.instance.loadClientEntries();
    }

    public static class ShimClassLoader extends ClassLoader {
        public ShimClassLoader() {
            super(ShimClassLoader.class.getClassLoader());
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                return super.loadClass(name);
            } catch (StringIndexOutOfBoundsException e) {
                throw new ClassNotFoundException();
            }
        }
    }
}
