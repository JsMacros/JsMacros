package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;

import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.literal;

@Mixin(SignEditScreen.class)
public abstract class MixinSignEditScreen implements ISignEditScreen {

    @Shadow @Final
    protected SignBlockEntity sign;


    @Shadow @Final
    protected String[] text;

    @Override
    public void jsmacros_setLine(int line, String text) {
        this.text[line] = text;
        this.sign.setTextOnRow(line, literal(text));
    }

}
