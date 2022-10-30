package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import xyz.wagyourtail.jsmacros.client.access.IBeaconScreen;

import java.util.Arrays;


/**
 * @since 1.5.1
 */
public class BeaconInventory extends Inventory<BeaconScreen> {
    protected BeaconInventory(BeaconScreen inventory) {
        super(inventory);
    }

    /**
     * @since 1.5.1
     * @return
     */
    public int getLevel() {
        return ((IBeaconScreen) inventory).jsmacros_getLevel();
    }

    /**
     * @since 1.5.1
     * @return
     */
    public String getFirstEffect() {
        StatusEffect effect = ((IBeaconScreen) inventory).jsmacros_getPrimaryEffect();
        return StatusEffect.field_3164.getIdentifier(effect).toString();
    }

    /**
     * @since 1.5.1
     * @return
     */
    public String getSecondEffect() {
        StatusEffect effect = ((IBeaconScreen) inventory).jsmacros_getSecondaryEffect();
        return StatusEffect.field_3164.getIdentifier(effect).toString();
    }

    /**
     * @param id
     * @since 1.5.1
     * @return
     */
    public boolean selectFirstEffect(String id) {
        StatusEffect matchEffect;
        for (int i = 0; i < Math.min(getLevel(), 2); i++) {
            matchEffect = Arrays.stream(BeaconBlockEntity.field_5017[i]).filter(e -> StatusEffect.field_3164.getIdentifier(e).toString().equals(id)).findFirst().orElse(null);
            if (matchEffect != null) {
                ((IBeaconScreen) inventory).jsmacros_setPrimaryEffect(matchEffect);
                return true;
            }
        }
        return false;
    }

    /**
     * @param id
     * @since 1.5.1
     * @return
     */
    public boolean selectSecondEffect(String id) {
        if (getLevel() >= 3) {
            StatusEffect primaryEffect = ((IBeaconScreen) inventory).jsmacros_getPrimaryEffect();
            if (primaryEffect != null &&
                StatusEffect.field_3164.getIdentifier(primaryEffect).toString().equals(id)) {
                ((IBeaconScreen) inventory).jsmacros_setSecondaryEffect(primaryEffect);
                return true;
            }
            StatusEffect matchEffect;
            for (int i = 0; i < getLevel(); i++) {
                matchEffect = Arrays.stream(BeaconBlockEntity.field_5017[i]).filter(e -> StatusEffect.field_3164.getIdentifier(e).toString().equals(id)).findFirst().orElse(null);
                if (matchEffect != null) {
                    if (primaryEffect != null && matchEffect.equals(StatusEffects.REGENERATION))
                        ((IBeaconScreen) inventory).jsmacros_setSecondaryEffect(matchEffect);
                    else {
                        ((IBeaconScreen) inventory).jsmacros_setPrimaryEffect(matchEffect);
                        ((IBeaconScreen) inventory).jsmacros_setSecondaryEffect(matchEffect);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @since 1.5.1
     * @return
     */
    public boolean applyEffects() {
        return ((IBeaconScreen) inventory).jsmacros_sendBeaconPacket();
    }
}
