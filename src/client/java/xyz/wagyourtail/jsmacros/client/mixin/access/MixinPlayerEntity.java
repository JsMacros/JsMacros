package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    @Unique
    private Text realCustomName;


    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        realCustomName = name;
    }

    @Inject(at = @At("HEAD"), method = "getName", cancellable = true)
    private void getName(CallbackInfoReturnable<Text> cir) {
        if (realCustomName != null) {
            cir.setReturnValue(realCustomName);
        }
    }

}
