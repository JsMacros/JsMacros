package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.function.Consumer;

import net.minecraft.text.Text;

public class TextInput extends Button {

    public TextInput(int x, int y, int width, int height, int color, int borderColor, int hilightColor, int textColor, Text message, Consumer<Button> onChange) {
        super(x, y, width, height, color, borderColor, hilightColor, textColor, message, null);
    }
    
    

}
