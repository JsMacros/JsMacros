package xyz.wagyourtail.jsmacros.client.mixins.access;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.JsonIntSerializable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StatHandler.class)
public interface MixinStatHandler {
    @Accessor
    Map<Stat, JsonIntSerializable> getField_9047();
}
