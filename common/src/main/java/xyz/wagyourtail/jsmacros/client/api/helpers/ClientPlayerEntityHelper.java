package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.client.access.IItemCooldownEntry;
import xyz.wagyourtail.jsmacros.client.access.IItemCooldownManager;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @see xyz.wagyourtail.jsmacros.client.api.helpers.PlayerEntityHelper
 * @since 1.0.3
 */
@SuppressWarnings("unused")
public class ClientPlayerEntityHelper<T extends ClientPlayerEntity> extends PlayerEntityHelper<T> {
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    public ClientPlayerEntityHelper(T e) {
        super(e);
    }

    /**
     * @param yaw   (was pitch prior to 1.2.6)
     * @param pitch (was yaw prior to 1.2.6)
     * @return
     * @since 1.0.3
     */
    public ClientPlayerEntityHelper<T> lookAt(double yaw, double pitch) {
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        base.prevPitch = base.pitch;
        base.prevYaw = base.yaw;
        base.pitch = (float)pitch;
        base.yaw = MathHelper.wrapDegrees((float)yaw);
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
        PositionCommon.Vec3D vec = new PositionCommon.Vec3D(base.getX(), base.getY() + base.getEyeHeight(base.getPose()), base.getZ(), x, y, z);
        lookAt(vec.getYaw(), vec.getPitch());
        return this;
    }

    /**
     * @param entity
     * @since 1.5.0
     */
    public ClientPlayerEntityHelper<T> attack(EntityHelper<?> entity) throws InterruptedException {
        return attack(entity, false);
    }

    /**
     * @since 1.6.0
     *
     * @param await
     * @param entity
     */
    public ClientPlayerEntityHelper<T> attack(EntityHelper<?> entity, boolean await) throws InterruptedException {
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        assert mc.interactionManager != null;
        if (entity.getRaw() == mc.player) throw new AssertionError("Can't interact with self!");
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
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @since 1.5.0
     */

    public ClientPlayerEntityHelper<T> attack(int x, int y, int z, int direction) throws InterruptedException {
        return attack(x, y, z, direction, false);
    }

    /**
     * @since 1.6.0
     *
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @param await
     *
     * @throws InterruptedException
     */
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
     * @since 1.6.0
     * @throws InterruptedException
     */
    public ClientPlayerEntityHelper<T> interactEntity(EntityHelper<?> entity, boolean offHand, boolean await) throws InterruptedException {
        assert mc.interactionManager != null;
        if (entity.getRaw() == mc.player) throw new AssertionError("Can't interact with self!");
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            ActionResult result = mc.interactionManager.interactEntity(mc.player, entity.getRaw(), hand);
            assert mc.player != null;
            if (result.isAccepted())
                mc.player.swingHand(hand);
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ActionResult result = mc.interactionManager.interactEntity(mc.player, entity.getRaw(), hand);
                assert mc.player != null;
                if (result.isAccepted())
                    mc.player.swingHand(hand);
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
     * @since 1.6.0
     * @param offHand
     * @param await
     */
    public ClientPlayerEntityHelper<T> interactItem(boolean offHand, boolean await) throws InterruptedException {
        assert mc.interactionManager != null;
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            ActionResult result = mc.interactionManager.interactItem(mc.player, mc.world, hand);
            assert mc.player != null;
            if (result.isAccepted())
                mc.player.swingHand(hand);
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ActionResult result = mc.interactionManager.interactItem(mc.player, mc.world, hand);
                assert mc.player != null;
                if (result.isAccepted())
                    mc.player.swingHand(hand);
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @param offHand
     * @since 1.5.0, renamed from {@code interact} in 1.6.0
     */
    public ClientPlayerEntityHelper<T> interactBlock(int x, int y, int z, int direction, boolean offHand) throws InterruptedException {
        return interactBlock(x, y, z, direction, offHand, false);
    }

    public ClientPlayerEntityHelper<T> interactBlock(int x, int y, int z, int direction, boolean offHand, boolean await) throws InterruptedException {
        assert mc.interactionManager != null;
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            ActionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, hand,
                new BlockHitResult(new Vec3d(x, y, z), Direction.values()[direction], new BlockPos(x, y, z), false)
            );
            assert mc.player != null;
            if (result.isAccepted())
                mc.player.swingHand(hand);
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ActionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, hand,
                    new BlockHitResult(new Vec3d(x, y, z), Direction.values()[direction], new BlockPos(x, y, z), false)
                );
                assert mc.player != null;
                if (result.isAccepted())
                    mc.player.swingHand(hand);
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
     * @since 1.6.0
     * @param await
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
     * @since 1.6.0
     * @param await
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
     * @since 1.6.3
     * @return
     */
    public ClientPlayerEntityHelper<T> setLongAttack(boolean stop) {
        if (!stop) KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(mc.options.keyAttack.getBoundKeyTranslationKey()));
        else KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(mc.options.keyAttack.getBoundKeyTranslationKey()), false);
        return this;
    }

    /**
     * @param stop
     * @since 1.6.3
     * @return
     */
    public ClientPlayerEntityHelper<T> setLongInteract(boolean stop) {
        if (!stop) KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(mc.options.keyUse.getBoundKeyTranslationKey()));
        else KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(mc.options.keyUse.getBoundKeyTranslationKey()), false);
        return this;
    }

    /**
     * @since 1.6.5
     * @return
     */
    public Map<String, Integer> getItemCooldownsRemainingTicks() {
        int tick = ((IItemCooldownManager) base.getItemCooldownManager()).getManagerTicks();
        Map<Item, IItemCooldownEntry> map = ((IItemCooldownManager) base.getItemCooldownManager()).getCooldownItems();
        return map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName().getString(), e -> e.getValue().getEndTick() - tick));
    }

    /**
     * @param item
     * @since 1.6.5
     * @return
     */
    public int getItemCooldownRemainingTicks(String item) {
        int tick = ((IItemCooldownManager) base.getItemCooldownManager()).getManagerTicks();
        Map<Item, IItemCooldownEntry> map = ((IItemCooldownManager) base.getItemCooldownManager()).getCooldownItems();
        IItemCooldownEntry entry = map.get(Registry.ITEM.get(new Identifier(item)));
        if (entry == null) return -1;
        return entry.getEndTick() - tick;
    }

    /**
     * @since 1.6.5
     * @return
     */
    public Map<String, Integer>  getTicksSinceCooldownsStart() {
        int tick = ((IItemCooldownManager) base.getItemCooldownManager()).getManagerTicks();
        Map<Item, IItemCooldownEntry> map = ((IItemCooldownManager) base.getItemCooldownManager()).getCooldownItems();
        return map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName().getString(), e -> e.getValue().getStartTick() - tick));
    }

    /**
     * @param item
     * @since 1.6.5
     * @return
     */
    public int getTicksSinceCooldownStart(String item) {
        int tick = ((IItemCooldownManager) base.getItemCooldownManager()).getManagerTicks();
        Map<Item, IItemCooldownEntry> map = ((IItemCooldownManager) base.getItemCooldownManager()).getCooldownItems();
        IItemCooldownEntry entry = map.get(Registry.ITEM.get(new Identifier(item)));
        if (entry == null) return -1;
        return entry.getStartTick() - tick;
    }

    /**
     * @return
     * @since 1.1.2
     */
    public int getFoodLevel() {
        return base.getHungerManager().getFoodLevel();
    }


    public String toString() {
        return "Client" + super.toString();
    }
}
