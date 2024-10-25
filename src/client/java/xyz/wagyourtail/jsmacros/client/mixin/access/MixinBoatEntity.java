package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(BoatEntity.class)
public interface MixinBoatEntity {

    @Accessor
    BoatEntity.Location getLocation();

}
