package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DisplayEntity.class)
public interface MixinDisplayEntity {

    @Invoker
    Brightness callGetBrightnessUnpacked();

    @Invoker
    float callGetViewRange();

    @Invoker
    float callGetShadowRadius();

    @Invoker
    float callGetShadowStrength();

    @Invoker
    float callGetDisplayWidth();

    @Invoker
    int callGetGlowColorOverride();

    @Invoker
    float callGetDisplayHeight();

    @Invoker
    DisplayEntity.BillboardMode callGetBillboardMode();

}
