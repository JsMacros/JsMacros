package xyz.wagyourtail.jsmacros.client.api.helpers.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AdvancedItemStackHelper extends ItemStackHelper {

    public AdvancedItemStackHelper(ItemStack itemStack) {
        super(itemStack);
    }

    /**
     * This flag only affects players in adventure mode and makes sure only specified blocks can be
     * destroyed by this item.
     *
     * @return {@code true} if the can destroy flag is set, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasDestroyRestrictions() {
        return base.getOrCreateNbt().contains("CanDestroy", 9);
    }

    /**
     * This flag only affects players in adventure mode and makes sure this item can only be placed
     * on specified blocks.
     *
     * @return {@code true} if the can place on flag is set, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasPlaceRestrictions() {
        return base.getOrCreateNbt().contains("CanPlaceOn", 9);
    }

    /**
     * @return a list of all filters set for the can destroy flag.
     *
     * @since 1.8.4
     */
    public List<String> getDestroyRestrictions() {
        if (hasDestroyRestrictions()) {
            return base.getOrCreateNbt().getList("CanDestroy", 8).stream().map(NbtElement::asString).toList();
        }
        return List.of();
    }

    /**
     * @return a list of all filters set for the can place on flag.
     *
     * @since 1.8.4
     */
    public List<String> getPlaceRestrictions() {
        if (hasPlaceRestrictions()) {
            return base.getOrCreateNbt().getList("CanPlaceOn", 8).stream().map(NbtElement::asString).toList();
        }
        return List.of();
    }

    /**
     * @return {@code true} if enchantments are hidden, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean areEnchantmentsHidden() {
        return isFlagSet(ItemStack.TooltipSection.ENCHANTMENTS);
    }

    /**
     * @return {@code true} if modifiers are hidden, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean areModifiersHidden() {
        return isFlagSet(ItemStack.TooltipSection.MODIFIERS);
    }

    /**
     * @return {@code true} if the unbreakable flag is hidden, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isUnbreakableHidden() {
        return isFlagSet(ItemStack.TooltipSection.UNBREAKABLE);
    }

    /**
     * @return {@code true} if the can destroy flag is hidden, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isCanDestroyHidden() {
        return isFlagSet(ItemStack.TooltipSection.CAN_DESTROY);
    }

    /**
     * @return {@code true} if the can place flag is hidden, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isCanPlaceHidden() {
        return isFlagSet(ItemStack.TooltipSection.CAN_PLACE);
    }

    /**
     * @return {@code true} if additional attributes are hidden, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean areAdditionalsHidden() {
        return isFlagSet(ItemStack.TooltipSection.ADDITIONAL);
    }

    /**
     * @return {@code true} if dye of colored leather armor is hidden, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isDyeHidden() {
        return isFlagSet(ItemStack.TooltipSection.DYE);
    }

    protected boolean isFlagSet(ItemStack.TooltipSection section) {
        NbtCompound nbtCompound = base.getOrCreateNbt();
        return (nbtCompound.getInt("HideFlags") & section.getFlag()) != 0;
    }

}
