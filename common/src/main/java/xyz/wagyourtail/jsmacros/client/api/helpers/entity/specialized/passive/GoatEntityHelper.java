package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive;

import net.minecraft.entity.passive.GoatEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class GoatEntityHelper extends AnimalEntityHelper<GoatEntity> {

    public GoatEntityHelper(GoatEntity base) {
        super(base);
    }
    
    public boolean isScreaming() {
        return base.isScreaming();
    }
    
    public boolean hasLeftHorn() {
        return base.hasLeftHorn();
    }
    
    public boolean hasRightHorn() {
        return base.hasRightHorn();
    }
    
    public boolean hasHorns() {
        return hasLeftHorn() || hasRightHorn();
    }
    
}
