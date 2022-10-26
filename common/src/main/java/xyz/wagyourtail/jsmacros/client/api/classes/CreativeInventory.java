package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.MathHelper;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinCreativeInventoryScreen;

import java.util.List;
import java.util.stream.Collectors;

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
     * The total scroll value is always clamp between 0 and 1.
     *
     * @param amount the amount to scroll by, between -1 and 1
     * @since 1.8.4
     */
    public void scroll(double amount) {
        scrollTo((float) (((MixinCreativeInventoryScreen) inventory).getScrollPosition() + amount));
    }

    /**
     * The total scroll value is always clamp between 0 and 1.
     *
     * @param position the position to scroll to, between 0 and 1
     * @since 1.8.4
     */
    public void scrollTo(double position) {
        if (((MixinCreativeInventoryScreen) inventory).invokeHasScrollbar()) {
            position = MathHelper.clamp(position, 0, 1);
            handler.scrollItems((float) position);
        }
    }

    /**
     * @return a list of all shown items.
     *
     * @since 1.8.4
     */
    public List<ItemStackHelper> getShownItems() {
        return handler.itemList.stream().map(ItemStackHelper::new).collect(Collectors.toList());
    }

    /**
     * @param search the string to search for
     * @since 1.8.4
     */
    public void search(String search) {
        if (((MixinCreativeInventoryScreen) inventory).getSelectedTab() != ItemGroup.SEARCH.getIndex()) {
            return;
        }
        ((MixinCreativeInventoryScreen) inventory).getSearchBox().setText(search);
        ((MixinCreativeInventoryScreen) inventory).invokeSearch();
    }

    /**
     * Select the search tab.
     *
     * @since 1.8.4
     */
    public void selectSearch() {
        selectTab(ItemGroup.SEARCH.getIndex());
    }

    /**
     * Select the inventory tab.
     *
     * @since 1.8.4
     */
    public void selectInventory() {
        selectTab(ItemGroup.INVENTORY.getIndex());
    }

    /**
     * Select the tab where the hotbars are stored.
     *
     * @since 1.8.4
     */
    public void selectHotbar() {
        selectTab(ItemGroup.HOTBAR.getIndex());
    }

    /**
     * @param tab the index of the tab to select
     * @since 1.8.4
     */
    public void selectTab(int tab) {
        selectTab(ItemGroup.GROUPS[tab]);
    }

    private void selectTab(ItemGroup group) {
        mc.execute(() -> ((MixinCreativeInventoryScreen) inventory).invokeSetSelectedTab(ItemGroup.GROUPS[group.getIndex()]));
    }

    /**
     * Destroys the currently held item.
     *
     * @since 1.8.4
     */
    public void destroyHeldItem() {
        handler.setCursorStack(ItemStack.EMPTY);
    }

    /**
     * Destroys all items in the player's inventory.
     *
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
     * @return a list of all items in the saved hotbar.
     *
     * @since 1.8.4
     */
    public List<ItemStackHelper> getSavedHotbar(int index) {
        HotbarStorage hotbarStorage = MinecraftClient.getInstance().getCreativeHotbarStorage();
        return hotbarStorage.getSavedHotbar(index).stream().map(ItemStackHelper::new).collect(Collectors.toList());
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
        return getSlot(40);
    }

    /**
     * @return the equipped helmet item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getHelmet() {
        return getSlot(39);
    }

    /**
     * @return the equipped chestplate item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getChestplate() {
        return getSlot(38);
    }

    /**
     * @return the equipped leggings item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getLeggings() {
        return getSlot(37);
    }

    /**
     * @return the equipped boots item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getBoots() {
        return getSlot(36);
    }

    @Override
    public String toString() {
        return String.format("CreativeInventory:{}");
    }

}