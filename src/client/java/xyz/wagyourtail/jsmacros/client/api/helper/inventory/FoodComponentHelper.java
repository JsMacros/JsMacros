package xyz.wagyourtail.jsmacros.client.api.helper.inventory;

import net.minecraft.component.type.FoodComponent;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FoodComponentHelper extends BaseHelper<FoodComponent> {

    public FoodComponentHelper(FoodComponent base) {
        super(base);
    }

    /**
     * @return the amount of hunger this food restores.
     * @since 1.8.4
     */
    public int getHunger() {
        return base.nutrition();
    }

    /**
     * @return the amount of saturation this food restores.
     * @since 1.8.4
     */
    public float getSaturation() {
        return base.saturation();
    }

    /**
     * @return {@code true} if this food can be eaten even when the player is not hungry,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAlwaysEdible() {
        return base.canAlwaysEat();
    }

    @Override
    public String toString() {
        return String.format("FoodComponentHelper:{\"hunger\": %d, \"saturation\": %f, \"alwaysEdible\": %b}", getHunger(), getSaturation(), isAlwaysEdible());
    }

}
