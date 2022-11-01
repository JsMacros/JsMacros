package xyz.wagyourtail.jsmacros.forge.client.gui.editor.highlighting.impl;

import io.noties.prism4j.Prism4j;
import net.minecraft.text.Style;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.client.access.IStyle;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl.TextStyleCompiler;

import java.util.Map;
import java.util.Optional;

public class TextStyleCompilerForge extends TextStyleCompiler {
    public TextStyleCompilerForge(Style defaultStyle, Map<String, short[]> themeData) {
        super(defaultStyle, themeData);
    }

    @Override
    protected void visitSyntax(@NotNull Prism4j.Syntax syntax) {
    }

}
