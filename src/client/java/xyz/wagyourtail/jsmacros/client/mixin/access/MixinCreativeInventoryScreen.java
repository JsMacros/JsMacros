package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(CreativeInventoryScreen.class)
public interface MixinCreativeInventoryScreen {

    // Don't make this static, it will disable the compile and reload feature!
    @Accessor
    ItemGroup getSelectedTab();

    @Accessor
    float getScrollPosition();

    @Accessor
    TextFieldWidget getSearchBox();

    @Invoker
    void invokeSetSelectedTab(ItemGroup group);

    @Invoker
    void invokeSearch();

    @Invoker
    boolean invokeHasScrollbar();

}
