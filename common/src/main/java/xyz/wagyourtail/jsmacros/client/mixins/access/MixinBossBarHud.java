package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.class_2839;
import net.minecraft.client.class_2840;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(class_2839.class)
public interface MixinBossBarHud {
    @Accessor("field_13306")
    Map<UUID, class_2840> getBossBars();
}
