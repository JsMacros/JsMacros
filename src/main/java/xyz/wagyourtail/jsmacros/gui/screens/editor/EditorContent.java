package xyz.wagyourtail.jsmacros.gui.screens.editor;

import io.noties.prism4j.Prism4j;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.screens.editor.hilighting.PrismGrammarLocator;
import xyz.wagyourtail.jsmacros.gui.screens.editor.hilighting.TextStyleCompiler;

import java.util.List;
import java.util.function.Consumer;

public class EditorContent extends Button {
    private final static Prism4j prism4j = new Prism4j(new PrismGrammarLocator());
    private final static OrderedText ellipses = new LiteralText("...").formatted(Formatting.DARK_GRAY).asOrderedText();
    private final int ellipsesWidth = mc.textRenderer.getWidth(ellipses);
    
    /*TODO: make py script to convert a copyleft monospaced font to png's for minecraft, or add class for on the fly from a ttf*/
    public final static Style defaultStyle = Style.EMPTY.withFont(new Identifier("jsmacros", "uniform"));
    
    private String language = "javascript";
    
    public boolean needSave = false;
    public Consumer<Boolean> updateNeedSave;
    public Consumer<Double> updateScrollPages;
    public Consumer<Double> forceScrollAmmount;
    
    protected LiteralText[] renderedText = new LiteralText[0];
    protected History history;
    
    protected int selColor;
    
    protected int selStartLine = 0;
    protected int selStartCol = 0;
    public int selStartLineIndex = 0;
    public int selStartIndex = 0;
    
    protected int dragStartIndex;
    
    protected int selEndLine = 0;
    protected int selEndCol = 0;
    public int selEndLineIndex = 0;
    public int selEndIndex = 0;
    protected int arrowLineIndex = 0;
    protected boolean arrowEnd = false;
    protected int scroll = 0;
    
    protected int lineSpread = mc.textRenderer.fontHeight + 1;
    protected int firstLine = 0;
    protected int lastLine;
    
    public EditorContent(int x, int y, int width, int height, int color, int borderColor, int hilightColor, int textColor, String message) {
        super(x, y, width, height, color, borderColor, hilightColor, textColor, new LiteralText(""), null);
        lastLine = (int) (height / (double) lineSpread) - 1;
        this.selColor = hilightColor;
        message = message.replaceAll("\r\n", "\n"); //no, bad windows.
        message = message.replaceAll("\t", "    "); //force 4 space, for rendering reasions.
        this.history = new History(message);
        this.updateSelStart(message.length());
        this.updateSelEnd(message.length());
        dragStartIndex = message.length();
        compileRenderedText();
    }
    
    @Override
    public EditorContent setPos(int x, int y, int width, int height) {
        double oldHeight = Math.max(this.height, 1);
        double oldTotalPages = Math.max(calcTotalPages(), 1);
        super.setPos(x, y, width, height);
        this.setScroll(scroll / oldHeight * calcTotalPages() / oldTotalPages);
        if (updateScrollPages != null) updateScrollPages.accept(calcTotalPages());
        return this;
    }
    
    private void compileRenderedText() {
        final List<Prism4j.Node> nodes = prism4j.tokenize(history.current, prism4j.grammar(language));
        final TextStyleCompiler visitor = new TextStyleCompiler(defaultStyle.withColor(TextColor.fromRgb(textColor)));
        visitor.visit(nodes);
        synchronized (renderedText) {
            renderedText = visitor.getResult().toArray(new LiteralText[0]);
        }
        if (updateScrollPages != null) updateScrollPages.accept(calcTotalPages());
    }
    
    public void setLanguage(String language) {
        this.language = language;
        compileRenderedText();
    }
    
    public void setScroll(double pages) {
        scroll = (int) (height * pages);
        int add = lineSpread - scroll % lineSpread;
        if (add == lineSpread) add = 0;
        firstLine = (int) Math.ceil(scroll / (double) lineSpread);
        lastLine = (int) (firstLine + (height - add) / (double) lineSpread) - 1;
    }
    
    private double calcTotalPages() {
        if (history == null) return 1;
        return (history.current.split("\n").length) / (Math.floor(height / (double) lineSpread) - 1D);
    }
    
