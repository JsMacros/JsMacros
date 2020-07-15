package xyz.wagyourtail.jsmacros.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;

public class ConfigManager {
    public ConfigOptions options;
    public File configFolder = new File(FabricLoader.getInstance().getConfigDirectory(), "jsMacros");
    public File macroFolder = new File(configFolder, "Macros");
    public File configFile = new File(configFolder, "options.json");
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        options = new ConfigOptions(true, false, "./jep.dll", "default", new HashMap<>());
        options.profiles.put("default", new ArrayList<>());
        options.profiles.get("default").add(new RawMacro(MacroEnum.KEY_RISING, "key.keyboard.j", "test.js", true));
        if (!macroFolder.exists()) {
            macroFolder.mkdirs();
        }
        File tf = new File(macroFolder, "test.js");
        if (!tf.exists()) try {
            tf.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        try {
            options = gson.fromJson(new FileReader(configFile), ConfigOptions.class);
        } catch (Exception e) {
            System.out.println("Config Failed To Load.");
            e.printStackTrace();
            if (configFile.exists()) {
                configFile.renameTo(new File(configFolder, "options.json.bak"));
            }
            saveConfig();
        }
        System.out.println("Loaded Profiles:");
        for (String key : jsMacros.config.options.profiles.keySet()) {
            System.out.println("    " + key);
        }

    }

    public void saveConfig() {
        try {
            System.out.println(configFile.toString());
            FileWriter fw = new FileWriter(configFile);
            fw.write(gson.toJson(options));
            fw.close();
        } catch (Exception e) {
            System.out.println("Config Failed To Save.");
            e.printStackTrace();
        }
    }
}