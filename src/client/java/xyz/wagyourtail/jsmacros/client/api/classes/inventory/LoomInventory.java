package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BannerPatternTags;
import net.minecraft.registry.tag.TagKey;
import xyz.wagyourtail.jsmacros.client.access.ILoomScreen;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @since 1.5.1
 */
@SuppressWarnings("unused")
public class LoomInventory extends Inventory<LoomScreen> {

    protected LoomInventory(LoomScreen inventory) {
        super(inventory);
    }

    private List<RegistryEntry<BannerPattern>> getPatternsFor(ItemStack stack) {
        var bannerPatternLookup = mc.getNetworkHandler().getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN);
        // Taken from LoomScreenHandler#getPatternsFor
        if (stack.isEmpty()) {
            return bannerPatternLookup.getOptional(BannerPatternTags.NO_ITEM_REQUIRED).map(ImmutableList::copyOf).orElse(ImmutableList.of());
        } else {
            TagKey<BannerPattern> tagKey = stack.get(DataComponentTypes.PROVIDES_BANNER_PATTERNS);
            return tagKey != null ? bannerPatternLookup.getOptional(tagKey).map(ImmutableList::copyOf).orElse(ImmutableList.of()) : List.of();
        }
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
        Iterable<RegistryEntry<BannerPattern>> patterns = getPatternsFor(inventory.getScreenHandler().getSlot(2).getStack());
        return StreamSupport.stream(patterns.spliterator(), false).map(e -> Objects.requireNonNull(mc.getNetworkHandler()).getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN).getId(e.value()).toString()).collect(Collectors.toList());
    }

    /**
     * @param id
     * @return success
     * @since 1.5.1
     */
    public boolean selectPatternId(String id) {
        List<RegistryEntry<BannerPattern>> patterns = getPatternsFor(inventory.getScreenHandler().getSlot(2).getStack());
        RegistryEntry<BannerPattern> pattern = StreamSupport.stream(patterns.spliterator(), false).filter(e -> Objects.requireNonNull(mc.getNetworkHandler()).getRegistryManager().getOrThrow(RegistryKeys.BANNER_PATTERN).getId(e.value()).toString().equals(id)).findFirst().orElse(null);

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
     * @return success
     * @since 1.5.1
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

    @Override
    public String toString() {
        return String.format("LoomInventory:{}");
    }

}
