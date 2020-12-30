package xyz.wagyourtail.jsmacros.client.gui.elements.editor;

import io.noties.prism4j.Prism4j;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.jsmacros.client.gui.elements.editor.children.History;
import xyz.wagyourtail.jsmacros.client.gui.elements.editor.children.PrismGrammarLocator;
import xyz.wagyourtail.jsmacros.client.gui.elements.editor.children.SelectCursor;
import xyz.wagyourtail.jsmacros.client.gui.elements.editor.children.TextStyleCompiler;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;

import java.util.List;
import java.util.function.Consumer;

public class EditorContent extends Button {
    private final static Prism4j prism4j = new Prism4j(new PrismGrammarLocator());
    private final static OrderedText ellipses = new LiteralText("...").formatted(Formatting.DARK_GRAY).asOrderedText();
    private final int ellipsesWidth = mc.textRenderer.getWidth(ellipses);
    
    /*TODO: make py script to convert a copyleft monospaced font to png's for minecraft, or add class for on the fly from a ttf*/
    public final static Style defaultStyle = Style.EMPTY.withFont(new Identifier("jsmacros", "uniform"));
    public long textRenderTime = 0;
    public String language = "javascript";
    public Consumer<Double> updateScrollPages;
    public Consumer<Double> scrollToPercent;
    public Runnable save;
    
    protected LiteralText[] renderedText = new LiteralText[0];
    public final History history;
    public final SelectCursor cursor = new SelectCursor(defaultStyle);
    
    protected int selColor;
    
    
    protected int scroll = 0;
    
    protected int lineSpread = mc.textRenderer.fontHeight + 1;
    protected int firstLine = 0;
    protected int lastLine;
    
    public EditorContent(int x, int y, int width, int height, int color, int borderColor, int hilightColor, int textColor, String message, Runnable save) {
        super(x, y, width, height, color, borderColor, hilightColor, textColor, new LiteralText(""), null);
        lastLine = (int) (height / (double) lineSpread) - 1;
        this.selColor = hilightColor;
        this.save = save;
        message = message.replaceAll("\r\n", "\n"); //no, bad windows.
        message = message.replaceAll("\t", "    "); //force 4 space, for rendering reasons.
        this.history = new History(message, cursor);
        cursor.updateStartIndex(message.length(), history.current);
        cursor.updateEndIndex(message.length(), history.current);
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
    
    private synchronized void compileRenderedText() {
        long time = System.currentTimeMillis();
        if (!history.current.equals("")) {
            final List<Prism4j.Node> nodes = prism4j.tokenize(history.current, prism4j.grammar(language));
            final TextStyleCompiler visitor = new TextStyleCompiler(defaultStyle.withColor(TextColor.fromRgb(textColor)));
            visitor.visit(nodes);
            synchronized (renderedText) {
                renderedText = visitor.getResult().toArray(new LiteralText[0]);
            }
        } else {
            renderedText = new LiteralText[] {new LiteralText("")};
        }
        if (updateScrollPages != null) updateScrollPages.accept(calcTotalPages());
        textRenderTime = System.currentTimeMillis() - time;
        scrollToCursor();
    }
    
    public void scrollToCursor() {
        if (scrollToPercent != null) {
            int cursorLine = cursor.arrowEnd ? cursor.endLine : cursor.startLine;
            if (cursorLine < firstLine || cursorLine > lastLine) {
                int pagelength = lastLine - firstLine;
                double scrollLines = history.current.split("\n", -1).length - pagelength;
                cursorLine -= pagelength / 2;
                scrollToPercent.accept(MathHelper.clamp(cursorLine, 0, scrollLines) / scrollLines);
            }
        }
    }
    
    public synchronized void setLanguage(String language) {
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
        return (history.current.split("\n", -1).length) / (Math.floor(height / (double) lineSpread) - 1D);
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
    
    public void selectWordAtCursor() {
        String currentLine = history.current.split("\n", -1)[cursor.startLine];
        String[] startWords = currentLine.substring(0, cursor.startLineIndex).split("\\b");
        int dStart = -startWords[startWords.length - 1].length();
        String[] endWords = currentLine.substring(cursor.startLineIndex, currentLine.length()).split("\\b");
        int dEnd = endWords[0].length();
        int currentIndex = cursor.startIndex;
        cursor.updateStartIndex(currentIndex + dStart, history.current);
        cursor.updateEndIndex(currentIndex + dEnd, history.current);
    }
    
    @Override
    public synchronized boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.clicked(mouseX, mouseY);
        if (this.isFocused()) {
            int index = getIndexPosition(mouseX - x - 30, mouseY - y + 1);
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
                    cursor.dragStartIndex = index;
                    cursor.arrowEnd = false;
                    cursor.arrowLineIndex = cursor.startLineIndex;
                } else {
                    cursor.updateStartIndex(index, history.current);
                    cursor.updateEndIndex(index, history.current);
                    cursor.dragStartIndex = index;
                    cursor.arrowEnd = false;
                    cursor.arrowLineIndex = cursor.startLineIndex;
                }
            }
            
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public synchronized boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isFocused()) {
            int index = getIndexPosition(mouseX - x - 30, mouseY - y);
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
    public boolean clicked(double mouseX, double mouseY) {
        boolean bl = super.clicked(mouseX, mouseY);
        if (this.isFocused() ^ bl) this.changeFocus(true);
        return bl;
    }
    
    @Override
    public synchronized boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isFocused()) {
            if (Screen.isSelectAll(keyCode)) {
                cursor.updateStartIndex(0, history.current);
                cursor.updateEndIndex(history.current.length(), history.current);
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
                    startSpaces = history.current.split("\n", -1)[cursor.startLine].split("[^\\s]", -1)[0];
                    if (cursor.startLineIndex <= startSpaces.length()) {
                        cursor.updateStartIndex(cursor.startIndex - cursor.startLineIndex, history.current);
                    } else {
                        cursor.updateStartIndex(cursor.startIndex - cursor.startLineIndex + startSpaces.length(), history.current);
                    }
                    cursor.updateEndIndex(cursor.startIndex, history.current);
//                    cursor.updateStartIndex(0, history.current);
//                    cursor.updateEndIndex(0, history.current);
//                    if (scrollToPercent != null) scrollToPercent.accept(0D);
                    break;
                case GLFW.GLFW_KEY_END:
                    int endLineLength = history.current.split("\n", -1)[cursor.endLine].length();
                    cursor.updateEndIndex(cursor.endIndex+(endLineLength - cursor.endLineIndex), history.current);
                    cursor.updateStartIndex(cursor.endIndex, history.current);
//                    cursor.updateStartIndex(history.current.length(), history.current);
//                    cursor.updateEndIndex(history.current.length(), history.current);
//                    if (scrollToPercent != null) scrollToPercent.accept(1D);
                    break;
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
                    break;
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
                    break;
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
                            if (cursor.startIndex != cursor.endIndex) {
                                cursor.updateEndIndex(cursor.startIndex, history.current);
                            } else {
                                cursor.updateStartIndex(index, history.current);
                                cursor.updateEndIndex(cursor.startIndex, history.current);
                            }
                        }
                    }
                    scrollToCursor();
                    break;
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
                    break;
                case GLFW.GLFW_KEY_Y:
                    int i = history.redo();
    
