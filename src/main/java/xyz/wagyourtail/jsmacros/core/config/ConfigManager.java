package xyz.wagyourtail.jsmacros.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager<T extends ConfigOptions> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Class<T> optionsClass;
    public T options;
    public final File configFolder;
    public final File macroFolder;
    public final File configFile;
    public final Logger LOGGER;

    public ConfigManager(File configFolder, File macroFolder, Class<T> configClass, Logger logger) {
        this.configFolder = configFolder;
        this.macroFolder = macroFolder;
        this.LOGGER = logger;
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        this.configFile = new File(configFolder, "options.json");
        if (!macroFolder.exists()) {
            macroFolder.mkdirs();
            final File tf = new File(macroFolder, "index.js");
            if (!tf.exists()) try {
                tf.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        optionsClass = configClass;
        try {
            loadConfig();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadConfig() throws IllegalAccessException, InstantiationException {
        try {
            options = gson.fromJson(new FileReader(configFile), optionsClass);
        } catch (Exception e) {
            LOGGER.error("Config Failed To Load.");
            e.printStackTrace();
            if (configFile.exists()) {
                final File back = new File(configFolder, "options.json.bak");
                if (back.exists()) back.delete();
                configFile.renameTo(back);
            }
            options = optionsClass.newInstance();
            saveConfig();
        }
        LOGGER.info("Loaded Profiles:");
        for (String key : options.profiles.keySet()) {
            LOGGER.info("    " + key);
        }

    }

    public void saveConfig() {
        try {
            final FileWriter fw = new FileWriter(configFile);
            fw.write(gson.toJson(options));
            fw.close();
        } catch (Exception e) {
            LOGGER.error("Config Failed To Save.");
            e.printStackTrace();
        }
    }
}