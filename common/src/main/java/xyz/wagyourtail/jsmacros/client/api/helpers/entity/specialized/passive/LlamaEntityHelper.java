package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive;

import net.minecraft.entity.passive.LlamaEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.DyeColorHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class LlamaEntityHelper<T extends LlamaEntity> extends DonkeyEntityHelper<T> {

    public LlamaEntityHelper(T base) {
        super(base);
    }

    /**
     * @return the variant of this llama.
     *
     * @since 1.8.4
     */
    public int getVariant() {
        return base.getVariant();
    }

    /**
     * @return the color of this llama's carpet.
     *
     * @since 1.8.4
     */
    public DyeColorHelper getCarpetColor() {
        return new DyeColorHelper(base.getCarpetColor());
    }

    /**
     * @return the strength of this llama.
     *
     * @since 1.8.4
     */
    public int getStrength() {
        return base.getStrength();
    }

    /**
     * @return {@code true} if this llama belongs to a wandering trader, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isTraderLlama() {
        return base.isTrader();
    }

}
