package xyz.wagyourtail.jsmacros.gui.screens.editor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.api.classes.FileHandler;
import xyz.wagyourtail.jsmacros.extensionbase.ILanguage;
import xyz.wagyourtail.jsmacros.gui.BaseScreen;
import xyz.wagyourtail.jsmacros.gui.containers.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

import java.io.File;
import java.io.IOException;

public class EditorScreen extends BaseScreen {
    protected final File file;
    protected final Text fileName;
    protected final FileHandler handler;
    protected final EditorContent content;
    protected final Scrollbar scrollbar;
    
    public EditorScreen(Screen parent, @NotNull File file) {
        super(new LiteralText("Editor"), parent);
        this.file = file;
        FileHandler handler = new FileHandler(file);
        String content;
        if (file.exists()) {
            try {
                content = handler.read();
            } catch (IOException e) {
                content = "An unexpected error has occured:\n\n" + e.toString();
                handler = null;
            }
        } else {
            content = "";
        }
        this.fileName = new LiteralText(file.getName());
        
        this.handler = handler;
        this.content = new EditorContent(0, 0, 0, 0, 0xFF2B2B2B, 0xFF707070, 0xFF33508F, 0xD8D8D8, content);
        this.content.setLanguage(getDefaultLanguage());
        this.scrollbar = new Scrollbar(0, 0, 0, 0, 0, 0xFF000000, 0xFFFFFFFF, 1, this.content::setScroll);
        this.content.updateScrollPages = scrollbar::setScrollPages;
    }
    
    public void init() {
        super.init();
        int width = this.width - 20;
        addButton(content.setPos(0, 12, width, height - 22));
        addButton(scrollbar.setPos(width + 5, 12, 10, height - 22));
        
        Button saveBtn = addButton(new Button(width / 2, 0, width / 6, 12, content.needSave ? 0xFFA0A000 : 0xFF00A000, 0xFF000000, content.needSave ? 0xFF707000 : 0xFF007000, 0xFFFFFF, new TranslatableText("jsmacros.save"), (btn) -> {
            try {
                handler.write(content.history.current);
            } catch (IOException e) {
                openOverlay(new ConfirmOverlay(this.width / 4, height / 4, this.width / 2, height / 2, textRenderer, new TranslatableText("jsmacros.errorsaving").append(new LiteralText("\n\n" + e.getMessage())), this::addButton, this::removeButton, this::closeOverlay, null));
            }
        }));
        
        content.updateNeedSave = (sav) -> {
            saveBtn.setColor(sav ? 0xFFA0A000 : 0xFF00A000);
            saveBtn.setHilightColor(sav ? 0xFF707000 : 0xFF007000);
        };
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
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (overlay == null && scrollbar != null) {
            scrollbar.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        textRenderer.drawWithShadow(matrices, Language.getInstance().reorder(textRenderer.trimToWidth(fileName, width / 2)), 2, 1, 0xFFFFFF);
        String linecol;
        if (content.cursor.arrowEnd) {
            linecol = String.format("%d:%d", content.cursor.endLine + 1, content.cursor.endLineIndex + 1);
        } else {
            linecol = String.format("%d:%d", content.cursor.startLine + 1, content.cursor.startLineIndex + 1);
        }
        if (content.cursor.startIndex != content.cursor.endIndex) {
            linecol = (content.cursor.endIndex - content.cursor.startIndex) + " " + linecol;
        }
        textRenderer.drawWithShadow(matrices, linecol, width - textRenderer.getWidth(linecol) - 10, height - 9, 0xFFFFFF);
        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            b.render(matrices, mouseX, mouseY, delta);
        }
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
}
