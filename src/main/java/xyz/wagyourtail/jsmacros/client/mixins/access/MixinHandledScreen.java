package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
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

@Mixin(HandledScreen.class)
public class MixinHandledScreen<T extends ScreenHandler> extends Screen implements IInventory {

    protected MixinHandledScreen(Text title) {
        super(title);
    }

    @Shadow
    private Slot getSlotAt(double x, double y) {
        return null;
    }

    @Shadow
    @Final
    protected T handler;

    @Shadow
    protected int x;

    @Shadow
    protected int y;

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public Slot jsmacros_getSlotUnder(double x, double y) {
        return getSlotAt(x, y);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V", shift = At.Shift.BEFORE))
    public void onDrawForeground(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!Core.getInstance().config.getOptions(ClientConfigV2.class).showSlotIndexes) {
            return;
        }
        matrices.push();
        // Make them render in front of the slot sprites, but still behind the tooltip
        matrices.translate(0, 0, 150);
        for (int i = 0; i < handler.slots.size(); i++) {
            Slot slot = handler.slots.get(i);
            if (slot.isEnabled()) {
                textRenderer.draw(matrices, String.valueOf(i), slot.x, slot.y, 0xFFFFFF);
            }
        }
        matrices.pop();
    }

}
