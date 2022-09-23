package xyz.wagyourtail.jsmacros.client.api.helpers.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.EnchantmentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Arrays;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CreativeItemStackHelper extends AdvancedItemStackHelper {

    public CreativeItemStackHelper(ItemStack itemStack) {
        super(itemStack);
    }

    /**
     * @param damage the damage the item should take
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper setDamage(int damage) {
        base.setDamage(damage);
        return this;
    }

    /**
     * @param durability the new durability of this item.
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper setDurability(int durability) {
        base.setDamage(base.getMaxDamage() - durability);
        return this;
    }

    /**
     * @param count the new count of the item
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper setCount(int count) {
        base.setCount(count);
        return this;
    }

    /**
     * @param name the new name of the item
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper setName(String name) {
        base.setCustomName(Text.literal(name));
        return this;
    }

    /**
     * @param name the new name of the item
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper setName(TextHelper name) {
        base.setCustomName(name.getRaw());
        return this;
    }

    /**
     * @param id    the id of the enchantment
     * @param level the level of the enchantment
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper addEnchantment(String id, int level) {
        base.addEnchantment(Registry.ENCHANTMENT.get(Identifier.tryParse(id)), level);
        return this;
    }

    /**
     * @param enchantment the enchantment to add
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper addEnchantment(EnchantmentHelper enchantment) {
        base.addEnchantment(enchantment.getRaw(), enchantment.getLevel());
        return this;
    }

    /**
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper clearEnchantments() {
        NbtCompound compound = base.getOrCreateNbt();
        if (compound.contains("Enchantments", 9)) {
            compound.remove("Enchantments");
        }
        return this;
    }

    /**
     * @param enchantment the enchantment to remove
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper removeEnchantment(EnchantmentHelper enchantment) {
        return removeEnchantment(enchantment.getId());
    }

    /**
     * @param id the id of the enchantment to remove
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper removeEnchantment(String id) {
        NbtCompound compound = base.getOrCreateNbt();
        if (compound.contains("Enchantments", 9)) {
            NbtList nbtList = compound.getList("Enchantments", 10);
            nbtList.forEach(nbtElement -> {
                if (nbtElement.asString().contains(id)) {
                    nbtList.remove(nbtElement);
                }
            });
        }
        return this;
    }

    /**
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper clearLore() {
        NbtCompound nbtCompound = base.getOrCreateSubNbt("display");
        if (nbtCompound.contains("Lore", 9)) {
            nbtCompound.remove("Lore");
        }
        return this;
    }

    /**
     * @param lore the new lore
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper setLore(Object... lore) {
        clearLore();
        return addLore(lore);
    }

    /**
     * @param lore the lore to add
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper addLore(Object... lore) {
        if (lore instanceof TextHelper[] textHelpers) {
            return addLoreInternal(Arrays.stream(textHelpers).map(BaseHelper::getRaw).toArray(Text[]::new));
        } else if (lore instanceof TextBuilder[] textBuilders) {
            return addLoreInternal(Arrays.stream(textBuilders).map(TextBuilder::build).map(TextHelper::getRaw).toArray(Text[]::new));
        } else if (lore instanceof String[] strings) {
            return addLoreInternal(Arrays.stream(strings).map(Text::literal).toArray(Text[]::new));
        } else {
            return addLoreInternal(Arrays.stream(lore).map(Object::toString).map(Text::literal).toArray(Text[]::new));
        }
    }

    /**
     * @param texts the lore to add
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    private CreativeItemStackHelper addLoreInternal(Text... texts) {
        NbtCompound nbtCompound = base.getOrCreateSubNbt("display");
        NbtList list = nbtCompound.contains("Lore", 9) ? nbtCompound.getList("Lore", 8) : new NbtList();
        for (Text text : texts) {
            list.add(NbtString.of(Text.Serializer.toJson(text)));
        }
        nbtCompound.put("Lore", list);

        return this;
    }

    /**
     * @param unbreakable whether the item should be unbreakable
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper setUnbreakable(boolean unbreakable) {
        base.getOrCreateNbt().putBoolean("Unbreakable", unbreakable);
        return this;
    }

    /**
     * @param hide whether to hide the enchantments
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideEnchantments(boolean hide) {
        return setHideFlag(ItemStack.TooltipSection.ENCHANTMENTS, hide);
    }

    /**
     * @param hide whether to hide attributes and modifiers
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideModifiers(boolean hide) {
        return setHideFlag(ItemStack.TooltipSection.MODIFIERS, hide);
    }

    /**
     * @param hide whether to hide the unbreakable flag
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */

    public CreativeItemStackHelper hideUnbreakable(boolean hide) {
        return setHideFlag(ItemStack.TooltipSection.UNBREAKABLE, hide);
    }

    /**
     * @param hide whether to hide the blocks this item can destroy
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideCanDestroy(boolean hide) {
        return setHideFlag(ItemStack.TooltipSection.CAN_DESTROY, hide);
    }

    /**
     * @param hide whether to hide the blocks this item can be placed on
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideCanPlace(boolean hide) {
        return setHideFlag(ItemStack.TooltipSection.CAN_PLACE, hide);
    }

    /**
     * These flags are for banner patterns, potion effects, book information and other special
     * flags.
     *
     * @param hide whether to hide additional flags
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideAdditional(boolean hide) {
        return setHideFlag(ItemStack.TooltipSection.ADDITIONAL, hide);
    }

    /**
     * @param hide whether to hide the color of colored leather armor
     * @return this helper for chaining.
     *
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideDye(boolean hide) {
        return setHideFlag(ItemStack.TooltipSection.DYE, hide);
    }

    protected CreativeItemStackHelper setHideFlag(ItemStack.TooltipSection section, boolean hide) {
        NbtCompound nbtCompound = base.getOrCreateNbt();
        if (hide) {
            nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") | section.getFlag());
        } else {
            nbtCompound.putInt("HideFlags", nbtCompound.getInt("HideFlags") & ~section.getFlag());
        }
        return this;
    }

}
