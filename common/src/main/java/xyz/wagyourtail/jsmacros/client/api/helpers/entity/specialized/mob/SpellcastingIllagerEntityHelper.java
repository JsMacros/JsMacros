package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob;

import net.minecraft.entity.mob.SpellcastingIllagerEntity;

import xyz.wagyourtail.jsmacros.client.mixins.access.MixinSpellcastingIllagerEntityHelper;

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
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean isCastingSpell() {
        return base.isSpellcasting();
    }

    /**
     * @return the spell this spell caster is currently casting.
     *
     * @since 1.8.4
     */
    public String getCastedSpell() {
        return switch (base.getDataTracker().get(((MixinSpellcastingIllagerEntityHelper) base).getSpellKey())) {
            case 0 -> "NONE";
            case 1 -> "SUMMON_VEX";
            case 2 -> "FANGS";
            case 3 -> "WOLOLO";
            case 4 -> "DISAPPEAR";
            case 5 -> "BLINDNESS";
            default -> "ERROR";
        };
    }

}