package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.class_3136;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.Window;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.screen.ChestScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ItemAction;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IInventory;
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
public class Inventory<T extends HandledScreen> {
    protected T inventory;
    protected Map<String, int[]> map;
    protected final ClientPlayerInteractionManager man;
    protected final int syncId;
    protected final ClientPlayerEntity player;
    protected static MinecraftClient mc = MinecraftClient.getInstance();

    public static Inventory<?> create() {
        Inventory<?> inv = create(mc.currentScreen);
        if (inv == null) {
            assert mc.player != null;
            return new Inventory<>(new SurvivalInventoryScreen(mc.player));
        }
        return inv;
    }

    public static Inventory<?> create(Screen s) {
        if (s instanceof HandledScreen) {
            if (s instanceof VillagerTradingScreen) return new VillagerInventory((VillagerTradingScreen) s);
            if (s instanceof EnchantingScreen) return new EnchantInventory((EnchantingScreen) s);
            if (s instanceof BeaconScreen) return new BeaconInventory((BeaconScreen) s);
            return new Inventory<>((HandledScreen) s);
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
        man.method_1224(syncId, slot, mousebutton, ItemAction.PICKUP, player);
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
        man.method_1224(syncId, -999, mousebutton - 1, ItemAction.QUICK_CRAFT, player); // start drag click
        for (int i : slots) {
            man.method_1224(syncId, i, mousebutton, ItemAction.QUICK_CRAFT, player);
        }
        man.method_1224(syncId, -999, mousebutton + 1, ItemAction.QUICK_CRAFT, player);
        return this;
    }

    /**
     * @since 1.5.0
     * @param slot
     */
    public Inventory<T> dropSlot(int slot) {
        man.method_1224(syncId, slot, 0, ItemAction.THROW, player);
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
        if (index >= 0 && index < PlayerInventory.getHotbarSize())
            player.inventory.selectedSlot = index;
    }

    /**
     * closes the inventory, (if the inventory/container is visible it will close the gui). also drops any "held on mouse" items.
     * 
     * @return
     */
    public Inventory<T> closeAndDrop() {
        ItemStack held = player.inventory.getCursorStack();
        if (!held.isEmpty()) man.method_1224(syncId, -999, 0, ItemAction.PICKUP, player);
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
        man.method_1224(syncId, slot, 0, ItemAction.QUICK_MOVE, player);
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
        net.minecraft.inventory.Inventory hoverSlotInv = inventory.screenHandler.slots.get(slot).inventory;
        for(Slot slot2 : inventory.screenHandler.slots) {
            if (slot2 != null
                && slot2.canTakeItems(mc.player)
                && slot2.hasStack()
                && slot2.inventory == hoverSlotInv
                && ScreenHandler.canInsertItemIntoSlot(slot2, cursorStack, true)) {
                count += slot2.getStack().getCount();
                man.method_1224(syncId, slot2.id, button, ItemAction.QUICK_MOVE, player);
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
        man.method_1224(syncId, slot1, 1, ItemAction.PICKUP, player);
        man.method_1224(syncId, slot2, 0, ItemAction.PICKUP, player);
        return this;
    }

    /**
     * Does that double click thingy to turn a incomplete stack pickup into a complete stack pickup if you have more in your inventory.
     * 
     * @param slot
     * @return
     */
    public Inventory<T> grabAll(int slot) {
        man.method_1224(syncId, slot, 0, ItemAction.PICKUP, player);
        man.method_1224(syncId, slot, 0, ItemAction.PICKUP_ALL, player);
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
        if (!is1) man.method_1224(syncId, slot1, 0, ItemAction.PICKUP, player);
        man.method_1224(syncId, slot2, 0, ItemAction.PICKUP, player);
        if (!is2) man.method_1224(syncId, slot1, 0, ItemAction.PICKUP, player);
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
        man.method_1224(syncId, slot, hotbarSlot, ItemAction.SWAP, player);
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
        MinecraftClient mc = MinecraftClient.getInstance();
        Window window = new Window(mc);
        double x = mc.mouse.x * (double)window.getScaledWidth() / (double)window.getWidth();
        double y = mc.mouse.y * (double)window.getScaledHeight() / (double)window.getHeight();
        if (this.inventory != mc.currentScreen) throw new RuntimeException("Inventory screen is not open.");
        Slot s = ((IInventory)this.inventory).jsmacros_getSlotUnder((int) x, (int) y);
        if (s == null) return -999;
        return this.inventory.screenHandler.slots.indexOf(s);
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
        if (this.inventory instanceof InventoryScreen || (this.inventory instanceof CreativeInventoryScreen && ((CreativeInventoryScreen) this.inventory).getSelectedTab() == ItemGroup.INVENTORY.getIndex())) {
            if (this.inventory instanceof CreativeInventoryScreen) {
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
            if (this.inventory instanceof  CreativeInventoryScreen) {
                map.put("delete", new int[] {0});
                map.remove("crafting_in");
                map.remove("craft_out");
            } 
        } else {
            map.put("hotbar", JsMacros.range(slots - 9, slots));
            map.put("main", JsMacros.range(slots - 9 - 27, slots - 9));
            if (inventory instanceof CreativeInventoryScreen) {
                map.remove("main");
                map.put("creative", JsMacros.range(slots - 9));
            } else if (inventory instanceof ChestScreen || inventory instanceof DispenserScreen || inventory instanceof HopperScreen) {
                map.put("container", JsMacros.range(slots - 9 - 27));
            } else if (inventory instanceof BeaconScreen) {
                map.put("slot", new int[] { slots - 9 - 27 - 1 });
            } else if ( inventory instanceof FurnaceScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("fuel", new int[] { slots - 9 - 27 - 2 });
                map.put("input", new int[] { slots - 9 - 27 - 3 });
            } else if (inventory instanceof BrewingStandScreen) {
                map.put("fuel", new int[] { slots - 9 - 27 - 1 });
                map.put("input", new int[] { slots - 9 - 27 - 2 });
                map.put("output", JsMacros.range(slots - 9 - 27 - 2));
            } else if (inventory instanceof CraftingTableScreen) {
                map.put("input", JsMacros.range(slots - 9 - 27 - 9, slots - 9 - 27));
                map.put("output", new int[] { slots - 9 - 27 - 10 });
            } else if (inventory instanceof EnchantingScreen) {
                map.put("lapis", new int[] { slots - 9 - 27 - 1 });
                map.put("item", new int[] { slots - 9 - 27 - 2 });
            } else if (inventory instanceof HorseScreen) {
                class_3136 h = (class_3136) ((IHorseScreen)this.inventory).jsmacros_getEntity();
                if (h.method_13990()) map.put("saddle", new int[] {0});
                if (h.method_13984()) map.put("armor", new int[] {1});
                if (h.method_13089()) {
                    map.put("container", JsMacros.range(2, slots - 9 - 27));
                }
            } else if (inventory instanceof AnvilScreen || inventory instanceof VillagerTradingScreen) {
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
        return ((ChestScreenHandler)(inventory.screenHandler)).getInventory().getName().asFormattedString();
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
