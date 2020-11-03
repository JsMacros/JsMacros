package xyz.wagyourtail.jsmacros.gui.screens.editor;

import io.noties.prism4j.AbsVisitor;
import io.noties.prism4j.Prism4j;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class TextStyleCompiler extends AbsVisitor {
    private final Style defaultStyle;
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
        Formatting[] update = colorForSyntax(syntax.type(), syntax.alias());
        Style newStyle = update == null ? defaultStyle : defaultStyle.withFormatting(update);
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
    
    protected Formatting[] colorForSyntax(String name, String alias) {
        Formatting[] val = colorForName(name);
        if (val != null) return val;
        else val = colorForName(alias);
        return val;
    }
    
    private Formatting[] colorForName(String name) {
        if (name == null) return null;
        switch (name) {
            case "keyword":
                return new Formatting[] { Formatting.BLUE };
            case "number":
                return new Formatting[] { Formatting.GOLD };
            case "function":
                return new Formatting[] { Formatting.DARK_AQUA };
            case "operator":
                return new Formatting[] { Formatting.BLACK };
            case "regex":
                return new Formatting[] { Formatting.DARK_PURPLE };
            case "function-variable":
                return new Formatting[] { Formatting.DARK_GRAY };
            case "string":
                return new Formatting[] { Formatting.DARK_GREEN };
            default:
                return null;
        }
    }
    
    public List<LiteralText> getResult() {
        return result;
    }
}
