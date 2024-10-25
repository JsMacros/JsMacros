package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.LlamaEntity;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.DyeColorHelper;

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
     * @since 1.8.4
     */
    @DocletReplaceReturn("LlamaVariant")
    public String getVariant() {
        return base.getVariant().asString();
    }

    /**
     * @return the color of this llama's carpet.
     * @since 1.8.4
     */
    public DyeColorHelper getCarpetColor() {
        return new DyeColorHelper(base.getCarpetColor());
    }

    /**
     * @return the strength of this llama.
     * @since 1.8.4
     */
    public int getStrength() {
        return base.getStrength();
    }

    /**
     * @return {@code true} if this llama belongs to a wandering trader, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isTraderLlama() {
        return base.isTrader();
    }

}
