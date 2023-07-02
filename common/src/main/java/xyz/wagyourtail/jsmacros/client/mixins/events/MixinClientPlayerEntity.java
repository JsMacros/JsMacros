package xyz.wagyourtail.jsmacros.client.mixins.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IClientPlayerEntity;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;
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
        super(world, profile, null);
    }

    @Shadow
    public abstract boolean shouldSlowDown();

    @Shadow public abstract void sendChatMessage(String message, @Nullable Text preview);

    @Override
    public void setAir(int air) {
        if (air % 20 == 0) new EventAirChange(air);
        super.setAir(air);
    }

    @Inject(at = @At("HEAD"), method = "setExperience")
    public void onSetExperience(float progress, int total, int level, CallbackInfo info) {
        new EventEXPChange(progress, total, level, this.experienceProgress, this.totalExperience, this.experienceLevel);
    }

    @Inject(at = @At("HEAD"), method = "openEditSignScreen", cancellable = true)
    public void onOpenEditSignScreen(SignBlockEntity sign, CallbackInfo info) {
        List<String> lines = new ArrayList<>(Arrays.asList("", "", "", ""));
        final EventSignEdit event = new EventSignEdit(lines, sign.getPos().getX(), sign.getPos().getY(), sign.getPos().getZ());
        lines = event.signText;
        if (event.closeScreen) {
            for (int i = 0; i < 4; ++i) {
                sign.setTextOnRow(i, Text.literal(lines.get(i)));
            }
            sign.markDirty();
            networkHandler.sendPacket(new UpdateSignC2SPacket(sign.getPos(), lines.get(0), lines.get(1), lines.get(2), lines.get(3)));
            info.cancel();
            return;
        }
        //this part to not info.cancel is here for more compatibility with other mods.
        boolean cancel = false;
        for (String line : lines) {
            if (!line.equals("")) {
                cancel = true;
                break;
            }
        } //else
        if (cancel) {
            final SignEditScreen signScreen = new SignEditScreen(sign, true);
            client.setScreen(signScreen);
            for (int i = 0; i < 4; ++i) {
                ((ISignEditScreen) signScreen).jsmacros_setLine(i, lines.get(i));
            }
            info.cancel();
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
        new EventRiding(true, entity);
    }

    @Inject(method = "dismountVehicle", at = @At("HEAD"))
    public void onStopRiding(CallbackInfo ci) {
        if (this.getVehicle() != null)
            new EventRiding(false, this.getVehicle());
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    public void onDropSelected(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        int selectedHotbarIndex = getInventory().selectedSlot;
        EventDropSlot event = new EventDropSlot(null, 36 + selectedHotbarIndex, entireStack);
        if (event.cancel) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    public void onSendChatMessage(String message, Text preview, CallbackInfo ci) {
        final String result = new EventSendMessage(message).message;
        if (result == null || result.equals("")) {
            ci.cancel();
        } else if (!result.equals(message)) {
            ci.cancel();
            ((IClientPlayerEntity) this).jsmacros_sendChatMessageBypass(result);
        }
    }

    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    public void onSendCommand(String command, Text preview, CallbackInfo ci) {
        final String result = new EventSendMessage("/" + command).message;
        if (result == null || result.equals("")) {
            ci.cancel();
        } else if (!result.equals("/" + command)) {
            ci.cancel();
            ((IClientPlayerEntity) this).jsmacros_sendChatMessageBypass(result);
        }
    }
}
