package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IHorseScreen;
import xyz.wagyourtail.jsmacros.client.access.IInventory;
import xyz.wagyourtail.jsmacros.client.access.IRecipeBookResults;
import xyz.wagyourtail.jsmacros.client.access.IRecipeBookWidget;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.RecipeHelper;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FClient;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Wagyourtail
 * @since 1.0.8
 */
@SuppressWarnings("unused")
public class Inventory<T extends HandledScreen<?>> {
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
            return new Inventory<>(new InventoryScreen(mc.player));
        }
        return inv;
    }

    public static Inventory<?> create(net.minecraft.client.gui.screen.Screen s) {
        if (s instanceof HandledScreen) {
            if (s instanceof MerchantScreen merchantScreen) {
                return new VillagerInventory(merchantScreen);
            } else if (s instanceof EnchantmentScreen enchantmentScreen) {
                return new EnchantInventory(enchantmentScreen);
            } else if (s instanceof LoomScreen loomScreen) {
                return new LoomInventory(loomScreen);
            } else if (s instanceof BeaconScreen beaconScreen) {
                return new BeaconInventory(beaconScreen);
            } else if (s instanceof AnvilScreen anvilScreen) {
                return new AnvilInventory(anvilScreen);
            } else if (s instanceof BrewingStandScreen brewingStandScreen) {
                return new BrewingStandInventory(brewingStandScreen);
            } else if (s instanceof CartographyTableScreen cartographyTableScreen) {
                return new CartographyInventory(cartographyTableScreen);
            } else if (s instanceof FurnaceScreen furnaceScreen) {
                return new FurnaceInventory(furnaceScreen);
            } else if (s instanceof GrindstoneScreen grindstoneScreen) {
                return new GrindStoneInventory(grindstoneScreen);
            } else if (s instanceof SmithingScreen smithingScreen) {
                return new SmithingInventory(smithingScreen);
            } else if (s instanceof StonecutterScreen stonecutterScreen) {
                return new StoneCutterInventory(stonecutterScreen);
            } else if (s instanceof GenericContainerScreen || s instanceof  Generic3x3ContainerScreen || s instanceof  HopperScreen || s instanceof ShulkerBoxScreen) {
                return new ContainerInventory<>((HandledScreen<?>) s);
            }
            return new Inventory<>((HandledScreen<?>) s);
        }
        return null;
    }

    protected Inventory(T inventory) {
        this.inventory = inventory;
        this.player = mc.player;
        assert player != null;
        this.man = mc.interactionManager;
        this.syncId = inventory.getScreenHandler().syncId;
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
     * Clicks a slot with a mouse button.~~if the slot is a container, it will click the first slot in the container
     *
     * @since 1.0.8
     * @param slot
     * @param mousebutton
     * @return
     */
    public Inventory<T> click(int slot, int mousebutton) {
        SlotActionType act = mousebutton == 2 ? SlotActionType.CLONE : SlotActionType.PICKUP;
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
        man.clickSlot(syncId, -999, mousebutton - 1, SlotActionType.QUICK_CRAFT, player); // start drag click
        for (int i : slots) {
            man.clickSlot(syncId, i, mousebutton, SlotActionType.QUICK_CRAFT, player);
        }
        man.clickSlot(syncId, -999, mousebutton + 1, SlotActionType.QUICK_CRAFT, player);
        return this;
    }

    /**
     * @since 1.5.0
     * @param slot
     */
    public Inventory<T> dropSlot(int slot) {
        man.clickSlot(syncId, slot, 0, SlotActionType.THROW, player);
        return this;
    }

    /**
     * @param slot  the slot to drop
     * @param stack decide whether to drop the whole stack or just a single item
     * @return this instance for chaining.
     *
     * @since 1.9.0
     */
    public Inventory<T> dropSlot(int slot, boolean stack) {
        man.clickSlot(syncId, slot, stack ? 1 : 0, SlotActionType.THROW, player);
        return this;
    }

    /**
     * @param item the item to check for
     * @return {@code true} if the item is contined anywhere in the inventory, {@code false}
     *         otherwise.
     *
     * @since 1.9.0
     */
    public boolean contains(ItemStackHelper item) {
        return getItems().stream().anyMatch(stack -> stack.equals(item));
    }

    /**
     * @param item the item to check for
     * @return {@code true} if the item is contined anywhere in the inventory, {@code false}
     *         otherwise.
     *
     * @since 1.9.0
     */
    public boolean contains(String item) {
        return getItems().stream().anyMatch(stack -> stack.getItemId().equals(item));
    }

    /**
     * @return the first empty slot in the main inventory or {@code -1} if there are no empty
     *         slots.
     *
     * @since 1.9.0
     */
    public int findFreeInventorySlot() {
        return findFreeSlot("main", "hotbar");
    }

    /**
     * @return the first empty hotbar slot or {@code -1} if there are no empty slots.
     *
     * @since 1.9.0
     */
    public int findFreeHotbarSlot() {
        return findFreeSlot("hotbar");
    }

    /**
     * @param mapIdentifiers the identifier used to get the slots from {@link #getMap()}.
     * @return the first empty slot in the given slots and -1 if there are no empty slots
     *
     * @since 1.9.0
     */
    public int findFreeSlot(String... mapIdentifiers) {
        for (int slot : getSlots(mapIdentifiers)) {
            if (getSlot(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * @return a map of all item ids mapped to their total count.
     *
     * @since 1.9.0
     */
    public Map<String, Integer> getItemCount() {
        Object2IntOpenHashMap<String> itemMap = new Object2IntOpenHashMap<>();
        getItems().stream().filter(Predicate.not(ItemStackHelper::isEmpty)).forEach(item -> itemMap.addTo(item.getItemId(), item.getCount()));
        return itemMap;
    }

    /**
     * @return a list of all items in the inventory.
     *
     * @since 1.9.0
     */
    public List<ItemStackHelper> getItems() {
        return IntStream.range(0, getTotalSlots()).mapToObj(this::getSlot).filter(Predicate.not(ItemStackHelper::isEmpty)).toList();
    }

    /**
     * @param mapIdentifiers the inventory sections
     * @return a list of all items in the given inventory sections.
     *
     * @since 1.9.0
     */
    public List<ItemStackHelper> getItems(String... mapIdentifiers) {
        return Arrays.stream(getSlots(mapIdentifiers)).mapToObj(this::getSlot).filter(Predicate.not(ItemStackHelper::isEmpty)).toList();
    }

    /**
     * @param item the item to search for
     * @return all slots containing the given item.
     *
     * @since 1.9.0
     */
    public int[] findItem(ItemStackHelper item) {
        IntList slots = new IntArrayList();
        for (int i = 0; i < getTotalSlots(); i++) {
            if (getSlot(i).equals(item)) {
                slots.add(i);
            }
        }
        return slots.toIntArray();
    }

    /**
     * @param item the item to search for
     * @return all slots containing the given item.
     *
     * @since 1.9.0
     */
    public int[] findItem(String item) {
        IntList slots = new IntArrayList();
        for (int i = 0; i < getTotalSlots(); i++) {
            if (getSlot(i).getItemId().equals(item)) {
                slots.add(i);
            }
        }
        return slots.toIntArray();
    }

    /**
     * @param mapIdentifiers the inventory sections
     * @return all slots indexes in the given inventory sections.
     *
     * @since 1.9.0
     */
    public int[] getSlots(String... mapIdentifiers) {
        Map<String, int[]> map = getMap();
        IntList slots = new IntArrayList();
        for (String key : mapIdentifiers) {
            if (map.containsKey(key)) {
                slots.addAll(IntList.of(map.get(key)));
            }
        }
        return slots.toIntArray();
    }

    /**
     * @param callback the callback for accepting the items.
     * @since 1.9.0
     */
    public void iterateItems(MethodWrapper<ItemStackHelper, Integer, ?, ?> callback) {
        int count = getTotalSlots();
        for (int i = 0; i < count; i++) {
            ItemStackHelper stack = getSlot(i);
            if (!stack.isEmpty()) {
                callback.accept(stack, i);
            }
        }
    }

    /**
     * @param callback       the callback for accepting the items
     * @param mapIdentifiers the inventory sections to iterate over
     * @since 1.9.0
     */
    public void iterateItems(MethodWrapper<ItemStackHelper, Integer, ?, ?> callback, String... mapIdentifiers) {
        IntStream.of(getSlots(mapIdentifiers)).forEach(i -> {
            ItemStackHelper stack = getSlot(i);
            if (!stack.isEmpty()) {
                callback.accept(stack, i);
            }
        });
    }
    
    /**
     * @since 1.2.5
     * 
     * @return the index of the selected hotbar slot.
     */
    public int getSelectedHotbarSlotIndex() {
        return player.getInventory().selectedSlot;
    }
    
    /**
     * @since 1.2.5
     * 
     * @param index
     */
    public void setSelectedHotbarSlotIndex(int index) {
        if (PlayerInventory.isValidHotbarIndex(index))
            player.getInventory().selectedSlot = index;
    }

    /**
     * closes the inventory, (if the inventory/container is visible it will close the gui). also drops any "held on mouse" items.
     * 
     * @return
     */
    public Inventory<T> closeAndDrop() {
        ItemStack held = inventory.getScreenHandler().getCursorStack();
        if (!held.isEmpty()) man.clickSlot(syncId, -999, 0, SlotActionType.PICKUP, player);
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
        man.clickSlot(syncId, slot, 0, SlotActionType.QUICK_MOVE, player);
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
        ItemStack cursorStack = inventory.getScreenHandler().slots.get(slot).getStack().copy();
        net.minecraft.inventory.Inventory hoverSlotInv = inventory.getScreenHandler().slots.get(slot).inventory;
        for(Slot slot2 : inventory.getScreenHandler().slots) {
            if (slot2 != null
                && slot2.canTakeItems(mc.player)
                && slot2.hasStack()
                && slot2.inventory == hoverSlotInv
                && ScreenHandler.canInsertItemIntoSlot(slot2, cursorStack, true)) {
                count += slot2.getStack().getCount();
                man.clickSlot(syncId, slot2.id, button, SlotActionType.QUICK_MOVE, player);
            }
        }
        return count;
    }

    /**
     * @return the held (by the mouse) item.
     */
    public ItemStackHelper getHeld() {
        return new ItemStackHelper(inventory.getScreenHandler().getCursorStack());
    }

    /**
     * 
     * @param slot
     * @return the item in the slot.
     */
    public ItemStackHelper getSlot(int slot) {
        return new ItemStackHelper(this.inventory.getScreenHandler().getSlot(slot).getStack());
    }

    /**
     * @return the size of the container/inventory.
     */
    public int getTotalSlots() {
        return this.inventory.getScreenHandler().slots.size();
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
        man.clickSlot(syncId, slot1, 1, SlotActionType.PICKUP, player);
        man.clickSlot(syncId, slot2, 0, SlotActionType.PICKUP, player);
        return this;
    }

    /**
     * Does that double click thingy to turn a incomplete stack pickup into a complete stack pickup if you have more in your inventory.
     * 
     * @param slot
     * @return
     */
    public Inventory<T> grabAll(int slot) {
        man.clickSlot(syncId, slot, 0, SlotActionType.PICKUP, player);
        man.clickSlot(syncId, slot, 0, SlotActionType.PICKUP_ALL, player);
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
        if (!is1) man.clickSlot(syncId, slot1, 0, SlotActionType.PICKUP, player);
        man.clickSlot(syncId, slot2, 0, SlotActionType.PICKUP, player);
        if (!is2) man.clickSlot(syncId, slot1, 0, SlotActionType.PICKUP, player);
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
        man.clickSlot(syncId, slot, hotbarSlot, SlotActionType.SWAP, player);
        return this;
    }
    
    /**
     * @since 1.2.8
     *
     */
     public void openGui() {
        mc.execute(() -> mc.setScreen(this.inventory));
     }

    /**
     * @since 1.1.3
     * 
     * @return the id of the slot under the mouse.
     */
    public int getSlotUnderMouse() {
        MinecraftClient mc = MinecraftClient.getInstance();
        double x = mc.mouse.getX() * (double)mc.getWindow().getScaledWidth() / (double)mc.getWindow().getWidth();
        double y = mc.mouse.getY() * (double)mc.getWindow().getScaledHeight() / (double)mc.getWindow().getHeight();
        if (this.inventory != mc.currentScreen) throw new RuntimeException("Inventory screen is not open.");
        Slot s = ((IInventory)this.inventory).jsmacros_getSlotUnder(x, y);
        if (s == null) return -999;
        return this.inventory.getScreenHandler().slots.indexOf(s);
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
     * @return
     *
     * @throws InterruptedException
     * @since 1.3.1
     */
    public List<RecipeHelper> getCraftableRecipes() throws InterruptedException {
        return getCraftableRecipes(true);
    }

    /**
     * @param craftable whether only to list craftable recipes
     * @return a list of recipes that can be crafted in this inventory.
     *
     * @throws InterruptedException
     * @since 1.9.0
     */
    public List<RecipeHelper> getCraftableRecipes(boolean craftable) throws InterruptedException {
        Stream<Recipe<?>> recipes;
        RecipeBookResults res;
        IRecipeBookWidget recipeBookWidget = getRecipeBookWidget();
        if (recipeBookWidget == null) {
            return null;
        }
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            if (mc.currentScreen != inventory) {
                ((RecipeBookWidget)recipeBookWidget).initialize(0, 0, mc, true, (AbstractRecipeScreenHandler<?>) inventory.getScreenHandler());
            }
            if (!((RecipeBookWidget) recipeBookWidget).isOpen()) {
                ((RecipeBookWidget) recipeBookWidget).reset();
            }
            try {
                recipeBookWidget.jsmacros_refreshResultList();
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException("refreshing the recipe list threw an error", t);
            }
        } else {
            Semaphore lock = new Semaphore(0);
            Throwable[] t = new Throwable[1];
            mc.execute(() -> {
                try {
                    if (mc.currentScreen != inventory) {
                        ((RecipeBookWidget) recipeBookWidget).initialize(0, 0, mc, true, (AbstractRecipeScreenHandler<?>) inventory.getScreenHandler());
                    }
                    if (!((RecipeBookWidget) recipeBookWidget).isOpen()) {
                        ((RecipeBookWidget) recipeBookWidget).reset();
                    }
                    recipeBookWidget.jsmacros_refreshResultList();
                } catch (Throwable e) {
                    t[0] = e;
                } finally {
                    lock.release();
                }
            });
            lock.acquire();
            if (t[0] != null) {
                t[0].printStackTrace();
                throw new RuntimeException("refreshing the recipe list threw an error", t[0]);
            }
        }
        res = recipeBookWidget.jsmacros_getResults();
        List<RecipeResultCollection> result = ((IRecipeBookResults) res).jsmacros_getResultCollections();
        recipes = result.stream().flatMap(e -> e.getRecipes(craftable).stream());
        return recipes.map(e -> new RecipeHelper(e, syncId)).collect(Collectors.toList());
    }

    @Nullable
    private IRecipeBookWidget getRecipeBookWidget() {
        IRecipeBookWidget recipeBookWidget;
        if (inventory instanceof CraftingScreen) {
            recipeBookWidget = (IRecipeBookWidget) ((CraftingScreen)inventory).getRecipeBookWidget();
        } else if (inventory instanceof InventoryScreen) {
            recipeBookWidget = (IRecipeBookWidget) ((InventoryScreen)inventory).getRecipeBookWidget();
        } else if (inventory instanceof AbstractFurnaceScreen) {
            recipeBookWidget = (IRecipeBookWidget) ((AbstractFurnaceScreen<?>)inventory).getRecipeBookWidget();
        } else {
            return null;
        }
        return recipeBookWidget;
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
            } else if (isContainer()) {
                map.put("container", JsMacros.range(slots - 9 - 27));
            } else if (inventory instanceof BeaconScreen) {
                map.put("slot", new int[] { slots - 9 - 27 - 1 });
            } else if (inventory instanceof BlastFurnaceScreen || inventory instanceof FurnaceScreen || inventory instanceof SmokerScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("fuel", new int[] { slots - 9 - 27 - 2 });
                map.put("input", new int[] { slots - 9 - 27 - 3 });
            } else if (inventory instanceof BrewingStandScreen) {
                map.put("fuel", new int[] { slots - 9 - 27 - 1 });
                map.put("input", new int[] { slots - 9 - 27 - 2 });
                map.put("output", JsMacros.range(slots - 9 - 27 - 2));
            } else if (inventory instanceof CraftingScreen) {
                map.put("input", JsMacros.range(slots - 9 - 27 - 9, slots - 9 - 27));
                map.put("output", new int[] { slots - 9 - 27 - 10 });
            } else if (inventory instanceof EnchantmentScreen) {
                map.put("lapis", new int[] { slots - 9 - 27 - 1 });
                map.put("item", new int[] { slots - 9 - 27 - 2 });
            } else if (inventory instanceof LoomScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("pattern", new int[] { slots - 9 - 27 - 2 });
                map.put("dye", new int[] { slots - 9 - 27 - 3 });
                map.put("banner", new int[] { slots - 9 - 27 - 4 });
            } else if (inventory instanceof StonecutterScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("input", new int[] { slots - 9 - 27 - 2 });
            } else if (inventory instanceof HorseScreen) {
                AbstractHorseEntity h = (AbstractHorseEntity) ((IHorseScreen)this.inventory).jsmacros_getEntity();
                if (h.canBeSaddled()) map.put("saddle", new int[] {0});
                if (h.hasArmorSlot()) map.put("armor", new int[] {1});
                if (h instanceof AbstractDonkeyEntity && ((AbstractDonkeyEntity) h).hasChest()) {
                    map.put("container", JsMacros.range(2, slots - 9 - 27));
                }
            } else if (inventory instanceof AnvilScreen || inventory instanceof  MerchantScreen || inventory instanceof SmithingScreen || inventory instanceof GrindstoneScreen || inventory instanceof CartographyTableScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("input", JsMacros.range(slots - 9 - 27 - 1));
            }
        }

        return map;
    }

    /**
     * @return {@code true} if the inventory is a container, {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean isContainer() {
        return inventory instanceof GenericContainerScreen || inventory instanceof Generic3x3ContainerScreen || inventory instanceof HopperScreen || inventory instanceof ShulkerBoxScreen;
    }

    /**
     * @return {@code true} if the recipe book is visible, {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean isRecipeBookOpened() {
        IRecipeBookWidget recipeBookWidget = getRecipeBookWidget();
        if (recipeBookWidget == null) {
            return false;
        }
        return ((RecipeBookWidget) recipeBookWidget).isOpen();
    }

    /**
     * @param open whether to open or close the recipe book
     * @since 1.9.0
     */
    public void setRecipeBookOpen(boolean open) {
        IRecipeBookWidget recipeBookWidget = getRecipeBookWidget();
        if (recipeBookWidget != null) {
            RecipeBookWidget rbw = (RecipeBookWidget) recipeBookWidget;
            if (rbw.isOpen() != open) {
                rbw.toggleOpen();
            }
        }
    }
    
    /**
     * @since 1.2.3
     * 
     * @return
     */
    public String getContainerTitle() {
        return this.inventory.getTitle().getString();
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
