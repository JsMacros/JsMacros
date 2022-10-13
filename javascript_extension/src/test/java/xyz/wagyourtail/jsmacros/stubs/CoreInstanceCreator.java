package xyz.wagyourtail.jsmacros.stubs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.wagyourtail.jsmacros.core.Core;

import java.io.File;

public class CoreInstanceCreator {
    private static final Logger LOGGER = LogManager.getLogger("JsMacros");
    private static final File configFolder = new File("run/config");
    private static final File macroFolder = new File(configFolder, "macro");

    public static Core<ProfileStub, EventRegistryStub> createCore() {
        Core<ProfileStub, EventRegistryStub> instance = (Core) Core.getInstance();
        if (instance == null) {
            instance = Core.createInstance(
                EventRegistryStub::new,
                ProfileStub::new,
                configFolder,
                macroFolder,
                LOGGER
            );
        }
        return instance;
    }
}
