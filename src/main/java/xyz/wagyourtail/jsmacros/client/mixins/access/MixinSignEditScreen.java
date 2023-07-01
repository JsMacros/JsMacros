package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinSignEditScreen implements ISignEditScreen {

    @Shadow @Final
    protected SignBlockEntity blockEntity;


    @Shadow @Final
    protected String[] text;

    @Override
    public void jsmacros_setLine(int line, String text) {
        this.text[line] = text;
        this.blockEntity.setTextOnRow(line, Text.literal(text));
    }

}
