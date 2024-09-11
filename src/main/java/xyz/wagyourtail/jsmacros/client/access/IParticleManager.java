package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Queue;

public interface IParticleManager {

    Map<ParticleTextureSheet, Queue<Particle>> jsmacros_getParticles();

    @Nullable
    <T extends ParticleEffect> Particle jsmacros_createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

    void jsmacros_clearParticles();

}
