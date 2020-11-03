package xyz.wagyourtail.jsmacros.gui.screens.editor;

import java.util.ArrayList;
import java.util.List;

public class History {
    protected int MAX_UNDO = 20;
    protected List<HistoryStep> undo = new ArrayList<>(MAX_UNDO );
    protected List<HistoryStep> redo = new ArrayList<>(MAX_UNDO );
    protected String current;
    
    public History(String start) {
        current = start;
    }
    
    public synchronized void add(int position, String content) {
        HistoryStep step = new Add(position, content);
        current = step.applyStep(current);
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
    }
    
    public synchronized void remove(int position, int length) {
        HistoryStep step = new Remove(position, length);
        current = step.applyStep(current);
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
    }
    
    public synchronized void replace(int position, int length, String content) {
        HistoryStep step = new Replace(position, length, content);
        current = step.applyStep(current);
        while (undo.size() >= MAX_UNDO) {
            undo.remove(0);
        }
        undo.add(step);
        redo.clear();
    }
    
    public synchronized void undo() {
        if (undo.size() > 0) {
            HistoryStep step = undo.remove(undo.size() - 1);
            current = step.unApplyStep(current);
            redo.add(step);
        }
    }
    
    public synchronized void redo() {
        if (redo.size() > 0) {
            HistoryStep step = redo.remove(redo.size() - 1);
            current = step.applyStep(current);
            undo.add(step);
        }
    }
    
    protected static abstract class HistoryStep {
        protected abstract String applyStep(String input);
        
        protected abstract String unApplyStep(String input);
    }
    
    protected static class Add extends HistoryStep {
        int position;
        String added;
    
        protected Add(int position, String added) {
            this.position = position;
            this.added = added;
        }
    
        protected String applyStep(String input) {
            return input.substring(0, position) + added + input.substring(position);
        }
        
        protected String unApplyStep(String input) {
            return input.substring(0, position) + input.substring(position+added.length());
        }
    }
    
    protected static class Remove extends HistoryStep {
        int position;
        int length;
        String removed;
    
        protected Remove(int position, int length) {
            this.position = position;
            this.length = length;
        }
    
        protected String applyStep(String input) {
            removed = input.substring(position, position+length);
            return input.substring(0, position) + input.substring(position + length);
        }
        
        protected String unApplyStep(String input) {
            return input.substring(0, position) + removed + input.substring(position);
        }
    }
    
    protected static class Replace extends HistoryStep {
        int position;
        int length;
        String oldContent;
        String newContent;
    
        protected Replace(int position, int length, String newContent) {
            this.position = position;
            this.length = length;
            this.newContent = newContent;
        }
    
        protected String applyStep(String input) {
            oldContent = input.substring(position, position+length);
            return input.substring(0, position) + newContent + input.substring(position+length);
        }
        
        protected String unApplyStep(String input) {
            return input.substring(0, position) + oldContent + input.substring(position+newContent.length());
        }
    }
}
