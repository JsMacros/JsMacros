package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventQuitGame;
import xyz.wagyourtail.jsmacros.client.api.helpers.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.config.Profile;
import xyz.wagyourtail.jsmacros.client.event.EventRegistry;
import xyz.wagyourtail.jsmacros.client.gui.screens.KeyMacrosScreen;
import xyz.wagyourtail.jsmacros.client.api.movement.MovementQueue;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.wagyourgui.BaseScreen;

import java.io.File;

public class JsMacrosClient extends JsMacros {
    public static KeyBinding keyBinding = new KeyBinding("jsmacros.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, I18n.translate("jsmacros.title"));
    public static final Core<Profile, EventRegistry> clientCore = new Core<>(EventRegistry::new, Profile::new, configFolder.getAbsoluteFile(), new File(configFolder, "Macros"), LOGGER);

    public static BaseScreen prevScreen;

    public static void onInitializeClient() {
        try {
            clientCore.config.addOptions("client", ClientConfigV2.class);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        prevScreen = new KeyMacrosScreen(null);

        // Init MovementQueue
        MovementQueue.clear();

        if (clientCore.config.getOptions(ClientConfigV2.class).serviceAutoReload) {
            clientCore.services.startReloadListener();
        }
        PacketByteBufferHelper.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> new EventQuitGame().trigger()));
    }

    static public Text getKeyText(String translationKey) {
        try {
            return InputUtil.fromTranslationKey(translationKey).getLocalizedText();
        } catch (Exception e) {
            return Text.literal(translationKey);
        }
    }

    // if any screen name is added or modified, check FHud#getOpenScreenName() and Inventory#is() annotation as well
    // i didn't put it here is because doclet won't check this class  -- aMelonRind
    static public String getScreenName(Screen s) {
        return switch (s) {
            case null -> null;
            case HandledScreen<?> handledScreen -> //add more ?
                switch (handledScreen) {
                    case GenericContainerScreen genericContainerScreen ->
                        String.format("%d Row Chest", genericContainerScreen.getScreenHandler().getRows());
                    case Generic3x3ContainerScreen ignored -> "3x3 Container";
                    case AnvilScreen ignored -> "Anvil";
                    case BeaconScreen ignored -> "Beacon";
                    case BlastFurnaceScreen ignored -> "Blast Furnace";
                    case BrewingStandScreen ignored -> "Brewing Stand";
                    case CraftingScreen ignored -> "Crafting Table";
                    case EnchantmentScreen ignored -> "Enchanting Table";
                    case FurnaceScreen ignored -> "Furnace";
                    case GrindstoneScreen ignored -> "Grindstone";
                    case HopperScreen ignored -> "Hopper";
                    case LoomScreen ignored -> "Loom";
                    case MerchantScreen ignored -> "Villager";
                    case ShulkerBoxScreen ignored -> "Shulker Box";
                    case SmithingScreen ignored -> "Smithing Table";
                    case SmokerScreen ignored -> "Smoker";
                    case CartographyTableScreen ignored -> "Cartography Table";
                    case StonecutterScreen ignored -> "Stonecutter";
                    case InventoryScreen ignored -> "Survival Inventory";
                    case HorseScreen ignored -> "Horse";
                    case CreativeInventoryScreen ignored -> "Creative Inventory";
                    default -> s.getClass().getName();
                };
            case ChatScreen ignored -> "Chat";
            default -> {
                Text t = s.getTitle();
                String ret = "";
                if (t != null) {
                    ret = t.getString();
                }
                if (ret.isEmpty()) {
                    ret = "unknown";
                }
                yield ret;
            }
        };
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
        int[] a = new int[end - start];
        for (int i = start; i < end; i += iter) {
            a[i - start] = i;
        }
        return a;
    }

}
