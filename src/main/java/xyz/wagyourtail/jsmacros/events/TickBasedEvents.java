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

                ItemStack newMainHand = inv.getMainHandStack();
                if (!newMainHand.equals(mainHand)) {
                    if (newMainHand.isItemEqualIgnoreDamage(mainHand) && newMainHand.getCount() == mainHand.getCount()) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newMainHand), newMainHand.getDamage());
                    }
                    HeldItemCallback.EVENT.invoker().interact(new ItemStackHelper(newMainHand), false);
                    mainHand = newMainHand;
                }
                
                ItemStack newOffHand = inv.offHand.get(0);
                if (!newOffHand.equals(offHand)) {
                    if (newOffHand.isItemEqualIgnoreDamage(offHand) && newOffHand.getCount() == offHand.getCount()) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newOffHand), newOffHand.getDamage());
                    }
                    HeldItemCallback.EVENT.invoker().interact(new ItemStackHelper(newOffHand), true);
                    offHand = newOffHand;
                }
                
                ItemStack newHeadArmor = inv.getArmorStack(3);
                if (!newHeadArmor.equals(headArmor)) {
                    if (newHeadArmor.isItemEqualIgnoreDamage(headArmor) && newHeadArmor.getCount() == headArmor.getCount()) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newHeadArmor), newHeadArmor.getDamage());
                    }
                    ArmorChangeCallback.EVENT.invoker().interact("HEAD", new ItemStackHelper(newHeadArmor));
                    headArmor = newHeadArmor;
                }
                
                ItemStack newChestArmor = inv.getArmorStack(2);
                if (!newChestArmor.equals(chestArmor)) {
                    if (newChestArmor.isItemEqualIgnoreDamage(chestArmor) && newChestArmor.getCount() == chestArmor.getCount()) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newChestArmor), newChestArmor.getDamage());
                    }
                    ArmorChangeCallback.EVENT.invoker().interact("CHEST", new ItemStackHelper(newChestArmor));
                    chestArmor = newChestArmor;
                    
                }
                
                ItemStack newLegArmor = inv.getArmorStack(1);
                if (!newLegArmor.equals(legArmor)) {
                    if (newLegArmor.isItemEqualIgnoreDamage(legArmor) && newLegArmor.getCount() == legArmor.getCount()) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newLegArmor), newLegArmor.getDamage());
                    }
                    ArmorChangeCallback.EVENT.invoker().interact("LEGS", new ItemStackHelper(newLegArmor));
                    legArmor = newLegArmor;
                }
                
                ItemStack newFootArmor = inv.getArmorStack(0);
                if (!newFootArmor.equals(footArmor)) {
                    if (newFootArmor.isItemEqualIgnoreDamage(footArmor) && newFootArmor.getCount() == footArmor.getCount()) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newFootArmor), newFootArmor.getDamage());
                    }
                    ArmorChangeCallback.EVENT.invoker().interact("FEET", new ItemStackHelper(newFootArmor));
                    footArmor = newFootArmor;
                }
            }
        });
    }
}
