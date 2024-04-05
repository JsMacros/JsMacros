
// InputUtil
//@Custom
type Key = string
| 'key.mouse.3'
| 'key.mouse.4'

// option.allKeys.map(getTranslationKey())
//@Eval Java.from(Client.getGameOptions().getRaw().field_1839).map(k => k.method_1431())
type Bind = string
//@Eval RegistryManager.method_30530(RegistryKeys.field_41236).method_10235().toArray().map(id => id.toString())
type Biome = string
//@RegistryHelper getItemIds
type ItemId = string
//@Eval Java.from(RegistryHelper.getItems()).flatMap(i => i.getDefaultStack().getTags())
type ItemTag = string
//@Eval Registries.field_41172.method_10235().toArray().map(id => id.toString())
type SoundId = string
//@Eval Registries.field_41173.method_10235().toArray().map(id => id.toString())
type FluidId = string
//@RegistryHelper getBlockIds
type BlockId = string
//@Eval Java.from(RegistryHelper.getBlocks()).flatMap(b => b.getTags())
type BlockTag = string
//@Custom
type EntityId = keyof EntityIdToTypeMap;
type EntityTypeFromId<E extends EntityId> = EntityIdToTypeMap[CompleteNamespace<E>] extends infer R ?
  EntityIdToTypeMap[EntityId] extends R ? EntityHelper : R : never;
type EntityIdToTypeMap = { [id: string]: EntityHelper }
//@Eval world.method_8433().method_8126().toArray().map(r => r.toString())
type RecipeId = string
//@Enum class_1934.method_8381
type Gamemode = string
//@Eval RegistryManager.method_30530(RegistryKeys.field_41241).method_10235().toArray().map(id => id.toString())
type Dimension = string
| string & {}

//@Custom
type ScreenName = string
| HandledScreenName

//@Custom
type ScreenClass = string
//@Enum class_1269.toString
type ActionResult = string
//@Eval const DamageSource = Java.type('net.minecraft.class_1282');const sources = world.method_48963();sources.getClass().getDeclaredFields().filter(f => f.getType().equals(DamageSource)).map(f => {f.setAccessible(true);return f.get(sources)}).filter(s => s && s instanceof DamageSource).map(s => s.method_5525())
type DamageSource = string
//@RegistryHelper getStatusEffectIds
type StatusEffectId = string
//@Enum class_3619.toString
type PistonBehaviour = string
//@Enum class_1297$class_5529.toString
type EntityUnloadReason = string
//@Enum class_1259$class_1260.name
type BossBarColor = string
//@Enum class_1259$class_1261.name
type BossBarStyle = string
//@Enum class_2558$class_2559.name
type TextClickAction = string
//@Enum class_2568$class_5247.method_15434
type TextHoverAction = string
//@Enum class_3854.toString
type VillagerStyle = string
//@RegistryHelper getVillagerProfessionIds
type VillagerProfession = string


//@RegistryHelper getFeatureIds
type FeatureId = string
//@RegistryHelper getPaintingIds
type PaintingId = string
//@RegistryHelper getParticleTypeIds
type ParticleId = string
//@RegistryHelper getStatTypeIds
type StatTypeId = string
//@RegistryHelper getRecipeTypeIds
type RecipeTypeId = string
//@RegistryHelper getSensorTypeIds
type SensorTypeId = string
//@RegistryHelper getPotionTypeIds
type PotionTypeId = string
//@Eval Java.from(Player.getPlayer().getAdvancementManager().getAdvancementsForIdentifiers().keySet().toArray())
type AdvancementId = string
//@RegistryHelper getParticleTypeIds
type ParticleTypeId = string
//@RegistryHelper getVillagerTypeIds
type VillagerTypeId = string
//@RegistryHelper getActivityTypeIds
type ActivityTypeId = string
//@RegistryHelper getScreenHandlerIds
type ScreenHandlerId = string
//@RegistryHelper getBlockEntityTypeIds
type BlockEntityTypeId = string
//@RegistryHelper getEntityAttributeIds
type EntityAttributeId = string
//@RegistryHelper getMemoryModuleTypeIds
type MemoryModuleTypeId = string
//@RegistryHelper getStructureFeatureIds
type StructureFeatureId = string
//@RegistryHelper getPointOfInterestTypeIds
type PointOfInterestTypeId = string
//@Eval Client.getMinecraft().method_1526().method_4665().keySet().toArray()
type Locale = string
//@Eval Java.from(Client.getGameOptions().control.getCategories())
type KeyCategory = string
//@Enum class_3419.method_14840
type SoundCategory = string
//@RegistryHelper getGameEventNames
type GameEventName = string
//@RegistryHelper getEnchantmentIds
type EnchantmentId = string
//@Eval Java.from(RegistryHelper.getEnchantments()).map(e => e.getRarity())
type EnchantmentRarity = string
//@Eval Java.from(RegistryHelper.getEnchantments()).map(e => e.getTargetType())
type EnchantmentTargetType = string
//@Eval Java.from(Client.createPacketByteBuffer().getPacketNames())
type PacketName = string
//\@Eval Java.from(World.createParticleAccessor().getParticleNames())
type ParticleName = string

//@Enum class_1767.method_7792
type DyeColorName = string
//@Eval const PhaseType = Java.type('net.minecraft.class_1527');const F = PhaseType.class.getDeclaredField('field_7070');F.setAccessible(true);Object.values(PhaseType).filter(f => f instanceof PhaseType).map(p => F.get(p))
type DragonPhase = string
//@Eval new Packages.net.minecraft.class_1510(null, world).method_5690().map(p => p.field_7006)
type DragonBodyPart = string
//@Enum class_5762$class_5767.method_33238
type AxolotlVariant = string
//@Eval Registries.field_41164.method_10235().toArray().map(id => id.toString())
type FrogVariant = string
//@Enum class_1501$class_7993.method_15434
type LlamaVariant = string
//@Enum class_1440$class_1443.method_15434
type PandaGene = string
//@Enum class_1453$class_7989.method_15434
type ParrotVariant = string
//@Enum class_1463$class_7990.method_15434
type RabbitVariant = string
//@Enum class_1474$class_1475.method_15434
type TropicalVariant = string
//@Enum class_1474$class_7991.name
type TropicalSize = string
//@Enum class_1690$class_1692.method_15434
type BoatType = string
//@Enum class_4081.name
type StatusEffectCategory = string
//@Enum class_5421.name
type RecipeBookCategory = string
//@Eval Java.type('net.minecraft.class_2580').field_11801.flat().map(e => Registries.field_41174.method_10221(e).toString())
type BeaconStatusEffect = string
//@Eval Java.from(Java.type('net.minecraft.class_124').method_540(true, false))
type FormattingColorName = string
//@Enum class_270$class_271.field_1436
type TeamCollisionRule = string
//@Enum class_270$class_272.field_1445
type TeamVisibilityRule = string
