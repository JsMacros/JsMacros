package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.base.Predicate;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;
import xyz.wagyourtail.jsmacros.client.api.helpers.*;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.movement.MovementDummy;
import xyz.wagyourtail.jsmacros.client.movement.MovementQueue;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Functions for getting and modifying the player's state.
 *
 * An instance of this class is passed to scripts as the {@code Player} variable.
 *
 * @author Wagyourtail
 */
@Library("Player")
@SuppressWarnings("unused")
public class FPlayer extends BaseLibrary {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * @return the Inventory handler
     * @see xyz.wagyourtail.jsmacros.client.api.classes.Inventory
     */
    public Inventory<?> openInventory() {
        assert mc.player != null && mc.player.inventory != null;
        return Inventory.create();
    }

    /**
     * @return the player entity wrapper.
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper
     * @since 1.0.3
     */
    public ClientPlayerEntityHelper<EntityPlayerSP> getPlayer() {
        assert mc.player != null;
        return new ClientPlayerEntityHelper<>(mc.player);
    }

    /**
     * @return the player's current gamemode.
     * @since 1.0.9
     */
    public String getGameMode() {
        assert mc.interactionManager != null;
        WorldSettings.GameType mode = mc.interactionManager.getCurrentGameMode();
        if (mode == null) mode = WorldSettings.GameType.NOT_SET;
        return mode.getName();
    }

    /**
     * @param distance
     * @param fluid
     * @return the block/liquid the player is currently looking at.
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper
     * @since 1.0.5
     */
    public BlockDataHelper rayTraceBlock(double distance, boolean fluid) {
        assert mc.world != null;
        assert mc.player != null;
        Vec3 vec3 = mc.player.getCameraPosVec(0);
        Vec3 vec31 = mc.player.getRotationVector(0);
        Vec3 vec32 = vec3.add(vec31.x * distance, vec31.y * distance, vec31.z * distance);
        MovingObjectPosition h = mc.world.rayTrace(vec3, vec32, fluid, false, true);
        if (h.type == MovingObjectPosition.MovingObjectType.MISS) return null;
        IBlockState b = mc.world.getBlockState(h.getBlockPos());
        TileEntity t = mc.world.getBlockEntity(h.getBlockPos());
        if (b.getBlock().equals(Blocks.AIR)) return null;
        return new BlockDataHelper(b, t, h.getBlockPos());
    }

    /**
     * @return the entity the camera is currently looking at.
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper
     * @since 1.0.5
     */
    public EntityHelper<?> rayTraceEntity() {
        if (mc.targetedEntity != null) return EntityHelper.create(mc.targetedEntity);
        else return null;
    }

    /**
     * @param distance
     * @since 1.8.3
     * @return entity the player entity is currently looking at (if any).
     */
    public EntityHelper<?> rayTraceEntity(int distance) {
        return getTargetedEntity(mc.player, distance).map(EntityHelper::create).orElse(null);
    }

    private static Optional<Entity> getTargetedEntity(@Nullable Entity entity, int distance) {
        if (entity == null) {
            return Optional.empty();
        } else {
            Vec3 vec3 = entity.getPos().add(0.0D, entity.getEyeHeight(), 0.0D);
            Vec3 vec32 = entity.getRotationVector(1.0F);
            vec32 = new Vec3(vec32.x * distance, vec32.y * distance, vec32.z * distance);
            Vec3 vec33 = vec3.add(vec32);
            AxisAlignedBB aABB = expandTowards(entity.getBoundingBox(), vec32.x, vec32.y, vec32.z).expand(1.0, 1.0, 1.0);
            int i = distance * distance;
            Predicate<Entity> predicate = entityx -> !entityx.removed;
            Entity entityHitResult = getEntityHitResult(entity, vec3, vec33, aABB, predicate, i);
            if (entityHitResult == null) {
                return Optional.empty();
            } else {
                return vec3.squaredDistanceTo(entityHitResult.getPos()) > (double)i ? Optional.empty() : Optional.ofNullable(entityHitResult.getEntity());
            }
        }
    }

