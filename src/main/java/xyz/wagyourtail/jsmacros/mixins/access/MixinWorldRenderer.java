package xyz.wagyourtail.jsmacros.mixins.access;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.api.functions.FHud;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Inject(at = @At("TAIL"), method = "render")
    public void render(CallbackInfo info) {
        
        synchronized (FHud.renders) {
            for (Draw3D d : FHud.renders) {
                try {
                    d.render();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
