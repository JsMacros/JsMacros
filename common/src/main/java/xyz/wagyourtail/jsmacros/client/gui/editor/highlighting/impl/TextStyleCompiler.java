package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl;

import io.noties.prism4j.AbsVisitor;
import io.noties.prism4j.Prism4j;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.access.IStyle;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class TextStyleCompiler extends AbsVisitor {
    protected final ChatStyle defaultStyle;
    protected final Map<String, short[]> themeData;
    protected final List<ChatComponentText> result = new LinkedList<>();

    public TextStyleCompiler(ChatStyle defaultStyle, Map<String, short[]> themeData) {
        this.defaultStyle = defaultStyle;
        this.themeData = themeData;
        result.add((ChatComponentText) new ChatComponentText("").setStyle(defaultStyle.copy()));
    }
    
    @Override
    protected void visitText(@NotNull Prism4j.Text text) {
        String[] lines = text.literal().replaceAll("\t", "    ").split("\r\n|\n", -1);
        int i = 0;
        while (i < lines.length) {
            result.get(result.size() - 1).append(new ChatComponentText(lines[i]).setStyle(defaultStyle.copy()));
            if (++i < lines.length) result.add((ChatComponentText) new ChatComponentText("").setStyle(defaultStyle.copy()));
        }
    }

    @Override
    protected void visitSyntax(@NotNull Prism4j.Syntax syntax) {
        Optional<Integer> update = colorForSyntax(syntax.type(), syntax.alias());
        ChatStyle newStyle = update.isPresent() ? ((IStyle)defaultStyle.copy()).jsmacros_setCustomColor(update.get()) : defaultStyle.copy();
        final TextStyleCompiler child = new TextStyleCompiler(newStyle, themeData);
        child.visit(syntax.children());
        appendChildResult(child.getResult());
    }

    protected void appendChildResult(List<ChatComponentText> childResult) {
        ChatComponentText first = childResult.remove(0);
        result.get(result.size() - 1).append(first);
        result.addAll(childResult);
    }
    
    protected Optional<Integer> colorForSyntax(String name, String alias) {
        Optional<Integer> val = getColorForToken(name);
        if (val.isPresent()) return val;
        else val = getColorForToken(alias);
        return val;
    }
    
    public List<ChatComponentText> getResult() {
        return result;
    }
    
    protected Optional<Integer> getColorForToken(@Nullable String name) {
        if (!themeData.containsKey(name)) return Optional.empty();
        short[] color = themeData.get(name);
        return Optional.of((color[0] & 255) << 16 | (color[1] & 255) << 8 | (color[2] & 255));
    }
}
