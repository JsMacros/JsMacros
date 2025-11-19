package xyz.wagyourtail.jsmacros.client.api.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletDeclareType;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.access.IClientPlayerInteractionManager;
import xyz.wagyourtail.jsmacros.client.api.classes.InteractionProxy;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.HitResultHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FClient;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Locale;
import java.util.concurrent.Semaphore;

/**
 * Helper for ClientPlayerInteractionManager
 * it accesses interaction manager from {@code mc} instead of {@code base}, to avoid issues
 * @author aMelonRind
 * @since 1.9.0
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class InteractionManagerHelper extends BaseHelper<ClientPlayerInteractionManager> {
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * indicates if the helper should auto update the base manager, default is true<br>
     * when the base doesn't equal to the current manager,<br>
     *  if this is false, raise an error;<br>
     *  else if base is updated, the method works as usual;<br>
     *  else if the method don't need manager or network interaction, work as usual with old manager;<br>
     *  else the method does nothing
     */
    public boolean autoUpdateBase = true;

    public InteractionManagerHelper(ClientPlayerInteractionManager base) {
        super(base);
    }

    /**
     * checks if the base matches the current manager
     * @param update true if the base should be updated. otherwise it'll raise an error if it's not up-to-date
     * @return true if base is available
     */
    public boolean checkBase(boolean update) {
        if (mc.interactionManager == base) return true;
        if (update) {
            if (mc.interactionManager != null) {
                base = mc.interactionManager;
                return true;
            } else return false;
        } else {
            throw new RuntimeException("Wrapped interaction manager doesn't match the current one in client");
        }
    }

    /**
     * @return the player's current gamemode.
     * @since 1.9.0
     */
    @DocletReplaceReturn("Gamemode")
    public String getGameMode() {
        checkBase(autoUpdateBase);
        return base.getCurrentGameMode().getId();
    }

    /**
     * @param gameMode possible values are survival, creative, adventure, spectator (case insensitive)
     * @return self for chaining
     * @since 1.9.0
     */
    @DocletReplaceParams("gameMode: Gamemode")
    public InteractionManagerHelper setGameMode(String gameMode) {
        checkBase(autoUpdateBase);
        base.setGameMode(GameMode.byId(gameMode.toLowerCase(Locale.ROOT), base.getCurrentGameMode()));
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    public InteractionManagerHelper setTarget(int x, int y, int z) {
        setTarget(x, y, z, 0);
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Direction")
    @DocletDeclareType(name = "Direction", type = "'up' | 'down' | 'north' | 'south' | 'east' | 'west'")
    public InteractionManagerHelper setTarget(int x, int y, int z, String direction) {
        InteractionProxy.Target.setTargetBlock(new BlockPos(x, y, z), Direction.byId(direction.toLowerCase(Locale.ROOT)));
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Hexit")
    public InteractionManagerHelper setTarget(int x, int y, int z, int direction) {
        InteractionProxy.Target.setTargetBlock(new BlockPos(x, y, z), Direction.byIndex(direction));
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    public InteractionManagerHelper setTarget(BlockPosHelper pos) {
        setTarget(pos, 0);
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    @DocletReplaceParams("bpos: BlockPosHelper, direction: Direction")
    public InteractionManagerHelper setTarget(BlockPosHelper pos, String direction) {
        InteractionProxy.Target.setTargetBlock(pos.getRaw(), Direction.byId(direction.toLowerCase(Locale.ROOT)));
        return this;
    }

    /**
     * sets crosshair target to a block
     * @return self for chaining
     * @since 1.9.0
     */
    @DocletReplaceParams("bpos: BlockPosHelper, direction: Hexit")
    public InteractionManagerHelper setTarget(BlockPosHelper pos, int direction) {
        InteractionProxy.Target.setTargetBlock(pos.getRaw(), Direction.byIndex(direction));
        return this;
    }

    /**
     * sets crosshair target to an entity
     * @return self for chaining
     * @since 1.9.0
     */
    public InteractionManagerHelper setTarget(EntityHelper<?> entity) {
        if (!entity.getRaw().canHit()) throw new AssertionError(String.format("Can't target not-hittable entity! (%s)", entity.getType()));
        if (entity.getRaw() == mc.player) throw new AssertionError("Can't target self!");
        InteractionProxy.Target.setTarget(new EntityHitResult(entity.getRaw()));
        return this;
    }

    /**
     * @return current hitResult
     * @since 1.9.1
     */
    public @Nullable HitResultHelper<?> getTarget() {
        return HitResultHelper.resolve(mc.crosshairTarget);
    }

    /**
     * @return targeted block pos, null if not targeting block
     * @since 1.9.0
     */
    @Nullable
    public BlockPosHelper getTargetedBlock() {
        HitResult target = mc.crosshairTarget;
        if (target != null && target.getType() == HitResult.Type.BLOCK) {
            return new BlockPosHelper(((BlockHitResult) target).getBlockPos());
        }
        return null;
    }

    /**
     * @return targeted entity, null if not targeting entity
     * @since 1.9.0
     */
    @Nullable
    public EntityHelper<?> getTargetedEntity() {
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
    public InteractionManagerHelper setTargetMissed() {
        InteractionProxy.Target.setTargetMissed();
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
     * clears target override
     * @return self for chaining
     * @since 1.9.0
     */
    public InteractionManagerHelper clearTargetOverride() {
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
    public InteractionManagerHelper setTargetRangeCheck(boolean enabled, boolean autoClear) {
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
    public InteractionManagerHelper setTargetAirCheck(boolean enabled, boolean autoClear) {
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
    public InteractionManagerHelper setTargetShapeCheck(boolean enabled, boolean autoClear) {
        InteractionProxy.Target.checkShape = enabled;
        InteractionProxy.Target.clearIfEmptyShape = autoClear;
        return this;
    }

    /**
     * resets all range, air and shape check settings to default.
     * @return self for chaining
     * @since 1.9.0
     */
    public InteractionManagerHelper resetTargetChecks() {
        InteractionProxy.Target.resetChecks();
        return this;
    }



    /**
     * @since 1.5.0
     */
    public InteractionManagerHelper attack() throws InterruptedException {
        return attack(false);
    }

    /**
     * @param await
     * @since 1.6.0
     */
    public InteractionManagerHelper attack(boolean await) throws InterruptedException {
        boolean joinedMain = JsMacrosClient.clientCore.profile.checkJoinedThreadStack();
        if (joinedMain) {
            mc.doAttack();
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                mc.doAttack();
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param entity
     * @since 1.5.0
     */
    public InteractionManagerHelper attack(EntityHelper<?> entity) throws InterruptedException {
        return attack(entity, false);
    }

    /**
     * @param await
     * @param entity
     * @since 1.6.0
     */
    public InteractionManagerHelper attack(EntityHelper<?> entity, boolean await) throws InterruptedException {
        if (!checkBase(autoUpdateBase)) return this;
        boolean joinedMain = JsMacrosClient.clientCore.profile.checkJoinedThreadStack();
        if (entity.getRaw() == mc.player) {
            throw new AssertionError("Can't interact with self!");
        }
        if (joinedMain) {
            base.attackEntity(mc.player, entity.getRaw());
            assert mc.player != null;
            mc.player.swingHand(Hand.MAIN_HAND);
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                base.attackEntity(mc.player, entity.getRaw());
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
    public InteractionManagerHelper attack(int x, int y, int z, String direction) throws InterruptedException {
        return attack(x, y, z, direction, false);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @since 1.5.0
     */
    @DocletReplaceParams("x: int, y: int, z: int, direction: Hexit")
    public InteractionManagerHelper attack(int x, int y, int z, int direction) throws InterruptedException {
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
    public InteractionManagerHelper attack(int x, int y, int z, String direction, boolean await) throws InterruptedException {
        return attack(x, y, z, Direction.byId(direction.toLowerCase(Locale.ROOT)), await);
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
    public InteractionManagerHelper attack(int x, int y, int z, int direction, boolean await) throws InterruptedException {
        return attack(x, y, z, Direction.byIndex(direction), await);
    }

    private InteractionManagerHelper attack(int x, int y, int z, Direction direction, boolean await) throws InterruptedException {
        if (!checkBase(autoUpdateBase)) return this;
        boolean joinedMain = JsMacrosClient.clientCore.profile.checkJoinedThreadStack();
        if (joinedMain) {
            base.attackBlock(new BlockPos(x, y, z), direction);
            assert mc.player != null;
            mc.player.swingHand(Hand.MAIN_HAND);
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                base.attackBlock(new BlockPos(x, y, z), direction);
                assert mc.player != null;
                mc.player.swingHand(Hand.MAIN_HAND);
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * breaks a block, will wait till it's done<br>
     * you can use {@code ClientPlayerEntityHelper#setTarget()} to specify which block to break
     * @return result, or null if interaction manager is unavailable
     * @see InteractionManagerHelper#setTarget(int, int, int, String)
     * @throws InterruptedException
     * @since 1.9.0
     */
    @Nullable
    public InteractionProxy.Break.BreakBlockResult breakBlock() throws InterruptedException {
        InteractionProxy.Break.BreakBlockResult insta = checkInstaBreak();
        if (insta != null) return insta;
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
            throw new IllegalThreadStateException("Attempted to wait on a thread that is currently joined to main!");
        }

        final InteractionProxy.Break.BreakBlockResult[] ret = {null};
        Semaphore wait = new Semaphore(0);

        InteractionProxy.Break.addCallback(res -> {
            ret[0] = res;
            wait.release();
        }, true);
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
    @Nullable
    public InteractionProxy.Break.BreakBlockResult breakBlock(int x, int y, int z) throws InterruptedException {
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
    @Nullable
    public InteractionProxy.Break.BreakBlockResult breakBlock(BlockPosHelper pos) throws InterruptedException {
        return breakBlock(pos.getRaw());
    }

    @Nullable
    private InteractionProxy.Break.BreakBlockResult breakBlock(BlockPos pos) throws InterruptedException {
        InteractionProxy.Break.BreakBlockResult insta = checkInstaBreak(pos);
        if (insta != null) return insta;
        InteractionProxy.Target.setTargetBlock(pos, Direction.DOWN);
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
    public InteractionManagerHelper breakBlockAsync(@Nullable MethodWrapper<InteractionProxy.Break.BreakBlockResult, Object, ?, ?> callback) throws InterruptedException {
        InteractionProxy.Break.BreakBlockResult insta = checkInstaBreak();
        if (insta != null) {
            if (callback != null) mc.execute(() -> callback.accept(insta));
            return this;
        }
        InteractionProxy.Break.addCallback(callback, true);
        preBreakBlock();
        return this;
    }

    @Nullable
    private InteractionProxy.Break.BreakBlockResult checkInstaBreak() throws InterruptedException {
        HitResult target = mc.crosshairTarget;
        if (target == null || target.getType() != HitResult.Type.BLOCK) return null;
        return checkInstaBreak(((BlockHitResult) target).getBlockPos());
    }

    @Nullable
    private InteractionProxy.Break.BreakBlockResult checkInstaBreak(BlockPos pos) throws InterruptedException {
        if (!checkBase(autoUpdateBase)) return InteractionProxy.Break.BreakBlockResult.UNAVAILABLE;
        if (mc.world == null || mc.player == null
        ||  ((IClientPlayerInteractionManager) base).jsmacros_getBlockBreakingCooldown() != 0
        ||  mc.world.getBlockState(pos).calcBlockBreakingDelta(mc.player, mc.player.getWorld(), pos) < 1.0F
        ) return null;
        int side = 0;
        HitResult target = mc.crosshairTarget;
        if (target != null && target.getType() == HitResult.Type.BLOCK) {
            side = ((BlockHitResult) target).getSide().getIndex();
        }
        attack(pos.getX(), pos.getY(), pos.getZ(), side, true);
        return new InteractionProxy.Break.BreakBlockResult("SUCCESS", new BlockPosHelper(pos));
    }

    private void preBreakBlock() throws InterruptedException {
        if (((IClientPlayerInteractionManager) base).jsmacros_getBlockBreakingCooldown() == 0) {
            HitResult target = mc.crosshairTarget;
            if (target == null || target.getType() != HitResult.Type.BLOCK) return;
            BlockPos pos = ((BlockHitResult) target).getBlockPos();
            attack(pos.getX(), pos.getY(), pos.getZ(), ((BlockHitResult) target).getSide(), true);
        }
    }

    /**
     * @since 1.8.0
     */
    public boolean isBreakingBlock() {
        checkBase(autoUpdateBase);
        return base.isBreakingBlock();
    }

    /**
     * @return {@code true} if there's not finished block breaking from {@code ClientPlayerEntityHelper#breakBlock()}
     * @since 1.9.0
     */
    public boolean hasBreakBlockOverride() {
        return InteractionProxy.Break.isBreaking();
    }

    /**
     * cancels breaking block that previously started by {@code ClientPlayerEntityHelper#breakBlock()} or
     *  {@code ClientPlayerEntityHelper#breakBlockAsync()}
     * @return self for chaining
     * @since 1.9.0
     */
    public InteractionManagerHelper cancelBreakBlock() {
        InteractionProxy.Break.setOverride(false, "CANCELLED");
        return this;
    }



    /**
     * @since 1.5.0
     */
    public InteractionManagerHelper interact() throws InterruptedException {
        return interact(false);
    }

    /**
     * @param await
     * @since 1.6.0
     */
    public InteractionManagerHelper interact(boolean await) throws InterruptedException {
        boolean joinedMain = JsMacrosClient.clientCore.profile.checkJoinedThreadStack();
        if (joinedMain) {
           mc.doItemUse();
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                mc.doItemUse();
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
    public InteractionManagerHelper interactEntity(EntityHelper<?> entity, boolean offHand) throws InterruptedException {
        return interactEntity(entity, offHand, false);
    }

    /**
     * @param entity
     * @param offHand
     * @param await
     * @throws InterruptedException
     * @since 1.6.0
     */
    public InteractionManagerHelper interactEntity(EntityHelper<?> entity, boolean offHand, boolean await) throws InterruptedException {
        if (!checkBase(autoUpdateBase)) return this;
        if (entity.getRaw() == mc.player) {
            throw new AssertionError("Can't interact with self!");
        }
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        boolean joinedMain = JsMacrosClient.clientCore.profile.checkJoinedThreadStack();
        if (joinedMain) {
            ActionResult result = base.interactEntity(mc.player, entity.getRaw(), hand);
            assert mc.player != null;
            if (result.isAccepted()) {
                mc.player.swingHand(hand);
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ActionResult result = base.interactEntity(mc.player, entity.getRaw(), hand);
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
    public InteractionManagerHelper interactItem(boolean offHand) throws InterruptedException {
        return interactItem(offHand, false);
    }

    /**
     * @param offHand
     * @param await
     * @since 1.6.0
     */
    public InteractionManagerHelper interactItem(boolean offHand, boolean await) throws InterruptedException {
        if (!checkBase(autoUpdateBase)) return this;
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        boolean joinedMain = JsMacrosClient.clientCore.profile.checkJoinedThreadStack();
        if (joinedMain) {
            ActionResult result = base.interactItem(mc.player, hand);
            assert mc.player != null;
            if (result.isAccepted()) {
                mc.player.swingHand(hand);
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ActionResult result = base.interactItem(mc.player, hand);
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
    public InteractionManagerHelper interactBlock(int x, int y, int z, String direction, boolean offHand) throws InterruptedException {
        return interactBlock(x, y, z, direction, offHand, false);
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
    public InteractionManagerHelper interactBlock(int x, int y, int z, int direction, boolean offHand) throws InterruptedException {
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
    public InteractionManagerHelper interactBlock(int x, int y, int z, String direction, boolean offHand, boolean await) throws InterruptedException {
        return interactBlock(x, y, z, Direction.byId(direction.toLowerCase(Locale.ROOT)), offHand, await);
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
    public InteractionManagerHelper interactBlock(int x, int y, int z, int direction, boolean offHand, boolean await) throws InterruptedException {
        return interactBlock(x, y, z, Direction.byIndex(direction), offHand, await);
    }

    private InteractionManagerHelper interactBlock(int x, int y, int z, Direction direction, boolean offHand, boolean await) throws InterruptedException {
        if (!checkBase(autoUpdateBase)) return this;
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        boolean joinedMain = JsMacrosClient.clientCore.profile.checkJoinedThreadStack();
        if (joinedMain) {
            ActionResult result = base.interactBlock(mc.player, hand,
                    new BlockHitResult(new Vec3d(x, y, z), direction, new BlockPos(x, y, z), false)
            );
            assert mc.player != null;
            if (result.isAccepted()) {
                mc.player.swingHand(hand);
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ActionResult result = base.interactBlock(mc.player, hand,
                        new BlockHitResult(new Vec3d(x, y, z), direction, new BlockPos(x, y, z), false)
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
     * starts/stops long interact
     * @return self for chaining
     * @since 1.9.0
     */
    public InteractionManagerHelper holdInteract(boolean holding) throws InterruptedException {
        return holdInteract(holding, false);
    }

    /**
     * starts/stops long interact
     * @return self for chaining
     * @since 1.9.0
     */
    public InteractionManagerHelper holdInteract(boolean holding, boolean awaitFirstClick) throws InterruptedException {
        if (!holding) {
            InteractionProxy.Interact.setOverride(false);
            return this;
        }
        boolean joinedMain = JsMacrosClient.clientCore.profile.checkJoinedThreadStack();
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
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
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

}