    private static AxisAlignedBB expandTowards(AxisAlignedBB bb, Vec3 vector) {
        return expandTowards(bb, vector.x, vector.y, vector.z);
    }

    private static AxisAlignedBB expandTowards(AxisAlignedBB bb, double x, double y, double z) {
        double d = bb.minX;
        double e = bb.minY;
        double f = bb.minZ;
        double g = bb.maxX;
        double h = bb.maxY;
        double i = bb.maxZ;
        if (x < 0.0) {
            d += x;
        } else if (x > 0.0) {
            g += x;
        }

        if (y < 0.0) {
            e += y;
        } else if (y > 0.0) {
            h += y;
        }

        if (z < 0.0) {
            f += z;
        } else if (z > 0.0) {
            i += z;
        }

        return new AxisAlignedBB(d, e, f, g, h, i);
    }

    @Nullable
    private static Entity getEntityHitResult(Entity shooter, Vec3 startVec, Vec3 endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance) {
        World level = shooter.world;
        double d = distance;
        Entity entity = null;
        Vec3 vec3 = null;

        for(Entity entity2 : level.getEntitiesIn(shooter, boundingBox, filter)) {
            AxisAlignedBB aABB = entity2.getBoundingBox();
            Optional<Vec3> optional = Optional.ofNullable(aABB.method_2060(startVec, endVec)).map(e -> e.pos);
            if (aABB.contains(startVec)) {
                if (d >= 0.0) {
                    entity = entity2;
                    vec3 = optional.orElse(startVec);
                    d = 0.0;
                }
            } else if (optional.isPresent()) {
                Vec3 vec32 = optional.get();
                double e = startVec.squaredDistanceTo(vec32);
                if (e < d || d == 0.0) {
                    if (getRootVehicle(entity2) == getRootVehicle(shooter)) {
                        if (d == 0.0) {
                            entity = entity2;
                            vec3 = vec32;
                        }
                    } else {
                        entity = entity2;
                        vec3 = vec32;
                        d = e;
                    }
                }
            }
        }

        return entity;
    }

    private static Entity getRootVehicle(Entity e) {
        Entity entity = e;
        while(entity.vehicle != null) {
            entity = entity.vehicle;
        }
        return entity;
    }

    /**
     * Write to a sign screen if a sign screen is currently open.
     *
     * @param l1
     * @param l2
     * @param l3
     * @param l4
     * @return of success.
     * @since 1.2.2
     */
    public boolean writeSign(String l1, String l2, String l3, String l4) {
        if (mc.currentScreen instanceof GuiEditSign) {
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(0, l1);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(1, l2);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(2, l3);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(3, l4);
            return true;
        }
        return false;
    }

    /**
     * @param folder
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     * @see #takeScreenshot(String, String, MethodWrapper)
     * @since 1.2.6
     */
    public void takeScreenshot(String folder, MethodWrapper<TextHelper, Object, Object, ?> callback) {
        assert folder != null;
        mc.execute(() -> {
            IChatComponent text = ScreenShotHelper.saveScreenshot(new File(Core.getInstance().config.macroFolder, folder), mc.getFramebuffer().viewportWidth, mc.getFramebuffer().viewportHeight, mc.getFramebuffer());
            if (callback != null) callback.accept(new TextHelper(text));
        });
    }

    public StatsHelper getStatistics() {
        assert mc.player != null;
        return new StatsHelper(mc.player.getStatHandler());
    }

    /**
     * Take a screenshot and save to a file.
     * <p>
     * {@code file} is the optional one, typescript doesn't like it not being the last one that's optional
     *
     * @param folder
     * @param file
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     * @since 1.2.6
     */
    public void takeScreenshot(String folder, String file, MethodWrapper<TextHelper, Object, Object, ?> callback) {
        assert folder != null && file != null;
        mc.execute(() -> {
            IChatComponent text = ScreenShotHelper.saveScreenshot(new File(Core.getInstance().config.macroFolder, folder), file, mc.getFramebuffer().viewportWidth, mc.getFramebuffer().viewportHeight, mc.getFramebuffer());
            if (callback != null) callback.accept(new TextHelper(text));
        });
    }

