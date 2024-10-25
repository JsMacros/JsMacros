package xyz.wagyourtail.jsmacros.client.api.helper;

import net.minecraft.predicate.NbtPredicate;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @since 1.9.1
 */
public class NbtPredicateHelper extends BaseHelper<NbtPredicate> {

    public NbtPredicateHelper(NbtPredicate base) {
        super(base);
    }

    /**
     * @since 1.9.1
     */
    public boolean test(EntityHelper<?> entity) {
        return base.test(entity.getRaw());
    }

    /**
     * @since 1.9.1
     */
    public boolean test(ItemStackHelper itemStack) {
        return base.test(itemStack.getRaw());
    }

    /**
     * @since 1.9.1
     */
    public boolean test(NBTElementHelper<?> nbtElement) {
        return base.test(nbtElement.getRaw());
    }

}
