package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;

import xyz.wagyourtail.jsmacros.runscript.classes.Draw3D;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public class jsmacros_WorldRendererMixin {
    @Inject(at = @At("TAIL"), method = "render")
    public void jsmacros_render(CallbackInfo info) {

        for (Draw3D d : ImmutableList.copyOf(hudFunctions.renders)) {
            try {
                d.render();
            } catch (Exception e) {}
        }
    }
}
