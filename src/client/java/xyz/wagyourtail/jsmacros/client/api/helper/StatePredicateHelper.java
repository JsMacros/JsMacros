package xyz.wagyourtail.jsmacros.client.api.helper;

import net.minecraft.predicate.StatePredicate;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.FluidStateHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @since 1.9.1
 */
public class StatePredicateHelper extends BaseHelper<StatePredicate> {

    public StatePredicateHelper(StatePredicate base) {
        super(base);
    }

    /**
     * @since 1.9.1
     */
    public boolean test(BlockStateHelper state) {
        return base.test(state.getRaw());
    }

    /**
     * @since 1.9.1
     */
    public boolean test(FluidStateHelper state) {
        return base.test(state.getRaw());
    }

}
