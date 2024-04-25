package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEnchantmentsComponent.class)
public interface MixinItemEnchantmentsComponent {

    @Accessor
    boolean getShowInTooltip();

}
