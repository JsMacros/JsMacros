package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;

import xyz.wagyourtail.jsmacros.client.access.ICreativeInventoryScreen;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CreativeInventory extends Inventory<CreativeInventoryScreen> {

    private final CreativeInventoryScreen.CreativeScreenHandler handler;

    protected CreativeInventory(CreativeInventoryScreen inventory) {
        super(inventory);
        this.handler = inventory.getScreenHandler();
    }

    /**
     * @param amount the amount to scroll by.
     * @since 1.8.4
     */
    public void scroll(float amount) {
        handler.scrollItems(getInterface().jsmacros_getScrollPosition() + amount);
    }

    /**
     * @param position the position to scroll to.
     * @since 1.8.4
     */
    public void scrollTo(float position) {
        handler.scrollItems(position);
    }

    /**
     * @since 1.8.4
     */
    public void selectInventory() {
        getInterface().jsmacros_setSelectedTab(ItemGroup.INVENTORY.getIndex());
    }

    /**
     * @since 1.8.4
     */
    public void selectSearch() {
        getInterface().jsmacros_setSelectedTab(ItemGroup.SEARCH.getIndex());
    }

    /**
     * @param search the string to search for
     * @since 1.8.4
     */
    public void search(String search) {
        getInterface().jsmacros_getSearchField().setText(search);
        getInterface().jsmacros_search();
    }

    /**
     * @return a list of all shown items.
     *
     * @since 1.8.4
     */
    public List<ItemStackHelper> getShownItems() {
        return handler.itemList.stream().map(ItemStackHelper::new).toList();
    }

    /**
     * @since 1.8.4
     */
    public void selectHotbar() {
        getInterface().jsmacros_setSelectedTab(ItemGroup.HOTBAR.getIndex());
    }

    /**
     * @param tab the index of the tab to select.
     * @since 1.8.4
     */
    public void selectTab(int tab) {
        getInterface().jsmacros_setSelectedTab(tab);
    }

    /**
     * @since 1.8.4
     */
    public void destroyHeldItem() {
        handler.setCursorStack(ItemStack.EMPTY);
    }

    /**
     * @since 1.8.4
     */
    public void destroyAllItems() {
        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
        for (int i = 0; i < getTotalSlots(); i++) {
            interactionManager.clickCreativeStack(ItemStack.EMPTY, i);
        }
    }

    /**
     * @param index the index to save the hotbar to, from 0 to 8
     * @since 1.8.4
     */
    public void saveHotbar(int index) {
        CreativeInventoryScreen.onHotbarKeyPress(MinecraftClient.getInstance(), index, false, true);
    }

    /**
     * @param index the index to save the hotbar to, from 0 to 8
     * @since 1.8.4
     */
    public void restoreHotbar(int index) {
        CreativeInventoryScreen.onHotbarKeyPress(MinecraftClient.getInstance(), index, true, false);
    }

    /**
     * @param index the index to save the hotbar to, from 0 to 8
     * @return a list of all saved items in the saved hotbar
     *
     * @since 1.8.4
     */
    public List<ItemStackHelper> getSavedHotbar(int index) {
        HotbarStorage hotbarStorage = MinecraftClient.getInstance().getCreativeHotbarStorage();
        return hotbarStorage.getSavedHotbar(index).stream().map(ItemStackHelper::new).toList();
    }

    /**
     * @param slot the slot to check
     * @return {@code true} if the slot is in the hotbar or the offhand slot, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean isInHotbar(int slot) {
        return PlayerScreenHandler.isInHotbar(slot);
    }

    /**
     * @return the item in the offhand.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getOffhand() {
        return getSlot(45);
    }

    /**
     * @return the equipped helmet item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getHelmet() {
        return getSlot(5);
    }

    /**
     * @return the equipped chestplate item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getChestplate() {
        return getSlot(6);
    }

    /**
     * @return the equipped leggings item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getLeggings() {
        return getSlot(7);
    }

    /**
     * @return the equipped boots item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getBoots() {
        return getSlot(8);
    }

    private ICreativeInventoryScreen getInterface() {
        return (ICreativeInventoryScreen) inventory;
    }

}
