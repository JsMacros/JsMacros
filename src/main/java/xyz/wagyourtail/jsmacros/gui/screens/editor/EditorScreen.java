package xyz.wagyourtail.jsmacros.gui.screens.editor;

import io.noties.prism4j.AbsVisitor;
import io.noties.prism4j.Prism4j;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.jsmacros.api.classes.FileHandler;
import xyz.wagyourtail.jsmacros.extensionbase.ILanguage;
import xyz.wagyourtail.jsmacros.gui.BaseScreen;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class EditorScreen extends BaseScreen {
    private final static Prism4j prism4j = new Prism4j(new PrismGrammarLocator());
    public final static Style defaultStyle = Style.EMPTY.withFont(new Identifier("minecraft", "uniform"));
    protected final File file;
    protected final FileHandler handler;
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
        final List<Prism4j.Node> nodes = prism4j.tokenize("\\\\(?:x[\\da-fA-F]{2}|u[\\da-fA-F]{4}|u\\{[\\da-fA-F]+\\}|c[a-zA-Z]|0[0-7]{0,2}|[123][0-7]{2}|.)", prism4j.grammar("regex"));
        System.out.println("test");
    }
    
    public void onChange() {
    
    }
    
    public void compileText() {
        final List<Prism4j.Node> nodes = prism4j.tokenize(history.current, prism4j.grammar(getLanguage()));
        final AbsVisitor visitor = new AbsVisitor() {
            @Override
            protected void visitText(@NotNull Prism4j.Text text) {
        
            }
    
            @Override
            protected void visitSyntax(@NotNull Prism4j.Syntax syntax) {
                visit(syntax.children());
            }
        };
        
    }
    
    public void save() {
    
    }
    
    public String getLanguage() {
        final String fname = file.getName();
        String ext = ".js";
        boolean flag = true;
        for (ILanguage l : RunScript.languages) {
            if (fname.endsWith(l.extension())) {
                flag = false;
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
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
}
