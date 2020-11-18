package xyz.wagyourtail.jsmacros.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.wagyourtail.jsmacros.core.IProfile;
import xyz.wagyourtail.jsmacros.core.event.IEventTrigger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ConfigManager {
    public static ConfigManager INSTANCE;
    public static IProfile PROFILE;
    public ConfigOptions options;
    public final File configFolder;
    public final File macroFolder;
    public final File configFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager(File configFolder, File macroFolder) {
        options = new ConfigOptions(true, "default", ScriptTrigger.SortMethod.Enabled, new HashMap<>(), new LinkedHashMap<>(), false);
        options.profiles.put("default", new ArrayList<>());
        options.profiles.get("default").add(new ScriptTrigger(IEventTrigger.TriggerType.KEY_RISING, "key.keyboard.j", "test.js", true));
        this.configFolder = configFolder;
        this.macroFolder = macroFolder;
        this.configFile = new File(configFolder, "options.json");
        if (!macroFolder.exists()) macroFolder.mkdirs();
        final File tf = new File(macroFolder, "test.js");
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
                final File back = new File(configFolder, "options.json.bak");
                if (back.exists()) back.delete();
                configFile.renameTo(back);
            }
            saveConfig();
        }
        System.out.println("Loaded Profiles:");
        for (String key : INSTANCE.options.profiles.keySet()) {
            System.out.println("    " + key);
        }

    }

    public void saveConfig() {
        try {
            final FileWriter fw = new FileWriter(configFile);
            fw.write(gson.toJson(options));
            fw.close();
        } catch (Exception e) {
            System.out.println("Config Failed To Save.");
            e.printStackTrace();
        }
    }
    
    public Comparator<ScriptTrigger> getSortComparator() {
        if (options.sortMethod == null) options.sortMethod = ScriptTrigger.SortMethod.Enabled;
        switch(options.sortMethod) {
            default:
            case Enabled:
                return new ScriptTrigger.SortByEnabled();
            case FileName:
                return new ScriptTrigger.SortByFileName();
            case TriggerName:
                return new ScriptTrigger.SortByTriggerName();
        }
    }
    
    public void setSortComparator(ScriptTrigger.SortMethod method) {
        options.sortMethod = method;
    }
}