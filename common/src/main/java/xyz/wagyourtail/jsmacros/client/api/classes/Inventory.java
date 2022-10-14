package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IHorseScreen;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.RecipeHelper;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wagyourtail
 * @since 1.0.8
 */
 @SuppressWarnings("unused")
public class Inventory<T extends GuiContainer> {
    protected T inventory;
    protected Map<String, int[]> map;
    protected final PlayerControllerMP man;
    protected final int syncId;
    protected final EntityPlayerSP player;
    protected static Minecraft mc = Minecraft.getInstance();

    public static Inventory<?> create() {
        Inventory<?> inv = create(mc.currentScreen);
        if (inv == null) {
            assert mc.player != null;
            return new Inventory<>(new GuiInventory(mc.player));
        }
        return inv;
    }

    public static Inventory<?> create(GuiScreen s) {
        if (s instanceof GuiContainer) {
            if (s instanceof GuiMerchant) return new VillagerInventory((GuiMerchant) s);
            if (s instanceof GuiEnchantment) return new EnchantInventory((GuiEnchantment) s);
            if (s instanceof GuiBeacon) return new BeaconInventory((GuiBeacon) s);
            return new Inventory<>((GuiContainer) s);
        }
        return null;
    }

    protected Inventory(T inventory) {
        this.inventory = inventory;
        this.player = mc.player;
        assert player != null;
        this.man = mc.interactionManager;
        this.syncId = this.inventory.screenHandler.syncId;
    }

    /**
     * @param slot
     * @since 1.5.0
     * @return
     */
    public Inventory<T> click(int slot) {
        click(slot, 0);
        return this;
    }

    /**
     * Clicks a slot with a mouse button.
     *
     * @since 1.0.8
     * @param slot
     * @param mousebutton
     * @return
     */
    public Inventory<T> click(int slot, int mousebutton) {
        int act = mousebutton == 2 ? 3 : 0;
        man.clickSlot(syncId, slot, mousebutton, act, player);
        return this;
    }

    /**
     * Does a drag-click with a mouse button. (the slots don't have to be in order or even adjacent, but when vanilla minecraft calls the underlying function they're always sorted...)
     * 
     * @param slots
     * @param mousebutton
     * @return
     */
    public Inventory<T> dragClick(int[] slots, int mousebutton) {
        mousebutton = mousebutton == 0 ? 1 : 5;
        man.clickSlot(syncId, -999, mousebutton - 1, 5, player); // start drag click
        for (int i : slots) {
            man.clickSlot(syncId, i, mousebutton, 5, player);
        }
        man.clickSlot(syncId, -999, mousebutton + 1, 5, player);
        return this;
    }

    /**
     * @since 1.5.0
     * @param slot
     */
    public Inventory<T> dropSlot(int slot) {
        man.clickSlot(syncId, slot, 0, 4, player);
        return this;
    }
    
    /**
     * @since 1.2.5
     * 
     * @return the index of the selected hotbar slot.
     */
    public int getSelectedHotbarSlotIndex() {
        return player.inventory.selectedSlot;
    }
    
    /**
     * @since 1.2.5
     * 
     * @param index
     */
    public void setSelectedHotbarSlotIndex(int index) {
        if (index >= 0 && index < InventoryPlayer.getHotbarSize())
            player.inventory.selectedSlot = index;
    }

    /**
     * closes the inventory, (if the inventory/container is visible it will close the gui). also drops any "held on mouse" items.
     * 
     * @return
     */
    public Inventory<T> closeAndDrop() {
        ItemStack held = player.inventory.getCursorStack();
        if (!held.isEmpty()) man.clickSlot(syncId, -999, 0, 0, player);
        mc.execute(player::closeHandledScreen);
        this.inventory = null;
        return this;
    }

    /**
     * Closes the inventory, and open gui if applicable.
     */
    public void close() {
        mc.execute(player::closeHandledScreen);
    }

    /**
     * simulates a shift-click on a slot.
     * It should be safe to chain these without {@link FClient#waitTick()} at least for a bunch of the same item.
     *
     * @param slot
     * @return
     */
    public Inventory<T> quick(int slot) {
        man.clickSlot(syncId, slot, 0, 1, player);
        return this;
    }

    /**
     * @param slot
     * @since 1.7.0
     * @return
     */
    public int quickAll(int slot) {
        return quickAll(slot, 0);
    }

