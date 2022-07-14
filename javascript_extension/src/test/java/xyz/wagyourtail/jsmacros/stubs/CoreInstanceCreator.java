package xyz.wagyourtail.jsmacros.stubs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.wagyourtail.jsmacros.core.Core;

import java.io.File;

public class CoreInstanceCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger("JsMacros");
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
