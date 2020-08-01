package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.compat.interfaces.ISignEditScreen;

@Mixin(SignEditScreen.class)
public class jsmacros_SignEditScreenMixin implements ISignEditScreen {

    @Shadow
    @Final
    private String[] field_24285;
    
    @Shadow
    @Final
    private SignBlockEntity sign;
    
    @Override
    public void setLine(int line, String text) {
        this.field_24285[line] = text;
        this.sign.setTextOnRow(line, new LiteralText(text));
    }
    
}
