package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraftforge.fml.common.registry.GameData;
import xyz.wagyourtail.jsmacros.client.access.IBeaconScreen;

import java.util.Arrays;


/**
 * @since 1.5.1
 */
public class BeaconInventory extends Inventory<GuiBeacon> {
    protected BeaconInventory(GuiBeacon inventory) {
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
        Potion effect = ((IBeaconScreen) inventory).jsmacros_getPrimaryEffect();
        return GameData.getPotionRegistry().getNameForObject(effect).toString();
    }

    /**
     * @since 1.5.1
     * @return
     */
    public String getSecondEffect() {
        Potion effect = ((IBeaconScreen) inventory).jsmacros_getSecondaryEffect();
        return GameData.getPotionRegistry().getNameForObject(effect).toString();
    }

    /**
     * @param id
     * @since 1.5.1
     * @return
     */
    public boolean selectFirstEffect(String id) {
        Potion matchEffect;
        for (int i = 0; i < Math.min(getLevel(), 2); i++) {
            matchEffect = Arrays.stream(TileEntityBeacon.field_146009_a[i]).filter(e -> GameData.getPotionRegistry().getNameForObject(e).toString().equals(id)).findFirst().orElse(null);
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
            Potion primaryEffect = ((IBeaconScreen) inventory).jsmacros_getPrimaryEffect();
            if (primaryEffect != null &&
                GameData.getPotionRegistry().getNameForObject(primaryEffect).toString().equals(id)) {
                ((IBeaconScreen) inventory).jsmacros_setSecondaryEffect(primaryEffect);
                return true;
            }
            Potion matchEffect;
            for (int i = 0; i < getLevel(); i++) {
                matchEffect = Arrays.stream(TileEntityBeacon.field_146009_a[i]).filter(e -> GameData.getPotionRegistry().getNameForObject(e).toString().equals(id)).findFirst().orElse(null);
                if (matchEffect != null) {
                    if (primaryEffect != null && matchEffect.equals(Potion.REGENERATION))
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
