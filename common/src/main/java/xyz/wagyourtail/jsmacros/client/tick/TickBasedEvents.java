package xyz.wagyourtail.jsmacros.client.tick;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventItemDamage;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventArmorChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventFallFlying;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventHeldItemChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventTick;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FClient;

public class TickBasedEvents {
    private static ItemStack mainHand = ItemStack.EMPTY;
    private static ItemStack offHand = ItemStack.EMPTY;

    private static ItemStack footArmor = ItemStack.EMPTY;
    private static ItemStack legArmor = ItemStack.EMPTY;
    private static ItemStack chestArmor = ItemStack.EMPTY;
    private static ItemStack headArmor = ItemStack.EMPTY;
    private static boolean previousFallFlyState = false;
    private static long counter = 0;

    public static final OldServerPinger serverListPinger = new OldServerPinger();


    public static boolean areEqualNoDamage(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem();
    }

    public static boolean areNotEqual(ItemStack a, ItemStack b) {
        return (a != null || b != null) && (a == null || b == null || !a.equalsIgnoreTags(b) || a.count != b.count || !ItemStack.equalsIgnoreDamage(a, b) || a.getDamage() != b.getDamage());
    }

    public static boolean areTagsEqualIgnoreDamage(ItemStack a, ItemStack b) {
        if (a == null && b == null) {
            return true;
        } else if (a != null && b != null) {
            if (a.getTag() == null && b.getTag() == null) {
                return true;
            } else {
                NBTTagCompound at;
                NBTTagCompound bt;
                if (a.getTag() != null) at = (NBTTagCompound) a.getTag().copy();
                else at = new NBTTagCompound();
                if (b.getTag() != null) bt = (NBTTagCompound) b.getTag().copy();
                else bt = new NBTTagCompound();
                at.remove("Damage");
                bt.remove("Damage");
                return at.equals(bt);
            }

        } else {
            return false;
        }
    }

    public static boolean areEqualIgnoreDamage(ItemStack a, ItemStack b) {
        return (a.isEmpty() && b.isEmpty()) || (!a.isEmpty() && !b.isEmpty() && a.isItemEqual(b) && a.getCount() == b.getCount() && areTagsEqualIgnoreDamage(a, b));
    }


    public static void onTick(Minecraft mc) {

        if (JsMacros.keyBinding.isPressed() && mc.currentScreen == null) {
            mc.openScreen(JsMacros.prevScreen);
        }

        FClient.tickSynchronizer.tick();
//        serverListPinger.tick();

        new EventTick();
        new EventJoinedTick();

        if (++counter % 10 == 0) {
            JsMacros.core.services.tickReloadListener();
        }
        
        if (mc.player != null) {
            boolean state = mc.player.isFallFlying();
            if (previousFallFlyState ^ state) {
                new EventFallFlying(state);
                previousFallFlyState = state;
            }
        }

        if (mc.player != null && mc.player.inventory != null) {
            InventoryPlayer inv = mc.player.inventory;

            ItemStack newMainHand = inv.getMainHandStack();
            if (areNotEqual(newMainHand, mainHand)) {
                if (areEqualIgnoreDamage(newMainHand, mainHand)) {
                    new EventItemDamage(newMainHand, newMainHand.getDamage());
                }
                new EventHeldItemChange(newMainHand, mainHand, false);
                mainHand = newMainHand != null ? newMainHand.copy() : null;
            }

            ItemStack newHeadArmor = inv.armor[3];
            if (areNotEqual(newHeadArmor, headArmor)) {
                if (areEqualIgnoreDamage(newHeadArmor, headArmor)) {
                    new EventItemDamage(newHeadArmor, newHeadArmor.getDamage());
                }
                new EventArmorChange("HEAD", newHeadArmor, headArmor);
                headArmor = newHeadArmor != null ? newHeadArmor.copy() : null;
            }

            ItemStack newChestArmor = inv.armor[2];
            if (areNotEqual(newChestArmor, chestArmor)) {
                if (areEqualIgnoreDamage(newChestArmor, chestArmor)) {
                    new EventItemDamage(newChestArmor, newChestArmor.getDamage());
                }
                new EventArmorChange("CHEST", newChestArmor, chestArmor);
                chestArmor = newChestArmor != null ? newChestArmor.copy() : null;

            }

            ItemStack newLegArmor = inv.armor[1];
            if (areNotEqual(newLegArmor, legArmor)) {
                if (areEqualIgnoreDamage(newLegArmor, legArmor)) {
                    new EventItemDamage(newLegArmor, newLegArmor.getDamage());
                }
                new EventArmorChange("LEGS", newLegArmor, legArmor);
                legArmor = newLegArmor != null ? newLegArmor.copy() : null;
            }

            ItemStack newFootArmor = inv.armor[0];
            if (areNotEqual(newFootArmor, footArmor)) {
                if (areEqualIgnoreDamage(newFootArmor, footArmor)) {
                    new EventItemDamage(newFootArmor, newFootArmor.getDamage());
                }
                new EventArmorChange("FEET", newFootArmor, footArmor);
                footArmor = newFootArmor != null ? newFootArmor.copy() : null;
            }
        }
    }
}
