package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BannerPatternTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import xyz.wagyourtail.jsmacros.client.access.ILoomScreen;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @since 1.5.1
 */
public class LoomInventory extends Inventory<LoomScreen> {

    protected LoomInventory(LoomScreen inventory) {
        super(inventory);
    }

    private List<RegistryEntry<BannerPattern>> getPatternsFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return (List) Registry.BANNER_PATTERN.getEntryList(BannerPatternTags.NO_ITEM_REQUIRED).map(ImmutableList::copyOf).orElse(ImmutableList.of());
        } else {
            Item var3 = stack.getItem();
            if (var3 instanceof BannerPatternItem) {
                BannerPatternItem bannerPatternItem = (BannerPatternItem)var3;
                return (List)Registry.BANNER_PATTERN.getEntryList(bannerPatternItem.getPattern()).map(ImmutableList::copyOf).orElse(ImmutableList.of());
            } else {
                return List.of();
            }
        }
    }

    /**
     * @since 1.5.1
     * @param name
     * @return success
     */
     @Deprecated
    public boolean selectPatternName(String name) {
        throw new NullPointerException("This method is deprecated. Use selectPatternId instead.");
    }

    /**
     * @since 1.7.0
     * @return available pattern ids
     */
    public List<String> listAvailableIds() {
        List<RegistryEntry<BannerPattern>> patterns = getPatternsFor(inventory.getScreenHandler().getSlot(2).getStack());
        return patterns.stream().map(e -> e.value().getId()).collect(Collectors.toList());
    }

    /**
     * @since 1.5.1
     * @param id
     * @return success
     */
    public boolean selectPatternId(String id) {
        List<RegistryEntry<BannerPattern>> patterns = getPatternsFor(inventory.getScreenHandler().getSlot(2).getStack());
        RegistryEntry<BannerPattern> pattern = patterns.stream().filter(e -> e.value().getId().equals(id)).findFirst().orElse(null);

        int iid = patterns.indexOf(pattern);
        if (pattern != null && ((ILoomScreen) inventory).jsmacros_canApplyDyePattern() &&
            inventory.getScreenHandler().onButtonClick(player, iid)) {
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
    List<RegistryEntry<BannerPattern>> patterns = getPatternsFor(inventory.getScreenHandler().getSlot(2).getStack());

        if (index >= 0 && index <= patterns.size() && ((ILoomScreen) inventory).jsmacros_canApplyDyePattern() &&
            inventory.getScreenHandler().onButtonClick(player, index)) {
            assert mc.interactionManager != null;
            mc.interactionManager.clickButton(syncId, index);
            return true;
        }
        return false;
    }

}
