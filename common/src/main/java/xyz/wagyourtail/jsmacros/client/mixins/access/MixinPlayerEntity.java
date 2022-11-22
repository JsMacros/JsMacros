package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinPlayerEntity extends EntityLivingBase {


    public MixinPlayerEntity(World world) {
        super(world);
    }

    @Inject(at = @At("HEAD"), method = "getName", cancellable = true)
    private void getName(CallbackInfoReturnable<IChatComponent> cir) {
        if (!getCustomName().isEmpty()) {
            cir.setReturnValue(new ChatComponentText(getCustomName()));
        }
    }
}
