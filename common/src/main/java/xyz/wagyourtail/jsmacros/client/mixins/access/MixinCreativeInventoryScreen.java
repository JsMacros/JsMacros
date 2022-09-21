package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ICreativeInventoryScreen;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen implements ICreativeInventoryScreen {

    @Shadow
    private static int selectedTab;

    @Shadow
    private float scrollPosition;

    @Shadow
    private TextFieldWidget searchBox;

    @Shadow
    @Nullable
    private Slot deleteItemSlot;

    @Shadow
    protected abstract void setSelectedTab(ItemGroup group);

    @Shadow
    protected abstract void search();

    @Override
    public int jsmacros_getSelectedTab() {
        return selectedTab;
    }

    @Override
    public float jsmacros_getScrollPosition() {
        return scrollPosition;
    }

    @Override
    public TextFieldWidget jsmacros_getSearchField() {
        return searchBox;
    }

    @Override
    public Slot jsmacros_getDeleteItemSlot() {
        return deleteItemSlot;
    }

    @Override
    public void jsmacros_setSelectedTab(int tab) {
        setSelectedTab(ItemGroup.GROUPS[tab]);
    }

    @Override
    public void jsmacros_search() {
        search();
    }
}
