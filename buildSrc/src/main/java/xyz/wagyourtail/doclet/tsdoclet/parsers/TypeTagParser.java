package xyz.wagyourtail.doclet.tsdoclet.parsers;

import com.sun.source.doctree.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * low quality parser because idk how to properly parse a ts type string
 * @author MelonRind
 */
public class TypeTagParser {

    static final public Set<String> typeTags = Set.of(
        "Event",
        "EventCallback",
        "EventFilter",
        "EventRes",

        "Bit",
        "Trit",
        "Dit",
        "Pentit",
        "Hexit",
        "Septit",
        "Octit",

        "Side",
        "HotbarSlot",
        "HotbarSwapSlot",
        "ClickSlotButton",
        "OffhandSlot",

        "KeyMods",
        "ArmorSlot",
        "TitleType",
        "Difficulty",
        "BlockUpdateType",
        "BossBarUpdateType",
        "HandledScreenName",

        "Key",
        "Bind",
        "Biome",
        "Sound",
        "ItemId",
        "ItemTag",
        "BlockId",
        "BlockTag",
        "EntityId",
        "RecipeId",
        "Gamemode",
        "Dimension",
        "ScreenName",
        "ScreenClass",
        "ActionResult",
        "InventoryType",
        "StatusEffectId",
        "PistonBehaviour",
        "EntityUnloadReason",
        "HealSource",
        "DamageSource",
        "BossBarColor",
        "BossBarStyle",
        "TextClickAction",
        "TextHoverAction",
        "VillagerStyle",
        "VillagerProfession",
        "RecvPacketType",
        "SendPacketType",
        "ParticleId",
        "Language",
        "GraphicsMode",
        "ChunkBuilderMode",
        "AttackIndicatorType",
        "CloudsMode",
        "ParticleMode",
        "KeyCategory",
        "ChatVisibility",
        "NarratorMode",
        "AdvancementId",
        "EnchantmentId",
        "EnchantmentRarity",
        "EnchantmentTargetType",
        "Direction",
        "MobCategory",
        "FeatureId",
        "StructureFeatureId",
        "PaintingId",
        "ParticleTypeId",
        "GameEventName",
        "BlockEntityTypeId",
        "ScreenHandlerId",
        "RecipeTypeId",
        "VillagerTypeId",
        "PointOfInterestTypeId",
        "MemoryModuleTypeId",
        "SensorTypeId",
        "ActivityTypeId",
        "StatTypeId",
        "EntityAttributeId",
        "PotionTypeId",
        "InvMapId",
        "WorldScannerOperation",
        "WorldScannerMethod"
    );
    static final public Map<String, String> typeTagDefs = new HashMap<String, String>() {{
        put("Event", "E");
        put("EventCallback", "MethodWrapper<Events[E], EventContainer>");
        put("EventFilter", "MethodWrapper<Events[E], undefined, boolean>");
        put("EventRes", "{ event: Events[E], context: EventContainer }");
    }};

    static public String parse(List<? extends DocTree> docList) {
        if (docList.isEmpty()) return null;
        if (!docList.get(0).toString().trim().startsWith("#")) return null;
        String type = "";
        for (DocTree tree : docList) {
            if (tree.getKind() == DocTree.Kind.LINK) break;
            type += tree.toString();
        }
        type = type.trim().substring(1);
        if (!type.contains("#")) return null;
        type = type.substring(0, type.indexOf("#")).trim();

        if (!Arrays.stream(type.split("\\b")).anyMatch(t -> typeTags.contains(t))) {
            System.out.println("unknown TypeTag: #" + type + "#");
            return null;
        }

        // check if there's no # at the end of tag but in description
        // or some typo spaces
        // it could be `keyof`, `extends`, but i think it's never used in this case
        if (Arrays.stream(type.split("\\b")).anyMatch(t -> t.matches(" +"))) {
            System.out.println("potential typo found: #" + type + "#");
        }

        if (count(type, '<') != count(type, '>')) {
            System.out.println("<> doesn't match: #" + type + "#");
            return null;
        }
        if (count(type, '(') != count(type, ')')) {
            System.out.println("() doesn't match: #" + type + "#");
            return null;
        }
        if (count(type, '{') != count(type, '}')) {
            System.out.println("{} doesn't match: #" + type + "#");
            return null;
        }
        if (type.replaceAll("[^\\[ ] *\\]|\\[ *[^\\] ]", "#").contains("#")) {
            System.out.println("incorrect array[]: #" + type + "#");
            return null;
        }

        // lite format
        type = type.replaceAll("  +", " ").replaceAll(",(?! )", ", ");

        for (String t : type.split("\\b"))
            if (typeTagDefs.containsKey(t))
                type = type.replaceAll("\\b" + t + "\\b", typeTagDefs.get(t));

        return type;
    }

    static public int count(String str, char c) {
        int res = 0;
        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == c) res++;
        return res;
    }

}