
type Bit    = 1 | 0
type Trit   = 2 | Bit
type Dit    = 3 | Trit
type Pentit = 4 | Dit
type Hexit  = 5 | Pentit
type Septit = 6 | Hexit
type Octit  = 7 | Septit

type Side = Hexit
type HotbarSlot = Octit | 8
type HotbarSwapSlot = HotbarSlot | OffhandSlot
type ClickSlotButton = HotbarSwapSlot | 9 | 10
type OffhandSlot = 40

type HealSource = DamageSource
type InvMapId = InvMapType.All

declare namespace KeyMod {
  type shift = 'key.keyboard.left.shift'
  type ctrl = 'key.keyboard.left.control'
  type alt = 'key.keyboard.left.alt'
}
type KeyMods = KeyMod.shift | KeyMod.ctrl | KeyMod.alt
| `${KeyMod.shift}+${KeyMod.ctrl | KeyMod.alt}`
| `${KeyMod.ctrl}+${KeyMod.alt}`
| `${KeyMod.shift}+${KeyMod.ctrl}+${KeyMod.alt}`
type ArmorSlot = 'HEAD' | 'CHEST' | 'LEGS' | 'FEET'
type TitleType = 'TITLE' | 'SUBTITLE' | 'ACTIONBAR'
type Difficulty = 'peaceful' | 'easy' | 'normal' | 'hard'
type BlockUpdateType = 'STATE' | 'ENTITY'
type BossBarUpdateType = 'ADD' | 'REMOVE' | 'UPDATE_PERCENT'
| 'UPDATE_NAME' | 'UPDATE_STYLE' | 'UPDATE_PROPERTIES'

type GraphicsMode = 'fast' | 'fancy' | 'fabulous'
type ChunkBuilderMode = 'none' | 'nearby' | 'player_affected'
type AttackIndicatorType = 'off' | 'crosshair' | 'hotbar'
type CloudsMode = 'off' | 'fast' | 'fancy'
type ParticleMode = 'minimal' | 'decreased' | 'all'
type ChatVisibility = 'FULL' | 'SYSTEM' | 'HIDDEN'
type NarratorMode = 'OFF' | 'ALL' | "CHAT" | 'SYSTEM'
type Direction = "up" | "down" | "north" | "south" | "east" | "west"
type MobCategory = 'UNDEAD' | 'DEFAULT' | 'ARTHROPOD' | 'ILLAGER' | 'AQUATIC' | 'UNKNOWN'
type WorldScannerOperation = '>' | '>=' | '<' | '<=' | '==' | '!='
type WorldScannerMethod = 'EQUALS' | 'CONTAINS' | 'STARTS_WITH' | 'ENDS_WITH' | 'MATCHES'

type HandledScreenName =
| '1 Row Chest'
| '2 Row Chest'
| '3 Row Chest'
| '4 Row Chest'
| '5 Row Chest'
| '6 Row Chest'
| '3x3 Container'
| 'Anvil'
| 'Beacon'
| 'Blast Furnace'
| 'Brewing Stand'
| 'Crafting Table'
| 'Enchanting Table'
| 'Furnace'
| 'Grindstone'
| 'Hopper'
| 'Loom'
| 'Villager'
| 'Shulker Box'
| 'Smithing Table'
| 'Smoker'
| 'Cartography Table'
| 'Stonecutter'
| 'Survival Inventory'
| 'Horse'
| 'Creative Inventory'

declare namespace InvMapType {
  type _inv = 'hotber' | 'main'
  type _invio = _inv | 'input' | 'output'

  type Inventory = _inv | 'offhand' | 'boots' | 'leggings' | 'chestplate' | 'helmet'
  | 'crafting_in' | 'craft_out'
  type CreativeInvInvTab = Exclude<Inventory, 'crafting_in' | 'crafting_out'> | 'delete'
  type CreativeInv = 'hotbar' | 'creative'
  type Container        = _inv | 'container'
  type Beacon           = _inv | 'slot'
  type Furnace          = _invio | 'fuel'
  type BrewingStand     = _invio | 'fuel'
  type Crafting         = _invio
  type Enchantment      = _inv | 'lapis' | 'item'
  type Loom             = _inv | 'output' | 'pattern' | 'dye' | 'banner'
  type Stonecutter      = _invio
  type Horse            = _inv | 'saddle' | 'armor' | 'container'
  type Anvil            = _invio
  type Merchant         = _invio
  type Smithing         = _invio
  type Grindstone       = _invio
  type CartographyTable = _invio

  type All = 
  | Inventory
  | CreativeInvInvTab
  | CreativeInv
  | Container
  | Beacon
  | Furnace
  | BrewingStand
  | Crafting
  | Enchantment
  | Loom
  | Stonecutter
  | Horse
  | Anvil
  | Merchant
  | Smithing
  | Grindstone
  | CartographyTable
}

//--- runtime generates
// InputUtil
//@Custom
type Key = string
// option.allKeys.map(getTranslationKey())
//@Eval Java.from(Client.getGameOptions().getRaw().field_1839).map(k => k.method_1431())
type Bind = string
//@Eval RegistryManager.method_30530(RegistryKeys.field_41236).method_10235().toArray().map(id => id.toString())
type Biome = string
//@Eval Registries.field_41172.method_10235().toArray().map(id => id.toString())
type Sound = string
//@RegistryHelper getItemIds
type ItemId = string
//@Eval Java.from(RegistryHelper.getItems()).flatMap(i => i.getDefaultStack().getTags())
type ItemTag = string
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
| 'unknown'
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
type Language = string
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
