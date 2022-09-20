package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.item.FoodComponent;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class FoodComponentHelper extends BaseHelper<FoodComponent> {

    public FoodComponentHelper(FoodComponent base) {
        super(base);
    }

    /**
     * @return the amount of hunger this food restores.
     *
     * @since 1.9.0
     */
    public int getHunger() {
        return base.getHunger();
    }

    /**
     * @return the amount of saturation this food restores.
     *
     * @since 1.9.0
     */
    public float getSaturation() {
        return base.getSaturationModifier();
    }

    /**
     * @return {@code true} if this food can be eaten by wolves, {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean isMeat() {
        return base.isMeat();
    }

    /**
     * @return {@code true} if this food can be eaten even when the player is not hungry,
     *         {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean isAlwaysEdible() {
        return base.isAlwaysEdible();
    }

    /**
     * @return {@code true} if the food can be eaten faster than usual, {@code false} otherwise.
     *
     * @since 1.9.0
     */
    public boolean isSnack() {
        return base.isSnack();
    }

    /**
     * @return a map of status effects and their respective probabilities.
     *
     * @since 1.9.0
     */
    public Map<StatusEffectHelper, Float> getStatusEffects() {
        Object2FloatArrayMap<StatusEffectHelper> effects = new Object2FloatArrayMap<>();
        base.getStatusEffects().forEach(e -> effects.put(new StatusEffectHelper(e.getFirst()), e.getSecond().floatValue()));
        return effects;
    }

}
