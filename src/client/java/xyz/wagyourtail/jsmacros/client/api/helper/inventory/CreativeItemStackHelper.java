package xyz.wagyourtail.jsmacros.client.api.helper.inventory;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;

import java.util.Arrays;

import static net.minecraft.text.Text.literal;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CreativeItemStackHelper extends ItemStackHelper {

    public CreativeItemStackHelper(ItemStack itemStack) {
        super(itemStack);
    }

    /**
     * @param damage the damage the item should take
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper setDamage(int damage) {
        base.setDamage(damage);
        return this;
    }

    /**
     * @param durability the new durability of this item
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper setDurability(int durability) {
        base.setDamage(base.getMaxDamage() - durability);
        return this;
    }

    /**
     * @param count the new count of the item
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper setCount(int count) {
        base.setCount(count);
        return this;
    }

    /**
     * @param name the new name of the item
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper setName(String name) {
        base.set(DataComponentTypes.CUSTOM_NAME, literal(name));
        return this;
    }

    /**
     * @param name the new name of the item
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper setName(TextHelper name) {
        base.set(DataComponentTypes.CUSTOM_NAME, name.getRaw());
        return this;
    }

    /**
     * @param id    the id of the enchantment
     * @param level the level of the enchantment
     * @return self for chaining.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<EnchantmentId>, level: int")
    public CreativeItemStackHelper addEnchantment(String id, int level) {
        return addEnchantment(mc.getNetworkHandler().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT)
                .getEntry(Identifier.of(id)).orElseThrow(), level);
    }

    /**
     * @param enchantment the enchantment to add
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper addEnchantment(EnchantmentHelper enchantment) {
        return addEnchantment(enchantment.getRaw(), enchantment.getLevel());
    }

    protected CreativeItemStackHelper addEnchantment(RegistryEntry<Enchantment> enchantment, int level) {
        base.addEnchantment(enchantment, level);
        return this;
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper clearEnchantments() {
        base.set(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        return this;
    }

    /**
     * @param enchantment the enchantment to remove
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper removeEnchantment(EnchantmentHelper enchantment) {
        return removeEnchantment(enchantment.getId());
    }

    /**
     * @param id the id of the enchantment to remove
     * @return self for chaining.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: EnchantmentId")
    public CreativeItemStackHelper removeEnchantment(String id) {
        ItemEnchantmentsComponent enchantments = base.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(enchantments);
        builder.remove((e) -> e.matchesId(Identifier.of(id)));

        return this;
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper clearLore() {
        base.set(DataComponentTypes.LORE, LoreComponent.DEFAULT);
        return this;
    }

    /**
     * @param lore the new lore
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper setLore(Object... lore) {
        clearLore();
        return addLore(lore);
    }

    /**
     * @param lore the lore to add
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper addLore(Object... lore) {
        return addLoreInternal(Arrays.stream(lore).map(e -> {
            if (e instanceof TextHelper) {
                return ((TextHelper) e).getRaw();
            } else if (e instanceof TextBuilder) {
                return ((TextBuilder) e).build().getRaw();
            } else {
                return literal(e.toString());
            }
        }).toArray(Text[]::new));
    }

    /**
     * @param texts the lore to add
     * @return self for chaining.
     * @since 1.8.4
     */
    private CreativeItemStackHelper addLoreInternal(Text... texts) {
        base.set(DataComponentTypes.LORE, new LoreComponent(Arrays.asList(texts)));
        return this;
    }

    /**
     * @param unbreakable whether the item should be unbreakable or not
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper setUnbreakable(boolean unbreakable) {
        if (unbreakable) {
            base.set(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);
        } else {
            base.remove(DataComponentTypes.UNBREAKABLE);
        }
        return this;
    }

    /**
     * @param hide whether to hide the enchantments or not
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideEnchantments(boolean hide) {
        return hideComponent(DataComponentTypes.ENCHANTMENTS, hide);
    }

    /**
     * @param hide whether to hide attributes and modifiers or not
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideModifiers(boolean hide) {
        return hideComponent(DataComponentTypes.ATTRIBUTE_MODIFIERS, hide);
    }

    /**
     * @param hide whether to hide the unbreakable flag or not
     * @return self for chaining.
     * @since 1.8.4
     */

    public CreativeItemStackHelper hideUnbreakable(boolean hide) {
        return hideComponent(DataComponentTypes.UNBREAKABLE, hide);
    }

    /**
     * @param hide whether to hide the blocks this item can destroy or not
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideCanDestroy(boolean hide) {
        return hideComponent(DataComponentTypes.CAN_BREAK, hide);
    }

    /**
     * @param hide whether to hide the blocks this item can be placed on or not
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideCanPlace(boolean hide) {
        return hideComponent(DataComponentTypes.CAN_PLACE_ON, hide);
    }

    /**
     * @param hide whether to hide the color of colored leather armor or not
     * @return self for chaining.
     * @since 1.8.4
     */
    public CreativeItemStackHelper hideDye(boolean hide) {
        return hideComponent(DataComponentTypes.DYED_COLOR, hide);
    }

    private CreativeItemStackHelper hideComponent(ComponentType<?> type, boolean hide) {
        base.set(DataComponentTypes.TOOLTIP_DISPLAY,
            base.getOrDefault(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplayComponent.DEFAULT).with(type, hide));
        return this;
    }

}
