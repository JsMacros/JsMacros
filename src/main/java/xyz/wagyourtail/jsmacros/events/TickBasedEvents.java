package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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
    
    public static boolean areEqual(ItemStack a, ItemStack b) {
        return (a.isEmpty() && b.isEmpty()) || (!a.isEmpty() && !b.isEmpty() && a.isItemEqualIgnoreDamage(b) && a.getCount() == b.getCount() && a.getDamage() == b.getDamage());
    }
    
    public static boolean areEqualIgnoreDamage(ItemStack a, ItemStack b) {
        return (a.isEmpty() && b.isEmpty()) || (!a.isEmpty() && !b.isEmpty() && a.isItemEqualIgnoreDamage(b) && a.getCount() == b.getCount());    
    }
    
    public static void init() {
        if (initialized) return;
        initialized = true;
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player != null && mc.player.inventory != null) {
                PlayerInventory inv = mc.player.inventory;

                ItemStack newMainHand = inv.getMainHandStack();
                if (!areEqual(newMainHand, mainHand)) {
                    if (areEqualIgnoreDamage(newMainHand, mainHand)) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newMainHand), newMainHand.getDamage());
                    }
                    HeldItemCallback.EVENT.invoker().interact(new ItemStackHelper(newMainHand), false);
                    mainHand = newMainHand.copy();
                }
                
                ItemStack newOffHand = inv.offHand.get(0);
                if (!areEqual(newOffHand, offHand)) {
                    if (areEqualIgnoreDamage(newOffHand, offHand)) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newOffHand), newOffHand.getDamage());
                    }
                    HeldItemCallback.EVENT.invoker().interact(new ItemStackHelper(newOffHand), true);
                    offHand = newOffHand.copy();
                }
                
                ItemStack newHeadArmor = inv.getArmorStack(3);
                if (!areEqual(newHeadArmor, headArmor)) {
                    if (areEqualIgnoreDamage(newHeadArmor, headArmor)) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newHeadArmor), newHeadArmor.getDamage());
                    }
                    ArmorChangeCallback.EVENT.invoker().interact("HEAD", new ItemStackHelper(newHeadArmor));
                    headArmor = newHeadArmor.copy();
                }
                
                ItemStack newChestArmor = inv.getArmorStack(2);
                if (!areEqual(newChestArmor, chestArmor)) {
                    if (areEqualIgnoreDamage(newChestArmor, chestArmor)) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newChestArmor), newChestArmor.getDamage());
                    }
                    ArmorChangeCallback.EVENT.invoker().interact("CHEST", new ItemStackHelper(newChestArmor));
                    chestArmor = newChestArmor.copy();
                    
                }
                
                ItemStack newLegArmor = inv.getArmorStack(1);
                if (!areEqual(newLegArmor, legArmor)) {
                    if (areEqualIgnoreDamage(newLegArmor, legArmor)) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newLegArmor), newLegArmor.getDamage());
                    }
                    ArmorChangeCallback.EVENT.invoker().interact("LEGS", new ItemStackHelper(newLegArmor));
                    legArmor = newLegArmor.copy();
                }
                
                ItemStack newFootArmor = inv.getArmorStack(0);
                if (!areEqual(newFootArmor, footArmor)) {
                    if (areEqualIgnoreDamage(newFootArmor, footArmor)) {
                        ItemDamageCallback.EVENT.invoker().interact(new ItemStackHelper(newFootArmor), newFootArmor.getDamage());
                    }
                    ArmorChangeCallback.EVENT.invoker().interact("FEET", new ItemStackHelper(newFootArmor));
                    footArmor = newFootArmor.copy();
                }
            }
        });
    }
}
