package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.config.Profile;
import xyz.wagyourtail.jsmacros.client.event.EventRegistry;
import xyz.wagyourtail.jsmacros.client.gui.screens.KeyMacrosScreen;
import xyz.wagyourtail.jsmacros.client.movement.MovementQueue;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.wagyourgui.BaseScreen;

import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.ServiceLoader;

public class JsMacros {
    public static final String MOD_ID = "jsmacros";
    public static final Logger LOGGER  = LogManager.getLogger();
    public static KeyBinding keyBinding = new KeyBinding("jsmacros.menu", Keyboard.KEY_K, "jsmacros.title");
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

    public static void openURI(String p_175282_1_) {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
            oclass.getMethod("browse", URI.class).invoke(object, new URI(p_175282_1_));
        }
        catch (Throwable throwable)
        {
            LOGGER.error("Couldn't open link", throwable);
        }
    }

    static public String getScreenName(GuiScreen s) {
        if (s == null) return null;
        if (s instanceof GuiContainer) {
            //add more ?
            if (s instanceof GuiChest) {
                return String.format("%d Row Chest", ((GuiChest) s).screenHandler.slots.size() / 9 - 4);
            } else if (s instanceof GuiDispenser) {
                return "3x3 Container";
            } else if (s instanceof GuiRepair) {
                return "Anvil";
            } else if (s instanceof GuiBeacon) {
                return "Beacon";
            } else if (s instanceof GuiBrewingStand) {
                return "Brewing Stand";
            } else if (s instanceof GuiCrafting) {
                return "Crafting Table";
            } else if (s instanceof GuiEnchantment) {
                return "Enchanting Table";
            } else if (s instanceof GuiFurnace) {
                return "Furnace";
            } else if (s instanceof GuiHopper) {
                return "Hopper";
            } else if (s instanceof GuiMerchant) {
                return "Villager";
            } else if (s instanceof GuiInventory) {
                return "Survival Inventory";
            } else if (s instanceof GuiScreenHorseInventory) {
                return "Horse";
            } else if (s instanceof GuiContainerCreative) {
                return "Creative Inventory";
            } else {
                return s.getClass().getName();
            }
        } else if (s instanceof GuiChat) {
            return "Chat";
        }
        return s.getClass().getTypeName();
    }

    @Deprecated
    static public String getLocalizedName(int keyCode) {
        return GameSettings.getFormattedNameForKeyCode(keyCode);
    }

    @Deprecated
    static public Minecraft getMinecraft() {
        return Minecraft.getInstance();
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
