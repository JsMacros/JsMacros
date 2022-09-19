package xyz.wagyourtail.jsmacros.client.api.helpers.block;

import net.minecraft.block.BambooBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CaveVinesHeadBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.SculkCatalystBlock;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SnowyBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.state.property.Properties;

import xyz.wagyourtail.jsmacros.client.api.helpers.DirectionHelper;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class UniversalBlockStateHelper extends BlockStateHelper {

    /**
     * Currently missing RAIL_SHAPE, RAIL_SHAPE_STRAIGHT
     *
     * @param base
     */
    public UniversalBlockStateHelper(BlockState base) {
        super(base);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getAttachment() {
        return base.get(Properties.ATTACHMENT).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getEastWallShape() {
        return base.get(Properties.EAST_WALL_SHAPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getNorthWallShape() {
        return base.get(Properties.NORTH_WALL_SHAPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getSouthWallShape() {
        return base.get(Properties.SOUTH_WALL_SHAPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getWestWallShape() {
        return base.get(Properties.WEST_WALL_SHAPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getEastWireConnection() {
        return base.get(Properties.EAST_WIRE_CONNECTION).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getNorthWireConnection() {
        return base.get(Properties.NORTH_WIRE_CONNECTION).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getSouthWireConnection() {
        return base.get(Properties.SOUTH_WIRE_CONNECTION).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getWestWireConnection() {
        return base.get(Properties.WEST_WIRE_CONNECTION).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getBlockHalf() {
        return base.get(Properties.BLOCK_HALF).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getDoubleBlockHalf() {
        return base.get(Properties.DOUBLE_BLOCK_HALF).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getRailShape() {
        return base.get(Properties.RAIL_SHAPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getStraightRailShape() {
        return base.get(Properties.STRAIGHT_RAIL_SHAPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getOrientation() {
        return base.get(Properties.ORIENTATION).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getWallMountLocation() {
        return base.get(Properties.WALL_MOUNT_LOCATION).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getHorizontalAxis() {
        return base.get(Properties.HORIZONTAL_AXIS).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getAxis() {
        return base.get(Properties.AXIS).getName();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public DirectionHelper getHorizontalFacing() {
        return new DirectionHelper(base.get(Properties.HORIZONTAL_FACING));
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public DirectionHelper getFacingHopper() {
        return new DirectionHelper(base.get(Properties.HOPPER_FACING));
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public DirectionHelper getFacing() {
        return new DirectionHelper(base.get(Properties.FACING));
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isFacingUp() {
        return base.get(Properties.UP);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isFacingDown() {
        return base.get(Properties.DOWN);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isFacingNorth() {
        return base.get(Properties.NORTH);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isFacingSouth() {
        return base.get(Properties.SOUTH);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isFacingEast() {
        return base.get(Properties.EAST);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isFacingWest() {
        return base.get(Properties.WEST);
    }

    /**
     * Used on beehives.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getHoneyLevel() {
        return base.get(BeehiveBlock.HONEY_LEVEL);
    }

    /**
     * Used on scaffolding.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isScaffoldingAtBottom() {
        return base.get(ScaffoldingBlock.BOTTOM);
    }

    /**
     * Used on bubble columns.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean doesBubbleColumnDragDown() {
        return base.get(BubbleColumnBlock.DRAG);
    }

    /**
     * Used on bubble columns.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean doesBubbleColumnLiftUp() {
        return !base.get(BubbleColumnBlock.DRAG);
    }

    /**
     * Used on trip wire hooks.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isTripWireHookAttached() {
        return base.get(TripwireHookBlock.ATTACHED);
    }

    /**
     * Used on trip wires.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isTripWireDisarmed() {
        return base.get(TripwireBlock.DISARMED);
    }

    /**
     * Used on trip wires.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isTripWireAttached() {
        return base.get(TripwireBlock.ATTACHED);
    }

    /**
     * Used on command blocks.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isCommandBlockConditional() {
        return base.get(CommandBlock.CONDITIONAL);
    }

    /**
     * Used on hoppers.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isHopperEnabled() {
        return base.get(HopperBlock.ENABLED);
    }

    /**
     * Used on pistons.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isPistonExtended() {
        return base.get(PistonBlock.EXTENDED);
    }

    /**
     * Used on piston heads.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isPistonHeadShort() {
        return base.get(PistonHeadBlock.SHORT);
    }

    /**
     * Used on end portal frames.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasEndPortalEye() {
        return base.get(EndPortalFrameBlock.EYE);
    }

    /**
     * Used on fluids.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isFluidFalling() {
        return base.get(FlowableFluid.FALLING);
    }

    /**
     * Used on fluids.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getFluidLevel() {
        return base.get(FlowableFluid.LEVEL);
    }

    /**
     * Used on lanterns.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isLanternHanging() {
        return base.get(LanternBlock.HANGING);
    }

    /**
     * Used on brewing stands.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasBrewingStandFirstBottle() {
        return base.get(BrewingStandBlock.BOTTLE_PROPERTIES[0]);
    }

    /**
     * Used on brewing stands.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasBrewingStandSecondBottle() {
        return base.get(BrewingStandBlock.BOTTLE_PROPERTIES[1]);
    }

    /**
     * Used on brewing stands.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasBrewingStandThirdBottle() {
        return base.get(BrewingStandBlock.BOTTLE_PROPERTIES[2]);
    }

    /**
     * Used on jukeboxes.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasJukeboxRecord() {
        return base.get(JukeboxBlock.HAS_RECORD);
    }

    /**
     * Used on lecterns.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasLecternBook() {
        return base.get(LecternBlock.HAS_BOOK);
    }

    /**
     * Used on daylight sensors.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isDayLightSenser() {
        return !base.get(DaylightDetectorBlock.INVERTED);
    }

    /**
     * Used on daylight sensors.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isNightLightSenser() {
        return base.get(DaylightDetectorBlock.INVERTED);
    }

    /**
     * Used on fence gates.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isFenceGateInWall() {
        return base.get(FenceGateBlock.IN_WALL);
    }

    /**
     * Used on fence gates, barrels, trap doors and doors.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isOpen() {
        return base.get(Properties.OPEN);
    }

    /**
     * Used on candles, all types of furnaces, campfires and redstone torches.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isBlockLit() {
        return base.get(Properties.LIT);
    }

    /**
     * Used on repeaters.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isRepeaterLocked() {
        return base.get(RepeaterBlock.LOCKED);
    }

    /**
     * Used on repeaters.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getRepeaterDelay() {
        return base.get(RepeaterBlock.DELAY);
    }

    /**
     * Used on beds.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isBedOccupied() {
        return base.get(BedBlock.OCCUPIED);
    }

    /**
     * Used on leaves.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isLeavePersistent() {
        return base.get(LeavesBlock.PERSISTENT);
    }

    /**
     * Used on leaves.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getLeaveDistance() {
        return base.get(LeavesBlock.DISTANCE);
    }

    /**
     * Used on bells, buttons, detector rails, diodes, doors, fence gates, lecterns, levers,
     * lightning rods, note blocks, observers, powered rails, pressure plates, trap doors, trip wire
     * hooks and trip wires.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isBlockPowered() {
        return base.get(Properties.POWERED);
    }

    /**
     * Used on campfires.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean doesCampfireSignalFire() {
        return base.get(CampfireBlock.SIGNAL_FIRE);
    }

    /**
     * Used on snowy dirt blocks.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isSnowyDirtSnowy() {
        return base.get(SnowyBlock.SNOWY);
    }

    /**
     * Used on dispensers.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isDispenserTriggered() {
        return base.get(DispenserBlock.TRIGGERED);
    }

    /**
     * Used on tnt.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isTntUnstable() {
        return base.get(TntBlock.UNSTABLE);
    }

    /**
     * Used on amethysts, corals, rails, dripleaves, dripleaf stems, campfires, candles, chains,
     * chests, conduits, fences, double plants, ender chests, iron bars, glass panes, glow lichen,
     * hanging roots, ladders, lanterns, light blocks, lightning rods, pointed dripstone,
     * scaffolding , sculk sensors, sea pickles, signs, stairs, slabs, trap doors, walls
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean isBlockWaterlogged() {
        return base.get(Properties.WATERLOGGED);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getBedPart() {
        return base.get(BedBlock.PART).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getDoorHinge() {
        return base.get(DoorBlock.HINGE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getInstrument() {
        return base.get(NoteBlock.INSTRUMENT).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getPistonType() {
        return base.get(PistonHeadBlock.TYPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getSlabType() {
        return base.get(SlabBlock.TYPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getStairShape() {
        return base.get(StairsBlock.SHAPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getStructureBlockMode() {
        return base.get(StructureBlock.MODE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getBambooLeabes() {
        return base.get(BambooBlock.LEAVES).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getTitle() {
        return base.get(Properties.TILT).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getVerticalDirection() {
        return base.get(PointedDripstoneBlock.VERTICAL_DIRECTION).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getThickness() {
        return base.get(PointedDripstoneBlock.THICKNESS).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getChestType() {
        return base.get(ChestBlock.CHEST_TYPE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getComparatorMode() {
        return base.get(ComparatorBlock.MODE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isVineEnd() {
        return base.get(Properties.VINE_END);
    }

    /**
     * Used on cave vine roots.
     *
     * @return
     *
     * @since 1.9.0
     */
    public boolean hasCaveVineBerries() {
        return base.get(CaveVinesHeadBlock.BERRIES);
    }

    /**
     * Used on bamboo.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getGrowthStageFromAge1() {
        return base.get(Properties.AGE_1);
    }

    /**
     * Used on cocoa plants.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getGrowthStageFromAge2() {
        return base.get(Properties.AGE_2);
    }

    /**
     * Used on beetroot, frosted ice, nether warts, sweet berries.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getGrowthStageFromAge3() {
        return base.get(Properties.AGE_3);
    }

    /**
     * Used on chorus flowers.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getGrowthStageFromAge5() {
        return base.get(Properties.AGE_5);
    }

    /**
     * Used on crop and stem blocks, i.e. wheat, pumpkin and melon stems
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getGrowthStageFromAge7() {
        return base.get(Properties.AGE_7);
    }

    /**
     * Used on cactus, fire, sugar cane.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getGrowthStageFromAge15() {
        return base.get(Properties.AGE_15);
    }

    /**
     * Used on growing plant heads.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getGrowthStageFromAge25() {
        return base.get(Properties.AGE_25);
    }

    /**
     * Used on cakes.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getBitesFromCake() {
        return base.get(CakeBlock.BITES);
    }

    /**
     * Used on candles.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getCandleCount() {
        return base.get(CandleBlock.CANDLES);
    }

    /**
     * Used on turtle eggs.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getTurtleEggCount() {
        return base.get(TurtleEggBlock.EGGS);
    }

    /**
     * Used on turtle eggs.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getHatchedTurtleEggCount() {
        return base.get(TurtleEggBlock.HATCH);
    }

    /**
     * Used on snow layers.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getSnowLayers() {
        return base.get(SnowBlock.LAYERS);
    }

    /**
     * Used on snow cauldrons.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getCauldronSnowLayers() {
        return base.get(LeveledCauldronBlock.LEVEL);
    }

    /**
     * Used on composters.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getComposterLevel() {
        return base.get(ComposterBlock.LEVEL);
    }

    /**
     * Used on farmland.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getFarmlandMoistureLevel() {
        return base.get(FarmlandBlock.MOISTURE);
    }

    /**
     * Used on note blocks.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getNoteFromNoteblock() {
        return base.get(NoteBlock.NOTE);
    }

    /**
     * Used on sea pickles.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getSeaPickleCount() {
        return base.get(SeaPickleBlock.PICKLES);
    }

    /**
     * Used on daylight sensors, redstone wires, sculk sensors, target blocks, weighted pressure
     * plates.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getBlockPower() {
        return base.get(Properties.POWER);
    }

    /**
     * Used on bamboo, saplings.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getGrowthStage() {
        return base.get(Properties.STAGE);
    }

    /**
     * Used on scaffolding.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getScaffoldingDistance() {
        return base.get(ScaffoldingBlock.DISTANCE);
    }

    /**
     * Used on respawn anchors.
     *
     * @return
     *
     * @since 1.9.0
     */
    public int getRespawnAnchorCharges() {
        return base.get(RespawnAnchorBlock.CHARGES);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isShrieking() {
        return base.get(SculkShriekerBlock.SHRIEKING);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean canSummon() {
        return base.get(SculkShriekerBlock.CAN_SUMMON);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getSculkSensorPhase() {
        return base.get(SculkSensorBlock.SCULK_SENSOR_PHASE).asString();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean isBlooming() {
        return base.get(SculkCatalystBlock.BLOOM);
    }

}
