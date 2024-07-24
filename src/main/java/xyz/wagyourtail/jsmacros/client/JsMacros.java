package xyz.wagyourtail.jsmacros.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.wagyourtail.jsmacros.client.config.Profile;
import xyz.wagyourtail.jsmacros.client.event.EventRegistry;
import xyz.wagyourtail.jsmacros.core.Core;

import java.io.File;
import java.util.ServiceLoader;

public class JsMacros {
    public static final String MOD_ID = "jsmacros";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    protected static final File configFolder = ServiceLoader.load(ConfigFolder.class).findFirst().orElseThrow().getFolder();
    protected static final ModLoader modLoader = ServiceLoader.load(ModLoader.class).findFirst().orElseThrow();

    public static final Core<Profile, EventRegistry> serverCore = new Core<>(EventRegistry::new, Profile::new, configFolder.getAbsoluteFile(), new File(configFolder, "Macros"), LOGGER);

    public static void onInitialize() {
    }

    public static ModLoader getModLoader() {
        return modLoader;
    }

}
