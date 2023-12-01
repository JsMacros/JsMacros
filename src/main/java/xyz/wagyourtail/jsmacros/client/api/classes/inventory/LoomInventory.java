package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import xyz.wagyourtail.jsmacros.client.access.ILoomScreen;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @since 1.5.1
 */
@SuppressWarnings("unused")
public class LoomInventory extends Inventory<LoomScreen> {

    protected LoomInventory(LoomScreen inventory) {
        super(inventory);
    }

    /**
     * @param name
     * @return success
     * @since 1.5.1
     */
    @Deprecated
    public boolean selectPatternName(String name) {
        throw new NullPointerException("This method is deprecated. Use selectPatternId instead.");
    }

    /**
     * @return available pattern ids
     * @since 1.7.0
     */
    public List<String> listAvailablePatterns() {
        return Arrays.stream(BannerPattern.values()).map(BannerPattern::getId).collect(Collectors.toList());
    }

    /**
     * @param id
     * @return success
     * @since 1.5.1
     */
    public boolean selectPatternId(String id) {
        BannerPattern pattern = BannerPattern.byId(id);
        if (pattern == null) return false;
        int iid = pattern.ordinal();
        if (iid <= BannerPattern.LOOM_APPLICABLE_COUNT && ((ILoomScreen) inventory).jsmacros_canApplyDyePattern() &&
            inventory.getScreenHandler().onButtonClick(player, iid)) {
            assert mc.interactionManager != null;
            mc.interactionManager.clickButton(syncId, iid);
            return true;
        }
        return false;
    }

    /**
     * @param index
     * @return success
     * @since 1.5.1
     */
    public boolean selectPattern(int index) {
        if (index >= 0 && index <= BannerPattern.LOOM_APPLICABLE_COUNT && ((ILoomScreen) inventory).jsmacros_canApplyDyePattern() &&
            inventory.getScreenHandler().onButtonClick(player, index)) {
            assert mc.interactionManager != null;
            mc.interactionManager.clickButton(syncId, index);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("LoomInventory:{}");
    }

}
