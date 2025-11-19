package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletDeclareType;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A class that can override crosshair target, handle breaking block and long interact.
 * @author aMelonRind
 * @since 1.9.0
 */
public class InteractionProxy {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void reset() {
        Target.resetChecks();
        Target.setTarget(null);
        Break.setOverride(false, "RESET");
        Interact.setOverride(false);
    }

    public static class Target {
        private static final BlockHitResult MISSED = BlockHitResult.createMissed(Vec3d.ZERO, Direction.DOWN, BlockPos.ORIGIN);

        @Nullable
        private static HitResult override = null;
        @Nullable
        private static Entity overrideEntity = null;
        public static boolean checkDistance = true;
        public static boolean clearIfOutOfRange = true;
        public static boolean checkAir = false;
        public static boolean clearIfIsAir = false;
        public static boolean checkShape = true;
        public static boolean clearIfEmptyShape = false;

        public static void resetChecks() {
            Target.checkDistance = true;
            Target.clearIfOutOfRange = true;
            Target.checkAir = false;
            Target.clearIfIsAir = false;
            Target.checkShape = true;
            Target.clearIfEmptyShape = false;
        }

        public static void setTargetBlock(@Nullable BlockPos pos, Direction direction) {
            setTarget(pos == null ? null : new BlockHitResult(pos.toCenterPos(), direction, pos, false));
        }

        public static void setTarget(@Nullable HitResult value) {
            synchronized (Target.class) {
                override = value;
                if (value != null && value.getType() == HitResult.Type.ENTITY) overrideEntity = ((EntityHitResult) value).getEntity();
                else overrideEntity = null;
                onUpdate(0f);
                Break.isBreaking();
            }
        }

        public static void setTargetMissed() {
            setTarget(MISSED);
        }

        public static boolean hasOverride() {
            return override != null;
        }

        public static boolean onUpdate(float tickDelta) {
            boolean cancel = false;
            synchronized (Target.class) {
                if (override != null) {
                    boolean shouldMiss = false;
                    if (overrideEntity != null) {
                        if (!overrideEntity.isAlive()) {
                            setTarget(null);
                            return shouldMiss;
                        }
                    } else if ((checkAir || checkShape) && override.getType() == HitResult.Type.BLOCK) {
                        if (mc.world == null) return shouldMiss;
                        BlockPos pos = ((BlockHitResult) override).getBlockPos();
                        BlockState state = mc.world.getBlockState(pos);
                        if (checkAir && state.isAir()) {
                            if (!clearIfIsAir) shouldMiss = true;
                            else {
                                setTarget(null);
                                return shouldMiss;
                            }
                        }
                        if (checkShape && !state.isAir() && state.getOutlineShape(mc.world, pos).isEmpty()) {
                            if (!clearIfEmptyShape) shouldMiss = true;
                            else {
                                setTarget(null);
                                return shouldMiss;
                            }
                        }
                    }
                    if (checkDistance && !isInRange(tickDelta)) {
                        if (!clearIfOutOfRange) shouldMiss = true;
                        else {
                            setTarget(null);
                            return shouldMiss;
                        }
                    }
                    if (override == null) return shouldMiss;
                    cancel = true;
                    if (shouldMiss) {
                        mc.crosshairTarget = MISSED;
                        mc.targetedEntity = null;
                    } else {
                        mc.crosshairTarget = override;
                        mc.targetedEntity = overrideEntity;
                    }
                }
            }
            return cancel;
        }

        public static boolean isInRange(float tickDelta) {
            synchronized (Target.class) {
                if (override == null || mc.player == null || mc.interactionManager == null) return false;
                if (override.getType() == HitResult.Type.MISS) return true;

                Vec3d campos = mc.player.getCameraPosVec(tickDelta);
                double blockReach = mc.player.getBlockInteractionRange();
                double entityReach = mc.player.getEntityInteractionRange();
                if (override.getPos().isInRange(campos, override.getType() == HitResult.Type.ENTITY ? entityReach : blockReach)) return true;

                if (override.getType() != HitResult.Type.BLOCK) return false;
                BlockPos pos = ((BlockHitResult) override).getBlockPos();
                return campos.squaredDistanceTo(
                        MathHelper.clamp(campos.x, pos.getX(), pos.getX() + 1),
                        MathHelper.clamp(campos.y, pos.getY(), pos.getY() + 1),
                        MathHelper.clamp(campos.z, pos.getZ(), pos.getZ() + 1)
                ) < blockReach * blockReach;
            }
        }

    }

    public static class Break {
        private static boolean override = false;
        private static final List<Consumer<BreakBlockResult>> callbacks = new ArrayList<>();
        @Nullable
        private static BlockPos lastTarget = null;

        public static void setOverride(boolean value) {
            setOverride(value, null, null);
        }

        public static void setOverride(boolean value, @Nullable String reason) {
            setOverride(value, reason, null);
        }

