package xyz.wagyourtail.jsmacros.client.mixin.access;

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

    // Don't make this static, it will disable the compile and reload feature!
    @Accessor("LOYALTY")
    TrackedData<Byte> getLoyalty();

}
