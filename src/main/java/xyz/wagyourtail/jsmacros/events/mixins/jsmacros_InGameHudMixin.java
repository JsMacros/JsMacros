package xyz.wagyourtail.jsmacros.events.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import xyz.wagyourtail.jsmacros.events.HeldItemCallback;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;

@Mixin(InGameHud.class)
class jsmacros_InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;
    
    @Shadow
    private ItemStack currentStack;
    
    @Inject(at = @At(value="FIELD", target="currentStack:Lnet/minecraft/item/ItemStack;", opcode=Opcodes.PUTFIELD), method="tick")
    public void jsmacros_tick(CallbackInfo info) {
        
        ItemStack mainHand = null;
        try {
            mainHand = client.player.inventory.getMainHandStack();
        } catch (NullPointerException e) {
            return;
        }
        
        if (mainHand != currentStack) 
            HeldItemCallback.EVENT.invoker().interact(new ItemStackHelper(mainHand));
    }
}