                    if (i != -1) {
                        compileRenderedText();
                    }
                    break;
                case GLFW.GLFW_KEY_S:
                    if (Screen.hasControlDown()) {
                        if (save != null) save.run();
                    }
                    break;
                case GLFW.GLFW_KEY_ENTER:
                    startSpaces = history.current.split("\n", -1)[cursor.startLine].split("[^\\s]", -1)[0];
                    if (cursor.startIndex != cursor.endIndex) {
                        history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, "\n" + startSpaces);
                        cursor.updateStartIndex(cursor.endIndex, history.current);
                    } else {
                        history.add(cursor.startIndex, "\n" + startSpaces);
                    }
    
                    compileRenderedText();
                    break;
                case GLFW.GLFW_KEY_TAB:
                    if (cursor.startIndex != cursor.endIndex || Screen.hasShiftDown()) {
                        history.tabLines(cursor.startLine, cursor.endLine - cursor.startLine + 1, Screen.hasShiftDown());
                    } else {
                        history.addChar(cursor.startIndex, '\t');
                    }
    
                    compileRenderedText();
                    break;
                case GLFW.GLFW_KEY_PAGE_UP:
                    currentPage = scroll / (0.0D + height);
                    scrollToPercent.accept(MathHelper.clamp((currentPage - 1)/(calcTotalPages() - 1), 0, 1));
                    break;
                case GLFW.GLFW_KEY_PAGE_DOWN:
                    currentPage = scroll / (0.0D + height);
                    scrollToPercent.accept(MathHelper.clamp((currentPage + 1)/(calcTotalPages() - 1), 0, 1));
                    break;
                default:
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public synchronized boolean charTyped(char chr, int keyCode) {
        if (cursor.startIndex != cursor.endIndex) {
            history.replace(cursor.startIndex, cursor.endIndex - cursor.startIndex, String.valueOf(chr));
            cursor.updateStartIndex(cursor.endIndex, history.current);
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
        fill(matrices, x, y, x + 1, y + height, borderColor);
        fill(matrices, x + width - 1, y, x + width, y + height, borderColor);
        fill(matrices, x + 1, y, x + width - 1, y + 1, borderColor);
        fill(matrices, x + 1, y + height - 1, x + width - 1, y + height, borderColor);
        
        Style lineNumStyle = defaultStyle.withColor(TextColor.fromRgb(textColor));
        int add = lineSpread - scroll % lineSpread;
        if (add == lineSpread) add = 0;
        int y = this.y + 1;
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
