package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.compat.interfaces.IEntityTrackingSoundInstance;

@Mixin(EntityTrackingSoundInstance.class)
public class jsmacros_EntityTrackingSoundInstance implements IEntityTrackingSoundInstance {
    
    @Shadow
    @Final
    private Entity entity;

    @Override
    public Entity getEntity() {
        return entity;
    }
}