    private int getIndexPosition(double x, double y) {
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
            col = mc.textRenderer.getTextHandler().trimToWidth(lines[line], (int) x, defaultStyle).length();
        }
        int count = 0;
        for (int i = 0; i < line; ++i) {
            count += lines[i].length() + 1;
        }
        count += col;
        return count;
    }
    
    public void updateSelStart(int startIndex) {
        selStartIndex = startIndex;
        String[] prev = history.current.substring(0, Math.min(history.current.length(), startIndex)).split("\n", -1);
        selStartLine = prev.length - 1;
        selStartCol = mc.textRenderer.getWidth(new LiteralText(prev[selStartLine]).setStyle(defaultStyle)) - 1;
        selStartLineIndex = prev[selStartLine].length();
    }
    
    public void updateSelEnd(int endIndex) {
        selEndIndex = endIndex;
        String[] prev = history.current.substring(0, Math.min(history.current.length(), endIndex)).split("\n", -1);
        selEndLine = prev.length - 1;
        selEndCol = mc.textRenderer.getWidth(new LiteralText(prev[selEndLine]).setStyle(defaultStyle));
        selEndLineIndex = prev[selEndLine].length();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.clicked(mouseX, mouseY);
        if (this.isFocused()) {
            int index = getIndexPosition(mouseX - x - 30, mouseY - y);
            updateSelStart(index);
            updateSelEnd(index);
            dragStartIndex = index;
            arrowEnd = false;
            arrowLineIndex = selStartLineIndex;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isFocused()) {
            int index = getIndexPosition(mouseX - x - 30, mouseY - y);
            if (index < dragStartIndex || (index == dragStartIndex && arrowEnd)) {
                arrowEnd = false;
                updateSelStart(index);
                arrowLineIndex = selStartLineIndex;
            } else if (index > dragStartIndex || !arrowEnd){
                arrowEnd = true;
                updateSelEnd(index);
                arrowLineIndex = selEndLineIndex;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean clicked(double mouseX, double mouseY) {
        boolean bl = super.clicked(mouseX, mouseY);
        if (this.isFocused() ^ bl) this.changeFocus(true);
        return bl;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isFocused()) {
            if (Screen.isSelectAll(keyCode)) {
                updateSelStart(0);
                updateSelEnd(history.current.length());
            } else if (Screen.isCopy(keyCode)) {
                mc.keyboard.setClipboard(history.current.substring(selStartIndex, selEndIndex));
            } else if (Screen.isPaste(keyCode)) {
                String pasteContent = mc.keyboard.getClipboard();
                history.replace(selStartIndex, selEndIndex - selStartIndex, pasteContent);
                updateSelEnd(selStartIndex + pasteContent.length());
                arrowEnd = true;
                
                compileRenderedText();
            } else if (Screen.isCut(keyCode)) {
                mc.keyboard.setClipboard(history.current.substring(selStartIndex, selEndIndex));
                history.replace(selStartIndex, selEndIndex - selStartIndex, "");
                updateSelEnd(selStartIndex);
                arrowEnd = false;
                
                compileRenderedText();
            }
            
            int index;
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE:
                    if (selStartIndex != selEndIndex) {
                        history.bkspacePos(selStartIndex, selEndIndex - selStartIndex);
                        compileRenderedText();
                        
                        updateSelEnd(selStartIndex);
                    } else if (selStartIndex > 0) {
                        history.bkspacePos(selStartIndex - 1, 1);
                        compileRenderedText();
    
                        updateSelStart(selStartIndex - 1);
                        updateSelEnd(selStartIndex);
                    }
                    break;
                case GLFW.GLFW_KEY_DELETE:
                    if (selStartIndex != selEndIndex) {
                        history.deletePos(selStartIndex, selEndIndex - selStartIndex);
                        updateSelEnd(selStartIndex);
                        compileRenderedText();
                    } else if (selStartIndex < history.current.length()) {
                        history.deletePos(selStartIndex, 1);
                        compileRenderedText();
                    }
                    break;
                case GLFW.GLFW_KEY_HOME:
                    updateSelStart(0);
                    updateSelEnd(0);
                    if (forceScrollAmmount != null) forceScrollAmmount.accept(0D);
                    break;
                case GLFW.GLFW_KEY_END:
                    updateSelStart(history.current.length());
                    updateSelEnd(history.current.length());
                    if (forceScrollAmmount != null) forceScrollAmmount.accept(1D);
                    break;
                case GLFW.GLFW_KEY_LEFT:
                    if (Screen.hasShiftDown()) {
                        if (arrowEnd && selStartIndex != selEndIndex) {
                            updateSelEnd(selEndIndex - 1);
                            arrowLineIndex = selEndLineIndex;
                        } else {
                            updateSelStart(selStartIndex - 1);
                            arrowLineIndex = selStartLineIndex;
                            arrowEnd = false;
                        }
                    } else {
                        if (selStartIndex != selEndIndex) {
                            updateSelEnd(selStartIndex);
                        } else {
                            updateSelStart(selStartIndex - 1);
                            updateSelEnd(selStartIndex);
                            arrowLineIndex = selStartLineIndex;
                        }
                    }
                    break;
                case GLFW.GLFW_KEY_RIGHT:
                    if (Screen.hasShiftDown()) {
                        if (arrowEnd || selEndIndex == selStartIndex) {
                            updateSelEnd(selEndIndex + 1);
                            arrowLineIndex = selEndLineIndex;
                            arrowEnd = true;
                        } else {
                            updateSelStart(selStartIndex + 1);
                            arrowLineIndex = selStartLineIndex;
                        }
                    } else {
                        if (selStartIndex != selEndIndex) {
                            updateSelStart(selEndIndex);
                        } else {
                            updateSelStart(selStartIndex + 1);
                            updateSelEnd(selStartIndex);
                            arrowLineIndex = selStartLineIndex;
                        }
                    }
                    break;
                case GLFW.GLFW_KEY_UP:
                    index = 0;
                    if (arrowEnd || selStartLine > 0) {
                        String[] lines = history.current.split("\n");
                        int line = (arrowEnd ? selEndLine : selStartLine) - 1;
                        for (int i = 0; i < line; ++i) {
                            index += lines[i].length() + 1;
                        }
                        index += Math.min(lines[line].length(), arrowLineIndex);
                    }
                    if (Screen.hasShiftDown()) {
                        if (arrowEnd && index >= selStartIndex) {
                            updateSelEnd(index);
                            arrowEnd = true;
                        } else {
                            updateSelStart(index);
                            arrowEnd = false;
                        }
                    } else {
                        if (selStartIndex != selEndIndex) {
                            updateSelEnd(selStartIndex);
                        } else {
                            updateSelStart(index);
                            updateSelEnd(selStartIndex);
                        }
                    }
                    break;
                case GLFW.GLFW_KEY_DOWN:
                    index = 0;
                    String[] lines = history.current.split("\n");
                    if (!arrowEnd || selEndLine < lines.length - 1) {
                        int line = (arrowEnd ? selEndLine : selStartLine) + 1;
                        for (int i = 0; i < line; ++i) {
                            index += lines[i].length() + 1;
                        }
                        index += Math.min(lines[line].length(), arrowLineIndex);
                    } else {
                        index = history.current.length();
                    }
                    if (Screen.hasShiftDown()) {
                        if (!arrowEnd && index <= selEndIndex) {
                            updateSelStart(index);
                            arrowEnd = false;
                        } else {
                            updateSelEnd(index);
                            arrowEnd = true;
                        }
                    } else {
                        if (selStartIndex != selEndIndex) {
                            updateSelStart(selEndIndex);
                        } else {
                            updateSelStart(index);
                            updateSelEnd(selStartIndex);
                        }
                    }
                    break;
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
                default:
            }
        }
    
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (selStartIndex != selEndIndex) {
            history.replace(selStartIndex, selEndIndex - selStartIndex, String.valueOf(chr));
        } else {
            history.addChar(selStartIndex, chr);
        }
        updateSelStart(selStartIndex + 1);
        updateSelEnd(selStartIndex);
        
        compileRenderedText();
        return false;
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, x, y, x + width, y + height, color);
        fill(matrices, x + 28, y, x + 29, y + height, borderColor);
        
        Style lineNumStyle = defaultStyle.withColor(TextColor.fromRgb(textColor));
        int add = lineSpread - scroll % lineSpread;
        if (add == lineSpread) add = 0;
        synchronized (renderedText) {
            if (renderedText != null)
                for (int i = 0, j = firstLine; j <= lastLine && j < renderedText.length; ++i, ++j) {
                    if (selStartLine == j && selEndLine == j) {
                        fill(matrices, x + 30 + selStartCol, y + add + i * lineSpread, x + 30 + selEndCol, y + add + (i + 1) * lineSpread, hilightColor);
                    } else if (selStartLine == j) {
                        fill(matrices, x + 30 + selStartCol, y + add + i * lineSpread, x + width, y + add + (i + 1) * lineSpread, hilightColor);
                    } else if (j > selStartLine && j < selEndLine) {
                        fill(matrices, x + 29, y + add + i * lineSpread, x + width, y + add + (i + 1) * lineSpread, hilightColor);
                    } else if (selEndLine == j) {
                        fill(matrices, x + 29, y + add + i * lineSpread, x + 30 + selEndCol, y + add + (i + 1) * lineSpread, hilightColor);
                    }
                    LiteralText lineNum = (LiteralText) new LiteralText(String.format("%d.", j + 1)).setStyle(lineNumStyle);
                    mc.textRenderer.draw(matrices, lineNum, x + 28 - mc.textRenderer.getWidth(lineNum), y + add + i * lineSpread, 0xFFFFFF);
                    mc.textRenderer.draw(matrices, trim(renderedText[j]), x + 30, y + add + i * lineSpread, 0xFFFFFF);
                }
        }
    }
    
    private OrderedText trim(LiteralText text) {
        if (mc.textRenderer.getWidth(text) > width - 30) {
            OrderedText trimmed = Language.getInstance().reorder(mc.textRenderer.trimToWidth(text, width - 30 - ellipsesWidth));
            return OrderedText.concat(trimmed, ellipses);
        } else {
            return text.asOrderedText();
        }
    }
    
}
