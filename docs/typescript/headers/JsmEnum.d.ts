
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
type Difficulty = Dit

type HealSource = DamageSource

type KeyMods =
| 'key.keyboard.left.shift'
| 'key.keyboard.left.control'
| 'key.keyboard.left.alt'
| 'key.keyboard.left.shift+key.keyboard.left.control'
| 'key.keyboard.left.shift+key.keyboard.left.alt'
| 'key.keyboard.left.control+key.keyboard.left.alt'
| 'key.keyboard.left.shift+key.keyboard.left.control+key.keyboard.left.alt'
type ArmorSlot = 'HEAD' | 'CHEST' | 'LEGS' | 'FEET'
type TitleType = 'TITLE' | 'SUBTITLE' | 'ACTIONBAR'
type BlockUpdateType = 'STATE' | 'ENTITY'
type BossBarUpdateType = 'ADD' | 'REMOVE' | 'UPDATE_PERCENT'
| 'UPDATE_NAME' | 'UPDATE_STYLE' | 'UPDATE_PROPERTIES'

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

//--- runtime generates
// class_3675
type Key = string
// Java.from(Client.getGameOptions().getRaw().field_1839).map(k => k.method_1431())
// option.allKeys.map(key.getTranslationKey())
type Bind = string
// Registry.field_25933
type Biome = string
// Registry.field_11156
type Sound = string
// Registry.field_11142
type ItemId = string
// collect through items
type ItemTag = string
// Registry.field_11146
type BlockId = string
// collect through blocks
type BlockTag = string
// Registry.field_11145
type EntityId = string
// Registry.field_17597
type RecipeId = string
// class_1934.method_8381
type Gamemode = string
// Registry.field_25490
type Dimension = string
// s.getClass().getName()
// s.getTitle()
type ScreenName =// string
| HandledScreenName
| 'unknown'
// | ScreenClass
// Registry.field_17429
type ScreenClass = string
// class_1269.toString
type ActionResult = string
// class_1282.method_5525
type DamageSource = string
// same as screenName but check if is container
type InventoryType =// string
| HandledScreenName
// Registry.field_11159
type StatusEffectId = string
// class_3619.toString
type PistonBehaviour = string
// class_1297$class_5529.toString
type EntityUnloadReason = string
// class_1259$class_1260.method_5421.toUpperCase
type BossBarColor = string
// class_1259$class_1261.method_5425.toUpperCase
type BossBarStyle = string
// class_2558$class_2559.method_10846
type TextClickAction = string
// class_2568$class_5247.method_27674
type TextHoverAction = string
// class_3854.toString
type VillagerStyle = string
// class_3852.comp_818
type VillagerProfession = string
