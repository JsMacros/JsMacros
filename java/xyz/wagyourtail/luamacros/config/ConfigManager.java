package xyz.wagyourtail.luamacros.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;

public class ConfigManager {
	public ConfigOptions options;
	public File configFolder = new File(FabricLoader.getInstance().getConfigDirectory(), "LuaMacros");
	public File macroFolder = new File(configFolder, "macros");
	public File configFile = new File(configFolder, "options.json");
	
	public ConfigManager() {
		options = new ConfigOptions();
		options.allowPrivateAccess = false;
		options.defaultProfile = "default";
		options.profiles.put("default", new Profile());
	}
	
	public void loadConfig() {
		try {
			options = new Gson().fromJson(new FileReader(configFile), ConfigOptions.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			System.out.println("load config failed, backing up old file if exists.");
			if (configFile.exists()) {
				configFile.renameTo(new File(configFolder, "options.json.bak"));
			}
			saveConfig();
		}
		
	}
	
	public void saveConfig() {
		try {
			new Gson().toJson(options, new FileWriter(configFile));
		} catch (JsonIOException | IOException e) {
			System.out.println("save config failed");
		}
	}
	
	private class ConfigOptions {
		public boolean allowPrivateAccess;
		public String defaultProfile;
		public HashMap<String, Profile> profiles = new HashMap<>();
	}
	
	private class Profile {
		
		public ArrayList<Macro> macros = new ArrayList<>();
	}
	
	private class Macro {
		public MacroType type;
		public File scriptFile;
	}
	
	private enum MacroType {
		KEY_FALLING,
		KEY_RISING,
		KEY_BOTH,
		EVENT
	}
}