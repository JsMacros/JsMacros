
// InputUtil
//@Custom
type Key = string
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
//@RegistryHelper getBlockIds
type BlockId = string
//@Eval Java.from(RegistryHelper.getBlocks()).flatMap(b => b.getTags())
type BlockTag = string
//@RegistryHelper getEntityTypeIds
type EntityId = string
//@Eval Client.getMinecraft().field_1687.method_8433().method_8126().toArray().map(r => r.method_8114().toString())
type RecipeId = string
//@Enum class_1934.method_8381
type Gamemode = string
//@Eval RegistryManager.method_30530(RegistryKeys.field_41241).method_10235().toArray().map(id => id.toString())
type Dimension = string
//@Unknown
type ScreenName =// string
| HandledScreenName
// | ScreenClass
//@Unknown
type ScreenClass = string
//@Enum class_1269.toString
type ActionResult = string
//@Enum class_1282.method_5525
type DamageSource = string
//@Unknown
type InventoryType =// string
| HandledScreenName
//@RegistryHelper getStatusEffectIds
type StatusEffectId = string
//@Enum class_3619.toString
type PistonBehaviour = string
//@Enum class_1297$class_5529.toString
type EntityUnloadReason = string
//@Enum class_1259$class_1260.method_5421.toUpperCase
type BossBarColor = string
//@Enum class_1259$class_1261.method_5425.toUpperCase
type BossBarStyle = string
//@Enum class_2558$class_2559.method_10846
type TextClickAction = string
//@Enum class_2568$class_5247.method_27674
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
//@Eval Java.from(Player.getPlayer().getAdvancementManager().getAdvancements()).map(a => a.getId())
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
//@Eval Client.getMinecraft().method_1526().method_4665().toArray().map(l => l.getCode())
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
