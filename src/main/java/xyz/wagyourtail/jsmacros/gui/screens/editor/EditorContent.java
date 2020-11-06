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
    public Consumer<Double> scrollToPercent;
    
    protected LiteralText[] renderedText = new LiteralText[0];
    protected History history;
    protected SelectCursor cursor = new SelectCursor(defaultStyle);
    
    protected int selColor;
    
    
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
        this.history = new History(message, cursor);
        cursor.updateSelStart(message.length(), history.current);
        cursor.updateSelEnd(message.length(), history.current);
        cursor.dragStartIndex = message.length();
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
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.clicked(mouseX, mouseY);
        if (this.isFocused()) {
            int index = getIndexPosition(mouseX - x - 30, mouseY - y);
            cursor.updateSelStart(index, history.current);
            cursor.updateSelEnd(index, history.current);
            cursor.dragStartIndex = index;
            cursor.arrowEnd = false;
            cursor.arrowLineIndex = cursor.startLineIndex;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isFocused()) {
            int index = getIndexPosition(mouseX - x - 30, mouseY - y);
            if (index < cursor.dragStartIndex || (index == cursor.dragStartIndex && cursor.arrowEnd)) {
                cursor.arrowEnd = false;
                cursor.updateSelStart(index, history.current);
                cursor.arrowLineIndex = cursor.startLineIndex;
            } else {
                cursor.arrowEnd = true;
                cursor.updateSelEnd(index, history.current);
                cursor.arrowLineIndex = cursor.endLineIndex;
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
                cursor.updateSelStart(0, history.current);
                cursor.updateSelEnd(history.current.length(), history.current);
                cursor.arrowEnd = true;
            } else if (Screen.isCopy(keyCode)) {
                mc.keyboard.setClipboard(history.current.substring(cursor.startIndex, cursor.endIndex));
            } else if (Screen.isPaste(keyCode)) {
                String pasteContent = mc.keyboard.getClipboard();
                history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, pasteContent);
                
                compileRenderedText();
            } else if (Screen.isCut(keyCode)) {
                mc.keyboard.setClipboard(history.current.substring(cursor.startIndex, cursor.endIndex));
                history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, "");
                
                compileRenderedText();
            }
            
            int index;
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE:
                    if (cursor.startIndex != cursor.endIndex) {
                        history.bkspacePos(cursor.startIndex, cursor.endIndex - cursor.startIndex);
                        compileRenderedText();
                    } else if (cursor.startIndex > 0) {
                        history.bkspacePos(cursor.startIndex - 1, 1);
                        compileRenderedText();
                    }
                    break;
                case GLFW.GLFW_KEY_DELETE:
                    if (cursor.startIndex != cursor.endIndex) {
                        history.deletePos(cursor.startIndex, cursor.endIndex - cursor.startIndex);
                        compileRenderedText();
                    } else if (cursor.startIndex < history.current.length()) {
                        history.deletePos(cursor.startIndex, 1);
                        compileRenderedText();
                    }
                    break;
                case GLFW.GLFW_KEY_HOME:
                    cursor.updateSelStart(0, history.current);
                    cursor.updateSelEnd(0, history.current);
                    if (scrollToPercent != null) scrollToPercent.accept(0D);
                    break;
                case GLFW.GLFW_KEY_END:
                    cursor.updateSelStart(history.current.length(), history.current);
                    cursor.updateSelEnd(history.current.length(), history.current);
                    if (scrollToPercent != null) scrollToPercent.accept(1D);
                    break;
                case GLFW.GLFW_KEY_LEFT:
                    if (Screen.hasShiftDown()) {
                        if (cursor.arrowEnd && cursor.startIndex != cursor.endIndex) {
                            cursor.updateSelEnd(cursor.endIndex - 1, history.current);
                            cursor.arrowLineIndex = cursor.endLineIndex;
                        } else {
                            cursor.updateSelStart(cursor.startIndex - 1, history.current);
                            cursor.arrowLineIndex = cursor.startLineIndex;
                            cursor.arrowEnd = false;
                        }
                    } else {
                        if (cursor.startIndex != cursor.endIndex) {
                            cursor.updateSelEnd(cursor.startIndex, history.current);
                        } else {
                            cursor.updateSelStart(cursor.startIndex - 1, history.current);
                            cursor.updateSelEnd(cursor.startIndex, history.current);
                            cursor.arrowLineIndex = cursor.startLineIndex;
                        }
                    }
                    break;
                case GLFW.GLFW_KEY_RIGHT:
                    if (Screen.hasShiftDown()) {
                        if (cursor.arrowEnd || cursor.endIndex == cursor.startIndex) {
                            cursor.updateSelEnd(cursor.endIndex + 1, history.current);
                            cursor.arrowLineIndex = cursor.endLineIndex;
                            cursor.arrowEnd = true;
                        } else {
                            cursor.updateSelStart(cursor.startIndex + 1, history.current);
                            cursor.arrowLineIndex = cursor.startLineIndex;
                        }
                    } else {
                        if (cursor.startIndex != cursor.endIndex) {
                            cursor.updateSelStart(cursor.endIndex, history.current);
                        } else {
                            cursor.updateSelStart(cursor.startIndex + 1, history.current);
                            cursor.updateSelEnd(cursor.startIndex, history.current);
                            cursor.arrowLineIndex = cursor.startLineIndex;
                        }
                    }
                    break;
                case GLFW.GLFW_KEY_UP:
                    if (Screen.hasAltDown()) {
                        history.shiftLine(cursor.startLine, cursor.endLine - cursor.startLine + 1, false);
                        compileRenderedText();
                    } else {
                        index = 0;
                        if (cursor.arrowEnd || cursor.startLine > 0) {
                            String[] lines = history.current.split("\n");
                            int line = (cursor.arrowEnd ? cursor.endLine : cursor.startLine) - 1;
                            for (int i = 0; i < line; ++i) {
                                index += lines[i].length() + 1;
                            }
                            index += Math.min(lines[line].length(), cursor.arrowLineIndex);
                        }
                        if (Screen.hasShiftDown()) {
                            if (cursor.arrowEnd && index >= cursor.startIndex) {
                                cursor.updateSelEnd(index, history.current);
                                cursor.arrowEnd = true;
                            } else {
                                cursor.updateSelStart(index, history.current);
                                cursor.arrowEnd = false;
                            }
                        } else {
                            if (cursor.startIndex != cursor.endIndex) {
                                cursor.updateSelEnd(cursor.startIndex, history.current);
                            } else {
                                cursor.updateSelStart(index, history.current);
                                cursor.updateSelEnd(cursor.startIndex, history.current);
                            }
                        }
                    }
                    break;
                case GLFW.GLFW_KEY_DOWN:
                    if (Screen.hasAltDown()) {
                        history.shiftLine(cursor.startLine, cursor.endLine - cursor.startLine + 1, true);
                        compileRenderedText();
                    } else {
                        index = 0;
                        String[] lines = history.current.split("\n");
                        if (!cursor.arrowEnd || cursor.endLine < lines.length - 1) {
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
                                cursor.updateSelStart(index, history.current);
                                cursor.arrowEnd = false;
                            } else {
                                cursor.updateSelEnd(index, history.current);
                                cursor.arrowEnd = true;
                            }
                        } else {
                            if (cursor.startIndex != cursor.endIndex) {
                                cursor.updateSelStart(cursor.endIndex, history.current);
                            } else {
                                cursor.updateSelStart(index, history.current);
                                cursor.updateSelEnd(cursor.startIndex, history.current);
                            }
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
        if (cursor.startIndex != cursor.endIndex) {
            history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, String.valueOf(chr));
        } else {
            history.addChar(cursor.startIndex, chr);
        }
        
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
                    if (cursor.startLine == j && cursor.endLine == j) {
                        fill(matrices, x + 30 + cursor.startCol, y + add + i * lineSpread, x + 30 + cursor.endCol, y + add + (i + 1) * lineSpread, hilightColor);
                    } else if (cursor.startLine == j) {
                        fill(matrices, x + 30 + cursor.startCol, y + add + i * lineSpread, x + width, y + add + (i + 1) * lineSpread, hilightColor);
                    } else if (j > cursor.startLine && j < cursor.endLine) {
                        fill(matrices, x + 29, y + add + i * lineSpread, x + width, y + add + (i + 1) * lineSpread, hilightColor);
                    } else if (cursor.endLine == j) {
                        fill(matrices, x + 29, y + add + i * lineSpread, x + 30 + cursor.endCol, y + add + (i + 1) * lineSpread, hilightColor);
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
