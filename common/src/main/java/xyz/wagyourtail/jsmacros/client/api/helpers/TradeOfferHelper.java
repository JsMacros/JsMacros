package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import xyz.wagyourtail.jsmacros.client.api.classes.VillagerInventory;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TradeOfferHelper extends BaseHelper<TradeOffer> {
    private final VillagerInventory inv;
    private final int index;
    public TradeOfferHelper(TradeOffer base, int index, VillagerInventory inv) {
        super(base);
        this.inv = inv;
        this.index = index;
    }
    
    /**
     * @return list of input items required
     */
    public List<ItemStackHelper> getInput() {
        List<ItemStackHelper> items = new ArrayList<>();
        ItemStack first = base.getAdjustedFirstBuyItem();
        if (!first.isEmpty()) items.add(new ItemStackHelper(first));
        ItemStack second = base.getSecondBuyItem();
        if (second != null && !second.isEmpty()) items.add(new ItemStackHelper(second));
        return items;
    }
    
    /**
     * @return output item that will be recieved
     */
    public ItemStackHelper getOutput() {
        return new ItemStackHelper(base.getSellItem());
    }
    
    /**
     * select trade offer on screen
     */
    public void select() {
        if (inv != null && MinecraftClient.getInstance().currentScreen == inv.getRawContainer())
            inv.selectTrade(index);
    }
    
    /**
     * @return
     */
    public boolean isAvailable() {
        return !base.isDisabled();
    }
    
    /**
     * @return trade offer as nbt tag
     */
    public NBTElementHelper<?> getNBT() {
        return NBTElementHelper.resolve(base.toNbt());
    }
    
    /**
     * @return current number of uses
     */
    public int getUses() {
        return base.getUses();
    }
    
    /**
     * @return max uses before it locks
     */
    public int getMaxUses() {
        return base.getMaxUses();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public boolean shouldRewardPlayerExperience() {
        return base.shouldRewardPlayerExperience();
    }
    
    /**
     * @return experience gained for trade
     */
    public int getExperience() {
        return base.getMerchantExperience();
    }
    
    /**
     * @return current price adjustment, negative is discount.
     */
    public int getCurrentPriceAdjustment() {
        return base.getAdjustedFirstBuyItem().getCount() - base.getOriginalFirstBuyItem().getCount();
    }

    /**
     * @return the original priced item without any adjustments due to rewards or demand
     *
     * @since 1.9.0
     */
    public ItemStackHelper getOriginalFirstInput() {
        return base.getOriginalFirstBuyItem().isEmpty() ? new ItemStackHelper(ItemStack.EMPTY) : new ItemStackHelper(base.getOriginalFirstBuyItem());
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getSpecialPrice() {
        return base.getSpecialPrice();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public float getPriceMultiplier() {
        return base.getPriceMultiplier();
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getDemandBonus() {
        return base.getDemandBonus();
    }

    public String toString() {
        return String.format("TradeOffer:{\"inputs\":%s, \"output\":%s}", getInput().toString(), getOutput().toString());
    }
}
