package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.class_2911;
import net.minecraft.client.class_2839;
import net.minecraft.client.class_2840;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventBossbar;

import java.util.Map;
import java.util.UUID;

@Mixin(class_2839.class)
public class MixinBossStatus {

    @Shadow @Final private Map<UUID, class_2840> field_13306;

    @Inject(at = @At("TAIL"), method = "method_12170")
    private void onBossStatus(class_2911 arg, CallbackInfo ci) {
        new EventBossbar(arg.method_12634().name(), arg.method_12631(), field_13306.get(arg.method_12631()));
    }
    
}
