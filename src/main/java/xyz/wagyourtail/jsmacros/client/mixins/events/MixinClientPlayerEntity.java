package xyz.wagyourtail.jsmacros.client.mixins.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;
import xyz.wagyourtail.jsmacros.client.api.event.impl.inventory.EventDropSlot;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventAirChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventEXPChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventRiding;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventSignEdit;
import xyz.wagyourtail.jsmacros.client.movement.MovementQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ClientPlayerEntity.class)
abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    @Shadow
    public Input input;
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    @Shadow
    @Final
    protected MinecraftClient client;

    // IGNORE
    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract boolean shouldSlowDown();

    @Override
    public void setAir(int air) {
        if (air % 20 == 0) {
            new EventAirChange(air).trigger();
        }
        super.setAir(air);
    }

    @Inject(at = @At("HEAD"), method = "setExperience")
    public void onSetExperience(float progress, int total, int level, CallbackInfo info) {
        new EventEXPChange(progress, total, level, this.experienceProgress, this.totalExperience, this.experienceLevel).trigger();
    }

    @Inject(at = @At("HEAD"), method = "openEditSignScreen", cancellable = true)
    public void onOpenEditSignScreen(SignBlockEntity sign, boolean front, CallbackInfo ci) {
        List<String> lines = new ArrayList<>(Arrays.asList("", "", "", ""));
        final EventSignEdit event = new EventSignEdit(lines, sign.getPos().getX(), sign.getPos().getY(), sign.getPos().getZ());
        event.trigger();
        lines = event.signText;
        if (lines == null) lines = Arrays.asList("", "", "", "");
        if (event.closeScreen || event.isCanceled()) {
            SignText text = new SignText();
            for (int i = 0; i < 4; ++i) {
                text = text.withMessage(i, Text.of(lines.get(i)));
            }
            sign.setText(text, front);
            sign.markDirty();
            networkHandler.sendPacket(new UpdateSignC2SPacket(sign.getPos(), front, lines.get(0), lines.get(1), lines.get(2), lines.get(3)));
            ci.cancel();
            return;
        }
        //this part to not info.cancel is here for more compatibility with other mods.
        boolean cancel = false;
        for (String line : lines) {
            if (!line.isEmpty()) {
                cancel = true;
                break;
            }
        } //else
        if (cancel) {
            final SignEditScreen signScreen = new SignEditScreen(sign, front, true);
            client.setScreen(signScreen);
            for (int i = 0; i < 4; ++i) {
                //noinspection DataFlowIssue
                ((ISignEditScreen) signScreen).jsmacros_setLine(i, lines.get(i));
            }
            ci.cancel();
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onMovement(Lnet/minecraft/client/input/Input;)V"))
    public void overwriteInputs(CallbackInfo ci) {
        PlayerInput moveInput = MovementQueue.tick(client.player);
        if (moveInput == null) {
            return;
        }
        this.input.movementForward = moveInput.movementForward;
        this.input.movementSideways = moveInput.movementSideways;
        this.input.jumping = moveInput.jumping;
        this.input.sneaking = moveInput.sneaking;
        this.client.options.sprintKey.setPressed(moveInput.sprinting);
        this.setYaw(moveInput.yaw);
        this.setPitch(moveInput.pitch);

        if (this.shouldSlowDown()) {
            // Don't ask me, this is the way minecraft does it.
            this.input.movementSideways = (float) ((double) this.input.movementSideways * 0.3D);
            this.input.movementForward = (float) ((double) this.input.movementForward * 0.3D);
        }
    }

    @Inject(method = "startRiding", at = @At(value = "RETURN", ordinal = 1))
    public void onStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        new EventRiding(true, entity).trigger();
    }

    @Inject(method = "dismountVehicle", at = @At("HEAD"))
    public void onStopRiding(CallbackInfo ci) {
        if (this.getVehicle() != null) {
            new EventRiding(false, this.getVehicle()).trigger();
        }
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    public void onDropSelected(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        int selectedHotbarIndex = getInventory().selectedSlot;
        EventDropSlot event = new EventDropSlot(null, 36 + selectedHotbarIndex, entireStack);
        event.trigger();
        if (event.isCanceled()) {
            cir.setReturnValue(false);
        }
    }

}
