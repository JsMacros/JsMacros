package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.PufferfishEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class PufferfishEntityHelper extends FishEntityHelper<PufferfishEntity> {

    public PufferfishEntityHelper(PufferfishEntity base) {
        super(base);
    }

    /**
     * A state of 0 means the fish is deflated, a state of 1 means the fish is inflated and a state
     * of 2 means the fish is fully inflated.
     *
     * @return the puff state of this pufferfish.
     * @since 1.8.4
     */
    public int getSize() {
        return base.getPuffState();
    }

}