        private static void setOverride(boolean value, @Nullable String reason, @Nullable BlockPos pos) {
            lastTarget = null;
            override = value;
            if (!value) {
                if (mc.interactionManager != null && !mc.options.attackKey.isPressed()) mc.interactionManager.cancelBlockBreaking();
                runCallback(reason, pos);
            }
        }

        public static void addCallback(Consumer<BreakBlockResult> callback, boolean breaking) {
            synchronized (callbacks) {
                if (callback != null) callbacks.add(callback);
                if (breaking) setOverride(true);
            }
        }

        private static void runCallback(@Nullable String reason, @Nullable BlockPos pos) {
            if (callbacks.isEmpty()) return;
            runCallback(new BreakBlockResult(reason, pos == null ? null : new BlockPosHelper(pos)));
        }

        private static void runCallback(BreakBlockResult result) {
            synchronized (callbacks) {
                if (!callbacks.isEmpty()) {
                    callbacks.forEach(cb -> {
                        try {
                            cb.accept(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    callbacks.clear();
                }
            }
        }

        public static boolean isBreaking() {
            if (mc.world == null) setOverride(false, "RESET");
            synchronized (callbacks) {
                if (!override) {
                    if (!callbacks.isEmpty()) runCallback("NO_OVERRIDE", null);
                    return false;
                }
            }
            if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) {
                setOverride(false, lastTarget == null ? "NO_TARGET" : "TARGET_LOST");
                return false;
            }
            if (lastTarget == null) {
                lastTarget = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            } else if (!((BlockHitResult) mc.crosshairTarget).getBlockPos().equals(lastTarget)) {
                setOverride(false, "TARGET_CHANGE");
                return false;
            }
            if (mc.world.getBlockState(lastTarget).isAir()) {
                setOverride(false, "IS_AIR");
                return false;
            }
            return true;
        }

        public static void onBreakBlock(BlockPos pos, boolean ret) {
            if (override && ret) setOverride(false, "SUCCESS", pos);
        }

        public static class BreakBlockResult {
            public static final BreakBlockResult UNAVAILABLE = new BreakBlockResult("UNAVAILABLE", null);
            /**
             * Can be one of the following reason:<br>
             * * `SUCCESS` - the block has been destroyed on client side, no promise that the server will accept it.<br>
             *      for example if the block is in protected area, or the server is lagging very hard, then the break will get ignored.<br>
             * * `CANCELLED` - if {@link xyz.wagyourtail.jsmacros.client.api.helper.InteractionManagerHelper#cancelBreakBlock()} was called.<br>
             * * `INTERRUPTED` - if block breaking was interrupted by attack key. (left mouse by default)<br>
             * * `NOT_BREAKING` - if the block breaking was invalid for some reason.<br>
             * * `RESET` - if interaction proxy has been reset or not in a world.<br>
             * * `NO_OVERRIDE` - if block breaking override was false but there's remaining callback for some reason.<br>
             * * `IS_AIR` - if the targeted block was air.<br>
             * * `NO_TARGET` - if there's no targeted block.<br>
             * * `TARGET_LOST` - if there's no longer a targeted block.<br>
             * * `TARGET_CHANGE` - if the targeted block has changed.<br>
             * * `UNAVAILABLE` - if InteractionManager was unavailable. (mc.interactionManager is null)<br>
             * * null - unknown. (proxy method has been called outside the api)<br>
             */
            @DocletReplaceReturn("BreakBlockResult$Reason | null")
            @DocletDeclareType(name = "BreakBlockResult$Reason", type = "'SUCCESS' | 'CANCELLED' | 'INTERRUPTED' | 'NOT_BREAKING' | 'RESET' | 'NO_OVERRIDE' | 'IS_AIR' | 'NO_TARGET' | 'TARGET_LOST' | 'TARGET_CHANGE' | 'UNAVAILABLE'")
            @Nullable
            public final String reason;
            @Nullable
            public final BlockPosHelper pos;

            public BreakBlockResult(@Nullable String reason, @Nullable BlockPosHelper pos) {
                this.reason = reason;
                this.pos = pos;
            }

            @Override
            public String toString() {
                return "BreakBlockResult:{\"reason\": "
                        + (reason == null ? "null" : "\"" + reason + "\"")
                        + ", \"pos\": "
                        + (pos == null ? "null" : pos.toString())
                        + "}";
            }

        }

    }

    public static class Interact {
        private static boolean override = false;
        private static boolean releaseCheck = false;

        public static void setOverride(boolean value) {
            if (override && !value) releaseCheck = true;
            else if (value) releaseCheck = false;
            override = value;
            if (value) mc.doItemUse();
        }

        public static boolean isInteracting() {
            return override;
        }

        public static void ensureInteracting(int cooldown) {
            if (mc.player == null) return;
            if (mc.options.useKey.isPressed()) override = false;
            if (isInteracting() && cooldown == 0 && !mc.player.isUsingItem()) mc.doItemUse();
            else if (releaseCheck) {
                if (mc.interactionManager != null && mc.player.isUsingItem() && !mc.options.useKey.isPressed()) mc.interactionManager.stopUsingItem(mc.player);
                releaseCheck = false;
            }
        }

    }

}
