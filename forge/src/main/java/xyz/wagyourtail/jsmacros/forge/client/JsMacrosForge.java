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
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.CommandBuilderForge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

@Mod(JsMacros.MOD_ID)
public class JsMacrosForge {
    public static final File configFolder = new File(MinecraftClient.getInstance().runDirectory, "config/jsMacros");
    public static final CombineClassLoader classLoader = new CombineClassLoader(JsMacrosEarlyRiser.loader, Thread.currentThread().getContextClassLoader());

    public JsMacrosForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitialize);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitializeClient);
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((mc, parent) -> {
            JsMacros.prevScreen.setParent(parent);
            return JsMacros.prevScreen;
        }));

        // needs to be earlier because forge does this too late and Core.instance ends up null for first sound event
        Thread.currentThread().setContextClassLoader(classLoader);
        JsMacros.onInitialize();
    }

    public void onInitialize(FMLCommonSetupEvent event) {

        // initialize loader-specific stuff
        BaseLanguage.preThread = () -> Thread.currentThread().setContextClassLoader(classLoader);
        CommandBuilder.createNewBuilder = CommandBuilderForge::new;
        MinecraftForge.EVENT_BUS.addListener(this::onTick);
        ClientRegistry.registerKeyBinding(JsMacros.keyBinding);

        // load fabric-style plugins
        FakeFabricLoader.instance.loadEntries();
    }

    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TickBasedEvents.onTick(MinecraftClient.getInstance());
        }
    }

    public void onInitializeClient(FMLClientSetupEvent event) {
        JsMacros.onInitializeClient();

        // load fabric-style plugins
        FakeFabricLoader.instance.loadClientEntries();
    }


    public static class CombineClassLoader extends ClassLoader {
        ClassLoader a;
        ClassLoader b;

        public CombineClassLoader(ClassLoader a, ClassLoader b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                return a.loadClass(name);
            } catch (ClassNotFoundException e) {
                return b.loadClass(name);
            }
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            try {
                return a.getResources(name);
            } catch (IOException e) {
                return b.getResources(name);
            }
        }

        @Nullable
        @Override
        public InputStream getResourceAsStream(String name) {
            InputStream s = a.getResourceAsStream(name);
            if (s == null) {
                return b.getResourceAsStream(name);
            }
            return s;
        }

        @Nullable
        @Override
        public URL getResource(String name) {
            URL s = a.getResource(name);
            if (s == null) {
                return b.getResource(name);
            }
            return s;
        }
    }
}
