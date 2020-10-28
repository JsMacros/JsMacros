package xyz.wagyourtail.jsmacros.gui.editor;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.gui.BaseScreen;

import java.io.File;

public class EditorScreen extends BaseScreen {

    public EditorScreen(Screen parent, File file) {
        super(new LiteralText("Editor"), parent);
    }


}
