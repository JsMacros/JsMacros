package xyz.wagyourtail.jsmacros.client.gui.editor;

import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class History {
    protected int MAX_UNDO = JsMacrosClient.clientCore.config.getOptions(ClientConfigV2.class).editorHistorySize;
    protected List<HistoryStep> undo = new ArrayList<>(MAX_UNDO + 1);
    protected List<HistoryStep> redo = new ArrayList<>(MAX_UNDO + 1);

    public Consumer<String> onChange;
    protected SelectCursor cursor;
    public String current;

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
        return add(position, content == '\t' ? "    " : String.valueOf(content));
    }

    public synchronized boolean add(int position, String content) {
        Add step = new Add(position, content, cursor);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof Add && prev.position + ((Add) prev).added.length() == position) {
                if (!content.startsWith("\n")) {
                    ((Add) prev).added += step.added;
                    if (onChange != null) {
                        onChange.accept(current);
                    }
                    return false;
                }
            }
        }
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        if (onChange != null) {
            onChange.accept(current);
        }
        return true;

    }

    /**
     * @param position
     * @return is new step.
     */
    public synchronized boolean deletePos(int position, int length) {
        if (position == current.length()) {
            return false;
        }
        Remove step = new Remove(position, length, false, cursor);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof Remove && !((Remove) prev).isBkspace && prev.position == position) {
                ((Remove) prev).removed += step.removed;
                ((Remove) prev).length += length;
                if (onChange != null) {
                    onChange.accept(current);
                }
                return false;
            }
        }
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        if (onChange != null) {
            onChange.accept(current);
        }
        return true;
    }

    /**
     * @param position
     * @return is new step
     */
    public synchronized boolean bkspacePos(int position, int length) {
        if (position < 0) {
            return false;
        }
        Remove step = new Remove(position, length, true, cursor);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof Remove && ((Remove) prev).isBkspace && position + length == prev.position) {
                ((Remove) prev).removed = step.removed + ((Remove) prev).removed;
                --prev.position;
                ((Remove) prev).length += length;
                if (onChange != null) {
                    onChange.accept(current);
                }
                return false;
            }
        }
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        if (onChange != null) {
            onChange.accept(current);
        }
        return true;
    }

    public synchronized boolean shiftLine(int startLine, int lines, boolean shiftDown) {
        if (startLine + lines == current.split("\n", -1).length && shiftDown
                || startLine == 0 && !shiftDown) {
            return false;
        }
        ShiftLine step = new ShiftLine(startLine, lines, 1, shiftDown, cursor);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof ShiftLine && ((ShiftLine) prev).shiftDown == shiftDown && ((ShiftLine) prev).lineCount == lines && startLine == ((ShiftLine) prev).startLine + (shiftDown ? ((ShiftLine) prev).shiftAmount : -((ShiftLine) prev).shiftAmount)) {
                ++((ShiftLine) prev).shiftAmount;
                if (onChange != null) {
                    onChange.accept(current);
                }
                return false;
            }
        }
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        if (onChange != null) {
            onChange.accept(current);
        }
        return true;
    }

    public synchronized void replace(int position, int length, String content) {
        Replace step = new Replace(position, length, content, cursor);
        current = step.applyStep(current);
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        if (onChange != null) {
            onChange.accept(current);
        }
    }

    public synchronized void tabLines(int startLine, int lineCount, boolean reverse) {
        TabLines step = new TabLines(startLine, lineCount, reverse, cursor);
        current = step.applyStep(current);
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        if (onChange != null) {
            onChange.accept(current);
        }
    }

    public synchronized void tabLinesKeepCursor(int startLine, int startLineIndex, int endLineIndex, int lineCount, boolean reverse) {
        TabLinesKeepCursor step = new TabLinesKeepCursor(startLine, startLineIndex, endLineIndex, lineCount, reverse, cursor);
        current = step.applyStep(current);
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
        if (onChange != null) {
            onChange.accept(current);
        }
    }

    /**
     * @return position of step. -1 if nothing to undo.
     */
    public synchronized int undo() {
        if (undo.size() > 0) {
            HistoryStep step = undo.remove(undo.size() - 1);
            current = step.unApplyStep(current);
            redo.add(step);
            if (onChange != null) {
                onChange.accept(current);
            }
            return step.position;
        }
        if (onChange != null) {
            onChange.accept(current);
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
            if (onChange != null) {
                onChange.accept(current);
            }
            return step.position;
        }
        if (onChange != null) {
            onChange.accept(current);
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

        @Override
        protected String applyStep(String input) {
            String result = input.substring(0, position) + added + input.substring(position);
            cursor.updateStartIndex(position + added.length(), result);
            cursor.updateEndIndex(position + added.length(), result);
            return result;
        }

        @Override
        protected String unApplyStep(String input) {
            String result = input.substring(0, position) + input.substring(position + added.length());
            cursor.updateStartIndex(position, result);
            cursor.updateEndIndex(position, result);
            return result;
        }

    }

    protected static class Remove extends HistoryStep {
        boolean isBkspace;
        int length;
        String removed;

        protected Remove(int position, int length, boolean isBkspace, SelectCursor cursor) {
            this.position = position;
            this.length = length;
            this.isBkspace = isBkspace;
            this.cursor = cursor;
        }

        @Override
        protected String applyStep(String input) {
            removed = input.substring(position, position + length);
            String result = input.substring(0, position) + input.substring(position + length);
            cursor.updateStartIndex(position, result);
            cursor.updateEndIndex(position, result);
            return result;
        }

        @Override
        protected String unApplyStep(String input) {
            String result = input.substring(0, position) + removed + input.substring(position);
            cursor.updateStartIndex(position, result);
            cursor.updateEndIndex(position + length, result);
            cursor.arrowEnd = isBkspace;
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

        @Override
        protected String applyStep(String input) {
            oldContent = input.substring(position, position + length);
            String result = input.substring(0, position) + newContent + input.substring(position + length);
            cursor.updateStartIndex(position, result);
            cursor.updateEndIndex(position + newContent.length(), result);
            return result;
        }

        @Override
        protected String unApplyStep(String input) {
            String result = input.substring(0, position) + oldContent + input.substring(position + newContent.length());
            cursor.updateStartIndex(position, result);
            cursor.updateEndIndex(position + oldContent.length(), result);
            return result;
        }

    }

    protected static class ShiftLine extends HistoryStep {
        int startLine;
        int lineCount;
        int shiftAmount;
        boolean shiftDown;

        protected ShiftLine(int startLine, int lineCount, int shiftAmount, boolean shiftDown, SelectCursor cursor) {
            this.startLine = startLine;
            this.lineCount = lineCount;
            this.shiftAmount = shiftAmount;
            this.shiftDown = shiftDown;
            this.cursor = cursor;
        }

        @Override
        protected String applyStep(String input) {
            return shift(input, startLine, lineCount, shiftAmount, shiftDown);
        }

        @Override
        protected String unApplyStep(String input) {
            return shift(input, shiftDown ? startLine + shiftAmount : startLine - shiftAmount, lineCount, shiftAmount, !shiftDown);
        }

        private String shift(String input, int startLine, int lineCount, int shiftAmount, boolean shiftDown) {
            String[] lines = input.split("\n", -1);
            String[] shifted = new String[lines.length];
            int i;
            int startIndex = 0;
            int endIndex;
            if (shiftDown) {
                for (i = 0; i < startLine; ++i) {
                    shifted[i] = lines[i];
                    startIndex += shifted[i].length() + 1;
                }
                for (int j = 0; j < shiftAmount; ++j, ++i) {
                    shifted[i] = lines[startLine + lineCount + j];
                    startIndex += shifted[i].length() + 1;
                }
                endIndex = startIndex;
                for (int j = 0; j < lineCount; ++j, ++i) {
                    shifted[i] = lines[startLine + j];
                    endIndex += shifted[i].length() + 1;
                }
            } else {
                for (i = 0; i < startLine - shiftAmount; ++i) {
                    shifted[i] = lines[i];
                    startIndex += shifted[i].length() + 1;
                }
                endIndex = startIndex;
                for (int j = 0; j < lineCount; ++j, ++i) {
                    shifted[i] = lines[startLine + j];
                    endIndex += shifted[i].length() + 1;
                }
                for (int j = startLine - shiftAmount; j < startLine; ++j, ++i) {
                    shifted[i] = lines[j];
                }
            }
            for (; i < lines.length; ++i) {
                shifted[i] = lines[i];
            }
            String result = String.join("\n", shifted);
            cursor.updateStartIndex(startIndex, result);
            cursor.updateEndIndex(--endIndex, result);
            return result;
        }

    }

    protected static class TabLines extends HistoryStep {
        int startLine;
        int lineCount;
        boolean reversed;

        public TabLines(int startLine, int lineCount, boolean reversed, SelectCursor cursor) {
            this.startLine = startLine;
            this.lineCount = lineCount;
            this.reversed = reversed;
            this.cursor = cursor;
        }

        @Override
        protected String applyStep(String input) {
            return tab(input, startLine, lineCount, reversed);
        }

        @Override
        protected String unApplyStep(String input) {
            return tab(input, startLine, lineCount, !reversed);
        }

        private String tab(String input, int startLine, int lineCount, boolean reversed) {
            String[] lines = input.split("\n", -1);
            int startIndex = 0;
            for (int i = 0; i < startLine; ++i) {
                startIndex += lines[i].length() + 1;
            }
            int endIndex = startIndex;
            for (int i = startLine; i < startLine + lineCount; ++i) {
                if (reversed) {
                    lines[i] = lines[i].replaceFirst("^ {0,4}", "");
                } else {
                    lines[i] = "    " + lines[i];
                }
                endIndex += lines[i].length() + 1;
            }
            String result = String.join("\n", lines);
            cursor.updateStartIndex(startIndex, result);
            cursor.updateEndIndex(--endIndex, result);
            return result;
        }

    }

    protected static class TabLinesKeepCursor extends HistoryStep {
        int startLine;
        int startLineIndex;
        int endLineIndex;
        int lineCount;
        boolean reversed;

        public TabLinesKeepCursor(int startLine, int startLineIndex, int endLineIndex, int lineCount, boolean reversed, SelectCursor cursor) {
            this.startLine = startLine;
            this.startLineIndex = startLineIndex;
            this.endLineIndex = endLineIndex;
            this.lineCount = lineCount;
            this.reversed = reversed;
            this.cursor = cursor;
        }

        @Override
        protected String applyStep(String input) {
            return tab(input, startLine, lineCount, reversed, false);
        }

        @Override
        protected String unApplyStep(String input) {
            return tab(input, startLine, lineCount, !reversed, true);
        }

        private String tab(String input, int startLine, int lineCount, boolean reversed, boolean undo) {
            String[] lines = input.split("\n", -1);
            int startIndex = 0;
            for (int i = 0; i < startLine; ++i) {
                startIndex += lines[i].length() + 1;
            }
            int endIndex = startIndex;
            for (int i = startLine; i < startLine + lineCount; ++i) {
                if (reversed) {
                    lines[i] = lines[i].replaceFirst("^ {0,4}", "");
                } else {
                    lines[i] = "    " + lines[i];
                }
                endIndex += lines[i].length() + 1;
            }
            String result = String.join("\n", lines);
            if (undo) {
                startIndex += Math.max(0, startLineIndex);
                endIndex -= Math.max(0, lines[startLine + lineCount - 1].length() - endLineIndex);
            } else {
                if (reversed) {
                    startIndex += Math.max(0, startLineIndex - 4);
                    endIndex -= Math.max(0, lines[startLine + lineCount - 1].length() - endLineIndex + 4);
                } else {
                    startIndex += startLineIndex + 4;
                    endIndex -= Math.max(0, lines[startLine + lineCount - 1].length() - 4 - endLineIndex);
                }
            }
            cursor.updateStartIndex(startIndex, result);
            cursor.updateEndIndex(--endIndex, result);
            return result;
        }

    }

}
