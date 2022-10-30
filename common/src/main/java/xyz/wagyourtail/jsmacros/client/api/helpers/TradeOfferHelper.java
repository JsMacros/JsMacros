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
        ItemStack first = base.getFirstStack();
        if (!first.isEmpty()) items.add(new ItemStackHelper(first));
        ItemStack second = base.getSecondStack();
        if (second != null && !second.isEmpty()) items.add(new ItemStackHelper(second));
        return items;
    }
    
    /**
     * @return output item that will be recieved
     */
    public ItemStackHelper getOutput() {
        return new ItemStackHelper(base.getResult());
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
     * @return experience gained for trade
     */
    public int getExperience() {
        return 0;
    }

    /**
     * @return current price adjustment, negative is discount.
     */
    public int getCurrentPriceAdjustment() {
        return 0;
    }
    
    public String toString() {
        return String.format("TradeOffer:{\"inputs\":%s, \"output\":%s}", getInput().toString(), getOutput().toString());
    }
}
