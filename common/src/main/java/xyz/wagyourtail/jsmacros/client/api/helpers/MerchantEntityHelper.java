package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.Trader;
import net.minecraft.village.TradeOffer;
import xyz.wagyourtail.jsmacros.client.access.IMerchantEntity;

import java.util.ArrayList;
import java.util.List;

public class MerchantEntityHelper<T extends LivingEntity & Trader> extends LivingEntityHelper<T> {
    
    public MerchantEntityHelper(T e) {
        super(e);
    }
    
    /**
     * these might not work... depends on the data the server sends, maybe just singleplayer.
     * @return
     */
    public List<TradeOfferHelper> getTrades() {
        List<TradeOfferHelper> offers = new ArrayList<>();
        for (TradeOffer offer : base.getOffers(MinecraftClient.getInstance().player)) {
            offers.add(new TradeOfferHelper(offer, 0, null));
        }
        return offers;
    }

    public List<TradeOfferHelper> refreshTrades() {
        ((IMerchantEntity) base).jsmacros_refreshOffers();
        return getTrades();
    }
    
    /**
     * @return
     */
    public int getExperience() {
        return 0;
    }
    
    /**
     * @return
     */
    public boolean hasCustomer() {
        return base.getCurrentCustomer() != null;
    }

    @Override
    public String toString() {
        return "Merchant" + super.toString();
    }

}
