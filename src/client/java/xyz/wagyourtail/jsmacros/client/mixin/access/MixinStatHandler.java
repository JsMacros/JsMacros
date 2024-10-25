package xyz.wagyourtail.jsmacros.client.mixin.access;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatHandler.class)
public interface MixinStatHandler {
    @Accessor
    Object2IntMap<Stat<?>> getStatMap();

}
