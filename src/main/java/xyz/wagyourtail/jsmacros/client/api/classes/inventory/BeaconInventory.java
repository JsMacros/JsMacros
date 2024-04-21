package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.client.access.IBeaconScreen;

import java.util.Arrays;
import java.util.Optional;

/**
 * @since 1.5.1
 */
@SuppressWarnings("unused")
public class BeaconInventory extends Inventory<BeaconScreen> {
    protected BeaconInventory(BeaconScreen inventory) {
        super(inventory);
    }

    /**
     * @return
     * @since 1.5.1
     */
    public int getLevel() {
        return inventory.getScreenHandler().getProperties();
    }

    /**
     * @return
     * @since 1.5.1
     */
    @DocletReplaceReturn("BeaconStatusEffect | null")
    @Nullable
    public String getFirstEffect() {
        StatusEffect effect = ((IBeaconScreen) inventory).jsmacros_getPrimaryEffect();
        return effect == null ? null : Registries.STATUS_EFFECT.getId(effect).toString();
    }

    /**
     * @return
     * @since 1.5.1
     */
    @DocletReplaceReturn("BeaconStatusEffect | null")
    @Nullable
    public String getSecondEffect() {
        StatusEffect effect = ((IBeaconScreen) inventory).jsmacros_getSecondaryEffect();
        return effect == null ? null : Registries.STATUS_EFFECT.getId(effect).toString();
    }

    /**
     * @param id
     * @return
     * @since 1.5.1
     */
    @DocletReplaceParams("id: BeaconStatusEffect")
    public boolean selectFirstEffect(String id) {
        StatusEffect matchEffect;
        for (int i = 0; i < Math.min(getLevel(), 2); i++) {
            matchEffect = Arrays.stream(BeaconBlockEntity.EFFECTS_BY_LEVEL[i]).filter(e -> Registry.STATUS_EFFECT.getId(e).toString().equals(id)).findFirst().orElse(null);
            if (matchEffect != null) {
                ((IBeaconScreen) inventory).jsmacros_setPrimaryEffect(matchEffect);
                return true;
            }
        }
        return false;
    }

    /**
     * @param id
     * @return
     * @since 1.5.1
     */
    @DocletReplaceParams("id: BeaconStatusEffect")
    public boolean selectSecondEffect(String id) {
        if (getLevel() >= 3) {
            StatusEffect primaryEffect = ((IBeaconScreen) inventory).jsmacros_getPrimaryEffect();
            if (primaryEffect != null && Registry.STATUS_EFFECT.getId(primaryEffect).toString().equals(id)) {
                ((IBeaconScreen) inventory).jsmacros_setSecondaryEffect(primaryEffect);
                return true;
            }
            StatusEffect matchEffect;
            for (int i = 0; i < getLevel(); i++) {
                matchEffect = Arrays.stream(BeaconBlockEntity.EFFECTS_BY_LEVEL[i]).filter(e -> Registry.STATUS_EFFECT.getId(e).toString().equals(id)).findFirst().orElse(null);
                if (matchEffect != null) {
                    if (primaryEffect != null && matchEffect.equals(StatusEffects.REGENERATION)) {
                        ((IBeaconScreen) inventory).jsmacros_setSecondaryEffect(matchEffect);
                    } else {
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
     * @return
     * @since 1.5.1
     */
    public boolean applyEffects() {
        if (inventory.getScreenHandler().hasPayment()) {
            mc.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(StatusEffect.getRawId(((IBeaconScreen) inventory).jsmacros_getPrimaryEffect()), StatusEffect.getRawId(((IBeaconScreen) inventory).jsmacros_getSecondaryEffect())));
            player.closeHandledScreen();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("BeaconInventory:{}");
    }

}
