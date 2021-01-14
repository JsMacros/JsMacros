package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class SelectorDropdownOverlay extends OverlayContainer {
    private final int lineHeight;
    private final List<Text> choices;
    private final List<Button> scrollChoices = new LinkedList<>();
    private final Consumer<Integer> onChoice;
    
    public SelectorDropdownOverlay(int x, int y, int width, int height, List<Text> choices, TextRenderer textRenderer, IOverlayParent parent, Consumer<Integer> onChoice) {
        super(x, y, width, height, textRenderer, parent);
        this.choices = choices;
        this.lineHeight = textRenderer.fontHeight + 1;
        this.onChoice = onChoice;
    }
    
    @Override
    public void init() {
        super.init();
        this.scroll = (Scrollbar) addButton(new Scrollbar(x+width-8, y, 8, height, 0, 0xFF000000, 0xFFFFFFFF, height / (float) lineHeight, this::onScroll));
        int pos = 0;
        for (Text choice : choices) {
            final int finalPos = pos;
            scrollChoices.add((Button) this.addButton(new Button(x, y + pos * lineHeight, width - 8, lineHeight, textRenderer, 0, 0xFF000000, 0x4FFFFFFF, 0xFFFFFFFF, choice, (b) -> {
                if (onChoice != null) onChoice.accept(finalPos);
            })));
            pos += 1;
        }
        
        onScroll(0);
    }
    
    public void onScroll(double page) {
        int pos = 0;
        for (Button btn : scrollChoices) {
            int height = (int) (y + pos * lineHeight - page * this.height);
            btn.visible = height > y && height + lineHeight <= y + this.height;
            btn.setPos(x, height, width - 8, lineHeight);
            pos += 1;
        }
    }
    
    public void onClick(double mouseX, double mouseY, int button) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) close();
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
}
