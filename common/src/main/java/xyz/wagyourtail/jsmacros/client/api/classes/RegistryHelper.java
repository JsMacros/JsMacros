package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import xyz.wagyourtail.jsmacros.client.api.helpers.EnchantmentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.FluidStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.item.ItemHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.item.ItemStackHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class RegistryHelper {

    /**
     * @param id the item's id
     * @return an {@link ItemHelper} for the given item.
     *
     * @since 1.8.4
     */
    public ItemHelper getItem(String id) {
        return new ItemHelper(Registry.ITEM.get(Identifier.tryParse(id)));
    }

    /**
     * @param id the item's id
     * @return an {@link ItemStackHelper} for the given item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getItemStack(String id) {
        return new ItemStackHelper(new ItemStack(Registry.ITEM.get(Identifier.tryParse(id))));
    }

    /**
     * @param id  the item's id
     * @param nbt the item's nbt
     * @return an {@link ItemStackHelper} for the given item and nbt data.
     *
     * @throws CommandSyntaxException if the nbt data is invalid.
     * @since 1.8.4
     */
    public ItemStackHelper getItemStack(String id, String nbt) throws CommandSyntaxException {
        ItemStringReader.ItemResult itemResult = ItemStringReader.item(new CommandRegistryWrapper.Impl<>(Registry.ITEM), new StringReader(id + nbt));
        return new ItemStackHelper(new ItemStack(itemResult.item()));
    }

    /**
     * @return a list of all registered item ids.
     *
     * @since 1.8.4
     */
    public List<String> getItemIds() {
        return Registry.ITEM.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all registered items.
     *
     * @since 1.8.4
     */
    public List<ItemHelper> getItems() {
        return Registry.ITEM.stream().map(ItemHelper::new).toList();
    }

    /**
     * @param id the block's id
     * @return an {@link BlockHelper} for the given block.
     *
     * @since 1.8.4
     */
    public BlockHelper getBlock(String id) {
        return new BlockHelper(Registry.BLOCK.get(Identifier.tryParse(id)));
    }

    /**
     * @param id the block's id
     * @return an {@link BlockStateHelper} for the given block.
     *
     * @since 1.8.4
     */
    public BlockStateHelper getBlockState(String id) {
        return new BlockStateHelper(Registry.BLOCK.get(Identifier.tryParse(id)).getDefaultState());
    }

    /**
     * @param id  the block's id
     * @param nbt the block's nbt
     * @return an {@link BlockStateHelper} for the given block with the specified nbt.
     *
     * @throws CommandSyntaxException if the nbt data is invalid.
     * @since 1.8.4
     */
    public BlockStateHelper getBlockState(String id, String nbt) throws CommandSyntaxException {
        return new BlockStateHelper(BlockArgumentParser.block(Registry.BLOCK, id + nbt, false).blockState());
    }

    /**
     * @return a list of all registered block ids.
     *
     * @since 1.8.4
     */
    public List<String> getBlockIds() {
        return Registry.BLOCK.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all registered blocks.
     *
     * @since 1.8.4
     */
    public List<BlockHelper> getBlocks() {
        return Registry.BLOCK.stream().map(BlockHelper::new).toList();
    }

    /**
     * @param id the enchantment's id
     * @return an {@link EnchantmentHelper} for the given enchantment.
     *
     * @since 1.8.4
     */
    public EnchantmentHelper getEnchantment(String id) {
        return getEnchantment(id, 0);
    }

    /**
     * @param id    the enchantment's id
     * @param level the level of the enchantment
     * @return an {@link EnchantmentHelper} for the given enchantment with the specified level.
     *
     * @since 1.8.4
     */
    public EnchantmentHelper getEnchantment(String id, int level) {
        return new EnchantmentHelper(Registry.ENCHANTMENT.get(Identifier.tryParse(id)), level);
    }

    /**
     * @return a list of all registered enchantment ids.
     *
     * @since 1.8.4
     */
    public List<String> getEnchantmentIds() {
        return Registry.ENCHANTMENT.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all registered enchantments.
     *
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getEnchantments() {
        return Registry.ENCHANTMENT.stream().map(EnchantmentHelper::new).toList();
    }

    /**
     * @param type the id of the entity's type
     * @return an {@link EntityHelper} for the given entity.
     *
     * @since 1.8.4
     */
    public EntityHelper<?> getEntity(String type) {
        return EntityHelper.create(Registry.ENTITY_TYPE.get(Identifier.tryParse(type)).create(MinecraftClient.getInstance().world));
    }

    /**
     * @param type the id of the entity's type
     * @return an {@link EntityType} for the given entity.
     *
     * @since 1.8.4
     */
    public EntityType<?> getRawEntityType(String type) {
        return Registry.ENTITY_TYPE.get(Identifier.tryParse(type));
    }

    /**
     * @return a list of all entity type ids.
     *
     * @since 1.8.4
     */
    public List<String> getEntityTypeIds() {
        return Registry.ENTITY_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @param id the fluid's id
     * @return an {@link FluidStateHelper} for the given fluid.
     *
     * @since 1.8.4
     */
    public FluidStateHelper getFluidState(String id) {
        return new FluidStateHelper(Registry.FLUID.get(Identifier.tryParse(id)).getDefaultState());
    }

    /**
     * @return a list of all feature ids.
     *
     * @since 1.8.4
     */
    public List<String> getFeatureIds() {
        return Registry.FEATURE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all structure feature ids.
     *
     * @since 1.8.4
     */
    public List<String> getStructureFeatureIds() {
        return Registry.STRUCTURE_PIECE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all painting motive ids.
     *
     * @since 1.8.4
     */
    public List<String> getPaintingIds() {
        return Registry.PAINTING_VARIANT.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all particle type ids.
     *
     * @since 1.8.4
     */
    public List<String> getParticleTypeIds() {
        return Registry.PARTICLE_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all game event names.
     *
     * @since 1.8.4
     */
    public List<String> getGameEventNames() {
        return Registry.GAME_EVENT.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all status effect ids.
     *
     * @since 1.8.4
     */
    public List<String> getStatusEffectIds() {
        return Registry.STATUS_EFFECT.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all block entity type ids.
     *
     * @since 1.8.4
     */
    public List<String> getBlockEntityTypeIds() {
        return Registry.BLOCK_ENTITY_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all screen handler ids.
     *
     * @since 1.8.4
     */
    public List<String> getScreenHandlerIds() {
        return Registry.SCREEN_HANDLER.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all recipe type ids.
     *
     * @since 1.8.4
     */
    public List<String> getRecipeTypeIds() {
        return Registry.RECIPE_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all villager type ids.
     *
     * @since 1.8.4
     */
    public List<String> getVillagerTypeIds() {
        return Registry.VILLAGER_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all villager profession ids.
     *
     * @since 1.8.4
     */
    public List<String> getVillagerProfessionIds() {
        return Registry.VILLAGER_PROFESSION.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all point of interest type ids.
     *
     * @since 1.8.4
     */
    public List<String> getPointOfInterestTypeIds() {
        return Registry.POINT_OF_INTEREST_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all memory module type ids.
     *
     * @since 1.8.4
     */
    public List<String> getMemoryModuleTypeIds() {
        return Registry.MEMORY_MODULE_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all villager sensor type ids.
     *
     * @since 1.8.4
     */
    public List<String> getSensorTypeIds() {
        return Registry.SENSOR_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all villager activity type ids.
     *
     * @since 1.8.4
     */
    public List<String> getActivityTypeIds() {
        return Registry.ACTIVITY.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all stat type ids.
     *
     * @since 1.8.4
     */
    public List<String> getStatTypeIds() {
        return Registry.STAT_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all entity attribute ids.
     *
     * @since 1.8.4
     */
    public List<String> getEntityAttributeIds() {
        return Registry.ATTRIBUTE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * @return a list of all potion type ids.
     *
     * @since 1.8.4
     */
    public List<String> getPotionTypeIds() {
        return Registry.POTION.getIds().stream().map(Identifier::toString).toList();
    }

}
