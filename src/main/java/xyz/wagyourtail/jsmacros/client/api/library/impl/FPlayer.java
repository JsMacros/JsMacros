package xyz.wagyourtail.jsmacros.client.api.library.impl;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.movement.MovementDummy;
import xyz.wagyourtail.jsmacros.client.movement.MovementQueue;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * 
 * Functions for getting and modifying the player's state.
 * 
 * An instance of this class is passed to scripts as the {@code Player} variable.
 * 
 * @author Wagyourtail
 */
@Library("Player")
@SuppressWarnings("unused")
public class FPlayer extends BaseLibrary {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.classes.Inventory
     * 
     * @return the Inventory handler
     */
    public Inventory openInventory() {
        assert mc.player != null && mc.player.inventory != null;
        return Inventory.create();
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper
     * 
     * @since 1.0.3
     * 
     * @return the player entity wrapper.
     */
    public ClientPlayerEntityHelper<ClientPlayerEntity> getPlayer() {
        assert mc.player != null;
        return new ClientPlayerEntityHelper<>(mc.player);
    }

    /**
     * @since 1.0.9
     * 
     * @return the player's current gamemode.
     */
    public String getGameMode() {
        assert mc.interactionManager != null;
        GameMode mode = mc.interactionManager.getCurrentGameMode();
        if (mode == null) mode = GameMode.NOT_SET;
        return mode.getName();
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper
     * 
     * @since 1.0.5
     * 
     * @param distance
     * @param fluid
     * @return the block/liquid the player is currently looking at.
     */
    public BlockDataHelper rayTraceBlock(double distance, boolean fluid) {
        assert mc.world != null;
        assert mc.player != null;
        BlockHitResult h = (BlockHitResult) mc.player.raycast(distance, 0, fluid);
        if (h.getType() == HitResult.Type.MISS) return null;
        BlockState b = mc.world.getBlockState(h.getBlockPos());
        BlockEntity t = mc.world.getBlockEntity(h.getBlockPos());
        if (b.getBlock().equals(Blocks.VOID_AIR)) return null;
        return new BlockDataHelper(b, t, h.getBlockPos());
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper
     * 
     * @since 1.0.5
     * 
     * @return the entity the player is currently looking at.
     */
    public EntityHelper<Entity> rayTraceEntity() {
        if (mc.targetedEntity != null) return new EntityHelper<>(mc.targetedEntity);
        else return null;
    }

    /**
     * Write to a sign screen if a sign screen is currently open.
     * 
     * @since 1.2.2
     * 
     * @param l1
     * @param l2
     * @param l3
     * @param l4
     * @return {@link java.lang.Boolean boolean} of success.
     */
    public boolean writeSign(String l1, String l2, String l3, String l4) {
        if (mc.currentScreen instanceof SignEditScreen) {
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(0, l1);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(1, l2);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(2, l3);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(3, l4);
            return true;
        }
        return false;
    }

    /**
     * @see #takeScreenshot(String, String, MethodWrapper)
     *
     * @since 1.2.6
     * @param folder
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     */
    public void takeScreenshot(String folder, MethodWrapper<TextHelper, Object, Object> callback) {
        assert folder != null;
        ScreenshotUtils.saveScreenshot(new File(Core.instance.config.macroFolder, folder), mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
            mc.getFramebuffer(), (text) -> {
                if (callback != null) callback.accept(new TextHelper(text));
            });
    }
    
    /**
     * Take a screenshot and save to a file.
     *
     * {@code file} is the optional one, typescript doesn't like it not being the last one that's optional
     *
     * @since 1.2.6
     * 
     * @param folder
     * @param file
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     */
    public void takeScreenshot(String folder, String file, MethodWrapper<TextHelper, Object, Object> callback) {
        assert folder != null && file != null;
        ScreenshotUtils.saveScreenshot(new File(Core.instance.config.macroFolder, folder), file, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
            mc.getFramebuffer(), (text) -> {
                if (callback != null) callback.accept(new TextHelper(text));
            });
    }

    /**
     * Creates a new PlayerInput object.
     *
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput PlayerInput}.
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.3.2
     */
    public PlayerInput createPlayerInput() {
        return new PlayerInput();
    }

    /**
     * Creates a new {@code PlayerInput} object with the current inputs of the player.
     *
     * @return a new {@link xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput PlayerInput} with the current inputs.
     * @see xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput
     * @since 1.3.2
     */
    public PlayerInput getCurrentPlayerInput() {
        return new PlayerInput(mc.player.input, mc.player.yaw, mc.player.pitch, mc.player.isSprinting());
    }

    /**
     * Adds a new {@code PlayerInput} to {@code MovementQueue} to be executed
     *
     * @param input the PlayerInput to be executed
     * @see xyz.wagyourtail.jsmacros.client.movement.MovementQueue
     * @since 1.3.2
     */
    public void addInput(PlayerInput input) {
        MovementQueue.append(input, mc.player);
    }

    /**
     * Adds multiple new {@code PlayerInput} to {@code MovementQueue} to be executed
     *
     * @param inputs the PlayerInputs to be executed
     * @see xyz.wagyourtail.jsmacros.client.movement.MovementQueue
     * @since 1.3.2
     */
    public void addInputs(List<PlayerInput> inputs) {
        for (PlayerInput input : inputs) {
            addInput(input);
        }
    }

    /**
     * Predicts where one tick with a {@code PlayerInput} as input would lead to.
     *
     * @param input the PlayerInput for the prediction
     * @param draw  whether to visualize the result or not
     * @return the position after the input
     * @since 1.3.2
     */
    public Vec3d predictInput(PlayerInput input, boolean draw) {
        return predictInputs(Arrays.asList(input), draw).get(0);
    }

    /**
     * Predicts where each {@code PlayerInput} executed in a row would lead
     *
     * @param inputs the PlayerInputs for each tick for the prediction
     * @param draw   whether to visualize the result or not
     * @return the position after each input
     * @since 1.3.2
     */
    public List<Vec3d> predictInputs(List<PlayerInput> inputs, boolean draw) {
        MovementDummy dummy = new MovementDummy(mc.player);
        List<Vec3d> predictions = new ArrayList<>();
        for (PlayerInput input : inputs) {
            predictions.add(dummy.applyInput(input));
        }
        if (draw) {
            Draw3D predPoints = new Draw3D();
            for (Vec3d point : predictions) {
                predPoints.addPoint(point, 0.01, 0xff9500);

            }
            synchronized (FHud.renders) {
                FHud.renders.add(predPoints);
            }
        }
        return predictions;
    }

    /**
     * Adds a forward movement with a relative yaw value to the MovementQueue.
     *
     * @param yaw the relative yaw for the player
     * @since 1.3.2
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
     * @since 1.3.2
     */
    public void moveBackward(float yaw) {
        PlayerInput input = new PlayerInput();
        input.movementForward = -1.0F;
        input.yaw = getPlayer().getYaw() + yaw;
        addInput(input);
    }
}
