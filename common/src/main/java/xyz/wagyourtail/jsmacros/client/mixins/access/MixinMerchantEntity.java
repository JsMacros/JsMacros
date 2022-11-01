package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.village.TraderOfferList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IMerchantEntity;

@Mixin(AbstractTraderEntity.class)
public class MixinMerchantEntity implements IMerchantEntity {
    @Shadow @Nullable protected TraderOfferList offers;

    @Override
    public void jsmacros_refreshOffers() {
        this.offers = null;
    }

}
