package xyz.wagyourtail.wagyourgui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class SelectorDropdownOverlay extends OverlayContainer {
    private final int lineHeight;
    private final Collection<Text> choices;
    private final List<Button> scrollChoices = new LinkedList<>();
    private final Consumer<Integer> onChoice;
    protected int selected = -1;
    private final double pages;
    
    public SelectorDropdownOverlay(int x, int y, int width, int height, Collection<Text> choices, TextRenderer textRenderer, IOverlayParent parent, Consumer<Integer> onChoice) {
        super(x, y, width, height, textRenderer, parent);
        this.choices = choices;
        this.lineHeight = textRenderer.fontHeight + 1;
        this.onChoice = onChoice;
        this.pages = lineHeight * choices.size() / (height - 4F);
    }
    
    @Override
    public void init() {
        super.init();
        if (pages > 1) this.scroll = addDrawableChild(new Scrollbar(x+width-8, y, 8, height, 0, 0xFF000000, 0xFFFFFFFF, pages, this::onScroll));
        int pos = 0;
        int scrollwidth = pages <= 1 ? width - 4 : width - 10;
        for (Text choice : choices) {
            final int finalPos = pos;
            Button ch = this.addDrawableChild(new Button(x + 2, y + pos * lineHeight + 2, scrollwidth, lineHeight, textRenderer, 0, 0xFF000000, 0x4FFFFFFF, 0xFFFFFFFF, choice, (b) -> {
                if (onChoice != null) onChoice.accept(finalPos);
                close();
            }));
            ch.horizCenter = false;
            scrollChoices.add(ch);
            pos += 1;
        }
        
        if (pages > 1) {
            onScroll(0);
        }
    }
    
    public void onScroll(double page) {
        int pos = 0;
        for (Button btn : scrollChoices) {
            int height = (int) (y + pos * lineHeight - page * this.height + 2);
            btn.visible = height > y + 1 && height + lineHeight < y + this.height - 1;
            btn.setPos(x + 2, height, width - 10, lineHeight);
            pos += 1;
        }
    }
    
    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) close();
    }
    
    public void setSelected(int sel) {
        if (selected != -1) scrollChoices.get(selected).forceHover = false;
        selected = MathHelper.clamp(sel, -1, scrollChoices.size() - 1);
        if (selected != -1) scrollChoices.get(selected).forceHover = true;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_UP:
                if (selected == -1) return false;
                setSelected(selected - 1);
                return true;
            case GLFW.GLFW_KEY_DOWN:
                setSelected(selected + 1);
                return true;
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_TAB:
                if (onChoice != null) onChoice.accept(selected);
                close();
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }
    
}
