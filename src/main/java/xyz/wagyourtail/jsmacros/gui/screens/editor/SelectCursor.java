package xyz.wagyourtail.jsmacros.gui.screens.editor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.math.MathHelper;

public class SelectCursor {
    private MinecraftClient mc = MinecraftClient.getInstance();
    private Style defaultStyle;
    int startLine = 0;
    int endLine = 0;
    
    int startIndex = 0;
    int endIndex = 0;
    
    int startLineIndex = 0;
    int endLineIndex = 0;
    
    protected int dragStartIndex = 0;
    
    int arrowLineIndex = 0;
    boolean arrowEnd = false;
    
    int startCol = 0;
    int endCol = 0;
    
    public SelectCursor(Style defaultStyle) {
        this.defaultStyle = defaultStyle;
    }
    
    public synchronized void updateSelStart(int startIndex, String current) {
        this.startIndex = MathHelper.clamp(startIndex, 0, current.length());
        String[] prev = current.substring(0, this.startIndex).split("\n", -1);
        startLine = prev.length - 1;
        startCol = mc.textRenderer.getWidth(new LiteralText(prev[startLine]).setStyle(defaultStyle)) - 1;
        startLineIndex = prev[startLine].length();
    }
    
    public synchronized void updateSelEnd(int endIndex, String current) {
        this.endIndex = MathHelper.clamp(endIndex, 0, current.length());
        String[] prev = current.substring(0, this.endIndex).split("\n", -1);
        endLine = prev.length - 1;
        endCol = mc.textRenderer.getWidth(new LiteralText(prev[endLine]).setStyle(defaultStyle));
        endLineIndex = prev[endLine].length();
    }
}
