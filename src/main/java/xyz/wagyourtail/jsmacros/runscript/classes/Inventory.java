package xyz.wagyourtail.jsmacros.runscript.classes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.container.SlotActionType;
import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;

public class Inventory {
    private ContainerScreen<?> inventory;
    private ClientPlayerInteractionManager man;
    private int wID;
    private ClientPlayerEntity player;

    public Inventory() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen instanceof ContainerScreen) {
            this.inventory = (ContainerScreen<?>) mc.currentScreen;
        } else {
            this.inventory = new InventoryScreen(mc.player);
        }
        this.player = mc.player;
        this.man = mc.interactionManager;
        this.wID = this.inventory.getContainer().syncId;
    }

    public void click(int slot, int mousebutton) {
        SlotActionType act = mousebutton == 2 ? SlotActionType.CLONE : SlotActionType.PICKUP;
        man.clickSlot(wID, slot, mousebutton, act, player);
    }

    public void dragClick(int[] slots, int mousebutton) {
        mousebutton = mousebutton == 0 ? 1 : 5;
        man.clickSlot(wID, -999, mousebutton - 1, SlotActionType.QUICK_CRAFT, player); // start drag click
        for (int i : slots) {
            man.clickSlot(wID, i, mousebutton, SlotActionType.QUICK_CRAFT, player);
        }
        man.clickSlot(wID, -999, mousebutton + 1, SlotActionType.QUICK_CRAFT, player);
    }

    public void closeAndDrop() {
        ItemStack held = player.inventory.getCursorStack();
        if (!held.isEmpty()) man.clickSlot(wID, -999, 0, SlotActionType.PICKUP, player);
        player.closeContainer();
        this.inventory = null;
    }

    public void close() {
        player.closeContainer();
    }

    public void quick(int slot) {
        man.clickSlot(wID, slot, 0, SlotActionType.QUICK_MOVE, player);
    }

    public ItemStackHelper getHeld() {
        return new ItemStackHelper(player.inventory.getCursorStack());
    }

    public ItemStackHelper getSlot(int slot) {
        return new ItemStackHelper(this.inventory.getContainer().getSlot(slot).getStack());
    }

    public int getTotalSlots() {
        return this.inventory.getContainer().slots.size();
    }

    public void split(int slot1, int slot2) throws Exception {
        if (slot1 == slot2) throw new Exception("must be 2 different slots.");
        if (!getSlot(slot1).isEmpty() || !getSlot(slot2).isEmpty()) throw new Exception("slots must be empty.");
        man.clickSlot(wID, slot1, 1, SlotActionType.PICKUP, player);
        man.clickSlot(wID, slot2, 0, SlotActionType.PICKUP, player);
    }

    public void grabAll(int slot) {
        man.clickSlot(wID, slot, 0, SlotActionType.PICKUP, player);
        man.clickSlot(wID, slot, 0, SlotActionType.PICKUP_ALL, player);
    }

    public void swap(int slot1, int slot2) {
        boolean is1 = getSlot(slot1).isEmpty();
        boolean is2 = getSlot(slot2).isEmpty();
        if (is1 && is2) return;
        if (!is1) man.clickSlot(wID, slot1, 0, SlotActionType.PICKUP, player);
        man.clickSlot(wID, slot2, 0, SlotActionType.PICKUP, player);
        if (!is2) man.clickSlot(wID, slot1, 0, SlotActionType.PICKUP, player);
    }

    public ContainerScreen<?> getRawContainer() {
        return this.inventory;
    }
}
