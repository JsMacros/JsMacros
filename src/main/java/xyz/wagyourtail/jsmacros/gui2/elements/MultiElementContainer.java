package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

public abstract class MultiElementContainer extends DrawableHelper {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected ArrayList<AbstractButtonWidget> buttons = new ArrayList<>();
    private Consumer<AbstractButtonWidget> addButton;
    protected TextRenderer textRenderer;
    protected boolean visible = true;
    
    public MultiElementContainer(int x, int y, int width, int height, TextRenderer textRenderer, Consumer<AbstractButtonWidget> addButton) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.addButton = addButton;
    }
    
    public void init() {
        buttons.clear();
    }
    

    public boolean getVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        for (AbstractButtonWidget btn : buttons) {
            btn.visible = visible;
        }
        this.visible = visible;
    }
    
    public AbstractButtonWidget addButton(AbstractButtonWidget btn) {
        buttons.add(btn);
        addButton.accept(btn);
        return btn;
    }
    
    public ArrayList<AbstractButtonWidget> getButtons() {
        return buttons;
    }
    
    public void setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public abstract void render(MatrixStack matricies, int mouseX, int mouseY, float delta);
    
}
