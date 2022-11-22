package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;

@Mixin(SignEditScreen.class)
public abstract class MixinSignEditScreen implements ISignEditScreen {
    
    @Shadow
    @Final
    private SignBlockEntity sign;

    @Override
    public void jsmacros_setLine(int line, String text) {
        this.sign.field_145915_a[line] =  new ChatComponentText(text);
    }

}
