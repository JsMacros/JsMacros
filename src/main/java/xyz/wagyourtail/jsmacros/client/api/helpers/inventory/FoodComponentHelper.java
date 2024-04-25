package xyz.wagyourtail.jsmacros.client.api.helpers.inventory;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import net.minecraft.component.type.FoodComponent;
import xyz.wagyourtail.jsmacros.client.api.helpers.StatusEffectHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Map;

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

    /**
     * @return {@code true} if the food can be eaten faster than usual, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isFastFood() {
        return base.eatSeconds() < 1.6F;
    }

    /**
     * @return a map of status effects and their respective probabilities.
     * @since 1.8.4
     */
    public Map<StatusEffectHelper, Float> getStatusEffects() {
        Object2FloatArrayMap<StatusEffectHelper> effects = new Object2FloatArrayMap<>();
        base.effects().forEach(e -> effects.put(new StatusEffectHelper(e.effect()), e.probability()));
        return effects;
    }

    @Override
    public String toString() {
        return String.format("FoodComponentHelper:{\"hunger\": %d, \"saturation\": %f, \"alwaysEdible\": %b, \"fastFood\": %b}", getHunger(), getSaturation(), isAlwaysEdible(), isFastFood());
    }

}
