package xyz.wagyourtail.jsmacros.client.api.helpers.inventory;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ItemHelper extends BaseHelper<Item> {

    public ItemHelper(Item base) {
        super(base);
    }

    private Stream<ItemGroup> getGroups() {
        return ItemGroups.getGroups().parallelStream().filter(group -> !group.isSpecial() && group.getDisplayStacks().parallelStream().anyMatch(e -> e.isOf(base)));
    }

    /**
     * @return the name of this item's group or {@code "UNKNOWN"} if this item is not in a group.
     * @since 1.8.4
     */
    public List<TextHelper> getCreativeTab() {
        return getGroups().map(ItemGroup::getDisplayName).map(TextHelper::wrap).collect(Collectors.toList());
    }

    /**
     * @return the item stack representing the group of this item or {@code null} if this item is
     * not in a group.
     * @since 1.8.4
     */
    @Nullable
    public List<ItemStackHelper> getGroupIcon() {
        return getGroups() == null ? null : getGroups().map(ItemGroup::getIcon).map(ItemStackHelper::new).collect(Collectors.toList());
    }

    /**
     * @param stack the possible repair material
     * @return {@code true} if the given item stack can be used to repair item stacks of this item,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean canBeRepairedWith(ItemStackHelper stack) {
        // At least in vanilla the first item stack is never used
        return base.canRepair(null, stack.getRaw());
    }

    /**
     * @param block the block to check
     * @return {@code true} if the given block can be mined and drops when broken with this item,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSuitableFor(BlockHelper block) {
        return base.isSuitableFor(block.getDefaultState().getRaw());
    }

    /**
     * @param block the block to check
     * @return {@code true} if the given block can be mined and drops when broken with this item,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSuitableFor(BlockStateHelper block) {
        return base.isSuitableFor(block.getRaw());
    }

    /**
     * @return {@code true} if the item has a block representation, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isBlockItem() {
        return base instanceof BlockItem;
    }

    /**
     * @return the block representation of this item or {@code null} if this item has no
     * corresponding block.
     * @since 1.8.4
     */
    @Nullable
    public BlockHelper getBlock() {
        if (isBlockItem()) {
            return new BlockHelper(((BlockItem) base).getBlock());
        }
        return null;
    }

    /**
     * @param state the block state to check
     * @return the mining speed of this item against the given block state, returns {@code 1} by
     * default.
     * @since 1.8.4
     */
    public float getMiningSpeedMultiplier(BlockStateHelper state) {
        // At least in vanilla the item stack is never used
        return base.getMiningSpeedMultiplier(null, state.getRaw());
    }

    /**
     * @return {@code true} if the item has durability, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isDamageable() {
        return base.isDamageable();
    }

    /**
     * @return {@code true} if when crafter the item stack has a remainder, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean hasRecipeRemainder() {
        return base.hasRecipeRemainder();
    }

    /**
     * @return the recipe remainder if it exists and {@code null} otherwise.
     * @since 1.8.4
     */
    @Nullable
    public ItemHelper getRecipeRemainder() {
        return base.getRecipeRemainder() == null ? null : new ItemHelper(base.getRecipeRemainder());
    }

    /**
     * With increased enchantability the change to get more and better enchantments increases.
     *
     * @return the enchantability of this item, returns {@code 0} by default.
     * @since 1.8.4
     */
    public int getEnchantability() {
        return base.getEnchantability();
    }

    /**
     * @return the name of this item, translated to the current language.
     * @since 1.8.4
     */
    public String getName() {
        return base.getName().getString();
    }

    /**
     * @return the identifier of this item.
     * @since 1.8.4
     */
    @DocletReplaceReturn("ItemId")
    public String getId() {
        return Registries.ITEM.getId(base).toString();
    }

    /**
     * @return the maximum amount of items in a stack of this item.
     * @since 1.8.4
     */
    public int getMaxCount() {
        return base.getMaxCount();
    }

    /**
     * The damage an item has taken is the opposite of the durability still left.
     *
     * @return the maximum amount of damage this item can take.
     * @since 1.8.4
     */
    public int getMaxDurability() {
        return base.getMaxDamage();
    }

    /**
     * @return {@code true} if this item is fireproof, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isFireproof() {
        return base.isFireproof();
    }

    /**
     * @return {@code true} if this item is a tool, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isTool() {
        return base instanceof ToolItem;
    }

    /**
     * @return {@code true} if this item can be worn in the armor slot, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isWearable() {
        return base instanceof Equipment && ((Equipment) base).getSlotType().isArmorSlot();
    }

    /**
     * @return {@code true} if this item is food, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isFood() {
        return base.isFood();
    }

    /**
     * @return the food component of this item or {@code null} if this item is not food.
     * @since 1.8.4
     */
    @Nullable
    public FoodComponentHelper getFood() {
        if (isFood()) {
            return new FoodComponentHelper(base.getFoodComponent());
        }
        return null;
    }

    /**
     * @return {@code true} if this item can be nested, i.e. put into a bundle or shulker box,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean canBeNested() {
        return base.canBeNested();
    }

    /**
     * @return the default item stack of this item with a stack size of {@code 1}.
     * @since 1.8.4
     */
    public ItemStackHelper getDefaultStack() {
        return new ItemStackHelper(base.getDefaultStack());
    }

    /**
     * @param nbt the nbt data of the item stack
     * @return the item stack of this item with a stack size of {@code 1} and the given nbt.
     * @throws CommandSyntaxException if the nbt data is invalid.
     * @since 1.8.4
     */
    public ItemStackHelper getStackWithNbt(String nbt) throws CommandSyntaxException {
        ItemStringReader.ItemResult itemResult = ItemStringReader.item(Registries.ITEM.getReadOnlyWrapper(), new StringReader(getId() + nbt));
        return new ItemStackHelper(new ItemStack(itemResult.item()));
    }

    @Override
    public String toString() {
        return String.format("ItemHelper:{\"id\": \"%s\"}", getId());
    }

}