    /**
     * Creates a new PlayerInput object.
     *
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput createPlayerInput() {
        return new PlayerInput();
    }

    /**
     * Creates a new PlayerInput object.
     *
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput createPlayerInput(double movementForward, double movementSideways, double yaw) {
        return new PlayerInput((float)movementForward, (float)movementSideways, (float)yaw);
    }

    /**
     * Creates a new PlayerInput object.
     *
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput createPlayerInput(double movementForward, double yaw, boolean jumping, boolean sprinting) {
        return new PlayerInput((float)movementForward, (float)yaw, jumping, sprinting);
    }

    /**
     * Creates a new PlayerInput object.
     * @param movementForward  1 = forward input (W); 0 = no input; -1 = backward input (S)
     * @param movementSideways 1 = left input (A); 0 = no input; -1 = right input (D)
     * @param yaw              yaw of the player
     * @param pitch            pitch of the player
     * @param jumping          jump input
     * @param sneaking         sneak input
     * @param sprinting        sprint input
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput createPlayerInput(double movementForward, double movementSideways, double yaw, double pitch, boolean jumping, boolean sneaking, boolean sprinting) {
        return new PlayerInput(movementForward, movementSideways, yaw, pitch, jumping, sneaking, sprinting);
    }


    /**
     * Parses each row of CSV string into a {@code PlayerInput}.
     * The capitalization of the header matters.<br>
     * About the columns:
     * <ul>
     *   <li> {@code movementForward} and {@code movementSideways} as a number</li>
     *   <li>{@code yaw} and {@code pitch} as an absolute number</li>
     *   <li>{@code jumping}, {@code sneaking} and {@code sprinting} have to be boolean</li>
     * </ul>
     * <p>
     * The separation must be a "," it's a csv...(but spaces don't matter)<br>
     * Quoted values don't work
     *
     * @param csv CSV string to be parsed
     * @see PlayerInput#PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public List<PlayerInput> createPlayerInputsFromCsv(String csv) throws NoSuchFieldException, IllegalAccessException {
        return PlayerInput.fromCsv(csv);
    }

    /**
     * Parses a JSON string into a {@code PlayerInput} Object
     * For details see {@code PlayerInput.fromCsv()}, on what has to be present.<br>
     * Capitalization of the keys matters.
     *
     * @param json JSON string to be parsed
     * @return The JSON parsed into a {@code PlayerInput}
     * @see #createPlayerInputsFromCsv(String)
     * @since 1.4.0
     */
    public PlayerInput createPlayerInputsFromJson(String json) {
        return PlayerInput.fromJson(json);
    }

    /**
     * Creates a new {@code PlayerInput} object with the current inputs of the player.
     *
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.4.0
     */
    public PlayerInput getCurrentPlayerInput() {
        assert mc.player != null;
        return new PlayerInput(mc.player.input, mc.player.yaw, mc.player.pitch, mc.player.isSprinting());
    }

    /**
     * Adds a new {@code PlayerInput} to {@code MovementQueue} to be executed
     *
     * @param input the PlayerInput to be executed
     * @see xyz.wagyourtail.jsmacros.client.movement.MovementQueue
     * @since 1.4.0
     */
    public void addInput(PlayerInput input) {
        MovementQueue.append(input, mc.player);
    }

    /**
     * Adds multiple new {@code PlayerInput} to {@code MovementQueue} to be executed
     *
     * @param inputs the PlayerInputs to be executed
     * @see xyz.wagyourtail.jsmacros.client.movement.MovementQueue
     * @since 1.4.0
     */
    public void addInputs(PlayerInput[] inputs) {
        for (PlayerInput input : inputs) {
            addInput(input);
        }
    }

    /**
     * Clears all inputs in the {@code MovementQueue}
     *
     * @see xyz.wagyourtail.jsmacros.client.movement.MovementQueue
     * @since 1.4.0
     */
    public void clearInputs() {
        MovementQueue.clear();
    }

