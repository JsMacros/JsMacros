package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import xyz.wagyourtail.jsmacros.client.access.ILoomScreen;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @since 1.5.1
 */
public class LoomInventory extends Inventory<LoomScreen> {

    protected LoomInventory(LoomScreen inventory) {
        super(inventory);
    }

    private static BannerPattern byName(String id) {
        BannerPattern[] var1 = BannerPattern.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            BannerPattern bannerPattern = var1[var3];
            if (bannerPattern.getName().equals(id)) {
                return bannerPattern;
            }
        }

        return null;
    }

    /**
     * @since 1.5.1
     * @param name
     * @return success
     */
     @Deprecated
    public boolean selectPatternName(String name) {
        BannerPattern pattern = byName(name);
        if (pattern == null) return false;
        int id = pattern.ordinal() - 1;
        if (id >= 0 && id <= BannerPattern.LOOM_APPLICABLE_COUNT && ((ILoomScreen)inventory).jsmacros_canApplyDyePattern() &&
            inventory.getContainer().onButtonClick(player, id)) {
            assert mc.interactionManager != null;
            mc.interactionManager.clickButton(syncId, id);
            return true;
        }
        return false;
    }

    /**
     * @since 1.7.0
     * @return available pattern ids
     */
    public List<String> listAvailablePatterns() {
        return Arrays.stream(BannerPattern.values()).map(BannerPattern::getId).collect(Collectors.toList());
    }

    /**
     * @since 1.5.1
     * @param id
     * @return success
     */
    public boolean selectPatternId(String id) {
        BannerPattern pattern = BannerPattern.byId(id);
        if (pattern == null) return false;
        int iid = pattern.ordinal();
        if (iid <= BannerPattern.LOOM_APPLICABLE_COUNT && ((ILoomScreen) inventory).jsmacros_canApplyDyePattern() &&
            inventory.getContainer().onButtonClick(player, iid)) {
            assert mc.interactionManager != null;
            mc.interactionManager.clickButton(syncId, iid);
            return true;
        }
        return false;
    }

    /**
     * @param index
     * @since 1.5.1
     * @return success
     */
    public boolean selectPattern(int index) {
        if (index >= 0 && index <= BannerPattern.LOOM_APPLICABLE_COUNT && ((ILoomScreen) inventory).jsmacros_canApplyDyePattern() &&
            inventory.getContainer().onButtonClick(player, index)) {
            assert mc.interactionManager != null;
            mc.interactionManager.clickButton(syncId, index);
            return true;
        }
        return false;
    }

}
