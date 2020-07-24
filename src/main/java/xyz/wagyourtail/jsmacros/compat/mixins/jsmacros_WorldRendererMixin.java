package xyz.wagyourtail.jsmacros.compat.mixins;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import xyz.wagyourtail.jsmacros.runscript.classes.Draw3D;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public class jsmacros_WorldRendererMixin {
    @Inject(at = @At("TAIL"), method = "render")
    public void jsmacros_render(CallbackInfo info) {
        
        ArrayList<Draw3D> renders;
        
        try {
            renders = new ArrayList<>(hudFunctions.renders);
        } catch (Exception e) {
            return;
        }
        
        for (Draw3D d : renders) {
            d.render();
        }
    }
}
