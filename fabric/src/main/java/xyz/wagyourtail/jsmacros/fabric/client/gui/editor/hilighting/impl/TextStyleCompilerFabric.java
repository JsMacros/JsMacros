package xyz.wagyourtail.jsmacros.fabric.client.gui.editor.hilighting.impl;

import io.noties.prism4j.Prism4j;
import net.minecraft.text.Style;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl.TextStyleCompiler;
import xyz.wagyourtail.jsmacros.client.access.IStyle;

import java.util.Map;
import java.util.Optional;

public class TextStyleCompilerFabric extends TextStyleCompiler {


    public TextStyleCompilerFabric(Style defaultStyle, Map<String, short[]> themeData) {
        super(defaultStyle, themeData);
    }

    @Override
    protected void visitSyntax(@NotNull Prism4j.Syntax syntax) {
        Optional<Integer> update = colorForSyntax(syntax.type(), syntax.alias());
        Style newStyle = update.isPresent() ? ((IStyle)defaultStyle.copy()).setCustomColor(update.get()) : defaultStyle.copy();
        final TextStyleCompiler child = new TextStyleCompilerFabric(newStyle, themeData);
        child.visit(syntax.children());
        appendChildResult(child.getResult());
    }

}
