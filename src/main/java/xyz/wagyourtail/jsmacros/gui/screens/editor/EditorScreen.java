package xyz.wagyourtail.jsmacros.gui.screens.editor;

import io.noties.prism4j.Prism4j;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.jsmacros.api.classes.FileHandler;
import xyz.wagyourtail.jsmacros.extensionbase.ILanguage;
import xyz.wagyourtail.jsmacros.gui.BaseScreen;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class EditorScreen extends BaseScreen {
    private final static Prism4j prism4j = new Prism4j(new PrismGrammarLocator());
    public final static Style defaultStyle = Style.EMPTY.withFont(new Identifier("minecraft", "uniform"));
    protected final File file;
    protected final FileHandler handler;
    protected List<LiteralText> compiledText = new LinkedList<>();
    public History history;
    
    public EditorScreen(Screen parent, File file) {
        super(new LiteralText("Editor"), parent);
        this.file = file;
        this.handler = new FileHandler(file);
    }
    
    public void init() {
        if (history == null) {
            try {
                history = new History(handler.read());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        compileText();
    }
    
    public void onChange() {
    
    }
    
    public void compileText() {
        final List<Prism4j.Node> nodes = prism4j.tokenize(history.current, prism4j.grammar(getLanguage()));
        final TextStyleCompiler visitor = new TextStyleCompiler(defaultStyle.withColor(Formatting.BLACK));
        visitor.visit(nodes);
        compiledText = visitor.getResult();
    }
    
    public void save() {
    
    }
    
    public String getLanguage() {
        final String fname = file.getName();
        String ext = RunScript.defaultLang.extension();
        for (ILanguage l : RunScript.languages) {
            if (fname.endsWith(l.extension())) {
                ext = l.extension();
                break;
            }
        }
        switch (ext) {
            case ".py":
            case "jython.py":
                return "python";
            case ".lua":
                return "lua";
            default:
                return "javascript";
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        for (int i = 0; i < compiledText.size(); ++i) {
            textRenderer.draw(matrices, compiledText.get(i), 5, 5+i*textRenderer.fontHeight, 0xFFFFFF);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
    
}
