package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.village.TradeOffer;
import xyz.wagyourtail.jsmacros.client.access.IMerchantScreen;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.TradeOfferHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class VillagerInventory extends Inventory<MerchantScreen> {

    protected VillagerInventory(MerchantScreen inventory) {
        super(inventory);
    }

    /**
     * select the trade by its index
     *
     * @param index
     * @return self for chaining
     * @since 1.3.1
     */
    public VillagerInventory selectTrade(int index) {
        ((IMerchantScreen) inventory).jsmacros_selectIndex(index);
        return this;
    }

    /**
     * @return
     * @since 1.3.1
     */
    public int getExperience() {
        return inventory.getScreenHandler().getExperience();
    }

    /**
     * @return
     * @since 1.3.1
     */
    public int getLevelProgress() {
        return inventory.getScreenHandler().getLevelProgress();
    }

    /**
     * @return
     * @since 1.3.1
     */
    public int getMerchantRewardedExperience() {
        return inventory.getScreenHandler().getMerchantRewardedExperience();
    }

    /**
     * @return
     * @since 1.3.1
     */
    public boolean canRefreshTrades() {
        return inventory.getScreenHandler().canRefreshTrades();
    }

    /**
     * @return
     * @since 1.3.1
     */
    public boolean isLeveled() {
        return inventory.getScreenHandler().isLeveled();
    }

    /**
     * @return list of trade offers
     * @since 1.3.1
     */
    public List<TradeOfferHelper> getTrades() {
        List<TradeOfferHelper> offers = new LinkedList<>();
        int i = -1;
        for (TradeOffer offer : inventory.getScreenHandler().getRecipes()) {
            offers.add(new TradeOfferHelper(offer, ++i, this));
        }
        return offers;
    }

    @Override
    public String toString() {
        return String.format("VillagerInventory:{\"level\": %d}", getLevelProgress());
    }

}
