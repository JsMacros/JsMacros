package xyz.wagyourtail.jsmacros.client.mixins.access;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.access.IInventory;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.core.Core;

@Mixin(ContainerScreen.class)
public class MixinHandledScreen<T extends Container> extends Screen implements IInventory {

    protected MixinHandledScreen(Text title) {
        super(title);
    }

    @Shadow
    private Slot getSlotAt(double x, double y) {
        return null;
    }

    @Shadow
    @Final
    protected T container;

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Override
    public Slot jsmacros_getSlotUnder(double x, double y) {
        return getSlotAt(x, y);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;drawForeground(II)V", shift = At.Shift.BEFORE))
    public void onDrawForeground(int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!Core.getInstance().config.getOptions(ClientConfigV2.class).showSlotIndexes) {
            return;
        }
        RenderSystem.pushMatrix();
        // Make them render in front of the slot sprites, but still behind the tooltip
        RenderSystem.translatef(0, 0, 150);
        for (int i = 0; i < container.slots.size(); i++) {
            Slot slot = container.slots.get(i);
//            if (slot.isEnabled()) {
                font.draw(String.valueOf(i), slot.xPosition, slot.yPosition, 0xFFFFFF);
//            }
        }
        RenderSystem.popMatrix();
    }
}