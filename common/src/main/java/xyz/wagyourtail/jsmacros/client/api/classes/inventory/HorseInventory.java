package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;

import xyz.wagyourtail.jsmacros.client.access.IHorseScreen;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class HorseInventory extends Inventory<HorseScreen> {

    private final AbstractHorseEntity horse;

    protected HorseInventory(HorseScreen inventory) {
        super(inventory);
        this.horse = (AbstractHorseEntity) ((IHorseScreen) inventory).jsmacros_getEntity();
    }

    /**
     * @return {@code true} if the horse can be saddled, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean canBeSaddled() {
        return horse.canBeSaddled();
    }

    /**
     * @return {@code true} if the horse is saddled, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSaddled() {
        return horse.isSaddled();
    }

    /**
     * @return the saddle item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getSaddle() {
        return getSlot(0);
    }

    /**
     * @return {@code true} if the horse can eqiup armor, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasArmorSlot() {
        return horse.hasArmorSlot();
    }

    /**
     * @return the armor item.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getArmor() {
        return getSlot(1);
    }

    /**
     * @return {code true} if the horse has equipped a chest, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasChest() {
        return horse instanceof AbstractDonkeyEntity donkey && donkey.hasChest();
    }

    /**
     * @return the inventory size of the horse.
     *
     * @since 1.8.4
     */
    public int getInventorySize() {
        return horse instanceof AbstractDonkeyEntity donkey ? donkey.getInventoryColumns() * 3 : 0;
    }

    /**
     * @return a list of the horse's inventory items.
     *
     * @since 1.8.4
     */
    public List<ItemStackHelper> getHorseInventory() {
        final int otherSlots = 2;
        return IntStream.range(otherSlots, getInventorySize() + otherSlots).mapToObj(this::getSlot).toList();
    }

}
