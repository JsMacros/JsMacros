package xyz.wagyourtail.jsmacros.client.api.helpers;

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
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.block.FluidStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.entity.EntityHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class RegistryHelper {

    public RegistryHelper() {
    }

    /**
     * Returns an {@link ItemStackHelper} for the given item.
     *
     * @param id in the form {@code minecraft:diamond_sword}
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getItemStack(String id) {
        return new ItemStackHelper(new ItemStack(Registry.ITEM.get(Identifier.tryParse(id))));
    }

    /**
     * @param id
     * @param nbt
     * @return
     *
     * @throws CommandSyntaxException
     * @since 1.9.0
     */
    public ItemStackHelper getItemStack(String id, String nbt) throws CommandSyntaxException {
        ItemStringReader.ItemResult itemResult = ItemStringReader.item(new CommandRegistryWrapper.Impl<>(Registry.ITEM), new StringReader(id + nbt));
        return new ItemStackHelper(new ItemStack(itemResult.item()));
    }

    /**
     * Returns a list of all items.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredItems() {
        return Registry.ITEM.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns an {@link BlockStateHelper} for the given block.
     *
     * @param id in the form {@code minecraft:dirt}
     * @return
     *
     * @since 1.9.0
     */
    public BlockStateHelper getBlockState(String id) {
        return new BlockStateHelper(Registry.BLOCK.get(Identifier.tryParse(id)).getDefaultState());
    }

    /**
     * Returns an {@link BlockStateHelper} for the given block with the specified nbt.
     *
     * @param id
     * @param nbt
     * @return
     *
     * @throws CommandSyntaxException
     * @since 1.9.0
     */
    public BlockStateHelper getBlockState(String id, String nbt) throws CommandSyntaxException {
        return new BlockStateHelper(BlockArgumentParser.block(Registry.BLOCK, id + nbt, false).blockState());
    }

    /**
     * Returns an {@link BlockHelper} for the given block.
     *
     * @param id in the form {@code minecraft:dirt}
     * @return
     *
     * @since 1.9.0
     */
    public BlockHelper getBlock(String id) {
        return new BlockHelper(Registry.BLOCK.get(Identifier.tryParse(id)));
    }

    /**
     * Returns a list of all blocksS.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredBlocks() {
        return Registry.BLOCK.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns an {@link EnchantmentHelper} for the given enchantment.
     *
     * @param id in the form {@code minecraft:knockback}
     * @return
     *
     * @since 1.9.0
     */
    public EnchantmentHelper getEnchantment(String id) {
        return getEnchantment(id, 0);
    }

    /**
     * Returns an {@link EnchantmentHelper} for the given enchantment.
     *
     * @param id    in the form {@code minecraft:knockback}
     * @param level
     * @return
     *
     * @since 1.9.0
     */
    public EnchantmentHelper getEnchantment(String id, int level) {
        return new EnchantmentHelper(Registry.ENCHANTMENT.get(Identifier.tryParse(id)), level);
    }

    /**
     * Returns a list of all enchantments.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredEnchantments() {
        return Registry.ENCHANTMENT.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns an {@link EntityHelper} for the given entity, that will be summoned inside the
     * world.
     *
     * @param type in the form {@code minecraft:bat}
     * @return
     *
     * @since 1.9.0
     */
    public EntityHelper<?> getEntity(String type) {
        return EntityHelper.create(Registry.ENTITY_TYPE.get(Identifier.tryParse(type)).create(MinecraftClient.getInstance().world));
    }

    /**
     * Returns an {@link EntityType} for the given entity.
     *
     * @param type in the form {@code minecraft:bat}
     * @return
     *
     * @since 1.9.0
     */
    public EntityType<?> getRawEntityType(String type) {
        return Registry.ENTITY_TYPE.get(Identifier.tryParse(type));
    }

    /**
     * Returns a list of all entity types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredEntityTypes() {
        return Registry.ENTITY_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns an {@link FluidStateHelper} for the given fluid.
     *
     * @param id in the form {@code minecraft:water}
     * @return
     *
     * @since 1.9.0
     */
    public FluidStateHelper getFluidState(String id) {
        return new FluidStateHelper(Registry.FLUID.get(Identifier.tryParse(id)).getDefaultState());
    }

    /**
     * Returns a list of all features.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredFeatures() {
        return Registry.FEATURE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all structure features.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredStructureFeatures() {
        return Registry.STRUCTURE_PIECE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all painting motives.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredPaintings() {
        return Registry.PAINTING_VARIANT.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all particle types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredParticleTypes() {
        return Registry.PARTICLE_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all game events.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredGameEvents() {
        return Registry.GAME_EVENT.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all status effects.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredStatusEffects() {
        return Registry.STATUS_EFFECT.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all block entity types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredBlockEntityTypes() {
        return Registry.BLOCK_ENTITY_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all screen handlers.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredScreenHandlers() {
        return Registry.SCREEN_HANDLER.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all recipe types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredRecipeTypes() {
        return Registry.RECIPE_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all villager types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredVillagerTypes() {
        return Registry.VILLAGER_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all villager professions.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredVillagerProfessions() {
        return Registry.VILLAGER_PROFESSION.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all villager points of interest.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredPointOfInterestTypes() {
        return Registry.POINT_OF_INTEREST_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all villager memory module types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredMemoryModuleTypes() {
        return Registry.MEMORY_MODULE_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all villager sensor types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredSensorTypes() {
        return Registry.SENSOR_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all villager activity types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredActivityTypes() {
        return Registry.ACTIVITY.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all statistic types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredStatTypes() {
        return Registry.STAT_TYPE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all entity attributes.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredEntityAttributes() {
        return Registry.ATTRIBUTE.getIds().stream().map(Identifier::toString).toList();
    }

    /**
     * Returns a list of all potion types.
     *
     * @return
     *
     * @since 1.9.0
     */
    public List<String> getRegisteredPotionTypes() {
        return Registry.POTION.getIds().stream().map(Identifier::toString).toList();
    }

}
