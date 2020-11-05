package xyz.wagyourtail.jsmacros.gui.screens.editor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class History {
    protected int MAX_UNDO = 20;
    protected List<HistoryStep> undo = new ArrayList<>(MAX_UNDO);
    protected List<HistoryStep> redo = new ArrayList<>(MAX_UNDO);
    protected String current;
    
    public History(String start) {
        current = start;
    }
    
    /**
     * @param position
     * @param content
     * @return is new step.
     */
    public synchronized boolean addChar(int position, char content) {
        Add step = new Add(position, String.valueOf(content));
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
    public synchronized boolean deletePos(int position) {
        Remove step = new Remove(position, 1, false);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof Remove && !((Remove) prev).isBkspace && ((Remove) prev).position == position) {
                ((Remove) prev).removed += step.removed;
                ++((Remove) prev).length;
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
    public synchronized boolean bkspacePos(int position) {
        Remove step = new Remove(position, 1, false);
        current = step.applyStep(current);
        if (undo.size() > 0) {
            HistoryStep prev = undo.get(undo.size() - 1);
            if (prev instanceof Remove && ((Remove) prev).isBkspace && position + 1 == ((Remove) prev).position) {
                ((Remove) prev).removed = step.removed + ((Remove) prev).removed;
                --((Remove) prev).position;
                ++((Remove) prev).length;
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
    
    public synchronized void cutPaste(int position, int length, String content) {
        HistoryStep step = new Replace(position, length, content);
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
        protected abstract String applyStep(String input);
        
        protected abstract String unApplyStep(String input);
        
    }
    
    protected static class Add extends HistoryStep {
        String added;
        
        protected Add(int position, String added) {
            this.position = position;
            this.added = added;
        }
        
        protected String applyStep(String input) {
            return input.substring(0, position) + added + input.substring(position);
        }
        
        protected String unApplyStep(String input) {
            return input.substring(0, position) + input.substring(position + added.length());
        }
        
    }
    
    protected static class Remove extends HistoryStep {
        boolean isBkspace = false;
        int length;
        String removed;
        protected Remove(int position, int length, boolean isBkspace) {
            this.position = position;
            this.length = length;
            this.isBkspace = isBkspace;
        }
        
        protected String applyStep(String input) {
            removed = input.substring(position, position + length);
            return input.substring(0, position) + input.substring(position + length);
        }
        
        protected String unApplyStep(String input) {
            return input.substring(0, position) + removed + input.substring(position);
        }
        
    }
    
    protected static class Replace extends HistoryStep {
        int length;
        String oldContent;
        String newContent;
        
        protected Replace(int position, int length, String newContent) {
            this.position = position;
            this.length = length;
            this.newContent = newContent;
        }
        
        protected String applyStep(String input) {
            oldContent = input.substring(position, position + length);
            return input.substring(0, position) + newContent + input.substring(position + length);
        }
        
        protected String unApplyStep(String input) {
            return input.substring(0, position) + oldContent + input.substring(position + newContent.length());
        }
        
    }
    
}
