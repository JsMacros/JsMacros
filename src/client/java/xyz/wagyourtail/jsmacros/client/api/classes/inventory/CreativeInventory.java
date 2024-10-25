package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.MathHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinCreativeInventoryScreen;

import java.util.List;
import java.util.Objects;
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
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory scroll(double amount) {
        scrollTo((float) (((MixinCreativeInventoryScreen) inventory).getScrollPosition() + amount));
        return this;
    }

    /**
     * The total scroll value is always clamp between 0 and 1.
     *
     * @param position the position to scroll to, between 0 and 1
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory scrollTo(double position) {
        if (((MixinCreativeInventoryScreen) inventory).invokeHasScrollbar()) {
            position = MathHelper.clamp(position, 0, 1);
            handler.scrollItems((float) position);
        }
        return this;
    }

    /**
     * @return a list of all shown items.
     * @since 1.8.4
     */
    public List<ItemStackHelper> getShownItems() {
        return handler.itemList.stream().map(ItemStackHelper::new).collect(Collectors.toList());
    }

    /**
     * @param search the string to search for
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory search(String search) {
        if (((MixinCreativeInventoryScreen) inventory).getSelectedTab() == ItemGroups.getSearchGroup()) {
            ((MixinCreativeInventoryScreen) inventory).getSearchBox().setText(search);
            ((MixinCreativeInventoryScreen) inventory).invokeSearch();
        }
        return this;
    }

    /**
     * Select the search tab.
     *
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory selectSearch() {
        selectTab(ItemGroups.getSearchGroup());
        return this;
    }

    /**
     * Select the inventory tab.
     *
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory selectInventory() {
        ItemGroups.getGroups().stream().filter(e -> e.getType().equals(ItemGroup.Type.INVENTORY)).findFirst().ifPresent(this::selectTab);
        return this;
    }

    /**
     * Select the tab where the hotbars are stored.
     *
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory selectHotbar() {
        ItemGroups.getGroups().stream().filter(e -> e.getType().equals(ItemGroup.Type.HOTBAR)).findFirst().ifPresent(this::selectTab);
        return this;
    }

    /**
     * @param tabName the name of the tab to select
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory selectTab(String tabName) {
        //TODO detect if translatable and use translate id instead
        selectTab(ItemGroups.getGroups().stream().filter(e -> e.getDisplayName().getString().equals(tabName)).findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid tab name")));
        return this;
    }

    public List<String> getTabNames() {
        return ItemGroups.getGroups().stream().map(e -> e.getDisplayName().getString()).collect(Collectors.toList());
    }

    public List<TextHelper> getTabTexts() {
        return ItemGroups.getGroups().stream().map(e -> TextHelper.wrap(e.getDisplayName())).collect(Collectors.toList());
    }

    private CreativeInventory selectTab(ItemGroup group) {
        mc.execute(() -> ((MixinCreativeInventoryScreen) inventory).invokeSetSelectedTab(group));
        return this;
    }

    /**
     * Destroys the currently held item.
     *
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory destroyHeldItem() {
        handler.setCursorStack(ItemStack.EMPTY);
        return this;
    }

    /**
     * Destroys all items in the player's inventory.
     *
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory destroyAllItems() {
        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;
        for (int i = 0; i < getTotalSlots(); i++) {
            interactionManager.clickCreativeStack(ItemStack.EMPTY, i);
        }
        return this;
    }

    /**
     * @param stack the item stack to drag
     * @return self for chaining.
     * @see RegistryHelper#getItemStack(String, String)
     * @since 1.8.4
     */
    public CreativeInventory setCursorStack(ItemStackHelper stack) {
        handler.setCursorStack(stack.getRaw());
        return this;
    }

    /**
     * @param slot  the slot to insert the item into
     * @param stack the item stack to insert
     * @return self for chaining.
     * @see RegistryHelper#getItemStack(String, String)
     * @since 1.8.4
     */
    public CreativeInventory setStack(int slot, ItemStackHelper stack) {
        MinecraftClient.getInstance().interactionManager.clickCreativeStack(stack.getRaw(), slot);
        return this;
    }

    /**
     * @param index the index to save the hotbar to, from 0 to 8
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory saveHotbar(int index) {
        CreativeInventoryScreen.onHotbarKeyPress(MinecraftClient.getInstance(), index, false, true);
        return this;
    }

    /**
     * @param index the index to save the hotbar to, from 0 to 8
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeInventory restoreHotbar(int index) {
        CreativeInventoryScreen.onHotbarKeyPress(MinecraftClient.getInstance(), index, true, false);
        return this;
    }

    /**
     * @param index the index to save the hotbar to, from 0 to 8
     * @return a list of all items in the saved hotbar.
     * @since 1.8.4
     */
    public List<ItemStackHelper> getSavedHotbar(int index) {
        HotbarStorage hotbarStorage = mc.getCreativeHotbarStorage();
        return hotbarStorage.getSavedHotbar(index).deserialize(Objects.requireNonNull(mc.getNetworkHandler()).getRegistryManager()).stream().map(ItemStackHelper::new).collect(Collectors.toList());
    }

    /**
     * @param slot the slot to check
     * @return {@code true} if the slot is in the hotbar or the offhand slot, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isInHotbar(int slot) {
        return PlayerScreenHandler.isInHotbar(slot);
    }

    /**
     * @return the item in the offhand.
     * @since 1.8.4
     */
    public ItemStackHelper getOffhand() {
        return getSlot(40);
    }

    /**
     * @return the equipped helmet item.
     * @since 1.8.4
     */
    public ItemStackHelper getHelmet() {
        return getSlot(39);
    }

    /**
     * @return the equipped chestplate item.
     * @since 1.8.4
     */
    public ItemStackHelper getChestplate() {
        return getSlot(38);
    }

    /**
     * @return the equipped leggings item.
     * @since 1.8.4
     */
    public ItemStackHelper getLeggings() {
        return getSlot(37);
    }

    /**
     * @return the equipped boots item.
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
