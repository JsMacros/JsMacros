package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.village.TradeOffer;
import xyz.wagyourtail.jsmacros.client.access.IMerchantScreen;
import xyz.wagyourtail.jsmacros.client.api.helpers.TradeOfferHelper;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class VillagerInventory extends Inventory<MerchantScreen> {
    
    protected VillagerInventory(MerchantScreen inventory) {
        super(inventory);
    }
    
    /**
    *  select the trade by it's index
    *
     * @param index
     *
     * @return self for chaining
     */
    public VillagerInventory selectTrade(int index) {
        ((IMerchantScreen)inventory).selectIndex(index);
        return this;
    }
    
    /**
     * @return
     */
    public int getExperience() {
        return inventory.getScreenHandler().getExperience();
    }
    
    /**
     * @return
     */
    public int getLevelProgress() {
        return inventory.getScreenHandler().getLevelProgress();
    }
    
    /**
     * @return
     */
    public int getMerchantRewardedExperience() {
        return inventory.getScreenHandler().getMerchantRewardedExperience();
    }
    
    /**
     * @return
     */
    public boolean canRefreshTrades() {
        return inventory.getScreenHandler().canRefreshTrades();
    }
    
    /**
     * @return
     */
    public boolean isLeveled() {
        return inventory.getScreenHandler().isLeveled();
    }
    
    /**
     * @return list of trade offers
     */
    public List<TradeOfferHelper> getTrades() {
        List<TradeOfferHelper> offers = new LinkedList<>();
        int i = -1;
        for (TradeOffer offer : inventory.getScreenHandler().getRecipes()) {
            offers.add(new TradeOfferHelper(offer, ++i, this));
        }
        return offers;
    }
}
