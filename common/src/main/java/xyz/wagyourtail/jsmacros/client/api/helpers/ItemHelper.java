package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.Wearable;
import net.minecraft.util.registry.Registry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockStateHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ItemHelper extends BaseHelper<Item> {

    public ItemHelper(Item base) {
        super(base);
    }

    /**
     * @return the name of this item's group or {@code "unknown"} if this item is not in a group.
     *
     * @since 1.8.4
     */
    public String getGroup() {
        return base.getGroup() == null ? "unknown" : base.getGroup().getName();
    }

    /**
     * @return the item stack representing the group of this item or {@code null} if this item is
     *         not in a group.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getGroupIcon() {
        return base.getGroup() == null ? null : new ItemStackHelper(base.getGroup().getIcon());
    }

    /**
     * @param stack the possible repair material.
     * @return {@code true} if the given item stack can be used to repair item stacks of this item,
     *         {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean canBeRepairedWith(ItemStackHelper stack) {
        return base.canRepair(null, stack.getRaw());
    }

    /**
     * @param block the block to check.
     * @return {@code true} if the given block can be mined and drops when broken with this item,
     *         {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSuitableFor(BlockHelper block) {
        return base.isSuitableFor(block.getDefaultState().getRaw());
    }

    /**
     * @param block the block to check.
     * @return {@code true} if the given block can be mined and drops when broken with this item,
     *         {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSuitableFor(BlockStateHelper block) {
        return base.isSuitableFor(block.getRaw());
    }

    /**
     * @return {@code true} if the item has a block representation, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isBlock() {
        return base instanceof BlockItem;
    }

    /**
     * @return the block representation of this item or {@code null} if this item has no
     *         corresponding block.
     *
     * @since 1.8.4
     */
    public BlockHelper getBlock() {
        if (isBlock()) {
            return new BlockHelper(((BlockItem) base).getBlock());
        }
        return null;
    }

    /**
     * @param state the block state to check.
     * @return the mining speed of this item against the given block state, returns {@code 1} by
     *         default.
     *
     * @since 1.8.4
     */
    public float getMiningSpeedMultiplier(BlockStateHelper state) {
        //at least in vanilla the item stack is never used
        return base.getMiningSpeedMultiplier(null, state.getRaw());
    }

    /**
     * @return {@code true} if the item has durability, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isDamageable() {
        return base.isDamageable();
    }

    /**
     * @return {@code true} if when crafter the item stack has a remainder, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasRecipeRemainder() {
        return base.hasRecipeRemainder();
    }

    /**
     * @return the recipe remainder if it exists and {@code null} otherwise.
     *
     * @since 1.8.4
     */
    public ItemHelper getRecipeRemainder() {
        return base.getRecipeRemainder() == null ? null : new ItemHelper(base.getRecipeRemainder());
    }

    /**
     * With increased enchantability the change to get more and better enchantments increases.
     *
     * @return the enchantability of this item, returns {@code 0} by default.
     *
     * @since 1.8.4
     */
    public int getEnchantability() {
        return base.getEnchantability();
    }

    /**
     * @return the name of this item, translated to the current language.
     *
     * @since 1.8.4
     */
    public String getName() {
        return base.getName().getString();
    }

    /**
     * @return the identifier of this item.
     *
     * @since 1.8.4
     */
    public String getId() {
        return Registry.ITEM.getId(base).toString();
    }

    /**
     * @return the maximum amount of items in a stack of this item.
     *
     * @since 1.8.4
     */
    public int getMaxCount() {
        return base.getMaxCount();
    }

    /**
     * The damage an item has taken is the opposite of the durability still left.
     *
     * @return the maximum amount of damage this item can take.
     *
     * @since 1.8.4
     */
    public int getMaxDurability() {
        return base.getMaxDamage();
    }

    /**
     * @return {@code true} if this item is fireproof, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isFireproof() {
        return base.isFireproof();
    }

    /**
     * @return {@code true} if this item is a tool, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isTool() {
        return base instanceof ToolItem;
    }

    /**
     * @return {@code true} if this item can be worn in the armor slot, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isWearable() {
        return base instanceof Wearable;
    }

    /**
     * @return {@code true} if this item is food, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isFood() {
        return base.isFood();
    }

    /**
     * @return the food component of this item or {@code null} if this item is not food.
     *
     * @since 1.8.4
     */
    public FoodComponentHelper getFoodHelper() {
        if (isFood()) {
            return new FoodComponentHelper(base.getFoodComponent());
        }
        return null;
    }

    /**
     * @return {@code true} if this item can be nested, i.e. put into a bundle or shulker box,
     *         {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean canBeNested() {
        return base.canBeNested();
    }

    /**
     * @return the default item stack of this item with a stack size of {@code 1}.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getDefaultStack() {
        return new ItemStackHelper(base.getDefaultStack());
    }

    /**
     * @param nbt the nbt data of the item stack.
     * @return the item stack of this item with a stack size of {@code 1} and the given nbt.
     *
     * @throws CommandSyntaxException if the nbt data is invalid.
     * @since 1.8.4
     */
    public ItemStackHelper getStackWithNbt(String nbt) throws CommandSyntaxException {
        ItemStringReader.ItemResult itemResult = ItemStringReader.item(new CommandRegistryWrapper.Impl<>(Registry.ITEM), new StringReader(getId() + nbt));
        return new ItemStackHelper(new ItemStack(itemResult.item()));
    }

}