    /**
     * quicks all that match the slot
     * @param slot a slot from the section you want to move items from
     * @param button
     * @since 1.7.0
     * @return number of items that matched
     */
    public int quickAll(int slot, int button) {
        int count = 0;
        ItemStack cursorStack = inventory.screenHandler.slots.get(slot).getStack().copy();
        IInventory hoverSlotInv = inventory.screenHandler.slots.get(slot).inventory;
        for(Slot slot2 : inventory.screenHandler.slots) {
            if (slot2 != null
                && slot2.canTakeItems(mc.player)
                && slot2.hasStack()
                && slot2.inventory == hoverSlotInv
                && Container.canInsertItemIntoSlot(slot2, cursorStack, true)) {
                count += slot2.getStack().count;
                man.clickSlot(syncId, slot2.id, button, 1, player);
            }
        }
        return count;
    }

    /**
     * @return the held (by the mouse) item.
     */
    public ItemStackHelper getHeld() {
        return new ItemStackHelper(player.inventory.getCursorStack());
    }

    /**
     * 
     * @param slot
     * @return the item in the slot.
     */
    public ItemStackHelper getSlot(int slot) {
        return new ItemStackHelper(this.inventory.screenHandler.getSlot(slot).getStack());
    }

    /**
     * @return the size of the container/inventory.
     */
    public int getTotalSlots() {
        return this.inventory.screenHandler.slots.size();
    }

    /**
     * Splits the held stack into two slots. can be alternatively done with {@link Inventory#dragClick(int[], int)} if this one has issues on some servers.
     * 
     * @param slot1
     * @param slot2
     * @return
     * @throws Exception
     */
    public Inventory<T> split(int slot1, int slot2) throws Exception {
        if (slot1 == slot2) throw new Exception("must be 2 different slots.");
        if (!getSlot(slot1).isEmpty() || !getSlot(slot2).isEmpty()) throw new Exception("slots must be empty.");
        man.clickSlot(syncId, slot1, 1, 0, player);
        man.clickSlot(syncId, slot2, 0, 0, player);
        return this;
    }

    /**
     * Does that double click thingy to turn a incomplete stack pickup into a complete stack pickup if you have more in your inventory.
     * 
     * @param slot
     * @return
     */
    public Inventory<T> grabAll(int slot) {
        man.clickSlot(syncId, slot, 0, 0, player);
        man.clickSlot(syncId, slot, 0, 6, player);
        return this;
    }

    /**
     * swaps the items in two slots.
     * 
     * @param slot1
     * @param slot2
     * @return
     */
    public Inventory<T> swap(int slot1, int slot2) {
        boolean is1 = getSlot(slot1).isEmpty();
        boolean is2 = getSlot(slot2).isEmpty();
        if (is1 && is2) return this;
        if (!is1) man.clickSlot(syncId, slot1, 0, 0, player);
        man.clickSlot(syncId, slot2, 0, 0, player);
        if (!is2) man.clickSlot(syncId, slot1, 0, 0, player);
        return this;
    }

    /**
     * equivelent to hitting the numbers or f for swapping slots to hotbar
     *
     * @param slot
     * @param hotbarSlot 0-8 or 40 for offhand
     * @since 1.6.5 [citation needed]
     * @return
     */
    public Inventory<T> swapHotbar(int slot, int hotbarSlot) {
        if (hotbarSlot != 40) {
            if (hotbarSlot < 0 || hotbarSlot > 8)
                throw new IllegalArgumentException("hotbarSlot must be between 0 and 8 or 40 for offhand.");
        }
        man.clickSlot(syncId, slot, hotbarSlot, 2, player);
        return this;
    }
    
    /**
     * @since 1.2.8
     *
     */
     public void openGui() {
        mc.execute(() -> mc.openScreen(this.inventory));
     }

    /**
     * @since 1.1.3
     * 
     * @return the id of the slot under the mouse.
     */
    public int getSlotUnderMouse() {
        return inventory.screenHandler.slots.indexOf(this.inventory.getSlotUnderMouse());
    }
    
    /**
     * @since 1.1.3
     * 
     * @return the part of the mapping the slot is in.
     */
    public String getType() {
        return JsMacros.getScreenName(this.inventory);
    }

    /**
     * @since 1.1.3
     * 
     * @return the inventory mappings different depending on the type of open container/inventory.
     */
    public Map<String, int[]> getMap() {
        if (map == null) {
            map = getMapInternal();
        }
        return map;
    }
    
    /**
     * @since 1.1.3
     * 
     * @param slotNum
     * @return returns the part of the mapping the slot is in.
     */
    public String getLocation(int slotNum) {
        if (map == null) {
            map = getMapInternal();
        }
        for (String k : map.keySet()) {
           for (int i : map.get(k)) {
                if (i == slotNum) {
                    return k;
                }
            }
        }
        return null;
    }
    
