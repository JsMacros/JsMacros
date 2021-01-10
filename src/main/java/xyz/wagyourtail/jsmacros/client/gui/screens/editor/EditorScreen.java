package xyz.wagyourtail.jsmacros.client.gui.screens.editor;

import com.google.common.collect.ImmutableList;
import io.noties.prism4j.Prism4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.BaseScreen;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.client.gui.editor.History;
import xyz.wagyourtail.jsmacros.client.gui.editor.PrismGrammarLocator;
import xyz.wagyourtail.jsmacros.client.gui.editor.SelectCursor;
import xyz.wagyourtail.jsmacros.client.gui.editor.TextStyleCompiler;
import xyz.wagyourtail.jsmacros.client.gui.elements.overlays.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class EditorScreen extends BaseScreen {
    private final static Prism4j prism4j = new Prism4j(new PrismGrammarLocator());
    private final static OrderedText ellipses = new LiteralText("...").formatted(Formatting.DARK_GRAY).asOrderedText();
    /*TODO: make py script to convert a copyleft monospaced font to png's for minecraft, or add class for on the fly from a ttf*/
    public final static Style defaultStyle = Style.EMPTY.withFont(new Identifier("jsmacros", "uniform"));
    protected final File file;
    protected final FileHandler handler;
    public final History history;
    public final SelectCursor cursor = new SelectCursor(defaultStyle);
    private int ellipsesWidth;
    protected String savedString;
    protected Text fileName = new LiteralText("");
    protected String lineCol = "";
    protected Scrollbar scrollbar;
    protected Button saveBtn;
    protected LiteralText[] renderedText = new LiteralText[0];
    protected int scroll = 0;
    protected int lineSpread = 0;
    protected int firstLine = 0;
    protected int lastLine;
    public boolean blockFirst = false;
    public long textRenderTime = 0;
    public String language;
    
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
        this.language = getDefaultLanguage();
        this.history = new History(content.replaceAll("\r\n", "\n").replaceAll("\t", "    "), cursor);
        cursor.updateStartIndex(0, history.current);
        cursor.updateEndIndex(0, history.current);
        cursor.dragStartIndex = 0;
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
            case "rb":
                return "ruby";
            default:
                return "javascript";
        }
    }
    
    public static void openAndScrollToIndex(@NotNull File file, int startIndex, int endIndex) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int finalEndIndex = endIndex == -1 ? startIndex : endIndex;
        mc.execute(() -> {
            EditorScreen screen;
            try {
                if (JsMacros.prevScreen instanceof EditorScreen &&
                    ((EditorScreen) JsMacros.prevScreen).file.getCanonicalPath().equals(file.getCanonicalPath())) {
                    screen = (EditorScreen) JsMacros.prevScreen;
                } else {
                    screen = new EditorScreen(JsMacros.prevScreen, file);
                }
                screen.cursor.updateStartIndex(startIndex, screen.history.current);
                screen.cursor.updateEndIndex(finalEndIndex, screen.history.current);
                mc.openScreen(screen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    public static void openAndScrollToLine(@NotNull File file, int line, int col, int endCol) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.execute(() -> {
            EditorScreen screen;
            try {
                if (JsMacros.prevScreen instanceof EditorScreen &&
                    ((EditorScreen) JsMacros.prevScreen).file.getCanonicalPath().equals(file.getCanonicalPath())) {
                    screen = (EditorScreen) JsMacros.prevScreen;
                } else {
                    screen = new EditorScreen(JsMacros.prevScreen, file);
                }
                String[] lines = screen.history.current.split("\n", -1);
                int lineIndex = 0;
                int max = Math.min(lines.length - 1, line - 1);
                for (int i = 0; i < max; ++i) {
                    lineIndex += lines[i].length() + 1;
                }
                int startIndex;
                if (col == -1) startIndex = lineIndex;
                else startIndex = lineIndex + Math.min(lines[max].length(), col);
                screen.cursor.updateStartIndex(startIndex, screen.history.current);
                if (endCol == -1) startIndex = lineIndex + lines[max].length();
                else startIndex = lineIndex + Math.min(lines[max].length(), endCol);
                screen.cursor.updateEndIndex(startIndex, screen.history.current);
                mc.openScreen(screen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    public void setScroll(double pages) {
        scroll = (int) ((height - 24) * pages);
        int add = lineSpread - scroll % lineSpread;
        if (add == lineSpread) add = 0;
        firstLine = (int) Math.ceil(scroll / (double) lineSpread);
        lastLine = (int) (firstLine + (height - 24 - add) / (double) lineSpread) - 1;
    }
    
    public synchronized void setLanguage(String language) {
        this.language = language;
        compileRenderedText();
    }
    
    public void init() {
        super.init();
        assert client != null;
        ellipsesWidth = client.textRenderer.getWidth(ellipses);
        lineSpread = client.textRenderer.fontHeight + 1;
        int width = this.width - 10;
        
        scrollbar = addButton(new Scrollbar(width, 12, 10, height - 24, 0, 0xFF000000, 0xFFFFFFFF, 1, this::setScroll));
        saveBtn = addButton(new Button(width / 2, 0, width / 6, 12, textRenderer, needSave() ? 0xFFA0A000 : 0xFF00A000, 0xFF000000, needSave() ? 0xFF707000 : 0xFF007000, 0xFFFFFF, new TranslatableText("jsmacros.save"), (btn) -> save()));
        
        history.onChange = (content) -> {
            if (savedString.equals(content)) {
                saveBtn.setColor(0xFF00A000);
                saveBtn.setHilightColor(0xFF007000);
            } else {
                saveBtn.setColor(0xFFA0A000);
                saveBtn.setHilightColor(0xFF707000);
            }
        };
        
        cursor.onChange = (cursor) -> lineCol = (cursor.startIndex != cursor.endIndex ? (cursor.endIndex - cursor.startIndex) + " " : "") +
            (cursor.arrowEnd ? String.format("%d:%d", cursor.endLine + 1, cursor.endLineIndex + 1) : String.format("%d:%d", cursor.startLine + 1, cursor.startLineIndex + 1));
        
        addButton(new Button(width, 0, 10, 12, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> openParent()));
        
        addButton(new Button(this.width - width / 8, height - 12, width / 8, 12, textRenderer,0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(language), (btn) -> {
            switch (language) {
                case "python":
                    setLanguage("lua");
                    btn.setMessage(new LiteralText("lua"));
                    break;
                case "lua":
                    setLanguage("json");
                    btn.setMessage(new LiteralText("json"));
                    break;
                case "json":
                    setLanguage("ruby");
                    btn.setMessage(new LiteralText("ruby"));
                    break;
                case "ruby":
                    setLanguage("javascript");
                    btn.setMessage(new LiteralText("javascript"));
                    break;
                default:
                    setLanguage("python");
                    btn.setMessage(new LiteralText("python"));
                    break;
            }
        }));
        
        this.fileName = new LiteralText(textRenderer.trimToWidth(file.getName(), (width - 10) / 2));
        compileRenderedText();
        setScroll(0);
        scrollToCursor();
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        assert client != null;
        if (overlay == null) {
            setFocused(null);
        }
        if (Screen.isSelectAll(keyCode)) {
            cursor.updateStartIndex(0, history.current);
            cursor.updateEndIndex(history.current.length(), history.current);
            cursor.arrowEnd = true;
    
            return true;
        } else if (Screen.isCopy(keyCode)) {
            client.keyboard.setClipboard(history.current.substring(cursor.startIndex, cursor.endIndex));
            
            return true;
        } else if (Screen.isPaste(keyCode)) {
            String pasteContent = client.keyboard.getClipboard();
            history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, pasteContent);
            
            compileRenderedText();
            return true;
        } else if (Screen.isCut(keyCode)) {
            client.keyboard.setClipboard(history.current.substring(cursor.startIndex, cursor.endIndex));
            history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, "");
            
            compileRenderedText();
            return true;
        }
        String startSpaces;
        int index;
        double currentPage;
        switch (keyCode) {
            case GLFW.GLFW_KEY_BACKSPACE:
                if (cursor.startIndex != cursor.endIndex) {
                    history.bkspacePos(cursor.startIndex, cursor.endIndex - cursor.startIndex);
                    compileRenderedText();
                } else if (cursor.startIndex > 0) {
                    history.bkspacePos(cursor.startIndex - 1, 1);
                    compileRenderedText();
                }
                return true;
            case GLFW.GLFW_KEY_DELETE:
                if (cursor.startIndex != cursor.endIndex) {
                    history.deletePos(cursor.startIndex, cursor.endIndex - cursor.startIndex);
                    compileRenderedText();
                } else if (cursor.startIndex < history.current.length()) {
                    history.deletePos(cursor.startIndex, 1);
                    compileRenderedText();
                }
                return true;
            case GLFW.GLFW_KEY_HOME:
                startSpaces = history.current.split("\n", -1)[cursor.startLine].split("[^\\s]", -1)[0];
                if (cursor.startLineIndex <= startSpaces.length()) {
                    cursor.updateStartIndex(cursor.startIndex - cursor.startLineIndex, history.current);
                } else {
                    cursor.updateStartIndex(cursor.startIndex - cursor.startLineIndex + startSpaces.length(), history.current);
                }
                cursor.updateEndIndex(cursor.startIndex, history.current);
/*
                    //home to start of file impl
                    
                    cursor.updateStartIndex(0, history.current);
                    cursor.updateEndIndex(0, history.current);
                    if (scrollToPercent != null) scrollToPercent.accept(0D);
*/
                return true;
            case GLFW.GLFW_KEY_END:
                int endLineLength = history.current.split("\n", -1)[cursor.endLine].length();
                cursor.updateEndIndex(cursor.endIndex + (endLineLength - cursor.endLineIndex), history.current);
                cursor.updateStartIndex(cursor.endIndex, history.current);
/*
                    //end to end of file impl

                    cursor.updateStartIndex(history.current.length(), history.current);
                    cursor.updateEndIndex(history.current.length(), history.current);
                    if (scrollToPercent != null) scrollToPercent.accept(1D);
*/
                return true;
            case GLFW.GLFW_KEY_LEFT:
                if (Screen.hasShiftDown()) {
                    if (cursor.arrowEnd && cursor.startIndex != cursor.endIndex) {
                        cursor.updateEndIndex(cursor.endIndex - 1, history.current);
                        cursor.arrowLineIndex = cursor.endLineIndex;
                    } else {
                        cursor.updateStartIndex(cursor.startIndex - 1, history.current);
                        cursor.arrowLineIndex = cursor.startLineIndex;
                        cursor.arrowEnd = false;
                    }
                } else {
                    if (cursor.startIndex != cursor.endIndex) {
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                    } else {
                        cursor.updateStartIndex(cursor.startIndex - 1, history.current);
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                        cursor.arrowLineIndex = cursor.startLineIndex;
                    }
                }
                scrollToCursor();
                return true;
            case GLFW.GLFW_KEY_RIGHT:
                if (Screen.hasShiftDown()) {
                    if (cursor.arrowEnd || cursor.endIndex == cursor.startIndex) {
                        cursor.updateEndIndex(cursor.endIndex + 1, history.current);
                        cursor.arrowLineIndex = cursor.endLineIndex;
                        cursor.arrowEnd = true;
                    } else {
                        cursor.updateStartIndex(cursor.startIndex + 1, history.current);
                        cursor.arrowLineIndex = cursor.startLineIndex;
                    }
                } else {
                    if (cursor.startIndex != cursor.endIndex) {
                        cursor.updateStartIndex(cursor.endIndex, history.current);
                    } else {
                        cursor.updateStartIndex(cursor.startIndex + 1, history.current);
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                        cursor.arrowLineIndex = cursor.startLineIndex;
                    }
                }
                scrollToCursor();
                return true;
            case GLFW.GLFW_KEY_UP:
                if (Screen.hasAltDown()) {
                    history.shiftLine(cursor.startLine, cursor.endLine - cursor.startLine + 1, false);
                    cursor.arrowEnd = false;
                    compileRenderedText();
                } else {
                    index = 0;
                    if (cursor.startLine > 0 || (cursor.arrowEnd && cursor.endLine > 0)) {
                        String[] lines = history.current.split("\n", -1);
                        int line = (cursor.arrowEnd ? cursor.endLine : cursor.startLine) - 1;
                        for (int i = 0; i < line; ++i) {
                            index += lines[i].length() + 1;
                        }
                        index += Math.min(lines[line].length(), cursor.arrowLineIndex);
                    }
                    if (Screen.hasShiftDown()) {
                        if (cursor.arrowEnd && index >= cursor.startIndex) {
                            cursor.updateEndIndex(index, history.current);
                            cursor.arrowEnd = true;
                        } else {
                            cursor.updateStartIndex(index, history.current);
                            cursor.arrowEnd = false;
                        }
                    } else {
                        if (cursor.startIndex == cursor.endIndex) {
                            cursor.updateStartIndex(index, history.current);
                        }
                        cursor.updateEndIndex(cursor.startIndex, history.current);
                    }
                }
                scrollToCursor();
                return true;
            case GLFW.GLFW_KEY_DOWN:
                if (Screen.hasAltDown()) {
                    history.shiftLine(cursor.startLine, cursor.endLine - cursor.startLine + 1, true);
                    cursor.arrowEnd = true;
                    compileRenderedText();
                } else {
                    index = 0;
                    String[] lines = history.current.split("\n", -1);
                    if (cursor.endLine < lines.length - 1 || (!cursor.arrowEnd && cursor.startLine < lines.length - 1)) {
                        int line = (cursor.arrowEnd ? cursor.endLine : cursor.startLine) + 1;
                        for (int i = 0; i < line; ++i) {
                            index += lines[i].length() + 1;
                        }
                        index += Math.min(lines[line].length(), cursor.arrowLineIndex);
                    } else {
                        index = history.current.length();
                    }
                    if (Screen.hasShiftDown()) {
                        if (!cursor.arrowEnd && index <= cursor.endIndex) {
                            cursor.updateStartIndex(index, history.current);
                            cursor.arrowEnd = false;
                        } else {
                            cursor.updateEndIndex(index, history.current);
                            cursor.arrowEnd = true;
                        }
                    } else {
                        if (cursor.startIndex != cursor.endIndex) {
                            cursor.updateStartIndex(cursor.endIndex, history.current);
                        } else {
                            cursor.updateStartIndex(index, history.current);
                            cursor.updateEndIndex(cursor.startIndex, history.current);
                        }
                    }
                }
                scrollToCursor();
                return true;
            case GLFW.GLFW_KEY_Z:
                if (Screen.hasControlDown()) {
                    if (Screen.hasShiftDown()) {
                        int i = history.redo();
                        
                        if (i != -1) {
                            compileRenderedText();
                        }
                    } else {
                        int i = history.undo();
                        
                        if (i != -1) {
                            compileRenderedText();
                        }
                    }
                }
                return true;
            case GLFW.GLFW_KEY_Y:
                int i = history.redo();
                
                if (i != -1) {
                    compileRenderedText();
                }
                return true;
            case GLFW.GLFW_KEY_S:
                if (Screen.hasControlDown()) {
                    save();
                }
                return true;
            case GLFW.GLFW_KEY_ENTER:
                startSpaces = history.current.split("\n", -1)[cursor.startLine].split("[^\\s]", -1)[0];
                if (cursor.startIndex != cursor.endIndex) {
                    history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, "\n" + startSpaces);
                    cursor.updateStartIndex(cursor.endIndex, history.current);
                } else {
                    history.add(cursor.startIndex, "\n" + startSpaces);
                }
                
                compileRenderedText();
                return true;
            case GLFW.GLFW_KEY_TAB:
                if (cursor.startIndex != cursor.endIndex || Screen.hasShiftDown()) {
                    history.tabLines(cursor.startLine, cursor.endLine - cursor.startLine + 1, Screen.hasShiftDown());
                } else {
                    history.addChar(cursor.startIndex, '\t');
                }
                compileRenderedText();
                return true;
            case GLFW.GLFW_KEY_PAGE_UP:
                currentPage = scroll / (height - 24.0D);
                scrollbar.scrollToPercent(MathHelper.clamp((currentPage - 1) / (calcTotalPages() - 1), 0, 1));
                break;
            case GLFW.GLFW_KEY_PAGE_DOWN:
                currentPage = scroll / (height - 24.0D);
                scrollbar.scrollToPercent(MathHelper.clamp((currentPage + 1) / (calcTotalPages() - 1), 0, 1));
                return true;
            default:
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @SuppressWarnings("SynchronizeOnNonFinalField")
    private synchronized void compileRenderedText() {
        long time = System.currentTimeMillis();
        if (!history.current.equals("")) {
            final List<Prism4j.Node> nodes = prism4j.tokenize(history.current, Objects.requireNonNull(prism4j.grammar(language)));
            final TextStyleCompiler visitor = new TextStyleCompiler(defaultStyle);
            visitor.visit(nodes);
            synchronized (renderedText) {
                renderedText = visitor.getResult().toArray(new LiteralText[0]);
            }
        } else {
            synchronized (renderedText) {
                renderedText = new LiteralText[] {new LiteralText("")};
            }
        }
        this.scrollbar.setScrollPages(calcTotalPages());
        textRenderTime = System.currentTimeMillis() - time;
        scrollToCursor();
    }
    
    public void scrollToCursor() {
        int cursorLine = cursor.arrowEnd ? cursor.endLine : cursor.startLine;
        if (cursorLine < firstLine || cursorLine > lastLine) {
            int pagelength = lastLine - firstLine;
            double scrollLines = history.current.split("\n", -1).length - pagelength;
            cursorLine -= pagelength / 2;
            scrollbar.scrollToPercent(MathHelper.clamp(cursorLine, 0, scrollLines) / scrollLines);
        }
    }
    
    private double calcTotalPages() {
        if (history == null) return 1;
        return (history.current.split("\n", -1).length) / (Math.floor((height - 24) / (double) lineSpread) - 1D);
    }
    
    public void save() {
        if (needSave()) {
            String current = history.current;
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
    
    public boolean needSave() {
        return !savedString.equals(history.current);
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (overlay == null && scrollbar != null) {
            scrollbar.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    @SuppressWarnings("SynchronizeOnNonFinalField")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        assert client != null;
        renderBackground(matrices);
        
        textRenderer.drawWithShadow(matrices, fileName, 2, 2, 0xFFFFFF);
        
        textRenderer.drawWithShadow(matrices, String.format("%d ms", (int) textRenderTime), 2, height - 10, 0xFFFFFF);
        textRenderer.drawWithShadow(matrices, lineCol, width - textRenderer.getWidth(lineCol) - (width - 10) / 8F - 2, height - 10, 0xFFFFFF);
        
        fill(matrices, 0, 12, width - 10, height - 12, 0xFF2B2B2B);
        fill(matrices, 28, 12, 29, height - 12, 0xFF707070);
        fill(matrices, 0, 12, 1, height - 12, 0xFF707070);
        fill(matrices, width - 11, 12, width - 10, height - 12, 0xFF707070);
        fill(matrices, 1, 12, width - 11, 13, 0xFF707070);
        fill(matrices, 1, height - 13, width - 11, height - 12, 0xFF707070);
        
        Style lineNumStyle = defaultStyle.withColor(TextColor.fromRgb(0xD8D8D8));
        int add = lineSpread - scroll % lineSpread;
        if (add == lineSpread) add = 0;
        int y = 13;
        synchronized (renderedText) {
            if (renderedText != null)
                for (int i = 0, j = firstLine; j <= lastLine && j < renderedText.length; ++i, ++j) {
                    if (cursor.startLine == j && cursor.endLine == j) {
                        fill(matrices, 30 + cursor.startCol, y + add + i * lineSpread, 30 + cursor.endCol, y + add + (i + 1) * lineSpread, 0xFF33508F);
                    } else if (cursor.startLine == j) {
                        fill(matrices, 30 + cursor.startCol, y + add + i * lineSpread, width - 10, y + add + (i + 1) * lineSpread, 0xFF33508F);
                    } else if (j > cursor.startLine && j < cursor.endLine) {
                        fill(matrices, 29, y + add + i * lineSpread, width - 10, y + add + (i + 1) * lineSpread, 0xFF33508F);
                    } else if (cursor.endLine == j) {
                        fill(matrices, 29, y + add + i * lineSpread, 30 + cursor.endCol, y + add + (i + 1) * lineSpread, 0xFF33508F);
                    }
                    LiteralText lineNum = (LiteralText) new LiteralText(String.format("%d.", j + 1)).setStyle(lineNumStyle);
                    client.textRenderer.draw(matrices, lineNum, 28 - client.textRenderer.getWidth(lineNum), y + add + i * lineSpread, 0xFFFFFF);
                    client.textRenderer.draw(matrices, trim(renderedText[j]), 30, y + add + i * lineSpread, 0xFFFFFF);
                }
        }
        
        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            b.render(matrices, mouseX, mouseY, delta);
        }
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    private OrderedText trim(LiteralText text) {
        assert client != null;
        if (client.textRenderer.getWidth(text) > width - 30) {
            OrderedText trimmed = Language.getInstance().reorder(client.textRenderer.trimToWidth(text, width - 40 - ellipsesWidth));
            return OrderedText.concat(trimmed, ellipses);
        } else {
            return text.asOrderedText();
        }
    }
    
    @Override
    public void openParent() {
        if (needSave()) {
            openOverlay(new ConfirmOverlay(width / 4, height / 4, width / 2, height / 2, textRenderer, new TranslatableText("jsmacros.nosave"), this::addButton, this::removeButton, this::closeOverlay, (container) -> super.openParent()));
        } else {
            super.openParent();
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int btn) {
        setFocused(null);
        int index = getIndexPosition(mouseX - 30, mouseY - 12 + 1);
        if (Screen.hasShiftDown()) {
            if (index < cursor.dragStartIndex) {
                cursor.updateEndIndex(cursor.dragStartIndex, history.current);
                cursor.updateStartIndex(index, history.current);
                cursor.arrowEnd = false;
                cursor.arrowLineIndex = cursor.startLineIndex;
            } else {
                cursor.updateStartIndex(cursor.dragStartIndex, history.current);
                cursor.updateEndIndex(index, history.current);
                cursor.arrowEnd = true;
                cursor.arrowLineIndex = cursor.endLineIndex;
            }
        } else {
            if (cursor.startIndex == index && cursor.endIndex == index) {
                selectWordAtCursor();
            } else {
                cursor.updateStartIndex(index, history.current);
                cursor.updateEndIndex(index, history.current);
            }
            cursor.dragStartIndex = index;
            cursor.arrowEnd = false;
            cursor.arrowLineIndex = cursor.startLineIndex;
        }
        return super.mouseClicked(mouseX, mouseY, btn);
    }
    
    private int getIndexPosition(double x, double y) {
        assert client != null;
        int add = lineSpread - scroll % lineSpread;
        if (add == lineSpread) add = 0;
        int line = firstLine + (int) ((y - add) / (double) lineSpread);
        if (line < 0) line = 0;
        String[] lines = history.current.split("\n", -1);
        int col;
        if (line >= lines.length) {
            line = lines.length - 1;
            col = lines[lines.length - 1].length();
        } else {
            col = client.textRenderer.getTextHandler().trimToWidth(lines[line], (int) x, defaultStyle).length();
        }
        int count = 0;
        for (int i = 0; i < line; ++i) {
            count += lines[i].length() + 1;
        }
        count += col;
        return count;
    }
    
    public void selectWordAtCursor() {
        String currentLine = history.current.split("\n", -1)[cursor.startLine];
        String[] startWords = currentLine.substring(0, cursor.startLineIndex).split("\\b");
        int dStart = -startWords[startWords.length - 1].length();
        String[] endWords = currentLine.substring(cursor.startLineIndex).split("\\b");
        int dEnd = endWords[0].length();
        int currentIndex = cursor.startIndex;
        cursor.updateStartIndex(currentIndex + dStart, history.current);
        cursor.updateEndIndex(currentIndex + dEnd, history.current);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (getFocused() != scrollbar) {
            int index = getIndexPosition(mouseX - 30, mouseY - 12);
            if (index == cursor.dragStartIndex) {
                cursor.updateStartIndex(index, history.current);
                cursor.updateEndIndex(index, history.current);
            } else if (index < cursor.dragStartIndex) {
                cursor.updateEndIndex(cursor.dragStartIndex, history.current);
                cursor.arrowEnd = false;
                cursor.updateStartIndex(index, history.current);
                cursor.arrowLineIndex = cursor.startLineIndex;
            } else {
                cursor.updateStartIndex(cursor.dragStartIndex, history.current);
                cursor.arrowEnd = true;
                cursor.updateEndIndex(index, history.current);
                cursor.arrowLineIndex = cursor.endLineIndex;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public synchronized boolean charTyped(char chr, int keyCode) {
        if (overlay == null) {
            setFocused(null);
        }
        if (blockFirst) {
            blockFirst = false;
        } else {
            if (cursor.startIndex != cursor.endIndex) {
                history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, String.valueOf(chr));
                cursor.updateStartIndex(cursor.endIndex, history.current);
            } else {
                history.addChar(cursor.startIndex, chr);
            }
            compileRenderedText();
        }
        return false;
    }
    
}
