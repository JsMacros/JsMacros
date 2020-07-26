package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;

public class TickBasedEvents {
    private static boolean initialized = false;
    private static ItemStack mainHand = ItemStack.EMPTY;
    private static ItemStack offHand = ItemStack.EMPTY;

    private static ItemStack footArmor = ItemStack.EMPTY;
    private static ItemStack legArmor = ItemStack.EMPTY;
    private static ItemStack chestArmor = ItemStack.EMPTY;
    private static ItemStack headArmor = ItemStack.EMPTY;
    
    private static MinecraftClient mc = MinecraftClient.getInstance();
    
    public static void init() {
        if (initialized) return;
        initialized = true;
        ClientTickEvents.END_CLIENT_TICK.register(e -> {
            if (mc.player != null && mc.player.inventory != null) {
                PlayerInventory inv = mc.player.inventory;
                if (!mainHand.equals(inv.getMainHandStack())) {
                    mainHand = inv.getMainHandStack();
                    HeldItemCallback.EVENT.invoker().interact(new ItemStackHelper(mainHand), false);
                }
                if (!offHand.equals(inv.offHand.get(0))) {
                    offHand = inv.offHand.get(0);
                    HeldItemCallback.EVENT.invoker().interact(new ItemStackHelper(offHand), true);
                }
                if (!headArmor.equals(inv.getArmorStack(3))) {
                    headArmor = inv.getArmorStack(3);
                    ArmorChangeCallback.EVENT.invoker().interact("HEAD", new ItemStackHelper(headArmor));
                }
                if (!chestArmor.equals(inv.getArmorStack(2))) {
                    chestArmor = inv.getArmorStack(2);
                    ArmorChangeCallback.EVENT.invoker().interact("CHEST", new ItemStackHelper(chestArmor));
                    
                }
                if (!legArmor.equals(inv.getArmorStack(1))) {
                    legArmor = inv.getArmorStack(1);
                    ArmorChangeCallback.EVENT.invoker().interact("LEGS", new ItemStackHelper(legArmor));
                }
                if (!footArmor.equals(inv.getArmorStack(0))) {
                    footArmor = inv.getArmorStack(0);
                    ArmorChangeCallback.EVENT.invoker().interact("FEET", new ItemStackHelper(footArmor));
                }
            }
        });
    }
}