    /**
     * @since 1.3.1
     * @return all craftable recipes
     */
    public List<RecipeHelper> getCraftableRecipes() throws InterruptedException {
        throw new NullPointerException("Not implemented yet, it's theoretically possible, but no recipe book means it's harder.");
    }
    
    private Map<String, int[]> getMapInternal() {
        Map<String, int[]> map = new HashMap<>();
        int slots = getTotalSlots();
        if (this.inventory instanceof GuiInventory || (this.inventory instanceof GuiContainerCreative && ((GuiContainerCreative) this.inventory).getSelectedTab() == CreativeTabs.INVENTORY.getIndex())) {
            if (this.inventory instanceof GuiContainerCreative) {
                --slots;
            }
            map.put("hotbar", JsMacros.range(slots - 10, slots - 1)); // range(36, 45);
            map.put("offhand", new int[] { slots - 1 }); // range(45, 46);
            map.put("main", JsMacros.range(slots - 10 - 27, slots - 10)); // range(9, 36);
            map.put("boots", new int[] { slots - 10 - 27 - 1 }); // range(8, 9);
            map.put("leggings", new int[] { slots - 10 - 27 - 2 }); // range(7, 8);
            map.put("chestplate", new int[] { slots - 10 - 27 - 3 }); // range(6, 7);
            map.put("helmet", new int[] { slots - 10 - 27 - 4 }); // range(5, 6);
            map.put("crafting_in", JsMacros.range(slots - 10 - 27 - 4 - 4, slots - 10 - 27 - 4)); // range(1, 5);
            map.put("craft_out", new int[] { slots - 10 - 27 - 4 - 4 - 1 });
            if (this.inventory instanceof  GuiContainerCreative) {
                map.put("delete", new int[] {0});
                map.remove("crafting_in");
                map.remove("craft_out");
            } 
        } else {
            map.put("hotbar", JsMacros.range(slots - 9, slots));
            map.put("main", JsMacros.range(slots - 9 - 27, slots - 9));
            if (inventory instanceof GuiContainerCreative) {
                map.remove("main");
                map.put("creative", JsMacros.range(slots - 9));
            } else if (inventory instanceof GuiChest || inventory instanceof GuiDispenser || inventory instanceof GuiHopper) {
                map.put("container", JsMacros.range(slots - 9 - 27));
            } else if (inventory instanceof GuiBeacon) {
                map.put("slot", new int[] { slots - 9 - 27 - 1 });
            } else if ( inventory instanceof GuiFurnace) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("fuel", new int[] { slots - 9 - 27 - 2 });
                map.put("input", new int[] { slots - 9 - 27 - 3 });
            } else if (inventory instanceof GuiBrewingStand) {
                map.put("fuel", new int[] { slots - 9 - 27 - 1 });
                map.put("input", new int[] { slots - 9 - 27 - 2 });
                map.put("output", JsMacros.range(slots - 9 - 27 - 2));
            } else if (inventory instanceof GuiCrafting) {
                map.put("input", JsMacros.range(slots - 9 - 27 - 9, slots - 9 - 27));
                map.put("output", new int[] { slots - 9 - 27 - 10 });
            } else if (inventory instanceof GuiEnchantment) {
                map.put("lapis", new int[] { slots - 9 - 27 - 1 });
                map.put("item", new int[] { slots - 9 - 27 - 2 });
            } else if (inventory instanceof GuiScreenHorseInventory) {
                EntityHorse h = (EntityHorse) ((IHorseScreen)this.inventory).jsmacros_getEntity();
                if (h.isTame()) map.put("saddle", new int[] {0});
                if (h.func_175507_cI()) map.put("armor", new int[] {1});
                if (h.hasChest()) {
                    map.put("container", JsMacros.range(2, slots - 9 - 27));
                }
            } else if (inventory instanceof GuiRepair || inventory instanceof GuiMerchant) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("input", JsMacros.range(slots - 9 - 27 - 1));
            }
        }

        return map;
    }

    /**
     * @since 1.2.3
     * 
     * @return
     */
    public String getContainerTitle() {
        return ((ContainerChest)(inventory.screenHandler)).getInventory().getName().asFormattedString();
    }
    
    public T getRawContainer() {
        return this.inventory;
    }
    
    public String toString() {
        return String.format("Inventory:{\"Type\": \"%s\"}", this.getType());
    }

    /**
     * @since 1.6.0
     * @return
     */
    public int getCurrentSyncId() {
        return syncId;
    }
}
