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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.doclet.DocletEnumType;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;

import java.util.List;
import java.util.ArrayList;
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

        private static @Nullable HitResult override = null;
        private static @Nullable Entity overrideEntity = null;
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

        public static void setTargetBlock(@Nullable BlockPos pos, int direction) {
            setTarget(pos == null ? null : new BlockHitResult(pos.toCenterPos(), Direction.values()[direction], pos, false));
        }

        public static void setTarget(@Nullable HitResult value) {
            override = value;
            if (value != null && value.getType() == HitResult.Type.ENTITY) overrideEntity = ((EntityHitResult) value).getEntity();
            else overrideEntity = null;
            onUpdate(0, null);
            Break.isBreaking();
        }

        public static void setTargetMissed() {
            setTarget(MISSED);
        }

        public static boolean hasOverride() {
            return override != null;
        }

        public static void onUpdate(float tickDelta, @Nullable CallbackInfo ci) {
            if (override != null) {
                boolean shouldMiss = false;
                if (overrideEntity != null) {
                    if (!overrideEntity.isAlive()) {
                        setTarget(null);
                        return;
                    }
                } else if ((checkAir || checkShape) && override.getType() == HitResult.Type.BLOCK) {
                    if (mc.world == null) return;
                    BlockPos pos = ((BlockHitResult) override).getBlockPos();
                    BlockState state = mc.world.getBlockState(pos);
                    if (checkAir && state.isAir()) {
                        if (!clearIfIsAir) shouldMiss = true;
                        else {
                            setTarget(null);
                            return;
                        }
                    }
                    if (checkShape && !state.isAir() && state.getOutlineShape(mc.world, pos).isEmpty()) {
                        if (!clearIfEmptyShape) shouldMiss = true;
                        else {
                            setTarget(null);
                            return;
                        }
                    }
                }
                if (checkDistance && !isInRange(tickDelta)) {
                    if (!clearIfOutOfRange) shouldMiss = true;
                    else {
                        setTarget(null);
                        return;
                    }
                }
                if (override == null) return;
                if (ci != null) ci.cancel();
                if (shouldMiss) {
                    mc.crosshairTarget = MISSED;
                    mc.targetedEntity = null;
                } else {
                    mc.crosshairTarget = override;
                    mc.targetedEntity = overrideEntity;
                }
            }
        }

        public static boolean isInRange(float tickDelta) {
            if (override == null || mc.player == null || mc.interactionManager == null) return false;
            if (override.getType() == HitResult.Type.MISS) return true;

            Vec3d campos = mc.player.getCameraPosVec(tickDelta);
            double reach = mc.interactionManager.getReachDistance();
            // might need to rewrite entity distance check
            // not sure how to handle modded entity reach distance
            if (override.getPos().isInRange(campos, reach)) return true;

            if (override.getType() != HitResult.Type.BLOCK) return false;
            BlockPos pos = ((BlockHitResult) override).getBlockPos();
            return campos.squaredDistanceTo(
                    MathHelper.clamp(campos.x, pos.getX(), pos.getX() + 1),
                    MathHelper.clamp(campos.y, pos.getY(), pos.getY() + 1),
                    MathHelper.clamp(campos.z, pos.getZ(), pos.getZ() + 1)
            ) < reach * reach;
        }

    }

    public static class Break {
        private static boolean override = false;
        private static final List<Consumer<BreakBlockResult>> callbacks = new ArrayList<>();
        private static @Nullable BlockPos lastTarget = null;

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

        public static void addCallback(Consumer<BreakBlockResult> callback) {
            if (callback != null) mc.execute(() -> callbacks.add(callback));
        }

        private static void runCallback(@Nullable String reason, @Nullable BlockPos pos) {
            if (callbacks.isEmpty()) return;
            runCallback(new BreakBlockResult(reason, pos == null ? null : new BlockPosHelper(pos)));
        }

        private static void runCallback(BreakBlockResult result) {
            if (!callbacks.isEmpty()) mc.execute(() -> {
                callbacks.forEach(cb -> {
                    try {
                        cb.accept(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                callbacks.clear();
            });
        }

        public static boolean isBreaking() {
            if (mc.world == null) setOverride(false, "RESET");
            if (!override) {
                if (!callbacks.isEmpty()) runCallback("NO_OVERRIDE", null);
                return false;
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
            @DocletReplaceReturn("BreakBlockResult$Reason | null")
            @DocletEnumType(name = "BreakBlockResult$Reason", type = "'SUCCESS' | 'CANCELLED' | 'INTERRUPTED' | 'NOT_BREAKING' | 'RESET' | 'NO_OVERRIDE' | 'IS_AIR' | 'NO_SHAPE' | 'NO_TARGET' | 'TARGET_LOST' | 'TARGET_CHANGE'")
            public final @Nullable String reason;
            public final @Nullable BlockPosHelper pos;

            private BreakBlockResult(@Nullable String reason, @Nullable BlockPosHelper pos) {
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
            if (value) ((IMinecraftClient) mc).jsmacros_doItemUse();
        }

        public static boolean isInteracting() {
            return override;
        }

        public static void ensureInteracting(int cooldown) {
            if (mc.player == null) return;
            if (mc.options.useKey.isPressed()) override = false;
            if (isInteracting() && cooldown == 0 && !mc.player.isUsingItem()) ((IMinecraftClient) mc).jsmacros_doItemUse();
            else if (releaseCheck) {
                if (mc.interactionManager != null && mc.player.isUsingItem() && !mc.options.useKey.isPressed()) mc.interactionManager.stopUsingItem(mc.player);
                releaseCheck = false;
            }
        }

    }

}
