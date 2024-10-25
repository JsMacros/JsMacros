package xyz.wagyourtail.jsmacros.client.api.helper.world.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.VillagerInventory;
import xyz.wagyourtail.jsmacros.client.api.helper.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
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
        ItemStack first = base.getDisplayedFirstBuyItem();
        if (!first.isEmpty()) {
            items.add(new ItemStackHelper(first));
        }
        ItemStack second = base.getDisplayedSecondBuyItem();
        if (second != null && !second.isEmpty()) {
            items.add(new ItemStackHelper(second));
        }
        return items;
    }

    /**
     * The returned item uses the adjusted price, in form of its stack size and will be empty
     * {@link ItemStackHelper#isEmpty()} if the first input doesn't exist.
     *
     * @return the first input item.
     * @since 1.8.4
     */
    public ItemStackHelper getLeftInput() {
        return new ItemStackHelper(base.getFirstBuyItem().itemStack());
    }

    /**
     * The returned item uses the adjusted price, in form of its stack size and will be empty
     * {@link ItemStackHelper#isEmpty()} if the first input doesn't exist.
     *
     * @return the second input item.
     * @since 1.8.4
     */
    public ItemStackHelper getRightInput() {
        if (base.getSecondBuyItem().isEmpty()) {
            return new ItemStackHelper(ItemStack.EMPTY);
        }
        return new ItemStackHelper(base.getSecondBuyItem().get().itemStack());
    }

    /**
     * @return output item that will be received
     */
    public ItemStackHelper getOutput() {
        return new ItemStackHelper(base.getSellItem());
    }

    /**
     * @return the index if this trade in the given villager inventory.
     * @since 1.8.4
     */
    public int getIndex() {
        return index;
    }

    /**
     * select trade offer on screen
     */
    public TradeOfferHelper select() {
        if (inv != null && MinecraftClient.getInstance().currentScreen == inv.getRawContainer()) {
            inv.selectTrade(index);
        }
        return this;
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
    @DocletReplaceReturn("NBTElementHelper$NBTCompoundHelper")
    public NBTElementHelper<?> getNBT() {
        return NBTElementHelper.wrap(TradeOffer.CODEC.encodeStart(RegistryHelper.NBT_OPS_UNLIMITED, base).getOrThrow());
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
     * @return {@code true} if after a successful trade xp will be summoned, {@code false}
     * otherwise.
     * @since 1.8.4
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
        return base.getDisplayedFirstBuyItem().getCount() - base.getOriginalFirstBuyItem().getCount();
    }

    /**
     * @return the original priced item without any adjustments due to rewards or demand.
     * @since 1.8.4
     */
    public ItemStackHelper getOriginalFirstInput() {
        return new ItemStackHelper(base.getOriginalFirstBuyItem());
    }

    /**
     * @return the original price of the item without any adjustments due to rewards or demand.
     * @since 1.8.4
     */
    public int getOriginalPrice() {
        return base.getOriginalFirstBuyItem().getCount();
    }

    /**
     * @return the adjusted price of the item.
     * @since 1.8.4
     */
    public int getAdjustedPrice() {
        return base.getDisplayedFirstBuyItem().getCount();
    }

    /**
     * A negative value is a discount and means that the player has a good reputation with the
     * villager, while a positive value is a premium. Hero of the village will always affect and
     * reduce this value.
     *
     * @return the special price multiplier, which affects the price of the item depending on the
     * player's reputation with the villager.
     * @since 1.8.4
     */
    public int getSpecialPrice() {
        return base.getSpecialPrice();
    }

    /**
     * A higher price multiplier means that the price of these trades can vary much more than normal
     * ones. The default value is 0.05 and 0.2 for armor and tools.
     *
     * @return the price multiplier, which is only depended on the type of trade.
     * @since 1.8.4
     */
    public float getPriceMultiplier() {
        return base.getPriceMultiplier();
    }

    /**
     * The demand bonus is globally applied to all trades of this type for all villagers and
     * players. It is used to increase the price of trades that are in high demand. The demand is
     * only calculated and updated on restock. Note that a villager can always restock, even if no
     * items were traded with him. Updating the demand is done with the following formula:
     * <pre> {@code demand = demand + 2 * uses - maxUses} </pre>
     * <p> Thus trading only half of the max uses will not increase the demand.
     * The demand is also capped at 0, so it can not decrease the price.
     *
     * @return the demand bonus for this trade.
     * @since 1.8.4
     */
    public int getDemandBonus() {
        return base.getDemandBonus();
    }

    @Override
    public String toString() {
        return String.format("TradeOfferHelper:{\"inputs\": %s, \"output\": %s}", getInput().toString(), getOutput().toString());
    }

}
