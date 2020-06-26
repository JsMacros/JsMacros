package xyz.wagyourtail.jsmacros.gui2.elements;

import java.io.File;
import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class FileChooser extends OverlayContainer {
    private File directory;
    private File selected;
    private Scrollbar scroll;
    private Button select;
    private Button rename;
    private Button delete;
    private Consumer<File> setFile;
    private int topScroll;

    public FileChooser(int x, int y, int width, int height, TextRenderer textRenderer, File directory, Consumer<AbstractButtonWidget> addButton, Consumer<OverlayContainer> close, Consumer<File> setFile) {
        super(x, y, width, height, textRenderer, addButton, close);
        this.setFile = setFile;
        this.setDir(directory);
    }

    public void setDir(File dir) {
        this.directory = dir;
    }

    public void selectFile(File f) {

    }
    
    public void init() {
        super.init();
        int w = width - 4;
        this.addButton(new Button(x+width-12, y+2, 10, 10, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
            this.close();
        }));
        scroll = (Scrollbar) this.addButton(new Scrollbar(x + width - 10, y+12, 8, height - 24, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        select = (Button) this.addButton(new Button(x + w * 2 / 3, y + height - 12, w / 3, 10, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Select"), (btn) -> {
            
        }));
    }
    
    public void onScrollbar(double change) {
        
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        // black bg
        fill(matricies, x, y, x + width, y + height, 0xFF000000);
        // 2 layer border
        fill(matricies, x, y, x + width, y + 1, 0x7F7F7F7F);
        fill(matricies, x, y + height - 1, x + width, y + height, 0x7F7F7F7F);
        fill(matricies, x, y + 1, x + 1, y + height - 1, 0x7F7F7F7F);
        fill(matricies, x + width - 1, y + 1, x + width, y + height - 1, 0x7F7F7F7F);

        fill(matricies, x+1, y + 1, x + width - 1, y + 2, 0xFFFFFFFF);
        fill(matricies, x+1, y + height - 2, x + width-1, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + 1, y + 1, x + 2, y + height - 1, 0xFFFFFFFF);
        fill(matricies, x + width - 2, y + 1, x + width - 1, y + height - 1, 0xFFFFFFFF);
        super.render(matricies, mouseX, mouseY, delta);
    }
}
