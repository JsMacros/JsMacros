package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletEnumType;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.access.IItemCooldownEntry;
import xyz.wagyourtail.jsmacros.client.access.IItemCooldownManager;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.classes.InteractionProxy;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Vec3D;
import xyz.wagyourtail.jsmacros.client.api.helpers.AdvancementManagerHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FClient;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @see PlayerEntityHelper
 * @since 1.0.3
 */
@SuppressWarnings("unused")
public class ClientPlayerEntityHelper<T extends ClientPlayerEntity> extends PlayerEntityHelper<T> {
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    public ClientPlayerEntityHelper(T e) {
        super(e);
    }

    /**
     * @since 1.8.4
     */
    private ClientPlayerEntityHelper<T> setVelocity(Vec3d velocity) {
        base.setVelocity(velocity);
        return this;
    }

    /**
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> setVelocity(Pos3D velocity) {
        return setVelocity(velocity.toMojangDoubleVector());
    }

    /**
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> setVelocity(double x, double y, double z) {
        return setVelocity(new Vec3d(x, y, z));
    }

    /**
     * @since 1.8.4
     */
    private ClientPlayerEntityHelper<T> addVelocity(Vec3d velocity) {
        base.addVelocity(velocity);
        return this;
    }

    /**
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> addVelocity(Pos3D velocity) {
        return addVelocity(velocity.toMojangDoubleVector());
    }

    /**
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> addVelocity(double x, double y, double z) {
        return addVelocity(new Vec3d(x, y, z));
    }

    /**
     * @since 1.8.4
     */
    private ClientPlayerEntityHelper<T> setPos(Vec3d pos) {
        base.setPosition(pos);
        return this;
    }

    /**
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> setPos(Pos3D pos) {
        return setPos(pos.toMojangDoubleVector());
    }

    /**
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> setPos(double x, double y, double z) {
        return setPos(new Vec3d(x, y, z));
    }

    /**
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> addPos(Pos3D pos) {
        return setPos(getPos().add(pos));
    }

    /**
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> addPos(double x, double y, double z) {
        return setPos(getPos().add(new Pos3D(x, y, z)));
    }

    /**
     * Sets the player rotation along the given axis and keeps the other axis the same.
     *
     * @param direction possible values are "up", "down", "north", "south", "east", "west"
     * @return self for chaining.
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> lookAt(String direction) {
        Direction dir = Direction.byName(direction.toLowerCase(Locale.ROOT));
        double yaw = getYaw();
        double pitch = getPitch();
        if (dir.getAxis().isHorizontal()) {
            yaw = dir.asRotation();
        } else {
            pitch = dir == Direction.UP ? -90 : 90;
        }
        return lookAt(yaw, pitch);
    }

    /**
     * @param yaw   (was pitch prior to 1.2.6)
     * @param pitch (was yaw prior to 1.2.6)
     * @return
     * @since 1.0.3
     */
    public ClientPlayerEntityHelper<T> lookAt(double yaw, double pitch) {
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        base.prevPitch = base.getPitch();
        base.prevYaw = base.getYaw();
        base.setPitch((float) pitch);
        base.setYaw(MathHelper.wrapDegrees((float) yaw));
        if (base.getVehicle() != null) {
            base.getVehicle().onPassengerLookAround(base);
        }
        return this;
    }

    /**
     * look at the specified coordinates.
     *
     * @param x
     * @param y
     * @param z
     * @return
     * @since 1.2.8
     */
    public ClientPlayerEntityHelper<T> lookAt(double x, double y, double z) {
        Vec3D vec = new Vec3D(base.getX(), base.getY() + base.getEyeHeight(base.getPose()), base.getZ(), x, y, z);
        lookAt(vec.getYaw(), vec.getPitch());
        return this;
    }

