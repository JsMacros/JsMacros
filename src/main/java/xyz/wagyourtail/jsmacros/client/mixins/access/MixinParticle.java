package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Particle.class)
public interface MixinParticle {

    @Accessor("x")
    double getX();

    @Accessor("y")
    double getY();

    @Accessor("z")
    double getZ();

    @Accessor
    void setPrevPosX(double value);

    @Accessor
    void setPrevPosY(double value);

    @Accessor
    void setPrevPosZ(double value);

    @Accessor
    double getVelocityX();

    @Accessor
    double getVelocityY();

    @Accessor
    double getVelocityZ();

    @Accessor
    int getAge();

    @Accessor
    float getRed();

    @Accessor
    float getGreen();

    @Accessor
    float getBlue();

    @Accessor
    float getAlpha();

    @Invoker
    void invokeSetAlpha(float alpha);

}
