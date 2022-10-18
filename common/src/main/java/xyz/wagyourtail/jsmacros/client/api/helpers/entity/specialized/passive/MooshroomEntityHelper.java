package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive;

import net.minecraft.entity.passive.MooshroomEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class MooshroomEntityHelper extends AnimalEntityHelper<MooshroomEntity> {

    public MooshroomEntityHelper(MooshroomEntity base) {
        super(base);
    }

    /**
     * @since 1.8.4
     * @return
     */
    public boolean isShearable() {
        return base.isShearable();
    }
    
    public boolean isRed() {
        return base.getMooshroomType() == MooshroomEntity.Type.RED;
    }
    
    public boolean isBrown() {
        return base.getMooshroomType() == MooshroomEntity.Type.BROWN;
    }
    
}
