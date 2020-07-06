package xyz.wagyourtail.jsmacros.runscript.classes;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;

public class Inventory {
    private HandledScreen<?> inventory;
    private ClientPlayerInteractionManager man;
    private int wID;
    private ClientPlayerEntity player;

    public Inventory() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen instanceof HandledScreen) {
            this.inventory = (HandledScreen<?>) mc.currentScreen;
        } else {
            this.inventory = new InventoryScreen(mc.player);
        }
        this.player = mc.player;
        this.man = mc.interactionManager;
        this.wID = this.inventory.getScreenHandler().syncId;
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
        player.closeHandledScreen();
        this.inventory = null;
    }

    public void close() {
        player.closeHandledScreen();
    }

    public void quick(int slot) {
        man.clickSlot(wID, slot, 0, SlotActionType.QUICK_MOVE, player);
    }

    public ItemStackHelper getHeld() {
        return new ItemStackHelper(player.inventory.getCursorStack());
    }

    public ItemStackHelper getSlot(int slot) {
        return new ItemStackHelper(this.inventory.getScreenHandler().getSlot(slot).getStack());
    }

    public int getTotalSlots() {
        return this.inventory.getScreenHandler().slots.size();
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

    public int getSlotUnderMouse() {
        MinecraftClient mc = MinecraftClient.getInstance();
        double x = mc.mouse.getX() * (double)mc.getWindow().getScaledWidth() / (double)mc.getWindow().getWidth();
        double y = mc.mouse.getY() * (double)mc.getWindow().getScaledHeight() / (double)mc.getWindow().getHeight();
        
        //using reflection is annoying...
        try {
            Method getSlot = HandledScreen.class.getDeclaredMethod("getSlotAt", double.class, double.class);
            getSlot.setAccessible(true);
            Slot s = (Slot) getSlot.invoke(this.inventory, x, y);
            if (s == null) return -999;
            return this.inventory.getScreenHandler().slots.indexOf(s);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public String getType() {
        return jsMacros.getScreenName(this.inventory);
    }

    public HashMap<String, int[]> getMap() {
        HashMap<String, int[]> map = new HashMap<>();
        int slots = getTotalSlots();
        if (this.inventory instanceof InventoryScreen) {
            map.put("hotbar", jsMacros.range(slots - 10, slots - 1)); // range(36, 45);
            map.put("offhand", new int[] { slots - 1 }); // range(45, 46);
            map.put("main", jsMacros.range(slots - 10 - 27, slots - 10)); // range(9, 36);
            map.put("boots", new int[] { slots - 10 - 27 - 1 }); // range(8, 9);
            map.put("leggings", new int[] { slots - 10 - 27 - 2 }); // range(7, 8);
            map.put("chestplate", new int[] { slots - 10 - 27 - 3 }); // range(6, 7);
            map.put("helmet", new int[] { slots - 10 - 27 - 4 }); // range(5, 6);
            map.put("crafting_in", jsMacros.range(slots - 10 - 27 - 4 - 4, slots - 10 - 27 - 4)); // range(1, 5);
            map.put("craft_out", new int[] { slots - 10 - 27 - 4 - 4 - 1 });
        } else {
            map.put("hotbar", jsMacros.range(slots - 9, slots));
            map.put("main", jsMacros.range(slots - 9 - 27, slots - 9));
            if (inventory instanceof GenericContainerScreen || inventory instanceof Generic3x3ContainerScreen || inventory instanceof HopperScreen || inventory instanceof ShulkerBoxScreen) {
                map.put("container", jsMacros.range(slots - 9 - 27));
            } else if (inventory instanceof BeaconScreen) {
                map.put("slot", new int[] { slots - 9 - 27 - 1 });
            } else if (inventory instanceof BlastFurnaceScreen || inventory instanceof FurnaceScreen || inventory instanceof SmokerScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("fuel", new int[] { slots - 9 - 27 - 2 });
                map.put("input", new int[] { slots - 9 - 27 - 3 });
            } else if (inventory instanceof BrewingStandScreen) {
                map.put("fuel", new int[] { slots - 9 - 27 - 1 });
                map.put("input", new int[] { slots - 9 - 27 - 2 });
                map.put("output", jsMacros.range(slots - 9 - 27 - 2));
            } else if (inventory instanceof CraftingScreen) {
                map.put("input", jsMacros.range(slots - 9 - 27 - 9, slots - 9 - 27));
                map.put("output", new int[] { slots - 9 - 27 - 10 });
            } else if (inventory instanceof EnchantmentScreen) {
                map.put("lapis", new int[] { slots - 9 - 27 - 1 });
                map.put("item", new int[] { slots - 9 - 27 - 2 });
            } else if (inventory instanceof GrindstoneScreen) {
                // TODO
            } else if (inventory instanceof LoomScreen) {
                // TODO
            } else if (inventory instanceof StonecutterScreen) {
                // TODO
            } else if (inventory instanceof CartographyTableScreen) {
                // TODO
            } else if (inventory instanceof HorseScreen) {
                // TODO
            } else if (inventory instanceof AnvilScreen || inventory instanceof SmithingScreen) {
                map.put("output", new int[] { slots - 9 - 27 - 1 });
                map.put("input", jsMacros.range(slots - 9 - 27 - 1));
            }
        }

        return map;
    }

    public HandledScreen<?> getRawContainer() {
        return this.inventory;
    }

}
