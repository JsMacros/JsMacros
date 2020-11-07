package xyz.wagyourtail.jsmacros.mixins.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.api.events.EventAirChange;
import xyz.wagyourtail.jsmacros.api.events.EventDamage;
import xyz.wagyourtail.jsmacros.api.events.EventEXPChange;
import xyz.wagyourtail.jsmacros.api.events.EventSignEdit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ClientPlayerEntity.class)
class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    
    @Shadow
    @Final
    protected MinecraftClient client;
    
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    
    @Override
    public void setAir(int air) {
        if (air % 20 == 0) new EventAirChange(air);
        super.setAir(air);
    }
    
    @Inject(at = @At("HEAD"), method="setExperience")
    public void onSetExperience(float progress, int total, int level, CallbackInfo info) {
        new EventEXPChange(progress, total, level);
    }
    
    @Inject(at = @At("TAIL"), method="applyDamage")
    private void onApplyDamage(DamageSource source, float amount, final CallbackInfo info) {
        new EventDamage(source, this.getHealth(), amount);
    }
    
    @Inject(at = @At("HEAD"), method="openEditSignScreen", cancellable= true)
    public void onOpenEditSignScreen(SignBlockEntity sign, CallbackInfo info) {
        List<String> lines = new ArrayList<>(Arrays.asList(new String[]{"", "", "", ""}));
        final EventSignEdit event = new EventSignEdit(lines, sign.getPos().getX(), sign.getPos().getY(), sign.getPos().getZ());
        lines = event.signText;
        if (event.closeScreen) {
            for (int i = 0; i < 4; ++i) {
                sign.setTextOnRow(i, new LiteralText(lines.get(i)));
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
            final SignEditScreen signScreen = new SignEditScreen(sign);
            client.openScreen(signScreen);
            for (int i = 0; i < 4; ++i) {
                ((ISignEditScreen)signScreen).jsmacros_setLine(i, lines.get(i));
            }
            info.cancel();
        }
    }
    
    
    // IGNORE
    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }
}