    public void setDrawPredictions(boolean val) {
        MovementQueue.setDrawPredictions(val);
    }

    /**
     * Predicts where one tick with a {@code PlayerInput} as input would lead to.
     *
     * @param input the PlayerInput for the prediction
     * @return the position after the input
     * @see #predictInput(PlayerInput, boolean)
     * @since 1.4.0
     */
    public PositionCommon.Pos3D predictInput(PlayerInput input) {
        return predictInput(input, false);
    }

    /**
     * Predicts where one tick with a {@code PlayerInput} as input would lead to.
     *
     * @param input the PlayerInput for the prediction
     * @param draw  whether to visualize the result or not
     * @return the position after the input
     * @since 1.4.0
     */
    public PositionCommon.Pos3D predictInput(PlayerInput input, boolean draw) {
        return predictInputs(new PlayerInput[]{input}, draw).get(0);
    }

    /**
     * Predicts where each {@code PlayerInput} executed in a row would lead
     * without drawing it.
     *
     * @param inputs the PlayerInputs for each tick for the prediction
     * @return the position after each input
     * @see #predictInputs(PlayerInput[], boolean)
     * @since 1.4.0
     */
    public List<PositionCommon.Pos3D> predictInputs(PlayerInput[] inputs) {
        return predictInputs(inputs, false);
    }

    /**
     * @since 1.8.0
     * @return
     */
    public boolean isBreakingBlock() {
        assert mc.interactionManager != null;
        return mc.interactionManager.isBreakingBlock();
    }

    /**
     * Predicts where each {@code PlayerInput} executed in a row would lead
     *
     * @param inputs the PlayerInputs for each tick for the prediction
     * @param draw   whether to visualize the result or not
     * @return the position after each input
     * @since 1.4.0
     */
    public List<PositionCommon.Pos3D> predictInputs(PlayerInput[] inputs, boolean draw) {
        assert mc.player != null;
        MovementDummy dummy = new MovementDummy(mc.player);
        List<PositionCommon.Pos3D> predictions = new ArrayList<>();
        for (PlayerInput input : inputs) {
            predictions.add(new PositionCommon.Pos3D(dummy.applyInput(input)));
        }
        if (draw) {
            for (PositionCommon.Pos3D point : predictions) {
                MovementQueue.predPoints.addPoint(point, 0.01, 0xff9500);
            }
        }
        return predictions;
    }

    /**
     * Adds a forward movement with a relative yaw value to the MovementQueue.
     *
     * @param yaw the relative yaw for the player
     * @since 1.4.0
     */
    public void moveForward(float yaw) {
        PlayerInput input = new PlayerInput();
        input.movementForward = 1.0F;
        input.yaw = getPlayer().getYaw() + yaw;
        addInput(input);
    }

    /**
     * Adds a backward movement with a relative yaw value to the MovementQueue.
     *
     * @param yaw the relative yaw for the player
     * @since 1.4.0
     */
    public void moveBackward(float yaw) {
        PlayerInput input = new PlayerInput();
        input.movementForward = -1.0F;
        input.yaw = getPlayer().getYaw() + yaw;
        addInput(input);
    }

    /**
     * Adds sideways movement with a relative yaw value to the MovementQueue.
     * @param yaw the relative yaw for the player
     * @since 1.4.2
     */
    public void moveStrafeLeft(float yaw) {
        PlayerInput input = new PlayerInput();
        input.movementSideways = 1.0F;
        input.yaw = getPlayer().getYaw() + yaw;
        addInput(input);
    }

    /**
     * Adds sideways movement with a relative yaw value to the MovementQueue.
     * @param yaw the relative yaw for the player
     * @since 1.4.2
     */
    public void moveStrafeRight(float yaw) {
        PlayerInput input = new PlayerInput();
        input.movementSideways = -1.0F;
        input.yaw = getPlayer().getYaw() + yaw;
        addInput(input);
    }
}