    /**
     * @param x the x coordinate of the block to look at
     * @param y the y coordinate of the block to look at
     * @param z the z coordinate of the block to look at
     * @return {@code true} if the player is targeting the specified block, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean tryLookAt(int x, int y, int z) {
        return tryLookAt(new BlockPosHelper(x, y, z));
    }

    /**
     * Will try many rotations to find one that will make the player target the specified block. If
     * successful, the player will be turned towards the block and {@code true} will be returned. If
     * {@code false} is returned, the player will keep its current rotation.
     *
     * @param pos the position of the block to look at
     * @return {@code true} if the player is targeting the specified block, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean tryLookAt(BlockPosHelper pos) {
        BlockState state = MinecraftClient.getInstance().world.getBlockState(pos.getRaw());
        VoxelShape shape = state.getOutlineShape(MinecraftClient.getInstance().world, pos.getRaw());
        if (shape.isEmpty()) {
            return false;
        }
        Pos3D eyePos = getEyePos();
        double distance = base.getEyePos().distanceTo(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));

        List<Box> bounds = shape.getBoundingBoxes().stream().map(b -> b.offset(pos.getRaw())).collect(Collectors.toList());
        // Scale offset with distance to the target. Closer targets should have more rays to find a possible angle
        double offset = Math.min(0.25, 0.01 * Math.max(distance, 0.5));
        for (Box bound : bounds) {
            Vec3d center = bound.getCenter();
            double xDiff = (bound.maxX - bound.minX) / 2;
            double yDiff = (bound.maxY - bound.minY) / 2;
            double zDiff = (bound.maxZ - bound.minZ) / 2;
            // Round the offsets down so they perfectly fit the bounds
            double xOffset = xDiff / Math.ceil(xDiff / offset);
            double yOffset = yDiff / Math.ceil(yDiff / offset);
            double zOffset = zDiff / Math.ceil(zDiff / offset);
            // Iterate alternating around the center to iterate outwards which will give more pleasing results
            for (int yc = 0; yc < (int) (yDiff / yOffset) * 2 + 2; yc++) {
                for (int xc = 0; xc < (int) (xDiff / xOffset) * 2 + 2; xc++) {
                    for (int zc = 0; zc < (int) (zDiff / zOffset) * 2 + 2; zc++) {
                        // Don't remove the integer division, because we want to round down the value
                        // The 0.999 helps with edge cases, literally
                        double x = center.x + ((xc & 1) == 0 ? 1 : -1) * xOffset * (xc / 2) * 0.999;
                        double y = center.y + ((yc & 1) == 0 ? 1 : -1) * yOffset * (yc / 2) * 0.999;
                        double z = center.z + ((zc & 1) == 0 ? 1 : -1) * zOffset * (zc / 2) * 0.999;
                        BlockHitResult result = base.getWorld().raycast(new RaycastContext(base.getEyePos(), new Vec3d(x, y, z), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, base));
                        if (result.getType() == HitResult.Type.BLOCK && result.getBlockPos().equals(pos.getRaw())) {
                            Vec3D vec = new Vec3D(eyePos, new Pos3D(x, y, z));
                            lookAt(vec.getYaw(), vec.getPitch());
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> turnLeft() {
        return lookAt(getYaw() - 90, getPitch());
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> turnRight() {
        return lookAt(getYaw() + 90, getPitch());
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<T> turnBack() {
        return lookAt(getYaw() + 180, getPitch());
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> setTarget(int x, int y, int z) {
        setTarget(x, y, z, 0);
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Direction")
    @DocletEnumType(name = "Direction", type = "'up' | 'down' | 'north' | 'south' | 'east' | 'west'")
    public ClientPlayerEntityHelper<T> setTarget(int x, int y, int z, String direction) {
        setTarget(x, y, z, Direction.byName(direction.toLowerCase(Locale.ROOT)).getId());
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Hexit")
    public ClientPlayerEntityHelper<T> setTarget(int x, int y, int z, int direction) {
        InteractionProxy.Target.setTargetBlock(new BlockPos(x, y, z), direction);
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> setTarget(BlockPosHelper pos) {
        setTarget(pos, 0);
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    @DocletReplaceParams("bpos: BlockPosHelper, direction: Direction")
    public ClientPlayerEntityHelper<T> setTarget(BlockPosHelper pos, String direction) {
        setTarget(pos, Direction.byName(direction.toLowerCase(Locale.ROOT)).getId());
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    @DocletReplaceParams("bpos: BlockPosHelper, direction: Hexit")
    public ClientPlayerEntityHelper<T> setTarget(BlockPosHelper pos, int direction) {
        InteractionProxy.Target.setTargetBlock(pos.getRaw(), direction);
        return this;
    }

    /**
     * sets crosshair target to an entity
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> setTarget(EntityHelper<?> entity) {
        if (!entity.getRaw().canHit()) throw new AssertionError(String.format("Can't target not-hittable entity! (%s)", entity.getType()));
        if (entity.getRaw() == mc.player) throw new AssertionError("Can't target self!");
        InteractionProxy.Target.setTarget(new EntityHitResult(entity.getRaw()));
        return this;
    }

    /**
     * @return targeted block pos, null if not targeting block
     * @since 1.9.0
     */
    public @Nullable BlockPosHelper getTargetedBlock() {
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            return new BlockPosHelper(((BlockHitResult) mc.crosshairTarget).getBlockPos());
        }
        return null;
    }

    /**
     * @return targeted entity, null if not targeting entity
     * @since 1.9.0
     */
    public @Nullable EntityHelper<?> getTargetedEntity() {
        if (mc.targetedEntity != null) {
            return EntityHelper.create(mc.targetedEntity);
        }
        return null;
    }

    /**
     * sets crosshair target to missed (doesn't target anything)
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> setTargetMissed() {
        InteractionProxy.Target.setTargetMissed();
        return this;
    }

    /**
     * clears target override
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> clearTargetOverride() {
        InteractionProxy.Target.setTarget(null);
        return this;
    }

    /**
     * @param enabled if target overriding should check range. default is {@code true}
     * @param autoClear if override should clear when out of range.
     *                  if {@code false}, target will set to missed if out of range. default is {@code true}
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> setTargetRangeCheck(boolean enabled, boolean autoClear) {
        InteractionProxy.Target.checkDistance = enabled;
        InteractionProxy.Target.clearIfOutOfRange = autoClear;
        return this;
    }

    /**
     * @param enabled if target overriding should check air. default is {@code false}
     * @param autoClear if override should clear when is air.
     *                  if {@code false}, target will set to missed if is air. default is {@code false}
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> setTargetAirCheck(boolean enabled, boolean autoClear) {
        InteractionProxy.Target.checkAir = enabled;
        InteractionProxy.Target.clearIfIsAir = autoClear;
        return this;
    }

    /**
     * this check ignores air. use {@code ClientPlayerEntityHelper#setTargetAirCheck()} to check air.
     * @param enabled if target overriding should check block shape. default is {@code true}
     * @param autoClear if override should clear when shape is empty.
     *                  if {@code false}, target will set to missed if is empty. default is {@code false}
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> setTargetShapeCheck(boolean enabled, boolean autoClear) {
        InteractionProxy.Target.checkShape = enabled;
        InteractionProxy.Target.clearIfEmptyShape = autoClear;
        return this;
    }

    /**
     * resets all range, air and shape check settings to default.
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> resetTargetChecks() {
        InteractionProxy.Target.resetChecks();
        return this;
    }

    /**
     * @return {@code true} if target have been set by {@code ClientPlayerEntityHelper#setTarget()} or
     *  {@code ClientPlayerEntityHelper#setTargetMissed()}
     * @since 1.9.0
     */
    public boolean hasTargetOverride() {
        return InteractionProxy.Target.hasOverride();
    }

    /**
     * breaks a block, will wait till it's done<br>
     * you can use {@code ClientPlayerEntityHelper#setTarget()} to specify which block to break
     * @return result
     * @see ClientPlayerEntityHelper#setTarget(int, int, int, String)
     * @throws InterruptedException
     * @since 1.9.0
     */
    public InteractionProxy.Break.BreakBlockResult breakBlock() throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined to main!");
        }

        final InteractionProxy.Break.BreakBlockResult[] ret = {null};
        Semaphore wait = new Semaphore(0);

        InteractionProxy.Break.addCallback(res -> {
            ret[0] = res;
            wait.release();
        });
        InteractionProxy.Break.setOverride(true);
        preBreakBlock();

        wait.acquire();
        return ret[0];
    }

    /**
     * breaks a block, will wait till it's done<br>
     * this is the same as:
     * <pre>
     * setTarget(x, y, z);
     * let res = null;
     * if (getTargetedBlock()?.getRaw().equals(new BlockPos(x, y, z))) res = breakBlock();
     * clearTargetOverride();
     * return res;
     * </pre>
     * @return result
     * @throws InterruptedException
     * @since 1.9.0
     */
    public @Nullable InteractionProxy.Break.BreakBlockResult breakBlock(int x, int y, int z) throws InterruptedException {
        return breakBlock(new BlockPos(x, y, z));
    }

    /**
     * breaks a block, will wait till it's done<br>
     * this is the same as:
     * <pre>
     * setTarget(pos);
     * let res = null;
     * if (getTargetedBlock()?.equals(pos)) res = breakBlock();
     * clearTargetOverride();
     * return res;
     * </pre>
     * @return result
     * @throws InterruptedException
     * @since 1.9.0
     */
    public @Nullable InteractionProxy.Break.BreakBlockResult breakBlock(BlockPosHelper pos) throws InterruptedException {
        return breakBlock(pos.getRaw());
    }

    private @Nullable InteractionProxy.Break.BreakBlockResult breakBlock(BlockPos pos) throws InterruptedException {
        InteractionProxy.Target.setTargetBlock(pos, 0);
        InteractionProxy.Break.BreakBlockResult res = null;
        BlockPosHelper pos2 = getTargetedBlock();
        if (pos2 != null && pos2.getRaw().equals(pos)) res = breakBlock();
        clearTargetOverride();
        return res;
    }

    /**
     * starts breaking a block<br>
     * you can use {@code ClientPlayerEntityHelper#setTarget()} to specify which block to break
     * @param callback this will mostly be called on main thread!
     *                 Use {@code methodToJavaAsync()} instead of {@code methodToJava()} to avoid errors.
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> breakBlockAsync(@Nullable MethodWrapper<InteractionProxy.Break.BreakBlockResult, Object, ?, ?> callback) throws InterruptedException {
        InteractionProxy.Break.addCallback(callback);
        InteractionProxy.Break.setOverride(true);
        preBreakBlock();
        return this;
    }

    private void preBreakBlock() throws InterruptedException {
        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) return;
        BlockHitResult target = (BlockHitResult) mc.crosshairTarget;
        BlockPos pos = target.getBlockPos();
        attack(pos.getX(), pos.getY(), pos.getZ(), target.getSide().getId(), true);
    }

    /**
     * cancels breaking block that previously started by {@code ClientPlayerEntityHelper#breakBlock()} or
     *  {@code ClientPlayerEntityHelper#breakBlockAsync()}
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> cancelBreakBlock() {
        InteractionProxy.Break.setOverride(false, "CANCELLED");
        return this;
    }

    /**
     * @return {@code true} if there's not finished block breaking from {@code ClientPlayerEntityHelper#breakBlock()}
     * @since 1.9.0
     */
    public boolean hasBreakBlockOverride() {
        return InteractionProxy.Break.isBreaking();
    }

    /**
     * starts/stops long interact
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> holdInteract(boolean holding) throws InterruptedException {
        return holdInteract(holding, false);
    }

    /**
     * starts/stops long interact
     * @return self for chaining
     * @since 1.9.0
     */
    public ClientPlayerEntityHelper<T> holdInteract(boolean holding, boolean awaitFirstClick) throws InterruptedException {
        if (!holding) {
            InteractionProxy.Interact.setOverride(false);
            return this;
        }
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            InteractionProxy.Interact.setOverride(true);
        } else {
            Semaphore wait = new Semaphore(awaitFirstClick ? 0 : 1);
            mc.execute(() -> {
                InteractionProxy.Interact.setOverride(true);
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * interacts for specified number of ticks
     * @return remaining ticks if the interaction was interrupted
     * @throws InterruptedException
     * @since 1.9.0
     */
    public int holdInteract(int ticks) throws InterruptedException {
        return holdInteract(ticks, true);
    }

    /**
     * interacts for specified number of ticks
     * @param stopOnPause if {@code false}, this interaction will not return when interrupted by pause.
     *                    the timer will not decrease, meaning it'll continue right after unpause and interact exact amount of ticks.
     * @return remaining ticks if the interaction was interrupted
     * @throws InterruptedException
     * @since 1.9.0
     */
    public int holdInteract(int ticks, boolean stopOnPause) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined to main!");
        }

        holdInteract(true, true);
        while (ticks > 0) {
            FClient.tickSynchronizer.waitTick();
            if (!InteractionProxy.Interact.isInteracting()) break;
            if (!mc.isPaused()) ticks--;
            else if (stopOnPause) break;
        }
        holdInteract(false);
        return ticks;
    }

    /**
     * @return {@code true} if interaction from {@code ClientPlayerEntityHelper#holdInteract()} is active
     * @since 1.9.0
     */
    public boolean hasInteractOverride() {
        return InteractionProxy.Interact.isInteracting();
    }

    /**
     * @param entity
     * @since 1.5.0
     */
    public ClientPlayerEntityHelper<T> attack(EntityHelper<?> entity) throws InterruptedException {
        return attack(entity, false);
    }

    /**
     * @param await
     * @param entity
     * @since 1.6.0
     */
    public ClientPlayerEntityHelper<T> attack(EntityHelper<?> entity, boolean await) throws InterruptedException {
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        assert mc.interactionManager != null;
        if (entity.getRaw() == mc.player) {
            throw new AssertionError("Can't interact with self!");
        }
        if (joinedMain) {
            mc.interactionManager.attackEntity(mc.player, entity.getRaw());
            assert mc.player != null;
            mc.player.swingHand(Hand.MAIN_HAND);
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                mc.interactionManager.attackEntity(mc.player, entity.getRaw());
                assert mc.player != null;
                mc.player.swingHand(Hand.MAIN_HAND);
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param x         the x coordinate to attack
     * @param y         the y coordinate to attack
     * @param z         the z coordinate to attack
     * @param direction possible values are "up", "down", "north", "south", "east", "west"
     * @return self for chaining.
     * @since 1.8.4
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Direction")
    public ClientPlayerEntityHelper<T> attack(int x, int y, int z, String direction) throws InterruptedException {
        return attack(x, y, z, Direction.byName(direction.toLowerCase(Locale.ROOT)).getId(), false);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @since 1.5.0
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Hexit")
    public ClientPlayerEntityHelper<T> attack(int x, int y, int z, int direction) throws InterruptedException {
        return attack(x, y, z, direction, false);
    }

    /**
     * @param x         the x coordinate to attack
     * @param y         the y coordinate to attack
     * @param z         the z coordinate to attack
     * @param direction possible values are "up", "down", "north", "south", "east", "west"
     * @param await     whether to wait for the attack to finish
     * @return self for chaining.
     * @since 1.8.4
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Direction, await: boolean")
    public ClientPlayerEntityHelper<T> attack(int x, int y, int z, String direction, boolean await) throws InterruptedException {
        return attack(x, y, z, Direction.byName(direction.toLowerCase(Locale.ROOT)).getId(), await);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Hexit, await: boolean")
    public ClientPlayerEntityHelper<T> attack(int x, int y, int z, int direction, boolean await) throws InterruptedException {
        assert mc.interactionManager != null;
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            mc.interactionManager.attackBlock(new BlockPos(x, y, z), Direction.values()[direction]);
            assert mc.player != null;
            mc.player.swingHand(Hand.MAIN_HAND);
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                mc.interactionManager.attackBlock(new BlockPos(x, y, z), Direction.values()[direction]);
                assert mc.player != null;
                mc.player.swingHand(Hand.MAIN_HAND);
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param entity
     * @param offHand
     * @since 1.5.0, renamed from {@code interact} in 1.6.0
     */
    public ClientPlayerEntityHelper<T> interactEntity(EntityHelper<?> entity, boolean offHand) throws InterruptedException {
        return interactEntity(entity, offHand, false);
    }

    /**
     * @param entity
     * @param offHand
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    public ClientPlayerEntityHelper<T> interactEntity(EntityHelper<?> entity, boolean offHand, boolean await) throws InterruptedException {
        assert mc.interactionManager != null;
        if (entity.getRaw() == mc.player) {
            throw new AssertionError("Can't interact with self!");
        }
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            ActionResult result = mc.interactionManager.interactEntity(mc.player, entity.getRaw(), hand);
            assert mc.player != null;
            if (result.isAccepted()) {
                mc.player.swingHand(hand);
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ActionResult result = mc.interactionManager.interactEntity(mc.player, entity.getRaw(), hand);
                assert mc.player != null;
                if (result.isAccepted()) {
                    mc.player.swingHand(hand);
                }
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param offHand
     * @since 1.5.0, renamed from {@code interact} in 1.6.0
     */
    public ClientPlayerEntityHelper<T> interactItem(boolean offHand) throws InterruptedException {
        return interactItem(offHand, false);
    }

    /**
     * @param offHand
     * @param await
     * @since 1.6.0
     */
    public ClientPlayerEntityHelper<T> interactItem(boolean offHand, boolean await) throws InterruptedException {
        assert mc.interactionManager != null;
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            ActionResult result = mc.interactionManager.interactItem(mc.player, hand);
            assert mc.player != null;
            if (result.isAccepted()) {
                mc.player.swingHand(hand);
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ActionResult result = mc.interactionManager.interactItem(mc.player, hand);
                assert mc.player != null;
                if (result.isAccepted()) {
                    mc.player.swingHand(hand);
                }
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param x         the x coordinate to interact
     * @param y         the y coordinate to interact
     * @param z         the z coordinate to interact
     * @param direction possible values are "up", "down", "north", "south", "east", "west"
     * @return self for chaining.
     * @since 1.8.4
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Direction, offHand: boolean")
    public ClientPlayerEntityHelper<T> interactBlock(int x, int y, int z, String direction, boolean offHand) throws InterruptedException {
        return interactBlock(x, y, z, Direction.byName(direction.toLowerCase(Locale.ROOT)).getId(), offHand, false);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @param offHand
     * @since 1.5.0, renamed from {@code interact} in 1.6.0
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Hexit, offHand: boolean")
    public ClientPlayerEntityHelper<T> interactBlock(int x, int y, int z, int direction, boolean offHand) throws InterruptedException {
        return interactBlock(x, y, z, direction, offHand, false);
    }

    /**
     * @param x         the x coordinate to interact
     * @param y         the y coordinate to interact
     * @param z         the z coordinate to interact
     * @param direction possible values are "up", "down", "north", "south", "east", "west"
     * @param await     whether to wait for the interaction to complete
     * @return self for chaining.
     * @since 1.8.4
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Direction, offHand: boolean, await: boolean")
    public ClientPlayerEntityHelper<T> interactBlock(int x, int y, int z, String direction, boolean offHand, boolean await) throws InterruptedException {
        return interactBlock(x, y, z, Direction.byName(direction.toLowerCase(Locale.ROOT)).getId(), offHand, await);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @param offHand
     * @param await     whether to wait for the interaction to complete
     * @since 1.5.0, renamed from {@code interact} in 1.6.0
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Hexit, offHand: boolean, await: boolean")
    public ClientPlayerEntityHelper<T> interactBlock(int x, int y, int z, int direction, boolean offHand, boolean await) throws InterruptedException {
        assert mc.interactionManager != null;
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            ActionResult result = mc.interactionManager.interactBlock(mc.player, hand,
                    new BlockHitResult(new Vec3d(x, y, z), Direction.values()[direction], new BlockPos(x, y, z), false)
            );
            assert mc.player != null;
            if (result.isAccepted()) {
                mc.player.swingHand(hand);
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ActionResult result = mc.interactionManager.interactBlock(mc.player, hand,
                        new BlockHitResult(new Vec3d(x, y, z), Direction.values()[direction], new BlockPos(x, y, z), false)
                );
                assert mc.player != null;
                if (result.isAccepted()) {
                    mc.player.swingHand(hand);
                }
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @since 1.5.0
     */
    public ClientPlayerEntityHelper<T> interact() throws InterruptedException {
        return interact(false);
    }

    /**
     * @param await
     * @since 1.6.0
     */
    public ClientPlayerEntityHelper<T> interact(boolean await) throws InterruptedException {
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            ((IMinecraftClient) mc).jsmacros_doItemUse();
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ((IMinecraftClient) mc).jsmacros_doItemUse();
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @since 1.5.0
     */
    public ClientPlayerEntityHelper<T> attack() throws InterruptedException {
        return attack(false);
    }

    /**
     * @param await
     * @since 1.6.0
     */
    public ClientPlayerEntityHelper<T> attack(boolean await) throws InterruptedException {
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            ((IMinecraftClient) mc).jsmacros_doAttack();
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ((IMinecraftClient) mc).jsmacros_doAttack();
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param stop
     * @return
     * @since 1.6.3
     */
    @Deprecated
    public ClientPlayerEntityHelper<T> setLongAttack(boolean stop) {
        if (!stop) {
            KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(mc.options.attackKey.getBoundKeyTranslationKey()));
        } else {
            KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(mc.options.attackKey.getBoundKeyTranslationKey()), false);
        }
        return this;
    }

    /**
     * @param stop
     * @return
     * @since 1.6.3
     */
    @Deprecated
    public ClientPlayerEntityHelper<T> setLongInteract(boolean stop) {
        if (!stop) {
            KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(mc.options.useKey.getBoundKeyTranslationKey()));
        } else {
            KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(mc.options.useKey.getBoundKeyTranslationKey()), false);
        }
        return this;
    }

    /**
     * @return
     * @since 1.6.5
     */
    @DocletReplaceReturn("JavaMap<ItemId, int>")
    public Map<String, Integer> getItemCooldownsRemainingTicks() {
        int tick = ((IItemCooldownManager) base.getItemCooldownManager()).jsmacros_getManagerTicks();
        Map<Item, IItemCooldownEntry> map = ((IItemCooldownManager) base.getItemCooldownManager()).jsmacros_getCooldownItems();
        return map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName().getString(), e -> e.getValue().jsmacros_getEndTick() - tick));
    }

    /**
     * @param item
     * @return
     * @since 1.6.5
     */
    @DocletReplaceParams("item: ItemId")
    public int getItemCooldownRemainingTicks(String item) {
        int tick = ((IItemCooldownManager) base.getItemCooldownManager()).jsmacros_getManagerTicks();
        Map<Item, IItemCooldownEntry> map = ((IItemCooldownManager) base.getItemCooldownManager()).jsmacros_getCooldownItems();
        IItemCooldownEntry entry = map.get(Registries.ITEM.get(RegistryHelper.parseIdentifier(item)));
        if (entry == null) {
            return -1;
        }
        return entry.jsmacros_getEndTick() - tick;
    }

    /**
     * @return
     * @since 1.6.5
     */
    @DocletReplaceReturn("JavaMap<ItemId, int>")
    public Map<String, Integer> getTicksSinceCooldownsStart() {
        int tick = ((IItemCooldownManager) base.getItemCooldownManager()).jsmacros_getManagerTicks();
        Map<Item, IItemCooldownEntry> map = ((IItemCooldownManager) base.getItemCooldownManager()).jsmacros_getCooldownItems();
        return map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName().getString(), e -> e.getValue().jsmacros_getStartTick() - tick));
    }

    /**
     * @param item
     * @return
     * @since 1.6.5
     */
    @DocletReplaceParams("item: ItemId")
    public int getTicksSinceCooldownStart(String item) {
        int tick = ((IItemCooldownManager) base.getItemCooldownManager()).jsmacros_getManagerTicks();
        Map<Item, IItemCooldownEntry> map = ((IItemCooldownManager) base.getItemCooldownManager()).jsmacros_getCooldownItems();
        IItemCooldownEntry entry = map.get(Registries.ITEM.get(RegistryHelper.parseIdentifier(item)));
        if (entry == null) {
            return -1;
        }
        return entry.jsmacros_getStartTick() - tick;
    }

    /**
     * @return
     * @since 1.1.2
     */
    public int getFoodLevel() {
        return base.getHungerManager().getFoodLevel();
    }

    /**
     * This will return the invisible hunger decade that you may have seen in mods as a yellow overlay.
     *
     * @return the saturation level.
     * @since 1.8.4
     */
    public float getSaturation() {
        return base.getHungerManager().getSaturationLevel();
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public ClientPlayerEntityHelper<?> dropHeldItem(boolean dropStack) {
        base.dropSelectedItem(dropStack);
        return this;
    }

    /**
     * @return an advancement manager to work with advancements.
     * @since 1.8.4
     */
    public AdvancementManagerHelper getAdvancementManager() {
        return new AdvancementManagerHelper(base.networkHandler.getAdvancementHandler().getManager());
    }

    /**
     * The returned time is an approximation and will likely be off by a few ticks, although it
     * should always be less than the actual time.
     *
     * @param block the block to mine
     * @return the time in ticks that it will approximately take the player with the currently held
     * item to mine said block.
     */
    public int calculateMiningSpeed(BlockStateHelper block) {
        return calculateMiningSpeed(getMainHand(), block);
    }

    /**
     * Calculate mining speed for a given block mined with a specified item in ticks. Use air to
     * calculate the mining speed for the hand. The returned time is an approximation and will
     * likely be off by a few ticks, although it should always be less than the actual time.
     *
     * @param usedItem   the item to mine with
     * @param blockState the block to mine
     * @return the time in ticks that it will approximately take the player with the specified item
     * to mine said block.
     * @since 1.8.4
     */
    public int calculateMiningSpeed(ItemStackHelper usedItem, BlockStateHelper blockState) {
        PlayerEntity player = mc.player;
        BlockState state = blockState.getRaw();
        ItemStack item = usedItem.getRaw();

        if (!item.getItem().canMine(state, mc.world, player.getBlockPos(), player)) {
            return -1;
        } else if (player.isCreative()) {
            return 0;
        }
        float hardness = state.getHardness(mc.world, null);
        if (hardness == -1) {
            return -1;
        }
        float speedMultiplier = item.getMiningSpeedMultiplier(state);
        if (speedMultiplier > 1.0F) {
            int efficiency = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, item);
            if (efficiency > 0 && !item.isEmpty()) {
                speedMultiplier += (efficiency * efficiency + 1F);
            }
        }
        if (StatusEffectUtil.hasHaste(player)) {
            speedMultiplier *= 1.0F + (StatusEffectUtil.getHasteAmplifier(player) + 1F) * 0.2F;
        }
        if (player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            switch (player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    speedMultiplier *= 0.3;
                    break;
                case 1:
                    speedMultiplier *= 0.09;
                    break;
                case 2:
                    speedMultiplier *= 0.0027;
                    break;
                default:
                    speedMultiplier *= 0.00081;
                    break;
            }
        }
        if (player.isSubmergedIn(FluidTags.WATER) && EnchantmentHelper.getLevel(Enchantments.AQUA_AFFINITY, item) == 0) {
            speedMultiplier /= 5;
        }
        if (!player.isOnGround()) {
            speedMultiplier /= 5;
        }
        float damage = speedMultiplier / hardness;
        damage /= (!state.isToolRequired() || item.isSuitableFor(state)) ? 30 : 100;
        if (damage >= 1) {
            return 0;
        }
        return (int) Math.ceil(1 / damage);
    }

}
