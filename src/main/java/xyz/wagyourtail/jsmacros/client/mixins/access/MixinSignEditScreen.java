package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;

@Mixin(AbstractSignEditScreen.class)
public abstract class MixinSignEditScreen implements ISignEditScreen {

    @Shadow
    @Final
    private String[] messages;

//    @Shadow
//    @Final
//    private SignBlockEntity blockEntity;
//
//    @Shadow
//    @Final
//    private boolean front;

    @Shadow
    private SignText text;

    @Override
    public void jsmacros_setLine(int line, String text) {
        this.messages[line] = text; // actual
        this.text = this.text.withMessage(line, Text.of(text)); // gui visual
        // this needs to be called on main thread when sodium is installed
//        this.blockEntity.setText(this.text, this.front); // block visual
    }

}
