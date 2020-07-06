package xyz.wagyourtail.jsmacros;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.python.util.PythonInterpreter;

import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.gui2.KeyMacrosScreen;

public class jsMacros implements ClientModInitializer {
    public static final String MOD_ID = "jsmacros";
    public static ConfigManager config = new ConfigManager();
    public static Profile profile;
    public static KeyMacrosScreen keyMacrosScreen;
    public static boolean jythonFailed = true;
    public static String jythonFailStack;
    
    @Override
    public void onInitializeClient() {
        
        //janky hack mate
        try {
            if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
                InputStream f = this.getClass().getClassLoader().getResourceAsStream("META-INF/jars/jython-standalone-2.7.2.jar");
                File fi = new File(FabricLoader.getInstance().getGameDirectory(), "mods/jython-standalone-2.7.2.jar");
                if (!fi.exists()) FileUtils.copyInputStreamToFile(f, fi);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        config.loadConfig();
        profile = new Profile(config.options.defaultProfile);
        keyMacrosScreen = new KeyMacrosScreen(null);
        
        Thread t = new Thread(() -> {
            Builder build = Context.newBuilder("js");
            Context con = build.build();
            con.eval("js", "console.log('js loaded.')");
            try {
                PythonInterpreter interp = new PythonInterpreter();
                interp.exec("print('py loaded.')");
                interp.close();
                jythonFailed = false;
            } catch (Exception e) {
                jythonFailStack = e.getStackTrace().toString();
                e.printStackTrace();
            }
        });
        t.start();
    }

    static public Text getKeyText(String translationKey) {
        try {
            return InputUtil.fromTranslationKey(translationKey).getLocalizedText();
        } catch(Exception e) {
            return new LiteralText(translationKey);
        }
    }
    
    static public String getScreenName(Screen s) {
        if (s == null) return null;
        if (s instanceof HandledScreen) {
            //add more ?
            if (s instanceof GenericContainerScreen) {
                return String.format("%d row chest", ((GenericContainerScreen) s).getScreenHandler().getRows());
            } else if (s instanceof Generic3x3ContainerScreen) {
                return "3x3 container";
            } else if (s instanceof AnvilScreen) {
                return "Anvil";
            } else if (s instanceof BeaconScreen) {
                return "Beacon";
            } else if (s instanceof BlastFurnaceScreen) {
                return "Blast Furnace";
            } else if (s instanceof BrewingStandScreen) {
                return "Brewing Stand";
            } else if (s instanceof CraftingScreen) {
                return "Crafting Table";
            } else if (s instanceof EnchantmentScreen) {
                return "Enchanting Table";
            } else if (s instanceof FurnaceScreen) {
                return "Furnace";
            } else if (s instanceof GrindstoneScreen) {
                return "Grindstone";
            } else if (s instanceof HopperScreen) {
                return "Hopper";
            } else if (s instanceof LecternScreen) {
                return "Lectern";
            } else if (s instanceof LoomScreen) {
                return "Loom";
            } else if (s instanceof MerchantScreen) {
                return "Villager";
            } else if (s instanceof ShulkerBoxScreen) {
                return "Shulker Box";
            } else if (s instanceof SmithingScreen) {
                return "Smithing Table";
            } else if (s instanceof SmokerScreen) {
                return "Smoker";
            } else if (s instanceof CartographyTableScreen) {
                return "Cartography Table";
            } else if (s instanceof StonecutterScreen) {
                return "Stonecutter";
            } else if (s instanceof InventoryScreen) {
                return "Survival Inventory";
            } else if (s instanceof HorseScreen) {
                return "Horse";
            } else if (s instanceof CreativeInventoryScreen) {
                return "Creative Inventory";
            } else {
                return s.getClass().getName();
            }
        }
        return s.getTitle().getString();
    }
    
    @Deprecated
    static public String getLocalizedName(InputUtil.Key keyCode) {
        return I18n.translate(keyCode.getTranslationKey());
     }
    
    @Deprecated
    static public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }
    

    public static int[] range(int end) {
        return range(0, end, 1);
    }
    
    public static int[] range(int start, int end) {
        return range(0, end, 1);
    }
    
    public static int[] range(int start, int end, int iter) {
        int[] a = new int[Math.max(end-start, 0)];
        for (int i = start; i < end; i+=iter) {
            a[i-start] = i;
        }
        return a;
    }
}