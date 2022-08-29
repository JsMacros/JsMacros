package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

import java.io.File;

public class JsMacrosEarlyRiser implements IMixinConnector {
    public static final Logger LOGGER  = LogManager.getLogger("JsMacros EarlyRiser");
//    public static final List<URL> urls = new ArrayList<>();
//    public static ClassLoader loader;

    @Override
    public void connect() {
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        LOGGER.log(Level.INFO, "[JsMacros] Loading Mixins.");
        Mixins.addConfiguration("jsmacros.mixins.json");
        Mixins.addConfiguration("jsmacros-forge.mixins.json");
        Mixins.addConfiguration("fabric-command-api-v1.mixins.json");
    }
}