package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import xyz.wagyourtail.jsmacros.client.access.IItemCooldownEntry;
import xyz.wagyourtail.jsmacros.client.access.IItemCooldownManager;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * @author Wagyourtail
 * @see xyz.wagyourtail.jsmacros.client.api.helpers.PlayerEntityHelper
 * @since 1.0.3
 */
@SuppressWarnings("unused")
public class ClientPlayerEntityHelper<T extends EntityPlayerSP> extends PlayerEntityHelper<T> {
    protected final Minecraft mc = Minecraft.getInstance();

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
        PositionCommon.Vec3D vec = new PositionCommon.Vec3D(base.x, base.y + base.getEyeHeight(), base.z, x, y, z);
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
            mc.player.swingHand();
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                mc.interactionManager.attackEntity(mc.player, entity.getRaw());
                assert mc.player != null;
                mc.player.swingHand();
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
            mc.interactionManager.attackBlock(new BlockPos(x, y, z), EnumFacing.values()[direction]);
            assert mc.player != null;
            mc.player.swingHand();
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                mc.interactionManager.attackBlock(new BlockPos(x, y, z), EnumFacing.values()[direction]);
                assert mc.player != null;
                mc.player.swingHand();
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
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            boolean result = mc.interactionManager.interactEntityAtLocation(mc.player, entity.getRaw(), mc.result) ||
                mc.interactionManager.interactEntity(mc.player, entity.getRaw());
            assert mc.player != null;
            if (!result) {
                ItemStack itemstack1 = mc.player.inventory.getMainHandStack();

                boolean result2 = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.player, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR, mc.world, null, null).isCanceled();
                if (result2 && itemstack1 != null && mc.interactionManager.func_78769_a(mc.player, mc.world, itemstack1))
                {
                    mc.gameRenderer.firstPersonRenderer.func_78445_c();
                }
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                boolean result = mc.interactionManager.interactEntityAtLocation(mc.player, entity.getRaw(), mc.result) ||
                    mc.interactionManager.interactEntity(mc.player, entity.getRaw());
                assert mc.player != null;
                if (!result) {
                    ItemStack itemstack1 = mc.player.inventory.getMainHandStack();

                    boolean result2 = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.player, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR, mc.world, null, null).isCanceled();
                    if (result2 && itemstack1 != null && mc.interactionManager.func_78769_a(mc.player, mc.world, itemstack1))
                    {
                        mc.gameRenderer.firstPersonRenderer.func_78445_c();
                    }
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
     * @since 1.6.0
     * @param offHand
     * @param await
     */
    public ClientPlayerEntityHelper<T> interactItem(boolean offHand, boolean await) throws InterruptedException {
    assert mc.interactionManager != null;
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            ItemStack itemstack1 = mc.player.inventory.getMainHandStack();

            boolean result = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.player, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR, mc.world, null, null).isCanceled();
            if (result && itemstack1 != null && mc.interactionManager.func_78769_a(mc.player, mc.world, itemstack1))
            {
                mc.gameRenderer.firstPersonRenderer.func_78445_c();
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                ItemStack itemstack1 = mc.player.inventory.getMainHandStack();

                boolean result = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(mc.player, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_AIR, mc.world, null, null).isCanceled();
                if (result && itemstack1 != null && mc.interactionManager.func_78769_a(mc.player, mc.world, itemstack1))
                {
                    mc.gameRenderer.firstPersonRenderer.func_78445_c();
                }
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
    boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            BlockPos blockpos = new BlockPos(x, y, z);
            if (!mc.world.isAir(blockpos)) {
                ItemStack itemstack = mc.player.getMainHandStack();
                int i = itemstack != null ? itemstack.count : 0;
                if (mc.interactionManager.onRightClick(mc.player, mc.world, itemstack, blockpos, EnumFacing.values()[direction], new Vec3(x, y, z))) {
                    mc.player.swingHand();
                }
                if (itemstack == null) {
                    return this;
                }
                if (itemstack.count == 0) {
                    mc.player.inventory.main[mc.player.inventory.selectedSlot] = null;
                } else if (itemstack.count != i || mc.interactionManager.hasCreativeInventory()) {
                    mc.gameRenderer.firstPersonRenderer.func_78445_c();
                }
            }
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                BlockPos blockpos = new BlockPos(x, y, z);
                if (!mc.world.isAir(blockpos)) {
                    ItemStack itemstack = mc.player.getMainHandStack();
                    int i = itemstack != null ? itemstack.count : 0;
                    if (mc.interactionManager.onRightClick(mc.player, mc.world, itemstack, blockpos, EnumFacing.values()[direction], new Vec3(x, y, z))) {
                        mc.player.swingHand();
                    }
                    if (itemstack == null) {
                        return;
                    }
                    if (itemstack.count == 0) {
                        mc.player.inventory.main[mc.player.inventory.selectedSlot] = null;
                    } else if (itemstack.count != i || mc.interactionManager.hasCreativeInventory()) {
                        mc.gameRenderer.firstPersonRenderer.func_78445_c();
                    }
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
        if (!stop) KeyBinding.onKeyPressed(mc.options.keyAttack.getCode());
        else KeyBinding.setKeyPressed(mc.options.keyAttack.getCode(), false);
        return this;
    }

    /**
     * @param stop
     * @since 1.6.3
     * @return
     */
    public ClientPlayerEntityHelper<T> setLongInteract(boolean stop) {
        if (!stop) KeyBinding.onKeyPressed(mc.options.keyUse.getCode());
        else KeyBinding.setKeyPressed(mc.options.keyUse.getCode(), false);
        return this;
    }

    /**
     * @since 1.6.5
     * @return
     */
    public Map<String, Integer> getItemCooldownsRemainingTicks() {
        return new HashMap<>();
    }

    /**
     * @param item
     * @since 1.6.5
     * @return
     */
    public int getItemCooldownRemainingTicks(String item) {
        return 0;
    }

    /**
     * @since 1.6.5
     * @return
     */
    public Map<String, Integer>  getTicksSinceCooldownsStart() {
        return new HashMap<>();
    }

    /**
     * @param item
     * @since 1.6.5
     * @return
     */
    public int getTicksSinceCooldownStart(String item) {
        return -1;
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
