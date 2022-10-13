package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.config.Profile;
import xyz.wagyourtail.jsmacros.client.event.EventRegistry;
import xyz.wagyourtail.jsmacros.client.gui.screens.KeyMacrosScreen;
import xyz.wagyourtail.jsmacros.client.movement.MovementQueue;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.wagyourgui.BaseScreen;

import java.io.File;
import java.util.ServiceLoader;

public class JsMacros {
    public static final String MOD_ID = "jsmacros";
    public static final Logger LOGGER  = LogManager.getLogger(MOD_ID);
    public static KeyBinding keyBinding = new KeyBinding("jsmacros.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, I18n.translate("jsmacros.title"));
    public static BaseScreen prevScreen;
    protected static final File configFolder;

    static {
        ServiceLoader<ConfigFolder> cf = ServiceLoader.load(ConfigFolder.class);
        if (cf.iterator().hasNext()) {
            configFolder = cf.iterator().next().getFolder();
        } else {
            throw new NullPointerException("Config folder provider not found");
        }
    }

    public static final Core<Profile, EventRegistry> core = Core.createInstance(EventRegistry::new, Profile::new, configFolder.getAbsoluteFile(), new File(configFolder, "Macros"), LOGGER);

    public static void onInitialize() {
        // this is first, we just want core loaded here
        try {
            core.config.addOptions("client", ClientConfigV2.class);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        // HINT TO EXTENSION DEVS: Use this init to add your shit before any scripts are actually run
    }


    public static void onInitializeClient() {
        // this comes later, we want to do core's deferred init here
        core.deferredInit();

        prevScreen = new KeyMacrosScreen(null);

        // Init MovementQueue
        MovementQueue.clear();
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
                return String.format("%d Row Chest", ((GenericContainerScreen) s).getScreenHandler().getRows());
            } else if (s instanceof Generic3x3ContainerScreen) {
                return "3x3 Container";
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
        } else if (s instanceof ChatScreen) {
            return "Chat";
        }
        Text t = s.getTitle();
        String ret = "";
        if (t != null) ret = t.getString();
        if (ret.equals("")) ret = "unknown";
        return ret;
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
        return range(start, end, 1);
    }
    
    public static int[] range(int start, int end, int iter) {
        int[] a = new int[end-start];
        for (int i = start; i < end; i+=iter) {
            a[i-start] = i;
        }
        return a;
    }
}