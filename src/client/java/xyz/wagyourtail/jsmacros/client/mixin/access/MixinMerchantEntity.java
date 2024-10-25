package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.village.TradeOfferList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IMerchantEntity;

@Mixin(MerchantEntity.class)
public class MixinMerchantEntity implements IMerchantEntity {
    @Shadow
    @Nullable
    protected TradeOfferList offers;

    @Override
    public void jsmacros_refreshOffers() {
        this.offers = null;
    }

}
