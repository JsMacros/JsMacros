package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.access.IInventory;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;

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
    public int jsmacros$getX() {
        return x;
    }

    @Override
    public int jsmacros$getY() {
        return y;
    }

    @Override
    public Slot jsmacros_getSlotUnder(double x, double y) {
        return getSlotAt(x, y);
    }

    @Inject(method = "drawSlot", at = @At("TAIL"))
    private void onDrawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        if (!JsMacrosClient.clientCore.config.getOptions(ClientConfigV2.class).showSlotIndexes) return;

        if (!slot.isEnabled()) return;

        int index = handler.slots.indexOf(slot);
        context.drawText(MinecraftClient.getInstance().textRenderer, String.valueOf(index), slot.x, slot.y, 0xCCFFFFFF, false);
    }

}
