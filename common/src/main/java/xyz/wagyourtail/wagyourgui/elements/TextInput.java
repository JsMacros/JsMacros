package xyz.wagyourtail.wagyourgui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;

public class TextInput extends Button {
    public Consumer<String> onChange;
    public String mask = ".*";
    public String content;
    protected int selColor;
    protected int selStart;
    public int selStartIndex;
    protected int selEnd;
    public int selEndIndex;
    protected int arrowCursor;

    public TextInput(int x, int y, int width, int height, FontRenderer textRenderer, int color, int borderColor, int hilightColor, int textColor, String message, Consumer<Button> onClick, Consumer<String> onChange) {
        super(x, y, width, height, textRenderer, color, borderColor, color, textColor, new ChatComponentText(""), onClick);
        this.selColor = hilightColor;
        this.content = message;
        this.onChange = onChange;
        this.updateSelStart(content.length());
        this.updateSelEnd(content.length());
        this.arrowCursor = content.length();
    }

    public void setMessage(String message) {
        content = message;
    }

    public void updateSelStart(int startIndex) {
        selStartIndex = startIndex;
        if (startIndex == 0) selStart = x + 1;
        else selStart = x + 2 + textRenderer.getStringWidth(content.substring(0, startIndex));
    }

    public void updateSelEnd(int endIndex) {
        selEndIndex = endIndex;
        if (endIndex == 0) selEnd = x + 2;
        else selEnd = x + 3 + textRenderer.getStringWidth(content.substring(0, endIndex));
    }

    public boolean selected = false;

    @Override
    public boolean isMouseOver(Minecraft mc, int mouseX, int mouseY) {
        if (super.isMouseOver(mc, mouseX, mouseY)) {
            selected = true;
            int pos = textRenderer.trimToWidth(content, mouseX - x - 2).length();
            updateSelStart(pos);
            updateSelEnd(pos);
            arrowCursor = pos;
            return true;
        }
        selected = false;
        return false;
    }


    public boolean mouseDragged(int mouseX, int mouseY, int button, int dX, int dY) {
        if (selected) {
            int pos = textRenderer.trimToWidth(content, mouseX - x - 2).length();
            updateSelEnd(pos);
            arrowCursor = pos;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
    }

    public void swapStartEnd() {
        int temp1 = selStartIndex;
        updateSelStart(selEndIndex);
        updateSelEnd(temp1);

    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean ctrl;
        if (selected) {
            if (selEndIndex < selStartIndex) swapStartEnd();
            Minecraft mc = Minecraft.getInstance();
            if (GuiScreen.isSelectAll(keyCode)) {
                this.updateSelStart(0);
                this.updateSelEnd(content.length());
                return true;
            } else if (GuiScreen.isCopy(keyCode)) {
                GuiScreen.setClipboard(this.content.substring(selStartIndex, selEndIndex));
                return true;
            } else if (GuiScreen.isPaste(keyCode)) {
                content = content.substring(0, selStartIndex) + GuiScreen.getClipboard() + content.substring(selEndIndex);
                if (onChange != null) onChange.accept(content);
                updateSelEnd(selStartIndex + GuiScreen.getClipboard().length());
                arrowCursor = selStartIndex + GuiScreen.getClipboard().length();
                return true;
            } else if (GuiScreen.isCut(keyCode)) {
                GuiScreen.setClipboard(this.content.substring(selStartIndex, selEndIndex));
                content = content.substring(0, selStartIndex) + content.substring(selEndIndex);
                if (onChange != null) onChange.accept(content);
                updateSelEnd(selStartIndex);
                arrowCursor = selStartIndex;
                return true;
            }
            switch (keyCode) {
                case Keyboard.KEY_BACK:
                    if (selStartIndex == selEndIndex && selStartIndex > 0) updateSelStart(selStartIndex - 1);
                    content = content.substring(0, selStartIndex) + content.substring(selEndIndex);
                    if (onChange != null) onChange.accept(content);
                    updateSelEnd(selStartIndex);
                    arrowCursor = selStartIndex;
                    return true;
                case Keyboard.KEY_DELETE:
                    if (selStartIndex == selEndIndex && selStartIndex < content.length()) updateSelEnd(selEndIndex + 1);
                    content = content.substring(0, selStartIndex) + content.substring(selEndIndex);
                    if (onChange != null) onChange.accept(content);
                    updateSelEnd(selStartIndex);
                    arrowCursor = selStartIndex;
                    return true;
                case Keyboard.KEY_HOME:
                    updateSelStart(0);
                    updateSelEnd(0);
                    return true;
                case Keyboard.KEY_END:
                    this.updateSelStart(content.length());
                    this.updateSelEnd(content.length());
                    return true;
                case Keyboard.KEY_LEFT:
                    ctrl = !GuiScreen.hasControlDown();
                    if (arrowCursor > 0) if (arrowCursor < selEndIndex) {
                        updateSelStart(--arrowCursor);
                        if (ctrl) updateSelEnd(selStartIndex);
                    } else if (arrowCursor >= selEndIndex) {
                        updateSelEnd(--arrowCursor);
                        if (ctrl) updateSelStart(selEndIndex);
                    }
                    return true;
                case Keyboard.KEY_RIGHT:
                    ctrl = !GuiScreen.hasControlDown();
                    if (arrowCursor < content.length()) if (arrowCursor < selEndIndex) {
                        updateSelStart(++arrowCursor);
                        if (ctrl) updateSelEnd(selStartIndex);
                    } else {
                        updateSelEnd(++arrowCursor);
                        if (ctrl) updateSelStart(selEndIndex);
                    }
                    return true;
                case Keyboard.KEY_LCONTROL:
                case Keyboard.KEY_RCONTROL:
                case Keyboard.KEY_LMETA:
                case Keyboard.KEY_RMETA:
                case Keyboard.KEY_LMENU:
                case Keyboard.KEY_RMENU:
                case Keyboard.KEY_CAPITAL:
                case Keyboard.KEY_LSHIFT:
                case Keyboard.KEY_RSHIFT:
                    return true;
                default:
            }
            return charTyped(Keyboard.getEventCharacter(), keyCode);
        }
        return false;
    }

    public boolean charTyped(char chr, int keyCode) {
        if (selEndIndex < selStartIndex) swapStartEnd();
        String newContent = content.substring(0, selStartIndex) + chr + content.substring(selEndIndex);
        if (newContent.matches(mask)) {
            content = newContent;
            if (onChange != null) onChange.accept(content);
            updateSelStart(selStartIndex + 1);
            arrowCursor = selStartIndex;
            updateSelEnd(arrowCursor);
        }
        return false;
    }

    @Override
    protected void renderMessage() {
        fill(selStart, height > 9 ? y + 2 : y, Math.min(selEnd, x + width - 2), (height > 9 ? y + 2 : y) + textRenderer.fontHeight, selColor);
        drawWithShadow(textRenderer, textRenderer.trimToWidth(content, width - 4), x + 2, height > 9 ? y + 2 : y, textColor);
    }
}