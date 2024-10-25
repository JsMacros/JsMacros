package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import xyz.wagyourtail.doclet.DocletDeclareType;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinSpellcastingIllagerEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class SpellcastingIllagerEntityHelper<T extends SpellcastingIllagerEntity> extends IllagerEntityHelper<T> {

    public SpellcastingIllagerEntityHelper(T base) {
        super(base);
    }

    /**
     * @return {@code true} if this spell caster is currently casting a spell, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isCastingSpell() {
        return base.isSpellcasting();
    }

    /**
     * @return the spell this spell caster is currently casting.
     * @since 1.8.4
     */
    @DocletReplaceReturn("IllagerSpell")
    @DocletDeclareType(name = "IllagerSpell", type = "'NONE' | 'SUMMON_VEX' | 'FANGS' | 'WOLOLO' | 'DISAPPEAR' | 'BLINDNESS' | 'ERROR'")
    public String getCastedSpell() {
        switch (base.getDataTracker().get(((MixinSpellcastingIllagerEntityHelper) base).getSpellKey())) {
            case 0:
                return "NONE";
            case 1:
                return "SUMMON_VEX";
            case 2:
                return "FANGS";
            case 3:
                return "WOLOLO";
            case 4:
                return "DISAPPEAR";
            case 5:
                return "BLINDNESS";
            default:
                return "ERROR";
        }
    }

}
