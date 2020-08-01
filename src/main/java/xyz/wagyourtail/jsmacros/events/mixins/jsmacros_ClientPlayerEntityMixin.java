package xyz.wagyourtail.jsmacros.events.mixins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
import xyz.wagyourtail.jsmacros.compat.interfaces.ISignEditScreen;
import xyz.wagyourtail.jsmacros.events.AirChangeCallback;
import xyz.wagyourtail.jsmacros.events.DamageCallback;
import xyz.wagyourtail.jsmacros.events.SignEditCallback;

@Mixin(ClientPlayerEntity.class)
class jsmacros_ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    
    @Shadow
    @Final
    protected MinecraftClient client;
    
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    
    @Override
    public void setAir(int air) {
        if (air % 20 == 0) AirChangeCallback.EVENT.invoker().interact(air);
        super.setAir(air);
    }
    
    @Inject(at = @At("TAIL"), method="applyDamage")
    private void jsmacros_applyDamage(DamageSource source, float amount, final CallbackInfo info) {
        DamageCallback.EVENT.invoker().interact(source, this.getHealth(), amount);
    }
    
    @Inject(at = @At("HEAD"), method="openEditSignScreen", cancellable= true)
    public void openEditSignScreen(SignBlockEntity sign, CallbackInfo info) {
        List<String> lines = new ArrayList<String>(Arrays.asList(new String[]{"", "", "", ""}));
        if (SignEditCallback.EVENT.invoker().interact(lines, sign.getPos().getX(), sign.getPos().getY(), sign.getPos().getZ())) {
            networkHandler.sendPacket(new UpdateSignC2SPacket(sign.getPos(), lines.get(0), lines.get(1), lines.get(2), lines.get(3)));
            info.cancel();
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
            SignEditScreen signScreen = new SignEditScreen(sign);
            client.openScreen(signScreen);
            for (int i = 0; i < 4; ++i) {
                ((ISignEditScreen)signScreen).setLine(i, lines.get(i));
            }
            info.cancel();
        }
     }
    
    
    // IGNORE
    public jsmacros_ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
        // TODO Auto-generated constructor stub
    }
    //
}
