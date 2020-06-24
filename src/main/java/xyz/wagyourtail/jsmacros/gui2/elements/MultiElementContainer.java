package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.ArrayList;

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
    protected TextRenderer textRenderer;
    
    public MultiElementContainer(int x, int y, int width, int height, TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
        setPos(x, y, width, height);
        init();
    }
    
    public void init() {
        buttons.clear();
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
