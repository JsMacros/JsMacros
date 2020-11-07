package xyz.wagyourtail.jsmacros.gui.elements.editor.children;

import io.noties.prism4j.AbsVisitor;
import io.noties.prism4j.Prism4j;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class TextStyleCompiler extends AbsVisitor {
    private final Style defaultStyle;
    private final TextTheme theme = new DefaultTheme();
    private final List<LiteralText> result = new LinkedList<>();
    
    public TextStyleCompiler(Style defaultStyle) {
        this.defaultStyle = defaultStyle;
        result.add((LiteralText) new LiteralText("").setStyle(defaultStyle));
    }
    
    @Override
    protected void visitText(@NotNull Prism4j.Text text) {
        String[] lines = text.literal().replaceAll("\t", "    ").split("\r\n|\n", -1);
        int i = 0;
        while (i < lines.length) {
            result.get(result.size() - 1).append(new LiteralText(lines[i]).setStyle(defaultStyle));
            if (++i < lines.length) result.add((LiteralText) new LiteralText("").setStyle(defaultStyle));
        }
    }
    
    @Override
    protected void visitSyntax(@NotNull Prism4j.Syntax syntax) {
        TextColor update = colorForSyntax(syntax.type(), syntax.alias());
        Style newStyle = update == null ? defaultStyle : defaultStyle.withColor(update);
        final TextStyleCompiler child = new TextStyleCompiler(newStyle);
        child.visit(syntax.children());
        appendChildResult(child.getResult());
    }
    
    protected void appendChildResult(List<LiteralText> childResult) {
        LiteralText first = childResult.remove(0);
        result.get(result.size() - 1).append(first);
        for (LiteralText line : childResult) {
            result.add(line);
        }
    }
    
    protected TextColor colorForSyntax(String name, String alias) {
        TextColor val = theme.getColorForToken(name);
        if (val != null) return val;
        else val = theme.getColorForToken(alias);
        return val;
    }
    
    
    public List<LiteralText> getResult() {
        return result;
    }
}
