package xyz.wagyourtail.jsmacros.client.api.helper.world;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class UniversalBlockStateHelper extends BlockStateHelper {

    public UniversalBlockStateHelper(BlockState base) {
        super(base);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getAttachment() {
        return base.get(Properties.ATTACHMENT).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getEastWallShape() {
        return base.get(Properties.EAST_WALL_SHAPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getNorthWallShape() {
        return base.get(Properties.NORTH_WALL_SHAPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getSouthWallShape() {
        return base.get(Properties.SOUTH_WALL_SHAPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getWestWallShape() {
        return base.get(Properties.WEST_WALL_SHAPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getEastWireConnection() {
        return base.get(Properties.EAST_WIRE_CONNECTION).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getNorthWireConnection() {
        return base.get(Properties.NORTH_WIRE_CONNECTION).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getSouthWireConnection() {
        return base.get(Properties.SOUTH_WIRE_CONNECTION).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getWestWireConnection() {
        return base.get(Properties.WEST_WIRE_CONNECTION).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getBlockHalf() {
        return base.get(Properties.BLOCK_HALF).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getDoubleBlockHalf() {
        return base.get(Properties.DOUBLE_BLOCK_HALF).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getRailShape() {
        return base.get(Properties.RAIL_SHAPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getStraightRailShape() {
        return base.get(Properties.STRAIGHT_RAIL_SHAPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getOrientation() {
        return base.get(Properties.ORIENTATION).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getHorizontalAxis() {
        return base.get(Properties.HORIZONTAL_AXIS).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getAxis() {
        return base.get(Properties.AXIS).getId();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public DirectionHelper getHorizontalFacing() {
        return new DirectionHelper(base.get(Properties.HORIZONTAL_FACING));
    }

    /**
     * @return
     * @since 1.8.4
     */
    public DirectionHelper getHopperFacing() {
        return new DirectionHelper(base.get(Properties.HOPPER_FACING));
    }

    /**
     * @return
     * @since 1.8.4
     */
    public DirectionHelper getFacing() {
        return new DirectionHelper(base.get(Properties.FACING));
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isUp() {
        return base.get(Properties.UP);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isDown() {
        return base.get(Properties.DOWN);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isNorth() {
        return base.get(Properties.NORTH);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isSouth() {
        return base.get(Properties.SOUTH);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isEast() {
        return base.get(Properties.EAST);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isWest() {
        return base.get(Properties.WEST);
    }

    /**
     * Used on beehives.
     *
     * @return
     * @since 1.8.4
     */
    public int getHoneyLevel() {
        return base.get(Properties.HONEY_LEVEL);
    }

    /**
     * Used on scaffolding.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isBottom() {
        return base.get(Properties.BOTTOM);
    }

    /**
     * Used on bubble columns.
     *
     * @return
     * @since 1.8.4
     */
    @Ignore({"hasDrag"})
    public boolean isBubbleColumnDown() {
        return base.get(Properties.DRAG);
    }

    /**
     * Used on bubble columns.
     *
     * @return
     * @since 1.8.4
     */
    @Ignore
    public boolean isBubbleColumnUp() {
        return !base.get(Properties.DRAG);
    }

    /**
     * Used on trip wire hooks.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isAttached() {
        return base.get(Properties.ATTACHED);
    }

    /**
     * Used on trip wires.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isDisarmed() {
        return base.get(Properties.DISARMED);
    }

    /**
     * Used on command blocks.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isConditional() {
        return base.get(Properties.CONDITIONAL);
    }

    /**
     * Used on hoppers.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isEnabled() {
        return base.get(Properties.ENABLED);
    }

    /**
     * Used on pistons.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isExtended() {
        return base.get(Properties.EXTENDED);
    }

    /**
     * Used on piston heads.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isShort() {
        return base.get(Properties.SHORT);
    }

    /**
     * Used on end portal frames.
     *
     * @return
     * @since 1.8.4
     */
    public boolean hasEye() {
        return base.get(Properties.EYE);
    }

    /**
     * Used on fluids.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isFalling() {
        return base.get(Properties.FALLING);
    }

    // don't make static, causes crash in main function below
    private final IntProperty[] levels = {
            Properties.LEVEL_1_8,
            Properties.LEVEL_3,
            Properties.LEVEL_8,
            Properties.LEVEL_15
    };

    /**
     * Used on fluids and stuff
     *
     * @return
     * @since 1.8.4
     */
    @Ignore({"getLevel1_8", "getLevel3", "getLevel8", "getLevel15"})
    public int getLevel() {
        for (IntProperty level : levels) {
            if (base.contains(level)) {
                return base.get(level);
            }
        }
        throw new IllegalStateException("No level property found");
    }

    /**
     * @return
     * @since 1.8.4
     */
    @Ignore
    public int getMaxLevel() {
        for (IntProperty level : levels) {
            if (base.contains(level)) {
                return level.getValues().stream().max(Integer::compare).orElse(-1);
            }
        }
        throw new IllegalStateException("No level property found");
    }

    /**
     * @return
     * @since 1.8.4
     */
    @Ignore
    public int getMinLevel() {
        for (IntProperty level : levels) {
            if (base.contains(level)) {
                return level.getValues().stream().min(Integer::compare).orElse(-1);
            }
        }
        throw new IllegalStateException("No level property found");
    }

    /**
     * Used on lanterns.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isHanging() {
        return base.get(Properties.HANGING);
    }

    /**
     * Used on brewing stands.
     *
     * @return
     * @since 1.8.4
     */
    public boolean hasBottle0() {
        return base.get(Properties.HAS_BOTTLE_0);
    }

    /**
     * Used on brewing stands.
     *
     * @return
     * @since 1.8.4
     */
    public boolean hasBottle1() {
        return base.get(Properties.HAS_BOTTLE_1);
    }

    /**
     * Used on brewing stands.
     *
     * @return
     * @since 1.8.4
     */
    public boolean hasBottle2() {
        return base.get(Properties.HAS_BOTTLE_2);
    }

    /**
     * Used on jukeboxes.
     *
     * @return
     * @since 1.8.4
     */
    public boolean hasRecord() {
        return base.get(Properties.HAS_RECORD);
    }

    /**
     * Used on lecterns.
     *
     * @return
     * @since 1.8.4
     */
    public boolean hasBook() {
        return base.get(Properties.HAS_BOOK);
    }

    /**
     * Used on daylight sensors.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isInverted() {
        return base.get(Properties.INVERTED);
    }

    /**
     * Used on fence gates.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isInWall() {
        return base.get(Properties.IN_WALL);
    }

    /**
     * Used on fence gates, barrels, trap doors and doors.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isOpen() {
        return base.get(Properties.OPEN);
    }

    /**
     * Used on candles, all types of furnaces, campfires and redstone torches.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isLit() {
        return base.get(Properties.LIT);
    }

    /**
     * Used on repeaters.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isLocked() {
        return base.get(Properties.LOCKED);
    }

    /**
     * Used on repeaters.
     *
     * @return
     * @since 1.8.4
     */
    public int getDelay() {
        return base.get(Properties.DELAY);
    }

    /**
     * Used on beds.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isOccupied() {
        return base.get(Properties.OCCUPIED);
    }

    /**
     * Used on leaves.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isPersistent() {
        return base.get(Properties.PERSISTENT);
    }

    private final IntProperty distance[] = {
            Properties.DISTANCE_0_7,
            Properties.DISTANCE_1_7
    };

    /**
     * Used on leaves and scaffold.
     *
     * @return
     * @since 1.8.4
     */
    @Ignore({"getDistance0_7", "getDistance1_7"})
    public int getDistance() {
        for (IntProperty d : distance) {
            if (base.contains(d)) {
                return base.get(d);
            }
        }
        throw new IllegalStateException("No distance property found");
    }

    /**
     * Used on leaves and scaffold.
     *
     * @return
     * @since 1.8.4
     */
    @Ignore
    public int getMaxDistance() {
        for (IntProperty d : distance) {
            if (base.contains(d)) {
                return d.getValues().stream().max(Integer::compare).orElse(-1);
            }
        }
        throw new IllegalStateException("No distance property found");
    }

    /**
     * Used on leaves and scaffold.
     *
     * @return
     * @since 1.8.4
     */
    @Ignore
    public int getMinDistance() {
        for (IntProperty d : distance) {
            if (base.contains(d)) {
                return d.getValues().stream().min(Integer::compare).orElse(-1);
            }
        }
        throw new IllegalStateException("No distance property found");
    }

    /**
     * Used on bells, buttons, detector rails, diodes, doors, fence gates, lecterns, levers,
     * lightning rods, note blocks, observers, powered rails, pressure plates, trap doors, trip wire
     * hooks and trip wires.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isPowered() {
        return base.get(Properties.POWERED);
    }

    /**
     * Used on campfires.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isSignalFire() {
        return base.get(Properties.SIGNAL_FIRE);
    }

    /**
     * Used on snowy dirt blocks.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isSnowy() {
        return base.get(Properties.SNOWY);
    }

    /**
     * Used on dispensers.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isTriggered() {
        return base.get(Properties.TRIGGERED);
    }

    /**
     * Used on tnt.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isUnstable() {
        return base.get(Properties.UNSTABLE);
    }

    /**
     * Used on amethysts, corals, rails, dripleaves, dripleaf stems, campfires, candles, chains,
     * chests, conduits, fences, double plants, ender chests, iron bars, glass panes, glow lichen,
     * hanging roots, ladders, lanterns, light blocks, lightning rods, pointed dripstone,
     * scaffolding , sculk sensors, sea pickles, signs, stairs, slabs, trap doors and walls
     *
     * @return
     * @since 1.8.4
     */
    public boolean isWaterlogged() {
        return base.get(Properties.WATERLOGGED);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getBedPart() {
        return base.get(Properties.BED_PART).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getDoorHinge() {
        return base.get(Properties.DOOR_HINGE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getInstrument() {
        return base.get(Properties.INSTRUMENT).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getPistonType() {
        return base.get(Properties.PISTON_TYPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getSlabType() {
        return base.get(Properties.SLAB_TYPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getStairShape() {
        return base.get(Properties.STAIR_SHAPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getStructureBlockMode() {
        return base.get(Properties.STRUCTURE_BLOCK_MODE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getBambooLeaves() {
        return base.get(Properties.BAMBOO_LEAVES).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getTilt() {
        return base.get(Properties.TILT).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getVerticalDirection() {
        return base.get(Properties.VERTICAL_DIRECTION).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getThickness() {
        return base.get(Properties.THICKNESS).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getChestType() {
        return base.get(Properties.CHEST_TYPE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public String getComparatorMode() {
        return base.get(Properties.COMPARATOR_MODE).asString();
    }

    /**
     * Used on cave vine roots.
     *
     * @return
     * @since 1.8.4
     */
    public boolean hasBerries() {
        return base.get(Properties.BERRIES);
    }

    // don't make static, causes crash in main function below
    private final IntProperty[] ages = {
            Properties.AGE_1,
            Properties.AGE_2,
            Properties.AGE_3,
            Properties.AGE_4,
            Properties.AGE_5,
            Properties.AGE_7,
            Properties.AGE_15,
            Properties.AGE_25
    };

    /**
     * crop age and such
     *
     * @return
     * @author Wagyourtail
     * @since 1.8.4
     */
    @Ignore({"getAge1", "getAge2", "getAge3", "getAge4", "getAge5", "getAge7", "getAge15", "getAge25"})
    public int getAge() {
        for (IntProperty property : ages) {
            if (base.contains(property)) {
                return base.get(property);
            }
        }
        throw new IllegalStateException("No age property found");
    }

    @Ignore
    public int getMaxAge() {
        for (IntProperty property : ages) {
            if (base.contains(property)) {
                return property.getValues().stream().max(Integer::compareTo).orElse(-1);
            }
        }
        throw new IllegalStateException("No age property found");
    }

    /**
     * Used on cakes.
     *
     * @return
     * @since 1.8.4
     */
    public int getBites() {
        return base.get(Properties.BITES);
    }

    /**
     * Used on candles.
     *
     * @return
     * @since 1.8.4
     */
    public int getCandles() {
        return base.get(Properties.CANDLES);
    }

    /**
     * Used on turtle eggs.
     *
     * @return
     * @since 1.8.4
     */
    public int getEggs() {
        return base.get(Properties.EGGS);
    }

    /**
     * Used on turtle eggs.
     *
     * @return
     * @since 1.8.4
     */
    @Ignore("getHatch")
    public int getHatched() {
        return base.get(Properties.HATCH);
    }

    /**
     * Used on snow layers.
     *
     * @return
     * @since 1.8.4
     */
    public int getLayers() {
        return base.get(Properties.LAYERS);
    }

    /**
     * Used on farmland.
     *
     * @return
     * @since 1.8.4
     */
    public int getMoisture() {
        return base.get(Properties.MOISTURE);
    }

    /**
     * Used on note blocks.
     *
     * @return
     * @since 1.8.4
     */
    public int getNote() {
        return base.get(Properties.NOTE);
    }

    /**
     * Used on sea pickles.
     *
     * @return
     * @since 1.8.4
     */
    public int getPickles() {
        return base.get(Properties.PICKLES);
    }

    /**
     * Used on daylight sensors, redstone wires, sculk sensors, target blocks, weighted pressure
     * plates.
     *
     * @return
     * @since 1.8.4
     */
    public int getPower() {
        return base.get(Properties.POWER);
    }

    /**
     * Used on bamboo, saplings.
     *
     * @return
     * @since 1.8.4
     */
    public int getStage() {
        return base.get(Properties.STAGE);
    }

    /**
     * Used on respawn anchors.
     *
     * @return
     * @since 1.8.4
     */
    public int getCharges() {
        return base.get(Properties.CHARGES);
    }

    /**
     * Used on sculk sensors.
     *
     * @return
     * @since 1.8.4
     */
    public boolean isShrieking() {
        return base.get(Properties.SHRIEKING);
    }

    /**
     * Used on sculk sensors.
     *
     * @return
     * @since 1.8.4
     */
    public boolean canSummon() {
        return base.get(Properties.CAN_SUMMON);
    }

    /**
     * Used on sculk sensors.
     *
     * @return
     * @since 1.8.4
     */
    public String getSculkSensorPhase() {
        return base.get(Properties.SCULK_SENSOR_PHASE).asString();
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isBloom() {
        return base.get(Properties.BLOOM);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public int getRotation() {
        return base.get(Properties.ROTATION);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isSlot0Occupied() {
        return base.get(Properties.SLOT_0_OCCUPIED);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isSlot1Occupied() {
        return base.get(Properties.SLOT_1_OCCUPIED);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isSlot2Occupied() {
        return base.get(Properties.SLOT_2_OCCUPIED);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isSlot3Occupied() {
        return base.get(Properties.SLOT_3_OCCUPIED);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isSlot4Occupied() {
        return base.get(Properties.SLOT_4_OCCUPIED);
    }

    /**
     * @return
     * @since 1.8.4
     */
    public boolean isSlot5Occupied() {
        return base.get(Properties.SLOT_5_OCCUPIED);
    }

    /**
     * @since 1.9.0
     * @return
     */
    public int getFlowerAmount() {
        return base.get(Properties.FLOWER_AMOUNT);
    }

    /**
     * @since 1.9.0
     * @return
     */
    public String getBlockFace() {
        return base.get(Properties.BLOCK_FACE).asString();
    }

    /**
     * @since 1.9.0
     * @return
     */
    public int getDusted() {
        return base.get(Properties.DUSTED);
    }

    /**
     * @since 1.9.0
     * @return
     */
    public boolean isCracked() {
        return base.get(Properties.CRACKED);
    }

    /**
     * @since 2.0.0
     */
    public boolean isCrafting() {
        return base.get(Properties.CRAFTING);
    }

    /**
     * @since 2.0.0
     */
    public String getTrialSpawnerState() {
        return base.get(Properties.TRIAL_SPAWNER_STATE).asString();
    }

    /**
     * @since 2.0.0
     */
    public String getVaultState() {
        return base.get(Properties.VAULT_STATE).asString();
    }

    /**
     * @since 2.0.0
     */
    public boolean isOminous() {
        return base.get(Properties.OMINOUS);
    }

    @Ignore
    private static String SCREAMING_SNAKE_CASE_TO_PascalCase(String input) {
        StringBuilder result = new StringBuilder();
        String[] allWords = input.split("_");
        for (String word : allWords) {
            if (word.equalsIgnoreCase("has") || word.equalsIgnoreCase("is") || word.equalsIgnoreCase("can")) {
                continue;
            }
            //test if previous ended with a number and current starts with a number
            if (!result.isEmpty() && Character.isDigit(result.charAt(result.length() - 1))) {
                if (Character.isDigit(word.charAt(0))) {
                    result.append("_");
                }
            }
            result.append(Character.toUpperCase(word.charAt(0)));
            result.append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }

    @Ignore
    public static void main(String[] args) {
        Set<String> properties = new HashSet<>();
        for (Method declaredMethod : UniversalBlockStateHelper.class.getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(Ignore.class)) {
                properties.addAll(Arrays.asList(declaredMethod.getAnnotation(Ignore.class).value()));
            } else {
                properties.add(declaredMethod.getName());
            }
        }

        System.out.println("Properties to add:");

        StringBuilder builder = new StringBuilder();

        for (Field field : Properties.class.getDeclaredFields()) {
            if (Property.class.isAssignableFrom(field.getType())) {
                if (BooleanProperty.class.isAssignableFrom(field.getType())) {
                    if (!properties.remove("is" + SCREAMING_SNAKE_CASE_TO_PascalCase(field.getName())) &&
                            !properties.remove("has" + SCREAMING_SNAKE_CASE_TO_PascalCase(field.getName())) &&
                            !properties.remove("can" + SCREAMING_SNAKE_CASE_TO_PascalCase(field.getName()))) {
                        System.out.println("public boolean is" + SCREAMING_SNAKE_CASE_TO_PascalCase(field.getName()) + "() {");
                        System.out.println("    return base.get(Properties." + field.getName() + ");");
                        System.out.println("}");
                        System.out.println();
                    }
                } else {
                    if (!properties.remove("get" + SCREAMING_SNAKE_CASE_TO_PascalCase(field.getName()))) {
                        // get type of property
                        String type = field.getType().getSimpleName();
                        type = type.substring(0, type.length() - "Property".length());
                        // lowercase first letter
                        type = Character.toLowerCase(type.charAt(0)) + type.substring(1);
                        if (type.equals("enum")) {
                            type = "String";
                        }
                        System.out.println("public " + type + " get" + SCREAMING_SNAKE_CASE_TO_PascalCase(field.getName()) + "() {");
                        System.out.print("    return base.get(Properties." + field.getName() + ")");
                        if (type.equals("String")) {
                            System.out.print(".asString()");
                        }
                        System.out.println(";");
                        System.out.println("}");
                        System.out.println();
                    }
                }
            }
        }

        System.out.println("Properties not found:");

        System.out.println(builder);

        for (String property : properties) {
            System.out.println(property);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    private @interface Ignore {
        String[] value() default {};

    }

}
