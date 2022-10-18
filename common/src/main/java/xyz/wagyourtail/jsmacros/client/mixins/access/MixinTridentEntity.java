package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.TridentEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(TridentEntity.class)
public interface MixinTridentEntity {
    
    @Accessor("LOYALTY")
    static TrackedData<Byte> getLoyalty() {
        throw new RuntimeException("Mixin was not applied correctly!");
    }
    
}