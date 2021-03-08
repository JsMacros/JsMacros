package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.village.TradeOffer;

import java.util.ArrayList;
import java.util.List;

public class MerchantEntityHelper extends LivingEntityHelper<MerchantEntity> {
    
    public MerchantEntityHelper(MerchantEntity e) {
        super(e);
    }
    
    /**
     * @return
     */
    public List<TradeOfferHelper> getTrades() {
        List<TradeOfferHelper> offers = new ArrayList<>();
        for (TradeOffer offer : base.getOffers()) {
            offers.add(new TradeOfferHelper(offer, 0, null));
        }
        return offers;
    }
    
    /**
     * @return
     */
    public int getExperience() {
        return base.getExperience();
    }
    
    /**
     * @return
     */
    public boolean hasCustomer() {
        return base.hasCustomer();
    }
}
