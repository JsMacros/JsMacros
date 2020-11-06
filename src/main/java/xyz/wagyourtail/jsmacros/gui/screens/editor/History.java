package xyz.wagyourtail.jsmacros.gui.screens.editor;

import net.minecraft.text.StringVisitable;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class History {
    protected int MAX_UNDO = 20;
    protected List<HistoryStep> undo = new ArrayList<>(MAX_UNDO + 1);
    protected List<HistoryStep> redo = new ArrayList<>(MAX_UNDO + 1);
    protected SelectCursor cursor;
    protected String current;
    
    public History(String start, SelectCursor cursor) {
        this.current = start;
        this.cursor = cursor;
    }
    
    /**
     * @param position
     * @param content
     * @return is new step.
     */
    public synchronized boolean addChar(int position, char content) {
        Add step = new Add(position, String.valueOf(content), cursor);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof Add && ((Add) prev).position + ((Add) prev).added.length() == position) {
                if (content != '\n') {
                    ((Add) prev).added += String.valueOf(content);
                    return false;
                }
            }
        }
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        return true;
    }
    
    /**
     * @param position
     *
     * @return is new step.
     */
    public synchronized boolean deletePos(int position, int length) {
        if (position == current.length()) return false;
        Remove step = new Remove(position, length, false, cursor);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof Remove && !((Remove) prev).isBkspace && ((Remove) prev).position == position) {
                ((Remove) prev).removed += step.removed;
                ((Remove) prev).length += length;
                return false;
            }
        }
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        return true;
    }
    
    /**
     * @param position
     *
     * @return is new step
     */
    public synchronized boolean bkspacePos(int position, int length) {
        if (position < 0) return false;
        Remove step = new Remove(position, length, true, cursor);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof Remove && ((Remove) prev).isBkspace && position + length == ((Remove) prev).position) {
                ((Remove) prev).removed = step.removed + ((Remove) prev).removed;
                --((Remove) prev).position;
                ((Remove) prev).length += length;
                return false;
            }
        }
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        return true;
    }
    
    public synchronized boolean shiftLine(int startLine, int lines, boolean shiftDown) {
        if (startLine + lines == current.split("\n").length && shiftDown
            || startLine == 0 && !shiftDown) return false;
        ShiftLine step = new ShiftLine(startLine, lines, 1, shiftDown, cursor);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof ShiftLine && ((ShiftLine) prev).shiftDown == shiftDown && ((ShiftLine) prev).lineCount == lines && startLine == ((ShiftLine) prev).startLine + (shiftDown ? ((ShiftLine) prev).shiftAmmount : -((ShiftLine) prev).shiftAmmount)) {
                ++((ShiftLine) prev).shiftAmmount;
                return false;
            }
        }
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        return true;
    }
    
    public synchronized void replace(int position, int length, String content) {
        HistoryStep step = new Replace(position, length, content, cursor);
        current = step.applyStep(current);
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
    }
    
    /**
     * @return position of step. -1 if nothing to undo.
     */
    public synchronized int undo() {
        if (undo.size() > 0) {
            HistoryStep step = undo.remove(undo.size() - 1);
            current = step.unApplyStep(current);
            redo.add(step);
            return step.position;
        }
        return -1;
    }
    
    /**
     * @return position of step. -1 if nothing to redo.
     */
    public synchronized int redo() {
        if (redo.size() > 0) {
            HistoryStep step = redo.remove(redo.size() - 1);
            current = step.applyStep(current);
            undo.add(step);
            return step.position;
        }
        return -1;
    }
    
    protected static abstract class HistoryStep {
        int position;
        SelectCursor cursor;
        
        
        
        protected abstract String applyStep(String input);
        
        protected abstract String unApplyStep(String input);
        
    }
    
    protected static class Add extends HistoryStep {
        String added;
        
        protected Add(int position, String added, SelectCursor cursor) {
            this.position = position;
            this.added = added;
            this.cursor = cursor;
        }
        
        protected String applyStep(String input) {
            String result = input.substring(0, position) + added + input.substring(position);
            cursor.updateSelStart(position + added.length(), result);
            cursor.updateSelEnd(position + added.length(), result);
            return result;
        }
        
        protected String unApplyStep(String input) {
            String result = input.substring(0, position) + input.substring(position + added.length());
            cursor.updateSelStart(position, result);
            cursor.updateSelEnd(position, result);
            return result;
        }
        
    }
    
    protected static class Remove extends HistoryStep {
        boolean isBkspace = false;
        int length;
        String removed;
        protected Remove(int position, int length, boolean isBkspace, SelectCursor cursor) {
            this.position = position;
            this.length = length;
            this.isBkspace = isBkspace;
            this.cursor = cursor;
        }
        
        protected String applyStep(String input) {
            removed = input.substring(position, position + length);
            String result = input.substring(0, position) + input.substring(position + length);
            cursor.updateSelStart(position, result);
            cursor.updateSelEnd(position, result);
            return result;
        }
        
        protected String unApplyStep(String input) {
            String result = input.substring(0, position) + removed + input.substring(position);
            cursor.updateSelStart(position, result);
            cursor.updateSelEnd(position + length, result);
            if (isBkspace) cursor.arrowEnd = true;
            else cursor.arrowEnd = false;
            return result;
        }
        
    }
    
    protected static class Replace extends HistoryStep {
        int length;
        String oldContent;
        String newContent;
        
        protected Replace(int position, int length, String newContent, SelectCursor cursor) {
            this.position = position;
            this.length = length;
            this.newContent = newContent;
            this.cursor = cursor;
        }
        
        protected String applyStep(String input) {
            oldContent = input.substring(position, position + length);
            String result = input.substring(0, position) + newContent + input.substring(position + length);
            cursor.updateSelStart(position, result);
            cursor.updateSelEnd(position + newContent.length(), result);
            return result;
        }
        
        protected String unApplyStep(String input) {
            String result = input.substring(0, position) + oldContent + input.substring(position + newContent.length());
            cursor.updateSelStart(position, result);
            cursor.updateSelEnd(position + oldContent.length(), result);
            return result;
        }
        
    }
    
    protected static class ShiftLine extends HistoryStep {
        int startLine;
        int lineCount;
        int shiftAmmount;
        boolean shiftDown;
        
        protected ShiftLine(int startLine, int lineCount, int shiftAmmount, boolean shiftDown, SelectCursor cursor) {
            this.startLine = startLine;
            this.lineCount = lineCount;
            this.shiftAmmount = shiftAmmount;
            this.shiftDown = shiftDown;
            this.cursor = cursor;
        }
        
        protected String applyStep(String input) {
            return shift(input, startLine, lineCount, shiftAmmount, shiftDown);
        }
        
        protected String unApplyStep(String input) {
            return shift(input, shiftDown ? startLine + shiftAmmount : startLine - shiftAmmount, lineCount, shiftAmmount, !shiftDown);
        }
        
        private String shift(String input, int startLine, int lineCount, int shiftAmmount, boolean shiftDown) {
            String[] lines = input.split("\n");
            String[] shifted = new String[lines.length];
            if (shiftDown) {
                int i;
                int startIndex = 0;
                for (i = 0; i < startLine; ++i) {
                    shifted[i] = lines[i];
                    startIndex += shifted[i].length() + 1;
                }
                for (int j = 0; j < shiftAmmount; ++j, ++i) {
                    shifted[i] = lines[startLine + lineCount + j];
                    startIndex += shifted[i].length() + 1;
                }
                int endIndex = startIndex;
                for (int j = 0; j < lineCount; ++j, ++i) {
                    shifted[i] = lines[startLine + j];
                    endIndex += shifted[i].length() + 1;
                }
                for (; i < lines.length; ++i) {
                    shifted[i] = lines[i];
                }
                String result = String.join("\n", shifted);
                cursor.updateSelStart(startIndex, result);
                cursor.updateSelEnd(--endIndex, result);
                return result;
            } else {
                int i;
                int startIndex = 0;
                for (i = 0; i < startLine - shiftAmmount; ++i) {
                    shifted[i] = lines[i];
                    startIndex += shifted[i].length() + 1;
                }
                int endIndex = startIndex;
                for (int j = 0; j < lineCount; ++j, ++i) {
                    shifted[i] = lines[startLine + j];
                    endIndex += shifted[i].length() + 1;
                }
                for (int j = startLine - shiftAmmount; j < startLine; ++j, ++i) {
                    shifted[i] = lines[j];
                }
                for (; i < lines.length; ++i) {
                    shifted[i] = lines[i];
                }
                String result = String.join("\n", shifted);
                cursor.updateSelStart(startIndex, result);
                cursor.updateSelEnd(--endIndex, result);
                return result;
            }
        }
    }
    
}
