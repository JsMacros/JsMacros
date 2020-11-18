package xyz.wagyourtail.jsmacros.gui.screens.editor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.FileHandler;
import xyz.wagyourtail.jsmacros.gui.BaseScreen;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.gui.elements.editor.EditorContent;
import xyz.wagyourtail.jsmacros.gui.elements.overlays.ConfirmOverlay;

import java.io.File;
import java.io.IOException;

public class EditorScreen extends BaseScreen {
    protected final File file;
    protected String savedString;
    protected Text fileName = new LiteralText("");
    protected String lineCol = "";
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
        
        this.handler = handler;
        this.content = new EditorContent(0, 0, 0, 0, 0xFF2B2B2B, 0xFF707070, 0xFF33508F, 0xD8D8D8, content, this::save);
        this.content.setLanguage(getDefaultLanguage());
        this.scrollbar = new Scrollbar(0, 0, 0, 0, 0, 0xFF000000, 0xFFFFFFFF, 1, this.content::setScroll);
        this.content.updateScrollPages = scrollbar::setScrollPages;
        this.content.scrollToPercent = scrollbar::scrollToPercent;
    }
    
    public void init() {
        super.init();
        int width = this.width - 10;
        addButton(content.setPos(0, 12, width, height - 24));
        addButton(scrollbar.setPos(width, 12, 10, height - 24));
        
        saveBtn = addButton(new Button(width / 2, 0, width / 6, 12, needSave() ? 0xFFA0A000 : 0xFF00A000, 0xFF000000, needSave() ? 0xFF707000 : 0xFF007000, 0xFFFFFF, new TranslatableText("jsmacros.save"), (btn) -> save()));
        
        content.history.onChange = (content) -> {
            if (savedString.equals(content)) {
                saveBtn.setColor(0xFF00A000);
                saveBtn.setHilightColor(0xFF007000);
            } else {
                saveBtn.setColor(0xFFA0A000);
                saveBtn.setHilightColor(0xFF707000);
            }
        };
    
        content.cursor.onChange = (cursor) -> {
            lineCol = (content.cursor.startIndex != content.cursor.endIndex ? (content.cursor.endIndex - content.cursor.startIndex) + " " : "") +
                (content.cursor.arrowEnd ? String.format("%d:%d", content.cursor.endLine + 1, content.cursor.endLineIndex + 1) : String.format("%d:%d", content.cursor.startLine + 1, content.cursor.startLineIndex + 1));
        };
        
        addButton(new Button(width, 0, 10, 12,0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
            openParent();
        }));
        
        addButton(new Button(this.width - width / 8, height - 12, width / 8, 12, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(getDefaultLanguage()), (btn) -> {
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
        
        this.fileName = new LiteralText(textRenderer.trimToWidth(file.getName(), (width - 10) / 2));
        if (overlay == null && !content.isFocused()) {
            setFocused(content);
            content.changeFocus(true);
        }
    }
    
    public boolean needSave() {
        return !savedString.equals(content.history.current);
    }
    
    public void save() {
        if (needSave()) {
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
        if (overlay == null && !content.isFocused()) {
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
        
        textRenderer.drawWithShadow(matrices, fileName, 2, 2, 0xFFFFFF);
        
        textRenderer.drawWithShadow(matrices, String.format("%d ms", (int) content.textRenderTime), 2, height - 10, 0xFFFFFF);
        textRenderer.drawWithShadow(matrices, lineCol, width - textRenderer.getWidth(lineCol) - (width - 10) / 8 - 2, height - 10, 0xFFFFFF);
        
        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            b.render(matrices, mouseX, mouseY, delta);
        }
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            return this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void openParent() {
        if (needSave()) {
            openOverlay(new ConfirmOverlay(width  / 4, height / 4, width / 2, height / 2, textRenderer, new TranslatableText("jsmacros.nosave"), this::addButton, this::removeButton, this::closeOverlay, (container) -> {
                super.openParent();
            }));
        } else {
            super.openParent();
        }
    }
}
