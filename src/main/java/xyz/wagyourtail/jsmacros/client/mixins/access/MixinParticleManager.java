package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IParticleManager;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.ParticleHelper;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager implements IParticleManager {

    @Shadow
    @Final
    private Map<ParticleTextureSheet, Queue<Particle>> particles;

    @Shadow
    @Final
    private Queue<Particle> newParticles;

    @Shadow
    @Nullable
    protected abstract <T extends ParticleEffect> Particle createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

    @Shadow
    protected abstract void clearParticles();

    @Override
    public Map<ParticleTextureSheet, Queue<Particle>> jsmacros_getParticles() {
        return particles;
    }

    @Override
    @Nullable
    public <T extends ParticleEffect> Particle jsmacros_createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        return createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

    @Override
    public void jsmacros_clearParticles() {
        clearParticles();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void onTick(CallbackInfo ci) {
        ParticleHelper.Accessor.onTick(newParticles);
    }

}
