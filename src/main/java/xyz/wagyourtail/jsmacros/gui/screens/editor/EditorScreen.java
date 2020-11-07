package xyz.wagyourtail.jsmacros.gui.screens.editor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.api.classes.FileHandler;
import xyz.wagyourtail.jsmacros.gui.BaseScreen;
import xyz.wagyourtail.jsmacros.gui.elements.overlays.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.gui.elements.editor.EditorContent;

import java.io.File;
import java.io.IOException;

public class EditorScreen extends BaseScreen {
    protected final File file;
    protected String savedString;
    protected final Text fileName;
    protected final FileHandler handler;
    protected final EditorContent content;
    protected final Scrollbar scrollbar;
    protected Button saveBtn;
    
    public EditorScreen(Screen parent, @NotNull File file) {
        super(new LiteralText("Editor"), parent);
        this.file = file;
        FileHandler handler = new FileHandler(file);
        String content;
        if (file.exists()) {
            try {
                content = handler.read();
            } catch (IOException e) {
                content = I18n.translate("jsmacros.erroropening") + e.toString();
                handler = null;
            }
        } else {
            content = "";
        }
        savedString = content;
        this.fileName = new LiteralText(file.getName());
        
        this.handler = handler;
        this.content = new EditorContent(0, 0, 0, 0, 0xFF2B2B2B, 0xFF707070, 0xFF33508F, 0xD8D8D8, content);
        this.content.setLanguage(getDefaultLanguage());
        this.scrollbar = new Scrollbar(0, 0, 0, 0, 0, 0xFF000000, 0xFFFFFFFF, 1, this.content::setScroll);
        this.content.updateScrollPages = scrollbar::setScrollPages;
        this.content.scrollToPercent = scrollbar::scrollToPercent;
    }
    
    public void init() {
        super.init();
        int width = this.width - 20;
        addButton(content.setPos(0, 12, width, height - 24));
        addButton(scrollbar.setPos(width + 5, 12, 10, height - 24));
        
        saveBtn = addButton(new Button(width / 2, 0, width / 6, 12, needSave() ? 0xFF00A000 : 0xFFA0A000, 0xFF000000, needSave() ? 0xFF007000 : 0xFF707000, 0xFFFFFF, new TranslatableText("jsmacros.save"), this::save));
        
        content.history.onChange = (content) -> {
            if (savedString.equals(content)) {
                saveBtn.setColor(0xFF00A000);
                saveBtn.setHilightColor(0xFF707000);
            } else {
                saveBtn.setColor(0xFFA0A000);
                saveBtn.setHilightColor(0xFF007000);
            }
        };
        
        addButton(new Button(width + 5, 0, 10, 10,0, 0xFF000000, 0xFFFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
            openParent();
        }));
        
        addButton(new Button(this.width - width / 6, height - 12, width / 6, 12, 0, 0xFF000000, 0xFFFFFFFF, 0xFFFFFF, new LiteralText(getDefaultLanguage()), (btn) -> {
            switch (content.language) {
                case "python":
                    content.setLanguage("lua");
                    btn.setMessage(new LiteralText("lua"));
                    break;
                case "lua":
                    content.setLanguage("json");
                    btn.setMessage(new LiteralText("json"));
                    break;
                case "json":
                    content.setLanguage("javascript");
                    btn.setMessage(new LiteralText("javascript"));
                    break;
                default:
                    content.setLanguage("python");
                    btn.setMessage(new LiteralText("python"));
                    break;
            }
        }));
    }
    
    public boolean needSave() {
        return savedString.equals(content.history.current);
    }
    
    public void save(Button btn) {
        String current = content.history.current;
        try {
            handler.write(current);
            savedString = current;
            saveBtn.setColor(0xFF00A000);
            saveBtn.setHilightColor(0xFF707000);
        } catch (IOException e) {
            openOverlay(new ConfirmOverlay(this.width / 4, height / 4, this.width / 2, height / 2, textRenderer, new TranslatableText("jsmacros.errorsaving").append(new LiteralText("\n\n" + e.getMessage())), this::addButton, this::removeButton, this::closeOverlay, null));
        }
    }
    
    public String getDefaultLanguage() {
        final String[] fname = file.getName().split("\\.", -1);
        String ext = fname[fname.length - 1].toLowerCase();
        
        switch (ext) {
            case "py":
                return "python";
            case "lua":
                return "lua";
            case "json":
                return "json";
            default:
                return "javascript";
        }
    }
    
    public boolean mouseReleased(double mouseX, double mouseY, int btn) {
        if (overlay == null && content != null && !content.isFocused()) {
            setFocused(content);
            content.changeFocus(true);
        }
        return super.mouseReleased(mouseX, mouseY, btn);
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
        textRenderer.drawWithShadow(matrices, String.format("%d ms", (int) content.textRenderTime), 2, height - 9, 0xFFFFFF);
        textRenderer.drawWithShadow(matrices, linecol, width - textRenderer.getWidth(linecol) - 10, height - 9, 0xFFFFFF);
        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            b.render(matrices, mouseX, mouseY, delta);
        }
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public void openParent() {
        if (needSave()) {
            openOverlay(new ConfirmOverlay(this.width  / 4, height / 4, this.width / 2, height / 2, textRenderer, new TranslatableText("jsmacros.nosave"), this::addButton, this::removeButton, this::closeOverlay, (container) -> {
                super.openParent();
            }));
        } else {
            super.openParent();
        }
    }
}
