package xyz.wagyourtail.jsmacros.client.api.classes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.doclet.DocletReplaceTypeParams;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatusEffectHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.CreativeItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.EnchantmentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.FluidStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class RegistryHelper {
    MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * implemented in mixins to make this equal to any owner. used by NBT_PASS_OPS
     */
    public static final RegistryEntryOwner<?> ALL_EQUALITY_OWNER = new RegistryEntryOwner<>() {
        @Override
        public boolean ownerEquals(RegistryEntryOwner<Object> other) {
            return true;
        }
    };
    private static final RegistryOps.RegistryInfoGetter REGISTRY_INFO_GETTER_UNLIMITED = new RegistryOps.RegistryInfoGetter() {
        private final RegistryOps.RegistryInfo<?> INFO = new RegistryOps.RegistryInfo<>(ALL_EQUALITY_OWNER, null, null);

        @Override
        public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
            //noinspection unchecked
            return Optional.of((RegistryOps.RegistryInfo<T>) INFO);
        }

    };
    /**
     * for encoding unlimited data into NbtElement for getNBT methods
     */
    public static final RegistryOps<NbtElement> NBT_OPS_UNLIMITED = RegistryOps.of(NbtOps.INSTANCE, REGISTRY_INFO_GETTER_UNLIMITED);
    /**
     * for encoding unlimited data into NbtElement for getNBT methods<br>
     * for methods accepts WrapperLookup and only uses WrapperLookup#getOps()
     */
    public static final RegistryWrapper.WrapperLookup WRAPPER_LOOKUP_UNLIMITED = new RegistryWrapper.WrapperLookup() {
        @Override
        public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
            throw new RuntimeException("Unsupported operation.");
        }

        @Override
        public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
            throw new RuntimeException("Unsupported operation.");
        }

        @Override
        public <V> RegistryOps<V> getOps(DynamicOps<V> delegate) {
            return RegistryOps.of(delegate, REGISTRY_INFO_GETTER_UNLIMITED);
        }

    };

    /**
     * @param id the item's id
     * @return an {@link ItemHelper} for the given item.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<ItemId>")
    public ItemHelper getItem(String id) {
        return new ItemHelper(Registries.ITEM.get(parseIdentifier(id)));
    }

    /**
     * @param id the item's id
     * @return an {@link ItemStackHelper} for the given item.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<ItemId>")
    public ItemStackHelper getItemStack(String id) {
        return new CreativeItemStackHelper(new ItemStack(Registries.ITEM.get(parseIdentifier(id))));
    }

    /**
     * @param id  the item's id
     * @param nbt the item's nbt
     * @return an {@link ItemStackHelper} for the given item and nbt data.
     * @throws CommandSyntaxException if the nbt data is invalid.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<ItemId>, nbt: string")
    public ItemStackHelper getItemStack(String id, String nbt) throws CommandSyntaxException {
        ItemStringReader reader = new ItemStringReader(Objects.requireNonNull(mc.getNetworkHandler()).getRegistryManager());
        ItemStringReader.ItemResult itemResult = reader.consume(new StringReader(parseNameSpace(id) + nbt));
        ItemStack stack = new ItemStack(itemResult.item());
        stack.applyUnvalidatedChanges(itemResult.components());
        return new CreativeItemStackHelper(stack);
    }

    /**
     * @return a list of all registered item ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<ItemId>")
    public List<String> getItemIds() {
        return Registries.ITEM.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all registered items.
     * @since 1.8.4
     */
    public List<ItemHelper> getItems() {
        return Registries.ITEM.stream().map(ItemHelper::new).collect(Collectors.toList());
    }

    /**
     * @param id the block's id
     * @return an {@link BlockHelper} for the given block.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<BlockId>")
    public BlockHelper getBlock(String id) {
        return new BlockHelper(Registries.BLOCK.get(parseIdentifier(id)));
    }

    /**
     * @param id the block's id
     * @return an {@link BlockStateHelper} for the given block.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<BlockId>")
    public BlockStateHelper getBlockState(String id) {
        return new BlockStateHelper(Registries.BLOCK.get(parseIdentifier(id)).getDefaultState());
    }

    /**
     * @param id the status effect's id
     * @return an {@link StatusEffectHelper} for the given status effect with 0 ticks duration.
     */
    @DocletReplaceParams("id: CanOmitNamespace<StatusEffectId>")
    public StatusEffectHelper getStatusEffect(String id) {
        return new StatusEffectHelper(Registries.STATUS_EFFECT.get(parseIdentifier(id)));
    }

    /**
     * @return a list of all registered status effects as {@link StatusEffectHelper}s with 0 ticks duration.
     * @since 1.8.4
     */
    public List<StatusEffectHelper> getStatusEffects() {
        return Registries.STATUS_EFFECT.stream().map(StatusEffectHelper::new).collect(Collectors.toList());
    }

    /**
     * @param id  the block's id
     * @param nbt the block's nbt
     * @return an {@link BlockStateHelper} for the given block with the specified nbt.
     * @throws CommandSyntaxException if the nbt data is invalid.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<BlockId>, nbt: string")
    public BlockStateHelper getBlockState(String id, String nbt) throws CommandSyntaxException {
        return new BlockStateHelper(BlockArgumentParser.block(Registries.BLOCK.getReadOnlyWrapper(), parseNameSpace(id) + nbt, false).blockState());
    }

    /**
     * @return a list of all registered block ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<BlockId>")
    public List<String> getBlockIds() {
        return Registries.BLOCK.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all registered blocks.
     * @since 1.8.4
     */
    public List<BlockHelper> getBlocks() {
        return Registries.BLOCK.stream().map(BlockHelper::new).collect(Collectors.toList());
    }

    /**
     * @param id the enchantment's id
     * @return an {@link EnchantmentHelper} for the given enchantment.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: EnchantmentId")
    public EnchantmentHelper getEnchantment(String id) {
        return getEnchantment(id, 0);
    }

    /**
     * @param id    the enchantment's id
     * @param level the level of the enchantment
     * @return an {@link EnchantmentHelper} for the given enchantment with the specified level.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<EnchantmentId>, level: int")
    public EnchantmentHelper getEnchantment(String id, int level) {
        return new EnchantmentHelper(mc.getNetworkHandler().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(parseIdentifier(id)).orElseThrow(), level);
    }

    /**
     * @return a list of all registered enchantment ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<EnchantmentId>")
    public List<String> getEnchantmentIds() {
        return mc.getNetworkHandler().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all registered enchantments.
     * @since 1.8.4
     */
    public List<EnchantmentHelper> getEnchantments() {
        return mc.getNetworkHandler().getRegistryManager().get(RegistryKeys.ENCHANTMENT).streamEntries().map(EnchantmentHelper::new).collect(Collectors.toList());
    }

    /**
     * @param type the id of the entity's type
     * @return an {@link EntityHelper} for the given entity.
     * @since 1.8.4
     */
    @DocletReplaceTypeParams("E extends CanOmitNamespace<EntityId>")
    @DocletReplaceParams("type: E")
    @DocletReplaceReturn("EntityTypeFromId<E>")
    public EntityHelper<?> getEntity(String type) {
        return EntityHelper.create(Registries.ENTITY_TYPE.get(parseIdentifier(type)).create(MinecraftClient.getInstance().world));
    }

    /**
     * @param type the id of the entity's type
     * @return an {@link EntityType} for the given entity.
     * @since 1.8.4
     */
    @DocletReplaceParams("type: CanOmitNamespace<EntityId>")
    public EntityType<?> getRawEntityType(String type) {
        return Registries.ENTITY_TYPE.get(parseIdentifier(type));
    }

    /**
     * @return a list of all entity type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<EntityId>")
    public List<String> getEntityTypeIds() {
        return Registries.ENTITY_TYPE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @param id the fluid's id
     * @return an {@link FluidStateHelper} for the given fluid.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: CanOmitNamespace<FluidId>")
    public FluidStateHelper getFluidState(String id) {
        return new FluidStateHelper(Registries.FLUID.get(parseIdentifier(id)).getDefaultState());
    }

    /**
     * @return a list of all feature ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<FeatureId>")
    public List<String> getFeatureIds() {
        return Registries.FEATURE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all structure feature ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<StructureFeatureId>")
    public List<String> getStructureFeatureIds() {
        return Registries.STRUCTURE_PIECE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all painting motive ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<PaintingId>")
    public List<String> getPaintingIds() {
        return mc.getNetworkHandler().getRegistryManager().get(RegistryKeys.PAINTING_VARIANT).getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all particle type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<ParticleTypeId>")
    public List<String> getParticleTypeIds() {
        return Registries.PARTICLE_TYPE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all game event names.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<GameEventName>")
    public List<String> getGameEventNames() {
        return Registries.GAME_EVENT.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all status effect ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<StatusEffectId>")
    public List<String> getStatusEffectIds() {
        return Registries.STATUS_EFFECT.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all block entity type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<BlockEntityTypeId>")
    public List<String> getBlockEntityTypeIds() {
        return Registries.BLOCK_ENTITY_TYPE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all screen handler ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<ScreenHandlerId>")
    public List<String> getScreenHandlerIds() {
        return Registries.SCREEN_HANDLER.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all recipe type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<RecipeTypeId>")
    public List<String> getRecipeTypeIds() {
        return Registries.RECIPE_TYPE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all villager type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<VillagerTypeId>")
    public List<String> getVillagerTypeIds() {
        return Registries.VILLAGER_TYPE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all villager profession ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<VillagerProfession>")
    public List<String> getVillagerProfessionIds() {
        return Registries.VILLAGER_PROFESSION.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all point of interest type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<PointOfInterestTypeId>")
    public List<String> getPointOfInterestTypeIds() {
        return Registries.POINT_OF_INTEREST_TYPE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all memory module type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<MemoryModuleTypeId>")
    public List<String> getMemoryModuleTypeIds() {
        return Registries.MEMORY_MODULE_TYPE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all villager sensor type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<SensorTypeId>")
    public List<String> getSensorTypeIds() {
        return Registries.SENSOR_TYPE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all villager activity type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<ActivityTypeId>")
    public List<String> getActivityTypeIds() {
        return Registries.ACTIVITY.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all stat type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<StatTypeId>")
    public List<String> getStatTypeIds() {
        return Registries.STAT_TYPE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all entity attribute ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<EntityAttributeId>")
    public List<String> getEntityAttributeIds() {
        return Registries.ATTRIBUTE.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @return a list of all potion type ids.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaList<PotionTypeId>")
    public List<String> getPotionTypeIds() {
        return Registries.POTION.getIds().stream().map(Identifier::toString).collect(Collectors.toList());
    }

    /**
     * @param identifier the String representation of the identifier, with the namespace and path
     * @return the raw minecraft Identifier.
     * @since 1.8.4
     */
    public Identifier getIdentifier(String identifier) {
        return parseIdentifier(identifier);
    }

    public static Identifier parseIdentifier(String id) {
        return Identifier.of(parseNameSpace(id));
    }

    public static String parseNameSpace(String id) {
        return id.indexOf(':') != -1 ? id : "minecraft:" + id;
    }

}
