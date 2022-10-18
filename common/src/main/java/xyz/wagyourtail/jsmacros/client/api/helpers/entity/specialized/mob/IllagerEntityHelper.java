package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob;

import net.minecraft.entity.mob.IllagerEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class IllagerEntityHelper<T extends IllagerEntity> extends MobEntityHelper<T> {

    public IllagerEntityHelper(T base) {
        super(base);
    }

    public boolean isCelebrating() {
        return base.isCelebrating();
    }

    public String getState() {
        //yarn and mojang mappings have the same names
        return switch (base.getState()) {
            case CROSSED -> "CROSSED";
            case ATTACKING -> "ATTACKING";
            case SPELLCASTING -> "SPELLCASTING";
            case BOW_AND_ARROW -> "BOW_AND_ARROW";
            case CROSSBOW_HOLD -> "CROSSBOW_HOLD";
            case CROSSBOW_CHARGE -> "CROSSBOW_CHARGE";
            case CELEBRATING -> "CELEBRATING";
            case NEUTRAL -> "NEUTRAL";
        };
    }

}
