package xyz.wagyourtail.jsmacros.gui.screens.editor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.api.classes.FileHandler;
import xyz.wagyourtail.jsmacros.extensionbase.ILanguage;
import xyz.wagyourtail.jsmacros.gui.BaseScreen;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

import java.io.File;
import java.io.IOException;

public class EditorScreen extends BaseScreen {
    protected final File file;
    protected final FileHandler handler;
    protected final EditorContent content;
    
    public EditorScreen(Screen parent, @NotNull File file) {
        super(new LiteralText("Editor"), parent);
        this.file = file;
        FileHandler handler = new FileHandler(file);
        String content;
        try {
            content = handler.read();
        } catch (IOException e) {
            content = e.toString();
            handler = null;
        }
        this.handler = handler;
        this.content = new EditorContent(0, 0, 0, 0, 0xFF2B2B2B, 0xFF707070, 0xFF33508F, 0xD8D8D8, content, null, null, null);
        this.content.setLanguage(getDefaultLanguage());
    }
    
    public void init() {
        super.init();
        addButton(content.setPos(0, 10, width - 20, height - 20));
    }
    
    public String getDefaultLanguage() {
        final String fname = file.getName();
        String ext = RunScript.defaultLang.extension();
        for (ILanguage l : RunScript.languages) {
            if (fname.endsWith(l.extension())) {
                ext = l.extension();
                break;
            }
        }
        switch (ext) {
            case ".py":
            case "jython.py":
                return "python";
            case ".lua":
                return "lua";
            default:
                return "javascript";
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        
        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            ((Button) b).render(matrices, mouseX, mouseY, delta);
        }
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
}
