package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventQuitGame;
import xyz.wagyourtail.jsmacros.client.api.helper.PacketByteBufferHelper;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.config.ClientProfile;
import xyz.wagyourtail.jsmacros.client.event.EventRegistry;
import xyz.wagyourtail.jsmacros.client.gui.screens.KeyMacrosScreen;
import xyz.wagyourtail.jsmacros.client.movement.MovementQueue;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.wagyourgui.BaseScreen;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class JsMacrosClient extends JsMacros {
    public static KeyBinding keyBinding = new KeyBinding("jsmacros.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, I18n.translate("jsmacros.title"));
    public static final Core<ClientProfile, EventRegistry> clientCore = new Core<>(EventRegistry::new, ClientProfile::new, configFolder.getAbsoluteFile(), new File(configFolder, "Macros"), LOGGER);

    public static BaseScreen prevScreen;

    public static void onInitializeClient() {
        try {
            clientCore.config.addOptions("client", ClientConfigV2.class);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException | IOException e) {
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

}
