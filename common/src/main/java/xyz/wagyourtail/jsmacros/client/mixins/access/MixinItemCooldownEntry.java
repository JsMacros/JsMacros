package xyz.wagyourtail.jsmacros.client.mixins.access;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IItemCooldownEntry;

@Mixin(targets = "net.minecraft.entity.player.ItemCooldownManager$Entry")
public class MixinItemCooldownEntry implements IItemCooldownEntry {

    @Shadow
    @Final
    int startTick;

    @Shadow
    @Final
    int endTick;

    @Override
    public int getStartTick() {
        return startTick;
    }

    @Override
    public int getEndTick() {
        return endTick;
    }

}
