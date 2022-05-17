package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.client.access.IClientPlayerInteractionManager;
import xyz.wagyourtail.jsmacros.client.access.IItemCooldownEntry;
import xyz.wagyourtail.jsmacros.client.access.IItemCooldownManager;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

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

    private static BlockPos breakPos;
    private static boolean finishedBlockBreak;

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
        base.prevPitch = base.getPitch();
        base.prevYaw = base.getYaw();
        base.setPitch((float)pitch);
        base.setYaw(MathHelper.wrapDegrees((float)yaw));
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
     * @param direction
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
        if (!stop) KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(mc.options.attackKey.getBoundKeyTranslationKey()));
        else KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(mc.options.attackKey.getBoundKeyTranslationKey()), false);
        return this;
    }

    /**
     * @param stop
     * @since 1.6.3
     * @return
     */
    public ClientPlayerEntityHelper<T> setLongInteract(boolean stop) {
        if (!stop) KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(mc.options.useKey.getBoundKeyTranslationKey()));
        else KeyBinding.setKeyPressed(InputUtil.fromTranslationKey(mc.options.useKey.getBoundKeyTranslationKey()), false);
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

    private static boolean internalBreakBlock(int x, int y, int z){
        assert MinecraftClient.getInstance().interactionManager != null;
        assert MinecraftClient.getInstance().player != null;

        boolean value = MinecraftClient.getInstance().interactionManager.updateBlockBreakingProgress(new BlockPos(x, y, z), Direction.DOWN);
        MinecraftClient.getInstance().player.swingHand(Hand.MAIN_HAND);
        return value;
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     * @since 1.6.5
     */
    public ClientPlayerEntityHelper<T> breakBlock(int x, int y, int z) {
        assert MinecraftClient.getInstance().interactionManager != null;
        
        breakPos = new BlockPos(x, y, z);
        finishedBlockBreak = false;
        ((IClientPlayerInteractionManager)MinecraftClient.getInstance().interactionManager).jsmacros_setBreakingBlock(true);
        ((IClientPlayerInteractionManager)MinecraftClient.getInstance().interactionManager).jsmacros_setCurrentBreakingPos(breakPos);
        return this;
    }

    /**
     *
     * @return
     * @since 1.6.5
     */
    public ClientPlayerEntityHelper<T> stopBlockBreaking(){
        sendBlockFinish();
        finishedBlockBreak = true;
        breakPos = null;
        return this;
    }

    private static void sendBlockFinish(){
        assert MinecraftClient.getInstance().interactionManager != null;
        assert MinecraftClient.getInstance().world != null;
        assert MinecraftClient.getInstance().player != null;

        MinecraftClient.getInstance().world.setBlockBreakingInfo(MinecraftClient.getInstance().player.getId(), breakPos, -1);
        ((IClientPlayerInteractionManager) MinecraftClient.getInstance().interactionManager).jsmacros_sendPlayerAction(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, breakPos, Direction.DOWN);
    }


    /**
     * Should never ever be called because it is called every tick
     */
    public static void onTick(){
        if(MinecraftClient.getInstance().player != null) {
            if (breakPos != null) {
                finishedBlockBreak = !internalBreakBlock(breakPos.getX(), breakPos.getY(), breakPos.getZ());
                if (finishedBlockBreak) {
                    sendBlockFinish();
                    breakPos = null;
                    finishedBlockBreak = false;
                }
            }
        }
    }

    /**
     * @since 1.6.5
     * @param x
     * @param y
     * @param z
     * @param offHand
     * @param await
     * @return
     * @throws InterruptedException
     */
    public ClientPlayerEntityHelper<T> placeBlock(int x, int y, int z, boolean offHand, boolean swingHand, boolean await) throws InterruptedException {
        boolean joinedMain = Core.getInstance().profile.checkJoinedThreadStack();
        if (joinedMain) {
            Vec3d hitpos = new Vec3d(x + 0.5, y + 0.5, z + 0.5);
            BlockPos neighbor, placePos = new BlockPos(x, y, z);
            Direction side = getPlaceSide(placePos);

            if(side == null){
                side = Direction.UP;
                neighbor = placePos;
            }else{
                neighbor = placePos.offset(side.getOpposite());
                hitpos.add(side.getOffsetX() * 0.5, side.getOffsetY() * 0.5, side.getOffsetZ() * 0.5);
            }

            Direction s = side;

            internalPlace(new BlockHitResult(hitpos, s, neighbor, false), offHand ? Hand.OFF_HAND : Hand.MAIN_HAND, true);
        } else {
            Semaphore wait = new Semaphore(await ? 0 : 1);
            mc.execute(() -> {
                Vec3d hitpos = new Vec3d(x + 0.5, y + 0.5, z + 0.5);
                BlockPos neighbor, placePos = new BlockPos(x, y, z);
                Direction side = getPlaceSide(placePos);

                if(side == null){
                    side = Direction.UP;
                    neighbor = placePos;
                }else{
                    neighbor = placePos.offset(side.getOpposite());
                    hitpos.add(side.getOffsetX() * 0.5, side.getOffsetY() * 0.5, side.getOffsetZ() * 0.5);
                }

                Direction s = side;

                internalPlace(new BlockHitResult(hitpos, s, neighbor, false), offHand ? Hand.OFF_HAND : Hand.MAIN_HAND, true);
                wait.release();
            });
            wait.acquire();
        }
        return this;
    }

    /**
     * @since 1.6.5
     * @param x
     * @param y
     * @param z
     * @param offHand
     * @return true if block could be placed
     */
    public ClientPlayerEntityHelper<T> placeBlock(int x, int y, int z, boolean offHand) throws InterruptedException {
        return placeBlock(x, y, z, offHand, true, false);
    }

    /**
     * @since 1.6.5
     * @param x
     * @param y
     * @param z
     * @param offHand
     * @param swingHand
     * @return true if block could be placed
     */
    public ClientPlayerEntityHelper<T> placeBlock(int x, int y, int z, boolean offHand, boolean swingHand) throws InterruptedException {
        return placeBlock(x, y, z, offHand, swingHand, false);
    }

    private void internalPlace(BlockHitResult blockHitResult, Hand hand, boolean swingHand){
        assert mc.player != null;
        assert mc.interactionManager != null;
        assert mc.getNetworkHandler() != null;

        boolean wasSneaking = mc.player.input.sneaking;
        mc.player.input.sneaking = false;
        ActionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, hand, blockHitResult);

        if(result.shouldSwingHand()){
            if(swingHand) mc.player.swingHand(hand);
            else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
        }
        mc.player.input.sneaking = wasSneaking;
        mc.player.sendMessage(new LiteralText(result + " 1:"), false);
        mc.player.sendMessage(new LiteralText(result.shouldSwingHand() + " | " + result.isAccepted() + " 2:"), false);
    }

    private Direction getPlaceSide(BlockPos blockPos) {
        assert mc.world != null;

        for (Direction side : Direction.values()) {
            BlockPos neighbor = blockPos.offset(side);
            Direction side2 = side.getOpposite();

            BlockState state = mc.world.getBlockState(neighbor);

            // Check if neighbour isn't empty
            if (state.isAir() || isClickable(state.getBlock())) continue;

            // Check if neighbour is a fluid
            if (!state.getFluidState().isEmpty()) continue;

            return side2;
        }

        return null;
    }

    private static boolean isClickable(Block block) {
        return block instanceof CraftingTableBlock
                || block instanceof AnvilBlock
                || block instanceof AbstractButtonBlock
                || block instanceof AbstractPressurePlateBlock
                || block instanceof BlockWithEntity
                || block instanceof BedBlock
                || block instanceof FenceGateBlock
                || block instanceof DoorBlock
                || block instanceof NoteBlock
                || block instanceof TrapdoorBlock;
    }
}
