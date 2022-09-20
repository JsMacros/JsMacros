package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Arrays;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class CreativeItemStackHelper extends ItemStackHelper {

    public CreativeItemStackHelper(ItemStack i) {
        super(i);
    }

    /**
     * @param damage the damage the item should take.
     * @return this helper for chaining.
     *
     * @since 1.9.0
     */
    public CreativeItemStackHelper setDamage(int damage) {
        base.setDamage(damage);
        return this;
    }

    /**
     * @param count the new count of the item.
     * @return this helper for chaining.
     *
     * @since 1.9.0
     */
    public CreativeItemStackHelper setCount(int count) {
        base.setCount(count);
        return this;
    }

    /**
     * @param name the new name of the item.
     * @return this helper for chaining.
     *
     * @since 1.9.0
     */
    public CreativeItemStackHelper setName(String name) {
        base.setCustomName(Text.literal(name));
        return this;
    }

    /**
     * @param name the new name of the item.
     * @return this helper for chaining.
     *
     * @since 1.9.0
     */
    public CreativeItemStackHelper setName(TextHelper name) {
        base.setCustomName(name.getRaw());
        return this;
    }

    /**
     * @param id    the id of the enchantment.
     * @param level the level of the enchantment.
     * @return this helper for chaining.
     *
     * @since 1.9.0
     */
    public CreativeItemStackHelper addEnchantment(String id, int level) {
        base.addEnchantment(Registry.ENCHANTMENT.get(Identifier.tryParse(id)), level);
        return this;
    }

    /**
     * @param enchantment the enchantment to add.
     * @return this helper for chaining.
     *
     * @since 1.9.0
     */
    public CreativeItemStackHelper addEnchantment(EnchantmentHelper enchantment) {
        base.addEnchantment(enchantment.getRaw(), enchantment.getLevel());
        return this;
    }

    /**
     * @return this helper for chaining.
     *
     * @since 1.9.0
     */
    public CreativeItemStackHelper clearEnchantments() {
        NbtCompound compound = base.getOrCreateNbt();
        if (compound.contains("Enchantments", 9)) {
            compound.remove("Enchantments");
        }
        return this;
    }

    /**
     * @param id the id of the enchantment to remove.
     * @return this helper for chaining.
     *
     * @since 1.9.0
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
     * @param lore the lore to add.
     * @return this helper for chaining.
     *
     * @since 1.9.0
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
     * @param texts the lore to add.
     * @return this helper for chaining.
     *
     * @since 1.9.0
     */
    private CreativeItemStackHelper addLoreInternal(Text... texts) {
        NbtCompound nbtCompound = base.getOrCreateSubNbt("display");
        NbtList list = new NbtList();
        for (Text text : texts) {
            list.add(NbtString.of(Text.Serializer.toJson(text)));
        }
        nbtCompound.put("Lore", list);

        return this;
    }

    /**
     * @return this helper for chaining.
     *
     * @since 1.9.0
     */
    public CreativeItemStackHelper clearLore() {
        NbtCompound nbtCompound = base.getOrCreateSubNbt("display");
        if (nbtCompound.contains("Lore", 9)) {
            nbtCompound.remove("Lore");
        }
        return this;
    }

}
