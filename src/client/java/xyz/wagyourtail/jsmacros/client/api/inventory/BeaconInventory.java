package xyz.wagyourtail.jsmacros.client.api.inventory;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.access.IBeaconScreen;

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
        RegistryEntry<StatusEffect> effect = ((IBeaconScreen) inventory).jsmacros_getPrimaryEffect();
        return effect == null ? null : Registries.STATUS_EFFECT.getId(effect.value()).toString();
    }

    /**
     * @return
     * @since 1.5.1
     */
    @DocletReplaceReturn("BeaconStatusEffect | null")
    @Nullable
    public String getSecondEffect() {
        RegistryEntry<StatusEffect> effect = ((IBeaconScreen) inventory).jsmacros_getSecondaryEffect();
        return effect == null ? null : Registries.STATUS_EFFECT.getId(effect.value()).toString();
    }

    /**
     * @param id
     * @return
     * @since 1.5.1
     */
    @DocletReplaceParams("id: BeaconStatusEffect")
    public boolean selectFirstEffect(String id) {
        RegistryEntry<StatusEffect> matchEffect;
        for (int i = 0; i < Math.min(getLevel(), 2); i++) {
            matchEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL.get(i).stream().filter(e -> Registries.STATUS_EFFECT.getId(e.value()).toString().equals(id)).findFirst().orElse(null);
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
            RegistryEntry<StatusEffect> primaryEffect = ((IBeaconScreen) inventory).jsmacros_getPrimaryEffect();
            if (primaryEffect != null && Registries.STATUS_EFFECT.getId(primaryEffect.value()).toString().equals(id)) {
                ((IBeaconScreen) inventory).jsmacros_setSecondaryEffect(primaryEffect);
                return true;
            }
            RegistryEntry<StatusEffect> matchEffect;
            for (int i = 0; i < getLevel(); i++) {
                matchEffect = BeaconBlockEntity.EFFECTS_BY_LEVEL.get(i).stream().filter(e -> Registries.STATUS_EFFECT.getId(e.value()).toString().equals(id)).findFirst().orElse(null);
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
            mc.getNetworkHandler().sendPacket(new UpdateBeaconC2SPacket(
                Optional.ofNullable(((IBeaconScreen) inventory).jsmacros_getPrimaryEffect()),
                Optional.ofNullable(((IBeaconScreen) inventory).jsmacros_getSecondaryEffect())
            ));
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
